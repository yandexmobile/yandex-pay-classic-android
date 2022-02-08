package com.yandex.pay.core.events

import android.content.Context
import com.yandex.metrica.IReporter
import com.yandex.metrica.ReporterConfig
import com.yandex.metrica.YandexMetrica
import com.yandex.pay.core.BuildConfig
import com.yandex.xplat.common.Log
import com.yandex.xplat.common.Logger
import com.yandex.xplat.common.YSMap
import com.yandex.xplat.eventus.common.EventReporter
import com.yandex.xplat.eventus.common.EventusRegistry
import com.yandex.xplat.eventus.common.LoggingEvent
import com.yandex.xplat.yandex.pay.EventParams
import com.yandex.xplat.yandex.pay.GenericEventNames
import com.yandex.xplat.yandex.pay.YandexPayAnalytics

internal class MetricaLogger(context: Context, debug: Boolean, logging: Boolean) {
    private val reporter: IReporter

    init {
        val config = ReporterConfig.newConfigBuilder(BuildConfig.METRICA_API_KEY).apply {
            if (logging) {
                withLogs()
            }
        }.build()
        YandexMetrica.activateReporter(context, config)
        reporter = YandexMetrica.getReporter(context, BuildConfig.METRICA_API_KEY)
    }

    private val additionalParams =
        YandexPayAnalytics.environment.getAdditionalParams() +
            listOf(
                EventParams.IS_DEBUG to debug,
                EventParams.PAY_VERSION to BuildConfig.VERSION_NAME,
                EventParams.HOST_APP to context.applicationInfo.packageName
            )

    private fun logToMetrica(eventName: String, eventParams: Map<String, Any>) {
        reporter.reportEvent(eventName, eventParams + additionalParams)
    }

    companion object : EventReporter, Logger {
        private lateinit var instance: MetricaLogger

        fun setup(context: Context, isDebug: Boolean, enableLogging: Boolean) {
            instance = MetricaLogger(context, isDebug, enableLogging)
            YandexPayAnalytics.environment.reset()

            EventusRegistry.setEventReporter(this)
            Log.registerDefaultLogger(this)
        }

        override fun report(event: LoggingEvent) {
            logToMetrica(event.name, event.attributes)
        }

        override fun info(message: String) {
            logToMetrica(GenericEventNames.INFO, message)
        }

        override fun warn(message: String) {
            logToMetrica(GenericEventNames.WARN, message)
        }

        override fun error(message: String) {
            logToMetrica(GenericEventNames.ERROR, message)
        }

        private fun logToMetrica(name: String, message: String) =
            logToMetrica(name, mutableMapOf(EventParams.MESSAGE to message))

        private fun logToMetrica(name: String, params: YSMap<String, Any>) =
            instance.logToMetrica(name, params)
    }
}


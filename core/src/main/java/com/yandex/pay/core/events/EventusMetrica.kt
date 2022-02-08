package com.yandex.pay.core.events

import com.yandex.xplat.eventus.common.LoggingEvent

internal class EventusMetrica : YPayMetrica {
    override fun log(event: Event) {
        MetricaLogger.report(
            LoggingEvent(
                event.name,
                event.additionalParams.toMutableMap(),
            )
        )
    }
}

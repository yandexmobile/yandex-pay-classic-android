// <<< AUTOGENERATED BY YANDEX.SCRIPT FROM logging/logger.ts >>>

package com.yandex.xplat.common


public interface Logger {
    fun info(message: String): Unit
    fun warn(message: String): Unit
    fun error(message: String): Unit
}

public open class Log {
    companion object {
        @JvmStatic private val defaultName: String = "default"
        @JvmStatic private val loggers: YSMap<String, Logger> = mutableMapOf()
        @JvmStatic
        open fun registerLogger(name: String, value: Logger): Unit {
            Log.loggers.set(name, value)
        }

        @JvmStatic
        open fun registerDefaultLogger(logger: Logger): Unit {
            Log.registerLogger(Log.defaultName, logger)
        }

        @JvmStatic
        open fun getDefaultLogger(): Logger? {
            return undefinedToNull(Log.loggers.get(Log.defaultName))
        }

        @JvmStatic
        open fun info(message: String): Unit {
            val logger = Log.getDefaultLogger()
            if (logger != null) {
                logger.info(message)
            }
        }

        @JvmStatic
        open fun warn(message: String): Unit {
            val logger = Log.getDefaultLogger()
            if (logger != null) {
                logger.warn(message)
            }
        }

        @JvmStatic
        open fun error(message: String): Unit {
            val logger = Log.getDefaultLogger()
            if (logger != null) {
                logger.error(message)
            }
        }

        @JvmStatic
        open fun reset(): Unit {
            Log.loggers.clear()
        }

    }
}


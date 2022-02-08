package com.yandex.xplat.common

open class FileSystemError(message: String, platformError: Throwable? = null) : YSError(message, platformError) {
    companion object {
        @JvmStatic fun notExists(path: String): YSError =
            FileSystemError("File item is missing at path: '$path'")

        @JvmStatic fun alreadyExists(path: String): YSError =
            FileSystemError("File item already exists at destination path: '$path'")

        @JvmStatic fun failedToRead(path: String): YSError =
            FileSystemError("File item at path could not be read: '$path'")

        @JvmStatic fun unexpectedError(path: String, inner: Throwable? = null): YSError =
            FileSystemError("Received unexpected error when accessing file item at path: '$path'", inner)
    }
}

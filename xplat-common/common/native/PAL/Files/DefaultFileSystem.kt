package com.yandex.xplat.common

import android.content.Context
import android.util.Base64
import java.io.File
import java.io.InputStream
import java.lang.StringBuilder as StringBuilder
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import kotlin.io.DEFAULT_BUFFER_SIZE

class DefaultFileSystem(context: Context) : FileSystemImplementation, FileSystemDirectories {
    override val documentDirectory: String = context.filesDir.absolutePath
    override val cachesDirectory: String = context.cacheDir.absolutePath
    private val executorService = BoundExecutor.operationsExecutor("FileSystemExecutor")
    private val callbackService = BoundExecutor.ResultsExecutor()

    private fun getItemInfoSync(path: String): Result<ItemInfo> {
        try {
            val file = File(path)
            if (!file.exists()) {
                return Result(null, FileSystemError.notExists(path))
            }

            val info = ItemInfo(path, file.isFile, file.length(), file.lastModified())
            return Result(info, null)
        } catch (e: Throwable) {
            return Result(null, FileSystemError.unexpectedError(path, e))
        }
    }

    override fun getItemInfo(path: String): XPromise<ItemInfo> = asyncify(executorService, callbackService) { getItemInfoSync(path) }

    private fun existsSync(path: String) = try {
        File(path).exists()
    } catch (e: Throwable) {
        false
    }

    override fun exists(path: String): XPromise<Boolean> = asyncify(executorService, callbackService) { Result(existsSync(path), null) }

    private fun listDirectorySync(path: String): Result<YSArray<String>> {
        try {
            val file = File(path)
            if (!file.exists()) {
                return Result(null, FileSystemError.notExists(path))
            }
            val paths = file.listFiles().map { it.absolutePath }
            return Result(paths.toMutableList(), null)
        } catch (e: Throwable) {
            return Result(null, FileSystemError.unexpectedError(path, e))
        }
    }

    override fun listDirectory(path: String): XPromise<YSArray<String>> = asyncify(executorService, callbackService) { listDirectorySync(path) }

    private fun readAsStringWithParamsSync(path: String, parameters: ReadParameters): Result<String> {
        val file = File(path)
        val encoding = parameters.encoding

        try {
            if (!file.exists()) {
                return Result(null, FileSystemError.notExists(path))
            }

            if (file.isDirectory) {
                return Result(null, FileSystemError.failedToRead(path))
            }

            var inputStream: InputStream = file.inputStream()
            if (parameters.position != null) {
                inputStream.skip(parameters.position)
            }
            if (parameters.length != null) {
                inputStream = BoundedInputStream(inputStream, parameters.length)
            }

            val string = when (encoding) {
                Encoding.Base64 -> {
                    val bytes = inputStream.buffered().use { it.readBytes() }
                    Base64.encodeToString(bytes, Base64.NO_WRAP)
                }
                else -> {
                    val charset = encoding?.charset ?: StandardCharsets.UTF_8
                    inputStream.bufferedReader(charset).use { it.readText() }
                }
            }

            return Result(string, null)
        } catch (e: Throwable) {
            return Result(null, FileSystemError.unexpectedError(path, e))
        }
    }

    override fun readAsStringWithParams(
        path: String,
        parameters: ReadParameters
    ): XPromise<String> = asyncify(executorService, callbackService) { readAsStringWithParamsSync(path, parameters) }

    private fun writeAsStringWithParamsSync(
        path: String,
        contents: String,
        parameters: WriteParameters
    ): Result<Unit> {
        val file = File(path)
        val encoding = parameters.encoding

        try {
            if (file.exists()) {
                if (file.isFile && parameters.overwrite) {
                    if (!file.delete()) {
                        return Result(null, FileSystemError.unexpectedError(path))
                    }
                } else {
                    return Result(null, FileSystemError.alreadyExists(path))
                }
            }

            when (encoding) {
                Encoding.Base64 -> {
                    val bytes = Base64.decode(contents, Base64.DEFAULT)
                    file.outputStream().buffered().use { it.write(bytes) }
                }
                else -> {
                    val charset = encoding?.charset ?: StandardCharsets.UTF_8
                    file.bufferedWriter(charset).use { it.write(contents) }
                }
            }

            return Result(Unit, null)
        } catch (e: Throwable) {
            return Result(null, FileSystemError.unexpectedError(path, e))
        }
    }

    override fun writeAsStringWithParams(
        path: String,
        contents: String,
        parameters: WriteParameters
    ): XPromise<Unit> = asyncify(executorService, callbackService) { writeAsStringWithParamsSync(path, contents, parameters) }

    private fun readArrayBufferWithParamsSync(
        path: String,
        parameters: ReadParameters
    ): Result<ArrayBuffer> {
        val file = File(path)

        try {
            if (!file.exists()) {
                return Result(null, FileSystemError.notExists(path))
            }

            if (file.isDirectory) {
                return Result(null, FileSystemError.failedToRead(path))
            }

            var inputStream: InputStream = file.inputStream()
            if (parameters.position != null) {
                inputStream.skip(parameters.position)
            }
            if (parameters.length != null) {
                inputStream = BoundedInputStream(inputStream, parameters.length)
            }

            val bytes = inputStream.buffered().use { it.readBytes() }

            return Result(ArrayBuffer(bytes), null)
        } catch (e: Throwable) {
            return Result(null, FileSystemError.unexpectedError(path, e))
        }
    }

    override fun readArrayBufferWithParams(
        path: String,
        parameters: ReadParameters
    ): XPromise<ArrayBuffer> {
        return asyncify(executorService, callbackService) { readArrayBufferWithParamsSync(path, parameters) }
    }

    private fun writeArrayBufferWithParamsSync(
        path: String,
        contents: ArrayBuffer,
        parameters: WriteParameters
    ): Result<Unit> {
        val file = File(path)

        try {
            if (file.exists()) {
                if (file.isFile && parameters.overwrite) {
                    if (!file.delete()) {
                        return Result(null, FileSystemError.unexpectedError(path))
                    }
                } else {
                    return Result(null, FileSystemError.alreadyExists(path))
                }
            }

            file.outputStream().buffered().use { it.write(contents.byteArray) }

            return Result(Unit, null)
        } catch (e: Throwable) {
            return Result(null, FileSystemError.unexpectedError(path, e))
        }
    }

    override fun writeArrayBufferWithParams(
        path: String,
        contents: ArrayBuffer,
        parameters: WriteParameters
    ): XPromise<Unit> {
        return asyncify(executorService, callbackService) { writeArrayBufferWithParamsSync(path, contents, parameters) }
    }

    private fun deleteWithParamsSync(path: String, parameters: DeleteParameters): Result<Unit> {
        val file = File(path)

        try {
            if (!file.exists()) {
                return if (parameters.ignoreAbsence)
                    Result(Unit, null)
                else
                    Result(null, FileSystemError.notExists(path))
            }

            return if (file.deleteRecursively())
                Result(Unit, null)
            else
                Result(null, FileSystemError.unexpectedError(path))
        } catch (e: Throwable) {
            return Result(null, FileSystemError.unexpectedError(path, e))
        }
    }

    override fun deleteWithParams(
        path: String,
        parameters: DeleteParameters
    ): XPromise<Unit> = asyncify(executorService, callbackService) { deleteWithParamsSync(path, parameters) }

    private fun moveWithParamsSync(
        source: String,
        destination: String,
        parameters: MoveParameters
    ): Result<Unit> {
        val sourceFile = File(source)
        val destinationFile = File(destination)

        try {
            if (!sourceFile.exists()) {
                return Result(null, FileSystemError.notExists(source))
            }
            if (destinationFile.exists()) {
                if (parameters.overwrite) {
                    if (!destinationFile.delete()) {
                        return Result(null, FileSystemError.unexpectedError(destination))
                    }
                } else {
                    return Result(null, FileSystemError.alreadyExists(destination))
                }
            }
            ensureParentDirectoryExists(destination, parameters.createIntermediates)?.let { error ->
                return@moveWithParamsSync Result(null, error)
            }

            val rename = sourceFile.renameTo(destinationFile)
            if (!rename) {
                if (!sourceFile.copyRecursively(destinationFile)) {
                    return Result(null, FileSystemError.unexpectedError(source))
                }
                if (!sourceFile.deleteRecursively()) {
                    return Result(null, FileSystemError.unexpectedError(source))
                }
            }

            return Result(Unit, null)
        } catch (e: Throwable) {
            try {
                destinationFile.deleteRecursively()
            } catch (e: Throwable) {
                // quietly delete the destination file
            }
            return Result(null, FileSystemError.unexpectedError(destination, e))
        }
    }

    override fun moveWithParams(
        source: String,
        destination: String,
        parameters: MoveParameters
    ): XPromise<Unit> = asyncify(executorService, callbackService) { moveWithParamsSync(source, destination, parameters) }

    private fun copyWithParamsSync(
        source: String,
        destination: String,
        parameters: CopyParameters
    ): Result<Unit> {
        val sourceFile = File(source)
        val destinationFile = File(destination)

        try {
            if (!sourceFile.exists()) {
                return Result(null, FileSystemError.notExists(source))
            }
            if (destinationFile.exists()) {
                return Result(null, FileSystemError.alreadyExists(destination))
            }
            ensureParentDirectoryExists(destination, parameters.createIntermediates)?.let { error ->
                return@copyWithParamsSync Result(null, error)
            }

            if (!sourceFile.copyRecursively(destinationFile)) {
                return Result(null, FileSystemError.unexpectedError(source))
            }

            return Result(Unit, null)
        } catch (e: Throwable) {
            try {
                destinationFile.deleteRecursively()
            } catch (e: Throwable) {
                // quietly delete the destination file
            }
            return Result(null, FileSystemError.unexpectedError(destination, e))
        }
    }

    override fun copyWithParams(
        source: String,
        destination: String,
        parameters: CopyParameters
    ): XPromise<Unit> = asyncify(executorService, callbackService) { copyWithParamsSync(source, destination, parameters) }

    private fun makeDirectoryWithParamsSync(path: String, parameters: MakeDirectoryParameters): Result<Unit> {
        val file = File(path)

        try {
            if (file.exists()) {
                return Result(null, FileSystemError.alreadyExists(path))
            }

            val success = if (parameters.createIntermediates) file.mkdirs() else file.mkdir()
            return if (success)
                Result(Unit, null)
            else
                Result(null, FileSystemError.unexpectedError(path))
        } catch (e: Throwable) {
            return Result(null, FileSystemError.unexpectedError(path, e))
        }
    }

    override fun makeDirectoryWithParams(
        path: String,
        parameters: MakeDirectoryParameters
    ): XPromise<Unit> = asyncify(executorService, callbackService) { makeDirectoryWithParamsSync(path, parameters) }

    private fun hashSync(path: String, algorithm: HashType): Result<String> {
        val file = File(path)

        try {
            if (!file.exists()) {
                return Result(null, FileSystemError.notExists(path))
            }

            if (file.isDirectory) {
                return Result(null, FileSystemError.failedToRead(path))
            }

            val md = MessageDigest.getInstance(algorithm.messageDigestAlgorithm)

            file.inputStream().buffered().use { inputStream ->
                val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                while (true) {
                    val nRead = inputStream.read(buffer)
                    if (nRead < 0) {
                        break
                    }
                    md.update(buffer, 0, nRead)
                }
            }

            val hexString = StringBuilder()
            for (digestByte in md.digest())
                hexString.append(String.format("%02x", digestByte))

            return Result(hexString.toString(), null)
        } catch (e: Throwable) {
            return Result(null, FileSystemError.unexpectedError(path, e))
        }
    }

    override fun hash(path: String, algorithm: HashType): XPromise<String> = asyncify(executorService, callbackService) { hashSync(path, algorithm) }

    private fun ensureParentDirectoryExists(path: String, createIntermediates: Boolean): YSError? {
        val parentFile = File(path).parentFile

        try {
            if (parentFile.exists()) {
                return if (parentFile.isDirectory) null else FileSystemError.unexpectedError(parentFile.absolutePath)
            }

            return if (createIntermediates) {
                if (parentFile.mkdirs()) null else FileSystemError.unexpectedError(parentFile.absolutePath)
            } else {
                FileSystemError.notExists(parentFile.absolutePath)
            }
        } catch (e: Throwable) {
            return FileSystemError.unexpectedError(parentFile.absolutePath, e)
        }
    }
}

private val HashType.messageDigestAlgorithm: String
    get() = when (this) {
        HashType.Md5 -> "MD5"
        HashType.Sha256 -> "SHA-256"
        HashType.Sha512 -> "SHA-512"
    }

internal val Encoding.charset: Charset?
    get() = when (this) {
        Encoding.Base64 -> null
        Encoding.Utf8 -> StandardCharsets.UTF_8
    }

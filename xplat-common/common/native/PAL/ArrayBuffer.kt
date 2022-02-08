package com.yandex.xplat.common

import android.util.Base64
import java.nio.charset.StandardCharsets

class ArrayBuffer(val byteArray: ByteArray) {
    constructor(length: Int = 0) : this(ByteArray(length))

    val byteLength = byteArray.size
    fun slice(begin: Int, end: Int = byteLength) = byteArray.slice(begin..end)
}

object ArrayBufferHelpers {
    fun arrayBufferFromString(string: String, encoding: Encoding): Result<ArrayBuffer> {
        return try {
            val res = when (encoding) {
                Encoding.Base64 -> {
                    val byteArray = Base64.decode(string, Base64.DEFAULT)
                    ArrayBuffer(byteArray)
                }
                else -> {
                    val charset = encoding.charset ?: StandardCharsets.UTF_8
                    val byteArray = string.toByteArray(charset)
                    ArrayBuffer(byteArray)
                }
            }
            resultValue(res)
        } catch (e: Throwable) {
            resultError(YSError("Failed to encode string: \"$string\" to encoding: \"$encoding\"", e))
        }
    }

    fun arrayBufferToString(arrayBuffer: ArrayBuffer, encoding: Encoding): Result<String> {
        return try {
            val res = when (encoding) {
                Encoding.Base64 -> {
                    Base64.encodeToString(arrayBuffer.byteArray, Base64.DEFAULT)
                }
                else -> {
                    val charset = encoding.charset ?: StandardCharsets.UTF_8
                    String(arrayBuffer.byteArray, charset)
                }
            }
            resultValue(res)
        } catch (e: Throwable) {
            resultError(YSError("Failed to decode bytes: \"${arrayBuffer.byteArray}\" with encoding: \"$encoding\"", e))
        }
    }
}

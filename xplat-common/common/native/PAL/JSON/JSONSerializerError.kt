package com.yandex.xplat.common

open class JSONSerializerError(override val message: String, platformError: Throwable? = null) : YSError(message, platformError) {

    companion object {
        @JvmStatic fun unableToSerialize(inner: Throwable? = null): JSONSerializerError =
            JSONSerializerError("Unable to JSON-serialize object", Throwable(inner?.message))

        @JvmStatic fun unableToDeserialize(item: String? = null, inner: Throwable? = null): JSONSerializerError =
            JSONSerializerError("Unable to JSON-deserialize object \"${item ?: ""}\"", inner)

        @JvmStatic fun badTopLevelObject(kind: JSONItemKind): JSONSerializerError =
            JSONSerializerError("Unable to JSON-deserialize object: ${kind.name}")
    }
}

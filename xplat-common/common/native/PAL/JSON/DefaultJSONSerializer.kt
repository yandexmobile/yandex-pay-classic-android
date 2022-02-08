package com.yandex.xplat.common

import com.squareup.moshi.Moshi

class DefaultJSONSerializer : JSONSerializer {
    private val moshiInstance = Moshi.Builder().build()!!

    override fun serialize(item: JSONItem): Result<String> =
        if (item.kind != JSONItemKind.map && item.kind != JSONItemKind.array) {
            Result(null, JSONSerializerError.badTopLevelObject(item.kind))
        } else try {
            Result(serializeUsingMoshi(JSONItemSupport.from(item)), null)
        } catch (e: Throwable) {
            Result<String>(null, JSONSerializerError.unableToSerialize(e))
        }

    override fun deserialize(item: String): Result<JSONItem> {
        try {
            val jsonItem = if (item.isObjectRepresentation) {
                // The JSON is Object
                moshiInstance.adapter<Map<*, *>>(Map::class.java).fromJson(item)?.let(JSONItemSupport::into)
            } else {
                // The JSON is Array
                moshiInstance.adapter<List<*>>(List::class.java).fromJson(item)?.let(JSONItemSupport::into)
            } ?: return Result(null, JSONSerializerError.unableToDeserialize(item))
            return Result(jsonItem, null)
        } catch (e: Throwable) {
            return Result(null, JSONSerializerError.unableToDeserialize(item, e))
        }
    }

    private fun serializeUsingMoshi(item: Any?): String = when (item) {
        null -> "null"
        is Int -> moshiInstance.adapter(Int::class.java).toJson(item)
        is Long -> moshiInstance.adapter(Long::class.java).toJson(item)
        is Double -> moshiInstance.adapter(Double::class.java).toJson(item)
        is String -> moshiInstance.adapter(String::class.java).toJson(item)
        is Boolean -> moshiInstance.adapter(Boolean::class.java).toJson(item)
        is List<*> -> moshiInstance.adapter(List::class.java).serializeNulls().toJson(item)
        is Map<*, *> -> moshiInstance.adapter(Map::class.java).serializeNulls().toJson(item)
        else -> fatalError("Unknown object type to serialize: ${item::class.java.simpleName}")
    }
}

internal val String.isObjectRepresentation: Boolean
    get() = trimStart().startsWith("{")

object JSONItemSupport {
    fun into(item: Any?): JSONItem = when (item) {
        null -> into()
        is Int -> into(item.toLong())
        is Long -> into(item)
        is Double -> into(item)
        is String -> into(item)
        is Boolean -> into(item)
        is List<*> -> into(item)
        is Map<*, *> -> into(item)
        else -> fatalError("Unknown type of JSON value: $item")
    }

    private fun into(map: Map<*, *>): MapJSONItem =
        map.entries.fold(MapJSONItem()) { res, (key, value) ->
            res.apply { put(key as String, into(value)) }
        }

    private fun into(array: List<*>): ArrayJSONItem =
        array.fold(ArrayJSONItem()) { res, item ->
            res.apply { add(into(item)) }
        }

    private fun into(long: Long) = IntegerJSONItem.fromInt64(long)

    private fun into(double: Double) = DoubleJSONItem(double)

    private fun into(string: String) = StringJSONItem(string)

    private fun into(bool: Boolean) = BooleanJSONItem(bool)

    private fun into() = NullJSONItem()

    fun from(item: JSONItem): Any? = when (item.kind) {
        JSONItemKind.integer -> from(item as IntegerJSONItem)
        JSONItemKind.double -> from(item as DoubleJSONItem)
        JSONItemKind.string -> from(item as StringJSONItem)
        JSONItemKind.boolean -> from(item as BooleanJSONItem)
        JSONItemKind.nullItem -> from()
        JSONItemKind.array -> from(item as ArrayJSONItem)
        JSONItemKind.map -> from(item as MapJSONItem)
    }

    private fun from(int: IntegerJSONItem): Long = int.asInt64()

    private fun from(double: DoubleJSONItem): Double = double.value

    private fun from(string: StringJSONItem): String = string.value

    private fun from(bool: BooleanJSONItem): Boolean = bool.value

    private fun from() = null

    private fun from(map: MapJSONItem): Map<String, Any?> = map.asMap().mapValues { from(it.value) }

    private fun from(array: ArrayJSONItem): List<Any?> = array.asArray().map(::from)
}

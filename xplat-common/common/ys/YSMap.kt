package com.yandex.xplat.common

typealias YSMap<K, V> = MutableMap<K, V>

fun <K, V> YSMap<K, V>.keys(): MutableSet<K> = this.keys
fun <K, V> YSMap<K, V>.values(): MutableSet<V> = this.values.toMutableSet()

fun <K, V> YSMap<K, V>.set(key: K, value: V) = this.apply { put(key, value) }
fun <K, V> YSMap<K, V>.delete(key: K) = this.apply { remove(key) != null }

fun <K, V> YSMap<K, V>.__forEach(callback: (value: V, key: K) -> Unit) {
    this.forEach { entry -> callback(entry.value, entry.key) }
}

fun <K> YSMap<K, *>.has(key: K): Boolean = this.containsKey(key)

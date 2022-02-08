package com.yandex.xplat.common

class DefaultSharedPreferencesProvider(
    private val sharedPreferences: (String) -> android.content.SharedPreferences
) : SharedPreferencesProvider {
    override fun sharedPreferencesWithName(name: String): SharedPreferences = DefaultSharedPreferences(sharedPreferences(name))
}

private class DefaultSharedPreferences(private val prefs: android.content.SharedPreferences) : SharedPreferences {
    override fun getInt32(key: String, def: Int): Int = prefs.getInt(key, def)

    override fun getInt64(key: String, def: Long): Long = prefs.getLong(key, def)

    override fun getBoolean(key: String, def: Boolean): Boolean = prefs.getBoolean(key, def)

    override fun getDouble(key: String, def: Double): Double = prefs.getFloat(key, def.toFloat()).toDouble()

    override fun getString(key: String, def: String?): String? = prefs.getString(key, def)

    override fun getStringSet(key: String, def: YSSet<String>): YSSet<String> = YSSet(prefs.getStringSet(key, def.values)!!)

    override fun getAll(): YSMap<String, Any> = prefs.all.mapValuesTo(mutableMapOf(), { (key, value) -> value!! })

    override fun contains(key: String): Boolean = prefs.contains(key)

    override fun edit(): SharedPreferencesEditor = DefaultSharedPreferencesEditor(prefs.edit())
}

private class DefaultSharedPreferencesEditor(
    private val editor: android.content.SharedPreferences.Editor
) : SharedPreferencesEditor {
    override fun putInt32(key: String, value: Int): SharedPreferencesEditor = also { editor.putInt(key, value) }

    override fun putInt64(key: String, value: Long): SharedPreferencesEditor = also { editor.putLong(key, value) }

    override fun putBoolean(key: String, value: Boolean): SharedPreferencesEditor = also { editor.putBoolean(key, value) }

    override fun putDouble(key: String, value: Double): SharedPreferencesEditor = also { editor.putFloat(key, value.toFloat()) }

    override fun putString(key: String, value: String): SharedPreferencesEditor = also { editor.putString(key, value) }

    override fun putStringSet(key: String, value: YSSet<String>): SharedPreferencesEditor = also { editor.putStringSet(key, value.values) }

    override fun remove(key: String): SharedPreferencesEditor = also { editor.remove(key) }

    override fun clear(): SharedPreferencesEditor = also { editor.clear() }

    override fun commit() {
        editor.commit()
    }

    override fun apply() {
        editor.apply()
    }
}

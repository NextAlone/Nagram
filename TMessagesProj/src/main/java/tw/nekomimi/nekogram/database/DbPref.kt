package tw.nekomimi.nekogram.database

import android.content.SharedPreferences
import org.dizitart.no2.*
import org.dizitart.no2.filters.Filters
import org.telegram.messenger.FileLog
import tw.nekomimi.nekogram.utils.UIUtil

class DbPref(val connection: NitriteCollection) : SharedPreferences {

    init {
        if (!connection.hasIndex("key")) {
            connection.createIndex("key", IndexOptions.indexOptions(IndexType.Unique))
        }
    }

    val listeners = LinkedHashSet<SharedPreferences.OnSharedPreferenceChangeListener>()

    val isEmpty get() = connection.find(FindOptions.limit(0, 1)).count() == 0

    private inline fun <reified T> getAs(key: String, defValue: T): T {
        connection.find(Filters.eq("key", key)).apply {
            if (hasMore()) return first().get("value", T::class.java)
        }
        return defValue
    }

    override fun contains(key: String): Boolean {
        return connection.find(Filters.eq("key", key)).hasMore()
    }

    override fun getBoolean(key: String, defValue: Boolean) = getAs(key, defValue)

    override fun getInt(key: String, defValue: Int) = getAs(key, defValue)

    override fun getAll(): MutableMap<String, *> {
        val allValues = HashMap<String, Any>()
        connection.find().forEach {
            allValues[it.get("key", String::class.java)] = it["value"]
        }
        return all
    }

    override fun getLong(key: String, defValue: Long) = getAs(key, defValue)

    override fun getFloat(key: String, defValue: Float) = getAs(key, defValue)

    override fun getString(key: String, defValue: String?) = getAs(key, defValue)

    override fun getStringSet(key: String, defValues: MutableSet<String>?) = getAs(key, defValues)

    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        listeners.remove(listener)
    }

    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        listener?.apply { listeners.add(this) }
    }

    override fun edit(): PrefEditor {
        return PrefEditor()
    }

    inner class PrefEditor : SharedPreferences.Editor {

        var clear = false
        val toRemove = HashSet<String>()
        val toApply = HashMap<String, Any?>()

        override fun clear(): PrefEditor {
            clear = true
            return this
        }

        override fun putLong(key: String, value: Long): PrefEditor {
            toApply[key] = value
            return this
        }

        override fun putInt(key: String, value: Int): PrefEditor {
            toApply[key] = value
            return this
        }

        override fun remove(key: String): PrefEditor {
            toApply.remove(key)
            toRemove.add(key)
            return this
        }

        override fun putBoolean(key: String, value: Boolean): PrefEditor {
            toApply[key] = value
            return this
        }

        override fun putStringSet(key: String, values: MutableSet<String>?): PrefEditor {
            toApply[key] = values
            return this
        }

        override fun putFloat(key: String, value: Float): PrefEditor {
            toApply[key] = value
            return this
        }

        override fun putString(key: String, value: String?): PrefEditor {
            toApply[key] = value
            return this
        }

        fun putAll(map: MutableMap<String, out Any?>): PrefEditor {

            map.forEach { (key, value) ->

                if (value == null) {
                    connection.remove(Filters.eq("key", key))
                } else {
                    connection.update(Filters.eq("key", key), Document().apply {
                        put("key", key)
                        put("value", value)
                    }, UpdateOptions.updateOptions(true))
                }

            }

            return this

        }

        override fun commit(): Boolean {
            try {
                if (clear) {
                    connection.remove(Filters.ALL)
                } else {
                    toRemove.forEach {
                        connection.remove(Filters.eq("key", it))
                    }
                }
                toApply.forEach { (key, value) ->
                    if (value == null) {
                        connection.remove(Filters.eq("key", key))
                    } else {
                        connection.update(Filters.eq("key", key), Document().apply {
                            put("key", key)
                            put("value", value)
                        }, UpdateOptions.updateOptions(true))
                    }
                }
                return true
            } catch (ex: Exception) {
                FileLog.e(ex)
                return false
            }
        }

        override fun apply() {
            UIUtil.runOnIoDispatcher(Runnable {
                commit()
            })
        }

    }

}
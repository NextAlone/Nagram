package xyz.nextalone.nagram

import android.content.Context
import android.content.SharedPreferences
import org.telegram.messenger.ApplicationLoader
import java.util.IdentityHashMap

object NkmrConfig {
    private const val USER_PREFERENCES_NAME = "nkmrcfg"
    private const val DEFAULT_PREFERENCES_NAME = "nkmrcfg_default"

    private val lock = Any()
    private val listenerWrappers = IdentityHashMap<
        SharedPreferences.OnSharedPreferenceChangeListener,
        SharedPreferences.OnSharedPreferenceChangeListener,
    >()
    private val userPreferences = ApplicationLoader.applicationContext.getSharedPreferences(
        USER_PREFERENCES_NAME,
        Context.MODE_PRIVATE,
    )
    private val defaultPreferences = ApplicationLoader.applicationContext.getSharedPreferences(
        DEFAULT_PREFERENCES_NAME,
        Context.MODE_PRIVATE,
    )

    @JvmField
    val preferences: SharedPreferences = MergedSharedPreferences()

    @JvmStatic
    fun compact() {
        synchronized(lock) {
            val defaults = defaultPreferences.all
            val editor = userPreferences.edit()
            var changed = false
            userPreferences.all.forEach { (key, value) ->
                if (defaults.containsKey(key) && valuesEqual(value, defaults[key])) {
                    editor.remove(key)
                    changed = true
                }
            }
            if (changed) {
                editor.apply()
            }
        }
    }

    @JvmStatic
    fun clear() {
        synchronized(lock) {
            userPreferences.edit().clear().commit()
            defaultPreferences.edit().clear().commit()
        }
    }

    private fun registerDefault(key: String, value: Any?) {
        synchronized(lock) {
            val storedDefault = defaultPreferences.all[key]
            if (!defaultPreferences.contains(key) || !valuesEqual(storedDefault, value)) {
                putValue(defaultPreferences.edit(), key, value).commit()
            }
            if (userPreferences.contains(key) && valuesEqual(userPreferences.all[key], value)) {
                userPreferences.edit().remove(key).apply()
            }
        }
    }

    private fun valuesEqual(first: Any?, second: Any?): Boolean {
        return when {
            first is Set<*> && second is Set<*> -> first == second
            first == null || second == null -> first == second
            first.javaClass != second.javaClass -> false
            else -> first == second
        }
    }

    private fun putValue(
        editor: SharedPreferences.Editor,
        key: String,
        value: Any?,
    ): SharedPreferences.Editor {
        return when (value) {
            null -> editor.remove(key)
            is Boolean -> editor.putBoolean(key, value)
            is Int -> editor.putInt(key, value)
            is Long -> editor.putLong(key, value)
            is Float -> editor.putFloat(key, value)
            is String -> editor.putString(key, value)
            is Set<*> -> {
                require(value.all { it is String }) { "Unsupported preference value for $key: non-String set" }
                @Suppress("UNCHECKED_CAST")
                editor.putStringSet(key, HashSet(value as Set<String>))
            }
            else -> throw IllegalArgumentException("Unsupported preference value for $key: ${value.javaClass.name}")
        }
    }

    private class MergedSharedPreferences : SharedPreferences {
        override fun getAll(): Map<String, *> =
            HashMap<String, Any?>().apply {
                putAll(defaultPreferences.all)
                putAll(userPreferences.all)
            }

        override fun getString(key: String, defValue: String?): String? {
            registerDefault(key, defValue)
            return if (userPreferences.contains(key)) {
                userPreferences.getString(key, defValue)
            } else {
                defValue
            }
        }

        override fun getStringSet(key: String, defValues: Set<String>?): Set<String>? {
            registerDefault(key, defValues)
            return if (userPreferences.contains(key)) {
                userPreferences.getStringSet(key, defValues)
            } else {
                defValues
            }
        }

        override fun getInt(key: String, defValue: Int): Int {
            registerDefault(key, defValue)
            return if (userPreferences.contains(key)) {
                userPreferences.getInt(key, defValue)
            } else {
                defValue
            }
        }

        override fun getLong(key: String, defValue: Long): Long {
            registerDefault(key, defValue)
            return if (userPreferences.contains(key)) {
                userPreferences.getLong(key, defValue)
            } else {
                defValue
            }
        }

        override fun getFloat(key: String, defValue: Float): Float {
            registerDefault(key, defValue)
            return if (userPreferences.contains(key)) {
                userPreferences.getFloat(key, defValue)
            } else {
                defValue
            }
        }

        override fun getBoolean(key: String, defValue: Boolean): Boolean {
            registerDefault(key, defValue)
            return if (userPreferences.contains(key)) {
                userPreferences.getBoolean(key, defValue)
            } else {
                defValue
            }
        }

        override fun contains(key: String): Boolean =
            userPreferences.contains(key) || defaultPreferences.contains(key)

        override fun edit(): SharedPreferences.Editor = CompactingEditor()

        override fun registerOnSharedPreferenceChangeListener(
            listener: SharedPreferences.OnSharedPreferenceChangeListener?,
        ) {
            if (listener == null) return
            synchronized(lock) {
                if (listenerWrappers.containsKey(listener)) return
                val wrapper = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                    listener.onSharedPreferenceChanged(preferences, key)
                }
                listenerWrappers[listener] = wrapper
                userPreferences.registerOnSharedPreferenceChangeListener(wrapper)
            }
        }

        override fun unregisterOnSharedPreferenceChangeListener(
            listener: SharedPreferences.OnSharedPreferenceChangeListener?,
        ) {
            if (listener == null) return
            synchronized(lock) {
                listenerWrappers.remove(listener)?.let {
                    userPreferences.unregisterOnSharedPreferenceChangeListener(it)
                }
            }
        }
    }

    private class CompactingEditor : SharedPreferences.Editor {
        private val editorLock = Any()
        private val changes = LinkedHashMap<String, Any?>()
        private var clearRequested = false

        override fun putString(key: String, value: String?): SharedPreferences.Editor = apply {
            synchronized(editorLock) {
                changes[key] = value
            }
        }

        override fun putStringSet(key: String, values: Set<String>?): SharedPreferences.Editor = apply {
            synchronized(editorLock) {
                changes[key] = values?.let(::HashSet)
            }
        }

        override fun putInt(key: String, value: Int): SharedPreferences.Editor = apply {
            synchronized(editorLock) {
                changes[key] = value
            }
        }

        override fun putLong(key: String, value: Long): SharedPreferences.Editor = apply {
            synchronized(editorLock) {
                changes[key] = value
            }
        }

        override fun putFloat(key: String, value: Float): SharedPreferences.Editor = apply {
            synchronized(editorLock) {
                changes[key] = value
            }
        }

        override fun putBoolean(key: String, value: Boolean): SharedPreferences.Editor = apply {
            synchronized(editorLock) {
                changes[key] = value
            }
        }

        override fun remove(key: String): SharedPreferences.Editor = apply {
            synchronized(editorLock) {
                changes[key] = null
            }
        }

        override fun clear(): SharedPreferences.Editor = apply {
            synchronized(editorLock) {
                clearRequested = true
            }
        }

        override fun commit(): Boolean = execute(commit = true)

        override fun apply() {
            execute(commit = false)
        }

        private fun execute(commit: Boolean): Boolean {
            val pendingChanges: Map<String, Any?>
            val shouldClear: Boolean
            synchronized(editorLock) {
                pendingChanges = LinkedHashMap(changes)
                shouldClear = clearRequested
                changes.clear()
                clearRequested = false
            }

            synchronized(lock) {
                val editor = userPreferences.edit()
                if (shouldClear) {
                    editor.clear()
                }
                val defaults = defaultPreferences.all
                pendingChanges.forEach { (key, value) ->
                    if (value != null && defaults.containsKey(key) && valuesEqual(value, defaults[key])) {
                        editor.remove(key)
                    } else {
                        putValue(editor, key, value)
                    }
                }
                if (commit) {
                    return editor.commit()
                }
                editor.apply()
                return true
            }
        }
    }
}

package tw.nekomimi.nekogram.database

import android.content.SharedPreferences

class WarppedPref(val origin: SharedPreferences) : SharedPreferences by origin {

    override fun getBoolean(key: String, defValue: Boolean): Boolean {
        return try {
            origin.getBoolean(key, defValue)
        } catch (e: ClassCastException) {
            edit().remove(key).apply()
            defValue
        }
    }

    override fun getInt(key: String, defValue: Int): Int {
        return try {
            origin.getInt(key, defValue)
        } catch (e: ClassCastException) {
            edit().remove(key).apply()
            defValue
        }
    }

    override fun getLong(key: String, defValue: Long): Long {
        return try {
            origin.getLong(key, defValue)
        } catch (e: ClassCastException) {
            edit().remove(key).apply()
            defValue
        }
    }

    override fun getFloat(key: String, defValue: Float): Float {
        return try {
            origin.getFloat(key, defValue)
        } catch (e: java.lang.ClassCastException) {
            edit().remove(key).apply()
            defValue
        }
    }


    override fun getString(key: String, defValue: String?): String? {
        return try {
            origin.getString(key, defValue)
        } catch (e: java.lang.ClassCastException) {
            edit().remove(key).apply()
            defValue
        }
    }
}
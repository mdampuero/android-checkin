package cl.clickgroup.checkin.utils

import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesUtils {

    private const val PREFS_NAME = "CGPrefs"

    fun saveData(
        context: Context,
        key1: String, value1: String,
        key2: String, value2: String,
        key3: String, value3: String,
        key4: String, value4: String,
        key5: String, value5: Boolean,
        key6: String, value6: Boolean,
        key7: String, value7: String,
        key8: String, value8: String,
        key9: String, value9: String,
        key10: String, value10: Array<String>,
        key11: String, value11: String,
    ) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString(key1, value1)
        editor.putString(key2, value2)
        editor.putString(key3, value3)
        editor.putString(key4, value4)
        editor.putBoolean(key5, value5)
        editor.putBoolean(key6, value6)
        editor.putString(key7, value7)
        editor.putString(key8, value8)
        editor.putString(key9, value9)
        editor.putString(key10, value10.joinToString(","))
        editor.putString(key11, value11)
        editor.apply()
    }

    fun saveSingleData(context: Context, key: String, value: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString(key, value)

        editor.apply()
    }


    fun getData(context: Context, key: String): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, null)
    }
    fun getDataBoolean(context: Context, key: String): Boolean {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(key, false)
    }
}

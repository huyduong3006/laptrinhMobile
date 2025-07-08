package com.example.quanlydatve_sqlite.utils

import NguoiDung
import android.content.Context
import android.content.SharedPreferences

object SharedPrefHelper {

    private const val PREF_NAME = "user_prefs"
    private const val KEY_USER_ID = "key_user_id"
    private const val KEY_USERNAME = "key_username"
    private const val KEY_PASSWORD = "key_password"
    private const val KEY_HOTEN = "key_hoten"
    private const val KEY_EMAIL = "key_email"
    private const val KEY_NGAYSINH = "key_ngaysinh"
    private const val KEY_PHONE = "key_phone"
    private const val KEY_ROLE = "key_role"                      // ✅ Thêm dòng này
    private const val KEY_IS_LOGGED_IN = "key_is_logged_in"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveUser(context: Context, user: NguoiDung) {
        val editor = getPrefs(context).edit()
        editor.putInt(KEY_USER_ID, user.id)
        editor.putString(KEY_USERNAME, user.username)
        editor.putString(KEY_PASSWORD, user.matKhau)
        editor.putString(KEY_HOTEN, user.hoTen)
        editor.putString(KEY_EMAIL, user.email)
        editor.putString(KEY_NGAYSINH, user.ngaySinh)
        editor.putString(KEY_PHONE, user.phone)
        editor.putString(KEY_ROLE, user.role)                    // ✅ Lưu role
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.apply()
    }

    fun getUser(context: Context): NguoiDung {
        val prefs = getPrefs(context)
        return NguoiDung(
            id = prefs.getInt(KEY_USER_ID, 0),
            username = prefs.getString(KEY_USERNAME, "") ?: "",
            matKhau = prefs.getString(KEY_PASSWORD, "") ?: "",
            hoTen = prefs.getString(KEY_HOTEN, "") ?: "",
            email = prefs.getString(KEY_EMAIL, "") ?: "",
            ngaySinh = prefs.getString(KEY_NGAYSINH, "") ?: "",
            phone = prefs.getString(KEY_PHONE, "") ?: "",
            role = prefs.getString(KEY_ROLE, "user") ?: "user"
        )
    }

    fun getUserId(context: Context): Int? {
        val id = getPrefs(context).getInt(KEY_USER_ID, -1)
        return if (id != -1) id else null
    }

    fun clearUser(context: Context) {
        val editor = getPrefs(context).edit()
        editor.clear()
        editor.apply()
    }

    fun isLoggedIn(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun getUsername(context: Context): String? {
        return getPrefs(context).getString(KEY_USERNAME, null)
    }

    fun getRole(context: Context): String? {
        return getPrefs(context).getString(KEY_ROLE, "user")
    }

    fun clear(context: Context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
    }
}

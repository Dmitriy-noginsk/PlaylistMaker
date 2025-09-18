package com.example.playlistmaker

import android.content.SharedPreferences

class ThemeRepository(private val prefs: SharedPreferences) {
    companion object { private const val KEY_DARK = "KEY_DARK" }

    fun isDark(): Boolean = prefs.getBoolean(KEY_DARK, false)

    fun setDark(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DARK, enabled).apply()
    }
}

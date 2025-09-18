package com.example.playlistmaker

import androidx.appcompat.app.AppCompatDelegate

object ThemeManager {
    fun apply(isDark: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}

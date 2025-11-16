package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        val prefs = getSharedPreferences(PREFS, MODE_PRIVATE)
        val isDark = prefs.getBoolean(KEY_DARK_THEME, false)

        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    companion object {
        private const val PREFS = "playlist_prefs"
        private const val KEY_DARK_THEME = "KEY_DARK"
    }
}

package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.domain.interactor.SettingsInteractor

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        val settingsInteractor: SettingsInteractor =
            Creator.provideSettingsInteractor(applicationContext)

        val isDark = settingsInteractor.isDark()

        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}

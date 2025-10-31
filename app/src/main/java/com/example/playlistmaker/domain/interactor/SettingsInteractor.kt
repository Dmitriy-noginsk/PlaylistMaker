package com.example.playlistmaker.domain.interactor

interface SettingsInteractor {
    fun isDark(): Boolean
    fun setDark(enabled: Boolean)
}
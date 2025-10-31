package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.repository.SettingsRepository

class SettingsInteractorImpl(
    private val repo: SettingsRepository
) : SettingsInteractor {
    override fun isDark() = repo.isDarkTheme()
    override fun setDark(enabled: Boolean) = repo.setDarkTheme(enabled)
}
package com.example.playlistmaker.data.repositoryImpl

import com.example.playlistmaker.domain.repository.SettingsRepository
import com.example.playlistmaker.data.storage.ThemeRepository

class SettingsRepositoryImpl(
    private val themeRepo: ThemeRepository
) : SettingsRepository {
    override fun isDarkTheme() = themeRepo.isDark()
    override fun setDarkTheme(enabled: Boolean) = themeRepo.setDark(enabled)
}
package com.example.playlistmaker.data.repositoryImpl

import com.example.playlistmaker.domain.repository.SettingsRepository
import com.example.playlistmaker.data.storage.ThemeStorage

class SettingsRepositoryImpl(
    private val storage: ThemeStorage
) : SettingsRepository {
    override fun isDarkTheme() = storage.isDark()
    override fun setDarkTheme(enabled: Boolean) = storage.setDark(enabled)
}
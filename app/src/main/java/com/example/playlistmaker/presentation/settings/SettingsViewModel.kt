package com.example.playlistmaker.presentation.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.interactor.SettingsInteractor

class SettingsViewModel(
    private val interactor: SettingsInteractor
) : ViewModel() {

    private val _isDark = MutableLiveData<Boolean>()
    val isDark: LiveData<Boolean> = _isDark

    init {
        _isDark.value = interactor.isDark()
    }

    fun onThemeSwitched(enabled: Boolean) {
        interactor.setDark(enabled)
        _isDark.value = enabled
    }
}

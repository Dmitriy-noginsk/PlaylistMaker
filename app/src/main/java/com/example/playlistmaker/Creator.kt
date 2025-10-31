package com.example.playlistmaker

import android.content.Context
import com.example.playlistmaker.data.network.RetrofitClient
import com.example.playlistmaker.data.storage.SearchHistory
import com.example.playlistmaker.data.storage.ThemeRepository
import com.example.playlistmaker.data.repositoryImpl.TracksRepositoryImpl
import com.example.playlistmaker.data.repositoryImpl.HistoryRepositoryImpl
import com.example.playlistmaker.data.repositoryImpl.SettingsRepositoryImpl
import com.example.playlistmaker.domain.repository.TracksRepository
import com.example.playlistmaker.domain.repository.HistoryRepository
import com.example.playlistmaker.domain.repository.SettingsRepository
import com.example.playlistmaker.domain.interactor.TracksInteractor
import com.example.playlistmaker.domain.interactor.TracksInteractorImpl
import com.example.playlistmaker.domain.interactor.HistoryInteractor
import com.example.playlistmaker.domain.interactor.HistoryInteractorImpl
import com.example.playlistmaker.domain.interactor.SettingsInteractor
import com.example.playlistmaker.domain.interactor.SettingsInteractorImpl

object Creator {

    private fun getTracksRepository(): TracksRepository =
        TracksRepositoryImpl(RetrofitClient.api)

    private fun getHistoryRepository(context: Context): HistoryRepository =
        HistoryRepositoryImpl(
            SearchHistory(
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            )
        )

    private fun getSettingsRepository(context: Context): SettingsRepository =
        SettingsRepositoryImpl(
            ThemeRepository(
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            )
        )

    fun provideTracksInteractor(): TracksInteractor =
        TracksInteractorImpl(getTracksRepository())

    fun provideHistoryInteractor(context: Context): HistoryInteractor =
        HistoryInteractorImpl(getHistoryRepository(context))

    fun provideSettingsInteractor(context: Context): SettingsInteractor =
        SettingsInteractorImpl(getSettingsRepository(context))

    private const val PREFS_NAME = "playlist_prefs"
}
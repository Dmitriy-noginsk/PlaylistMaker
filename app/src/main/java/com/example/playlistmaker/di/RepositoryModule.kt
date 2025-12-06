package com.example.playlistmaker.di

import com.example.playlistmaker.data.repositoryImpl.HistoryRepositoryImpl
import com.example.playlistmaker.data.repositoryImpl.SettingsRepositoryImpl
import com.example.playlistmaker.data.repositoryImpl.TracksRepositoryImpl
import com.example.playlistmaker.domain.repository.HistoryRepository
import com.example.playlistmaker.domain.repository.SettingsRepository
import com.example.playlistmaker.domain.repository.TracksRepository
import org.koin.dsl.module

val repositoryModule = module {

    single<TracksRepository> {
        TracksRepositoryImpl(
            api = get()
        )
    }

    single<HistoryRepository> {
        HistoryRepositoryImpl(
            storage = get()
        )
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(
            storage = get()
        )
    }
}

package com.example.playlistmaker.di

import com.example.playlistmaker.domain.interactor.HistoryInteractor
import com.example.playlistmaker.domain.interactor.HistoryInteractorImpl
import com.example.playlistmaker.domain.interactor.SettingsInteractor
import com.example.playlistmaker.domain.interactor.SettingsInteractorImpl
import com.example.playlistmaker.domain.interactor.TracksInteractor
import com.example.playlistmaker.domain.interactor.TracksInteractorImpl
import org.koin.dsl.module

val interactorModule = module {

    single<TracksInteractor> {
        TracksInteractorImpl(
            repository = get()
        )
    }

    single<HistoryInteractor> {
        HistoryInteractorImpl(
            repository = get()
        )
    }

    single<SettingsInteractor> {
        SettingsInteractorImpl(
            repo = get()
        )
    }
}

package com.example.playlistmaker.di

import com.example.playlistmaker.presentation.library.favorites.FavoritesViewModel
import com.example.playlistmaker.presentation.library.playlists.PlaylistsViewModel
import com.example.playlistmaker.presentation.player.PlayerViewModel
import com.example.playlistmaker.presentation.search.SearchViewModel
import com.example.playlistmaker.presentation.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SearchViewModel(
            tracksInteractor = get(),
            historyInteractor = get()
        )
    }

    viewModel {
        SettingsViewModel(
            interactor = get()
        )
    }

    viewModel {
        PlayerViewModel(
            mediaPlayer = get()
        )
    }

    viewModel { FavoritesViewModel() }
    viewModel { PlaylistsViewModel() }
}

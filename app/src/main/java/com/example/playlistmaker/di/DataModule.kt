package com.example.playlistmaker.di

import android.content.Context
import com.example.playlistmaker.data.network.ItunesApi
import com.example.playlistmaker.data.network.RetrofitClient
import com.example.playlistmaker.data.storage.SearchHistory
import com.example.playlistmaker.data.storage.ThemeStorage
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private const val PREFS_NAME = "playlist_prefs"

val dataModule = module {

    single<ItunesApi> {
        RetrofitClient.api
    }

    single {
        androidContext().getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )
    }

    single {
        Gson()
    }

    single {
        SearchHistory(
            prefs = get(),
            gson = get()
        )
    }

    single {
        ThemeStorage(
            prefs = get()
        )
    }
}

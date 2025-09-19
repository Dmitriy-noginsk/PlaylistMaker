package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(
    private val prefs: SharedPreferences,
    private val gson: Gson = Gson()
) {
    companion object {
        private const val KEY_HISTORY_JSON = "KEY_HISTORY_JSON"
        private const val MAX_SIZE = 10
    }

    private val listType = object : TypeToken<ArrayList<Track>>() {}.type

    fun get(): List<Track> {
        val json = prefs.getString(KEY_HISTORY_JSON, null) ?: return emptyList()
        return try { gson.fromJson<ArrayList<Track>>(json, listType) ?: emptyList() }
        catch (_: Exception) { emptyList() }
    }

    fun add(track: Track) {
        val current = ArrayList(get())
        current.removeAll { it.trackId == track.trackId }
        current.add(0, track)
        save(current.take(MAX_SIZE))
    }

    fun clear() = save(emptyList())

    private fun save(list: List<Track>) {
        prefs.edit().putString(KEY_HISTORY_JSON, gson.toJson(list)).apply()
    }

    fun isNotEmpty(): Boolean = get().isNotEmpty()
}

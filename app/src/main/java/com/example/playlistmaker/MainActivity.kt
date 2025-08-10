package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.content.res.Configuration
import android.net.Uri
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.Toast
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.button.MaterialButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
data class Track(
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl100: String
)

object DemoTracks {
    val list: List<Track> = listOf(
        Track(
            "Smells Like Teen Spirit", "Nirvana", "5:01",
            "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"
        ),
        Track(
            "Billie Jean", "Michael Jackson", "4:35",
            "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"
        ),
        Track(
            "Stayin' Alive", "Bee Gees", "4:10",
            "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"
        ),
        Track(
            "Whole Lotta Love", "Led Zeppelin", "5:33",
            "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"
        ),
        Track(
            "Sweet Child O'Mine", "Guns N' Roses", "5:03",
            "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg"
        )
    )
}
class SearchActivity : AppCompatActivity() {

    companion object {
        private const val KEY_SEARCH_QUERY = "SEARCH_QUERY"
    }
    private lateinit var etSearch: EditText
    private lateinit var btnClear: ImageView
    private lateinit var rvTracks: RecyclerView
    private lateinit var adapter: TracksAdapter
    // Переменная для хранения текста
    private var searchQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        findViewById<View>(R.id.btn_back).setOnClickListener {
            finish()
        }
        // Находим элементы
        etSearch = findViewById(R.id.et_search)
        btnClear = findViewById(R.id.btn_clear)
        rvTracks = findViewById(R.id.rv_tracks)

        rvTracks.layoutManager = LinearLayoutManager(this)
        adapter = TracksAdapter(DemoTracks.list)
        rvTracks.adapter = adapter

        // Отслеживаем ввод текста
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Сохраняем текст в переменную
                searchQuery = s?.toString() ?: ""
                btnClear.visibility = if (searchQuery.isEmpty()) View.GONE else View.VISIBLE
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Кнопка очистки
        btnClear.setOnClickListener {
            etSearch.text.clear()
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(etSearch.windowToken, 0)
            etSearch.clearFocus()
        }

    }

    // Сохраняем текст
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_SEARCH_QUERY, searchQuery)
    }

    // Восстанавливаем текст
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchQuery = savedInstanceState.getString(KEY_SEARCH_QUERY, "")
        etSearch.setText(searchQuery)
    }
}

class TracksAdapter(private val tracks: List<Track>) :
    RecyclerView.Adapter<TracksAdapter.TrackViewHolder>() {
    class TrackViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
    ) {
        private val ivCover: ImageView = itemView.findViewById(R.id.iv_cover)
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        private val tvSubtitle: TextView = itemView.findViewById(R.id.tv_subtitle)

        fun bind(track: Track) {
            tvTitle.text = track.trackName
            tvSubtitle.text = "${track.artistName} • ${track.trackTime}"

            // Радиус из dimens -> px
            val radius = itemView.resources.getDimensionPixelSize(R.dimen.cover_radius)
            Glide.with(itemView)
                .load(track.artworkUrl100)
                .placeholder(R.drawable.placeholder_square)
                .error(R.drawable.placeholder_square)
                .transform(CenterCrop(), RoundedCorners(radius))
                .into(ivCover)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder =
        TrackViewHolder(parent)

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount() = tracks.size
}



class LibraryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)
    }
}

class SettingActivity : AppCompatActivity() {
    private lateinit var btnShare: LinearLayout
    private lateinit var btnSupport: LinearLayout
    private lateinit var btnAgreement: LinearLayout
    private lateinit var switchTheme: SwitchCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        btnShare = findViewById(R.id.btn_share)
        btnSupport = findViewById(R.id.btn_support)
        btnAgreement = findViewById(R.id.btn_agreement)
        switchTheme = findViewById(R.id.switch_dark_theme)

        findViewById<View>(R.id.btn_back).setOnClickListener {
            finish()
        }

        val switchTheme = findViewById<SwitchCompat>(R.id.switch_dark_theme)

        // Инициализируем по текущей теме
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        switchTheme.isChecked = (currentNightMode == Configuration.UI_MODE_NIGHT_YES)

        // Обработка переключения
        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
        
        btnShare.setOnClickListener { shareApp() }
        btnSupport.setOnClickListener { writeSupport() }
        btnAgreement.setOnClickListener { openUserAgreement() }

    }
    private fun shareApp() {
        val shareMessage = getString(R.string.share_message)

        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareMessage)
        }

        val shareIntent = Intent.createChooser(sendIntent, getString(R.string.chooser_share))
        startActivity(shareIntent)
    }

    private fun writeSupport() {
        val email = getString(R.string.support_email)
        val subject = getString(R.string.support_email_subject)
        val body = getString(R.string.support_email_body)

        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }

        val chooser = Intent.createChooser(emailIntent, getString(R.string.chooser_email))
        startActivity(chooser)
    }

    private fun openUserAgreement() {
        val agreementUrl = getString(R.string.agreement_url)
        val viewIntent = Intent(Intent.ACTION_VIEW, Uri.parse(agreementUrl))
        val chooser = Intent.createChooser(viewIntent, getString(R.string.chooser_browser))
        startActivity(chooser)
    }

}

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<MaterialButton>(R.id.btn_search).setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        findViewById<MaterialButton>(R.id.btn_library).setOnClickListener {
            val intent = Intent(this, LibraryActivity::class.java)
            startActivity(intent)
        }

        findViewById<MaterialButton>(R.id.btn_settings).setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }
    }

}



package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.content.res.Configuration
import android.net.Uri
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.button.MaterialButton

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
    }
}

class LibraryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)
    }
}

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

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
        //  Вяжем кнопку поделиться
        findViewById<LinearLayout>(R.id.btn_share).setOnClickListener {
            shareApp()
        }
        //  Вяжем кнопку отправки сообщения
        findViewById<LinearLayout>(R.id.btn_support).setOnClickListener {
            writeSupport()
        }
        //  Вяжем кнопку пользовательское соглашение
        findViewById<LinearLayout>(R.id.btn_agreement).setOnClickListener {
            openUserAgreement()
        }

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



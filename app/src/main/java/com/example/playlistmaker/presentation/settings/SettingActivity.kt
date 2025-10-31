package com.example.playlistmaker.presentation.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.playlistmaker.App
import com.example.playlistmaker.R

class SettingActivity : AppCompatActivity() {

    private lateinit var btnShare: LinearLayout
    private lateinit var btnSupport: LinearLayout
    private lateinit var btnAgreement: LinearLayout
    private lateinit var switchTheme: SwitchCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val root = findViewById<View>(R.id.root_settings)
        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val status = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val nav = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            val extraTop = resources.getDimensionPixelSize(R.dimen.content_top_margin)
            v.updatePadding(top = status.top + extraTop, bottom = nav.bottom)
            insets
        }

        findViewById<View>(R.id.btn_back).setOnClickListener { finish() }

        btnShare = findViewById(R.id.btn_share)
        btnSupport = findViewById(R.id.btn_support)
        btnAgreement = findViewById(R.id.btn_agreement)
        switchTheme = findViewById(R.id.switch_dark_theme)

        val app = applicationContext as App
        switchTheme.isChecked = app.darkTheme

        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            app.switchTheme(isChecked)
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
        startActivity(Intent.createChooser(sendIntent, getString(R.string.chooser_share)))
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
        startActivity(Intent.createChooser(emailIntent, getString(R.string.chooser_email)))
    }

    private fun openUserAgreement() {
        val url = getString(R.string.agreement_url)
        val viewIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(Intent.createChooser(viewIntent, getString(R.string.chooser_browser)))
    }
}
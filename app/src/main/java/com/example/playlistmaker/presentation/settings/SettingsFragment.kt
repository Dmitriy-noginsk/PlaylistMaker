package com.example.playlistmaker.presentation.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private lateinit var btnShare: LinearLayout
    private lateinit var btnSupport: LinearLayout
    private lateinit var btnAgreement: LinearLayout
    private lateinit var switchTheme: SwitchCompat

    private val vm: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnShare = view.findViewById(R.id.btn_share)
        btnSupport = view.findViewById(R.id.btn_support)
        btnAgreement = view.findViewById(R.id.btn_agreement)
        switchTheme = view.findViewById(R.id.switch_dark_theme)

        vm.isDark.observe(viewLifecycleOwner) { isDark ->
            if (switchTheme.isChecked != isDark) {
                switchTheme.isChecked = isDark
            }
        }

        switchTheme.setOnCheckedChangeListener { _, enabled ->
            vm.onThemeSwitched(enabled)

            AppCompatDelegate.setDefaultNightMode(
                if (enabled) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
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
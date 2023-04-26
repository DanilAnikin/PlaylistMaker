package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.example.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar(binding.toolbarInclude.toolbar, getString(R.string.settings_screen_toolbar_title)) {
            finish()
        }

        binding.flShareApp.setOnClickListener {
            shareApp()
        }

        binding.flWriteToSupport.setOnClickListener {
            writeToSupport()
        }

        binding.flTermsOfUse.setOnClickListener {
            showTermsOfUse()
        }

        val sharedPrefs = getSharedPreferences(App.PLAYLIST_MAKER_SHARED_PREFS, MODE_PRIVATE)
        binding.switcherDarkTheme.isChecked = (applicationContext as App).darkTheme
        binding.switcherDarkTheme.setOnCheckedChangeListener { _, checked ->
            (applicationContext as App).switchTheme(checked)
            sharedPrefs.edit { putBoolean(App.DARK_THEME_KEY, checked) }
        }
    }

    private fun showTermsOfUse() {
        val termsOfUseIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(getString(R.string.terms_of_use_url))
        }
        startActivity(termsOfUseIntent)
    }

    private fun writeToSupport() {
        val writeToSupportIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mail_subject))
            putExtra(Intent.EXTRA_TEXT, getString(R.string.mail_text))
        }
        startActivity(writeToSupportIntent)
    }

    private fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_url))
        }
        Intent.createChooser(shareIntent, null).also { startActivity(it) }
    }
}
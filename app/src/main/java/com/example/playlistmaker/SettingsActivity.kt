package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private var _binding: ActivitySettingsBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.flShareApp.setOnClickListener {
            shareApp()
        }

        binding.flWriteToSupport.setOnClickListener {
            writeToSupport()
        }

        binding.flTermsOfUse.setOnClickListener {
            showTermsOfUse()
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
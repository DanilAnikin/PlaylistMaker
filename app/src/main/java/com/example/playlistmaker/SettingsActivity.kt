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
            data = Uri.parse(TERMS_OF_USE_URL)
        }
        startActivity(termsOfUseIntent)
    }

    private fun writeToSupport() {
        val writeToSupportIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(SUPPORT_EMAIL))
            putExtra(Intent.EXTRA_SUBJECT, MAIL_SUBJECT)
            putExtra(Intent.EXTRA_TEXT, MAIL_TEXT)
        }
        startActivity(writeToSupportIntent)
    }

    private fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, SHARE_APP_URL)
        }
        Intent.createChooser(shareIntent, null).also { startActivity(it) }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val SHARE_APP_URL = "https://practicum.yandex.ru/android-developer/"
        const val SUPPORT_EMAIL = "global8iwr@gmail.com"
        const val MAIL_SUBJECT = "Сообщение разработчикам и разработчицам приложения Playlist Maker"
        const val MAIL_TEXT = "Спасибо разработчикам и разработчицам за крутое приложение!"
        const val TERMS_OF_USE_URL = "https://yandex.ru/legal/practicum_offer/"
    }
}
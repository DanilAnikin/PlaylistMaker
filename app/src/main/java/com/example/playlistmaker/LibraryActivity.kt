package com.example.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.playlistmaker.databinding.ActivityLibraryBinding

class LibraryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLibraryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.toolbarInclude.toolbar
        toolbar.title = getString(R.string.library_screen_toolbar_title)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }
    }
}
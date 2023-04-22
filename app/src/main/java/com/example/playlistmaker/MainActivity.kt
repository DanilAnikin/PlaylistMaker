package com.example.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.playlistmaker.databinding.ActivityMainBinding
import com.example.playlistmaker.search.presentation.ui.SearchActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSearch.setOnClickListener {
            Intent(this@MainActivity, SearchActivity::class.java).also { startActivity(it) }
        }

        binding.btnLibrary.setOnClickListener {
            Intent(this@MainActivity, LibraryActivity::class.java).also { startActivity(it) }
        }

        binding.btnSettings.setOnClickListener {
            Intent(this@MainActivity, SettingsActivity::class.java).also { startActivity(it) }
        }
    }
}
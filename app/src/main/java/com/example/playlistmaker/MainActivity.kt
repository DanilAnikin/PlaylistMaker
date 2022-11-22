package com.example.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.playlistmaker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
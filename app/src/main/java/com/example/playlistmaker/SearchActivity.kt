package com.example.playlistmaker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import com.example.playlistmaker.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {

    private var _binding: ActivitySearchBinding? = null
    private val binding get() = requireNotNull(_binding)

    private var searchFieldText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(savedInstanceState != null) {
            binding.etSearch.setText(savedInstanceState.getString(SEARCH_TEXT_KEY))
        }

        binding.etSearch.requestFocus()
        showKeyboard()

        val searchFieldTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.ivClear.isVisible = !binding.etSearch.text.isNullOrEmpty()
                searchFieldText = binding.etSearch.text.toString()
            }

            override fun afterTextChanged(p0: Editable?) {}
        }

        binding.etSearch.addTextChangedListener(searchFieldTextWatcher)

        binding.ivClear.setOnClickListener {
            binding.etSearch.text.clear()
            hideKeyboard()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, searchFieldText)
    }

    companion object {
        const val SEARCH_TEXT_KEY = "search_text"
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(binding.ivClear.windowToken, 0)
    }

    private fun showKeyboard() {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    }
}
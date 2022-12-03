package com.example.playlistmaker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import com.example.playlistmaker.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {

    private var _binding: ActivitySearchBinding? = null
    private val binding get() = requireNotNull(_binding)

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
                binding.ivClear.visibility = getClearBtnVisibility()
            }

            override fun afterTextChanged(p0: Editable?) {}
        }

        binding.etSearch.addTextChangedListener(searchFieldTextWatcher)

        binding.ivClear.setOnClickListener {
            clearSearchField()
            hideKeyboard()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, binding.etSearch.text.toString())
    }

    companion object {
        const val SEARCH_TEXT_KEY = "search_text"
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun clearSearchField() {
        binding.etSearch.text.clear()
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(binding.ivClear.windowToken, 0)
    }

    private fun showKeyboard() {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    }

    private fun getClearBtnVisibility(): Int {
        return if (binding.etSearch.text.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }
}
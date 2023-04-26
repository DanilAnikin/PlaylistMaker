package com.example.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.View.OnClickListener

fun <T : AppCompatActivity> T.setupToolbar(
    toolbar: Toolbar,
    title: String = "",
    listener: OnClickListener
) {
    toolbar.title = title
    setSupportActionBar(toolbar)
    toolbar.setNavigationOnClickListener(listener)
}
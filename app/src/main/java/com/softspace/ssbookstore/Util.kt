package com.softspace.ssbookstore

import android.app.Activity
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat


object  Util {

}

fun Activity.hideSoftKeyboard() {
    currentFocus?.let {
        val inputMethodManager = ContextCompat.getSystemService(this, InputMethodManager::class.java)!!
        inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
    }
}
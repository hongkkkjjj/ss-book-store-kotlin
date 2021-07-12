package com.softspace.ssbookstore.utility

import android.app.Activity
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


object  Util {
    fun convertStringToDate(input: String): LocalDate {
        val formatter = DateTimeFormatter.ofPattern("dd MMM yy", Locale.ENGLISH)
        return LocalDate.parse(input, formatter)
    }

    fun convertDateToString(input: LocalDate): String {
        return input.format(DateTimeFormatter.ofPattern("dd MMM yy"))
    }
}

fun Activity.hideSoftKeyboard() {
    currentFocus?.let {
        val inputMethodManager = ContextCompat.getSystemService(this, InputMethodManager::class.java)!!
        inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
    }
}

fun AppCompatActivity.blockInput() {
    window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
}

fun AppCompatActivity.unblockInput() {
    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
}

fun AppCompatActivity.blockInputForTask(task: () -> Unit) {
    blockInput()
    task.invoke()
    unblockInput()
}

package com.softspace.ssbookstore

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val ivClose = findViewById<ImageView>(R.id.ivClose)
        ivClose.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        setupUI(findViewById(R.id.clRegisterActivity))
    }

    private fun setupUI(view: View) {
        // Set up touch listener for non-text box views to hide keyboard.

        if (view !is EditText && view !is ImageView) {
            view.setOnClickListener {
                hideSoftKeyboard()
            }
        }

        //If a layout container, iterate over children and seed recursion.
        if (view is ViewGroup) {
            for (i in 0 until (view as ViewGroup).childCount) {
                val innerView: View = (view as ViewGroup).getChildAt(i)
                setupUI(innerView)
            }
        }
    }
}
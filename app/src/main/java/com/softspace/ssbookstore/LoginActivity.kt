package com.softspace.ssbookstore

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val tvRegister = findViewById<TextView>(R.id.tvRegister)
        tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        val ivLogo = findViewById<ImageView>(R.id.ivLogo)
        ivLogo.setImageResource(R.drawable.book_open_tate)
    }

    override fun onResume() {
        super.onResume()

        setupUI(findViewById(R.id.clLoginActivity))
    }

    private fun setupUI(view: View) {
        // Set up touch listener for non-text box views to hide keyboard.

        if (view !is EditText && view !is TextView) {
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
package com.softspace.ssbookstore

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.airbnb.lottie.LottieAnimationView
import com.softspace.ssbookstore.adapter.FirestoreAdapter
import com.softspace.ssbookstore.utility.SharePrefKeys
import com.softspace.ssbookstore.utility.hideSoftKeyboard


class LoginActivity : AppCompatActivity() {
    private val TAG = "Login Acitivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()
        hideLoadingScreen()

        val tvRegister = findViewById<TextView>(R.id.tvRegister)
        tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        val ivLogo = findViewById<ImageView>(R.id.ivLogo)
        ivLogo.setImageResource(R.drawable.book_open_tate)

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        btnLogin.setOnClickListener {
            validateLoginTextField()
        }

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val tvUsernameError = findViewById<TextView>(R.id.tvUsernameError)
        val tvPasswordError = findViewById<TextView>(R.id.tvPasswordError)
        addTextChangedListener(etUsername, tvUsernameError)
        addTextChangedListener(etPassword, tvPasswordError)
    }

    override fun onResume() {
        super.onResume()

        setupUI(findViewById(R.id.clLoginActivity))
    }

    private fun addTextChangedListener(editText: EditText, tvError: TextView) {
        editText.addTextChangedListener {
            tvError.isVisible = false
        }
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
            for (i in 0 until view.childCount) {
                val innerView: View = view.getChildAt(i)
                setupUI(innerView)
            }
        }
    }

    private fun validateLoginTextField() {
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val tvUsernameError = findViewById<TextView>(R.id.tvUsernameError)
        val tvPasswordError = findViewById<TextView>(R.id.tvPasswordError)

        val username = etUsername.text.toString()
        val password = etPassword.text.toString()
        tvUsernameError.isVisible = false
        tvPasswordError.isVisible = false

        if (username.isEmpty()) {
            tvUsernameError.text = getString(R.string.message_username_empty)
            tvUsernameError.isVisible = true
        } else if (password.isEmpty()) {
            tvPasswordError.text = getString(R.string.message_password_empty)
            tvPasswordError.isVisible = true
        } else {
            showLoadingScreen()
            FirestoreAdapter.loginWithFirestore(username, password) { boolResult ->
                hideLoadingScreen()
                if (boolResult) {
                    val sharedPref = getSharedPreferences(SharePrefKeys.isUserLogin, 0)
                    val editor = sharedPref.edit()
                    editor.putBoolean(SharePrefKeys.isUserLogin, true)
                    editor.apply()
                    val intent = Intent(this, BookListActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    tvUsernameError.text = getString(R.string.message_username_password_invalid)
                    tvPasswordError.text = getString(R.string.message_username_password_invalid)
                    tvUsernameError.isVisible = true
                    tvPasswordError.isVisible = true
                }
            }
        }
    }

    private fun showLoadingScreen() {
        Log.i(TAG, "Show loading screen")
        val bookListLoadingScreen = findViewById<ConstraintLayout>(R.id.loginLoadingScreen)
        val bookListAnimationView = findViewById<LottieAnimationView>(R.id.loginAnimationView)
        bookListLoadingScreen.isVisible = true
        bookListAnimationView.isVisible = true
        bookListAnimationView.playAnimation()
    }

    private fun hideLoadingScreen() {
        Log.i(TAG, "Hide loading screen")
        val bookListLoadingScreen = findViewById<ConstraintLayout>(R.id.loginLoadingScreen)
        val bookListAnimationView = findViewById<LottieAnimationView>(R.id.loginAnimationView)
        bookListLoadingScreen.isVisible = false
        bookListAnimationView.isVisible = false
        bookListAnimationView.pauseAnimation()
    }
}
package com.softspace.ssbookstore

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.button.MaterialButton
import com.softspace.ssbookstore.adapter.FirestoreAdapter
import com.softspace.ssbookstore.utility.hideSoftKeyboard
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    private val TAG = "Register Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar?.hide()
        hideLoadingScreen()

        val ivClose = findViewById<ImageView>(R.id.ivClose)
        ivClose.setOnClickListener {
            finish()
        }

        btnRegister.setOnClickListener {
            validateRegisterTextField()
        }

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etReenterPassword = findViewById<EditText>(R.id.etReenterPassword)
        val tvUsernameError = findViewById<TextView>(R.id.tvUsernameError)
        val tvPasswordError = findViewById<TextView>(R.id.tvPasswordError)
        val tvReenterPasswordError = findViewById<TextView>(R.id.tvReenterPasswordError)
        addTextChangedListener(etUsername, tvUsernameError)
        addTextChangedListener(etPassword, tvPasswordError)
        addTextChangedListener(etReenterPassword, tvReenterPasswordError)
    }

    override fun onResume() {
        super.onResume()

        setupUI(findViewById(R.id.clRegisterActivity))
    }

    private fun addTextChangedListener(editText: EditText, tvError: TextView) {
        editText.addTextChangedListener {
            tvError.isVisible = false
        }
    }

    private fun setupUI(view: View) {
        // Set up touch listener for non-text box views to hide keyboard.

        if (view !is EditText && view !is ImageView && view !is MaterialButton) {
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

    private fun validateRegisterTextField() {
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etReenterPassword = findViewById<EditText>(R.id.etReenterPassword)
        val tvUsernameError = findViewById<TextView>(R.id.tvUsernameError)
        val tvPasswordError = findViewById<TextView>(R.id.tvPasswordError)
        val tvReenterPasswordError = findViewById<TextView>(R.id.tvReenterPasswordError)

        val username = etUsername.text.toString()
        val password = etPassword.text.toString()
        val reenterPassword = etReenterPassword.text.toString()

        tvUsernameError.isVisible = false
        tvPasswordError.isVisible = false
        tvReenterPasswordError.isVisible = false

        if (username.isEmpty()) {
            tvUsernameError.text = getString(R.string.message_username_empty)
            tvUsernameError.isVisible = true
        } else if (password.isEmpty()) {
            tvPasswordError.text = getString(R.string.message_password_empty)
            tvPasswordError.isVisible = true
        } else if (reenterPassword.isEmpty()) {
            tvReenterPasswordError.text = getString(R.string.message_reenter_password_empty)
            tvReenterPasswordError.isVisible = true
        } else if (password != reenterPassword) {
            tvPasswordError.text = getString(R.string.message_password_reenter_password_not_same)
            tvReenterPasswordError.text = getString(R.string.message_password_reenter_password_not_same)
            tvPasswordError.isVisible = true
            tvReenterPasswordError.isVisible = true
        } else {
            showLoadingScreen()
            FirestoreAdapter.registerUserWithFirestore(username, password) { respObj ->
                hideLoadingScreen()
                if (respObj.isSuccess) {
                    showAlertDialog {
                        setMessage(respObj.message)
                        positiveButton(getString(R.string.ok)) {
                            finish()
                        }
                    }
                } else {
                    showAlertDialog {
                        setMessage(respObj.message)
                        positiveButton(getString(R.string.ok)) {}
                    }
                }
            }
        }
    }

    private fun showLoadingScreen() {
        Log.i(TAG, "Show loading screen")
        val bookListLoadingScreen = findViewById<ConstraintLayout>(R.id.registerLoadingScreen)
        val bookListAnimationView = findViewById<LottieAnimationView>(R.id.registerAnimationView)
        bookListLoadingScreen.isVisible = true
        bookListAnimationView.isVisible = true
        bookListAnimationView.playAnimation()
    }

    private fun hideLoadingScreen() {
        Log.i(TAG, "Hide loading screen")
        val bookListLoadingScreen = findViewById<ConstraintLayout>(R.id.registerLoadingScreen)
        val bookListAnimationView = findViewById<LottieAnimationView>(R.id.registerAnimationView)
        bookListLoadingScreen.isVisible = false
        bookListAnimationView.isVisible = false
        bookListAnimationView.pauseAnimation()
    }

    private fun showAlertDialog(dialogBuilder: AlertDialog.Builder.() -> Unit) {
        val builder = AlertDialog.Builder(this)
        builder.dialogBuilder()
        val dialog = builder.create()

        dialog.show()
    }

    private fun AlertDialog.Builder.positiveButton(text: String = "Okay", handleClick: (which: Int) -> Unit = {}) {
        this.setPositiveButton(text) { dialogInterface, which -> handleClick(which) }
    }
}
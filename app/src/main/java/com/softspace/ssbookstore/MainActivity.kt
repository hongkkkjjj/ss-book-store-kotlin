package com.softspace.ssbookstore

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.softspace.ssbookstore.utility.SharePrefKeys

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Handler(Looper.getMainLooper()).postDelayed({
            val sharedPref = getSharedPreferences(SharePrefKeys.isUserLogin, 0)
            if (sharedPref.getBoolean(SharePrefKeys.isUserLogin, true)) {
                val intent = Intent(this, BookListActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            finish()
        }, 3000)
    }
}
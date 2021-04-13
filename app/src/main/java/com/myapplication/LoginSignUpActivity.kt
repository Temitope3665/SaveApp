package com.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class LoginSignUpActivity : AppCompatActivity() {

    private var loginButton: Button? = null
    private var getStartedButton: Button? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_sign_up)

        loginButton = findViewById(R.id.login_btn)
        getStartedButton = findViewById(R.id.get_started_btn)

        // Login Activity
        loginButton?.setOnClickListener {
            val intLogin = Intent(this@LoginSignUpActivity, LoginActivity::class.java)
            startActivity(intLogin)
        }

        // Get started activity
        getStartedButton?.setOnClickListener {
            val intGetStarted = Intent(this@LoginSignUpActivity, GetStartedActivity::class.java)
            startActivity(intGetStarted)
        }


    }

    // on back pressed
    override fun onBackPressed() {
        return
    }

}

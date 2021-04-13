package com.myapplication


import android.app.LauncherActivity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class MainActivity : AppCompatActivity() {

    private val SPLASH_TIME_OUT = 3000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Splash screen on open app
        Handler().postDelayed({
            val intLoginSignUp = Intent(this@MainActivity, LoginSignUpActivity::class.java)
            startActivity(intLoginSignUp)
            finish()
            overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right)
        }, SPLASH_TIME_OUT)
    }
}

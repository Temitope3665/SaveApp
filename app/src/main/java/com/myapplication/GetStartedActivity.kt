package com.myapplication

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.util.regex.Pattern

class GetStartedActivity : AppCompatActivity() {

    // Password Format
    private val PASSWORD_PATTERN = Pattern.compile(
        "^" +
                "(?=.*[0-9])" +  // at least 1 digit
                "(?=.*[a-z])" +  // at least 1 lower case letter
                //"(?=.*[A-Z])" +  // at least 1 upper case letter
                "(?=\\S+$)" +  // no white spaces
                ".{6,12}" +  // at least 6 characters and less than 12
                "$"
    )

    private var backOption: ImageView? = null
    private var email: EditText? = null
    private var password: EditText? = null
    private var signUpBtn: Button? = null
    private var login: TextView? = null
    private var progressBar: ProgressBar? = null

    private var firebaseAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_started)

        backOption = findViewById(R.id.go_back)
        email = findViewById(R.id.input_email_field)
        password = findViewById(R.id.input_password_field)
        signUpBtn = findViewById(R.id.signUp_btn)
        login = findViewById(R.id.dont_have_acct)
        progressBar = findViewById(R.id.progress_bar)

        firebaseAuth = FirebaseAuth.getInstance()


        // On Click sign up
        signUpBtn?.setOnClickListener {

            val inputEmail = email?.text.toString().trim()
            val inputPassword = password?.text.toString().trim()


            if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail).matches()) {
                Toast.makeText(this@GetStartedActivity, "Enter a valid email address", Toast.LENGTH_SHORT).show()
            }

            else if (!PASSWORD_PATTERN.matcher(inputPassword).matches()) {
                Toast.makeText(this@GetStartedActivity, "Password must contain at least six char, one digit, one lowercase, no whitespace ", Toast.LENGTH_LONG).show()
            }

            else if ((!Patterns.EMAIL_ADDRESS.matcher(inputEmail).matches()) && (!PASSWORD_PATTERN.matcher(inputPassword).matches())) {
                Toast.makeText(this@GetStartedActivity, "Fields must not be empty", Toast.LENGTH_SHORT).show()
            }

            else {

                progressBar!!.visibility = View.VISIBLE

                firebaseAuth?.createUserWithEmailAndPassword(inputEmail, inputPassword)?.addOnCompleteListener ( object : OnCompleteListener<AuthResult> {
                    override fun onComplete(task: Task<AuthResult>) {

                        if (task.isSuccessful) {

                            progressBar!!.visibility = View.INVISIBLE

                            val user: FirebaseUser = firebaseAuth!!.currentUser!!
                            user.sendEmailVerification().addOnCompleteListener ( object : OnCompleteListener<Void> {

                                override fun onComplete(task: Task<Void>) {
                                    if (task.isSuccessful) {

                                        Toast.makeText(this@GetStartedActivity, "Great! Check your mail to verify your account", Toast.LENGTH_LONG).show()
                                        startActivity(Intent(this@GetStartedActivity, LoginActivity::class.java))
                                        finish()
                                    }

                                    else {
                                        progressBar!!.visibility = View.INVISIBLE
                                        val error = task.exception?.message
                                        Toast.makeText(this@GetStartedActivity, "Error: " + error, Toast.LENGTH_LONG).show()
                                    }
                                }
                            })
                        }

                        else {
                            val error = task.exception?.message
                            Toast.makeText(this@GetStartedActivity, "Error: " + error, Toast.LENGTH_LONG).show()
                        }
                    }
                })
            }
        }


        // calling the back option manually
        backOption?.setOnClickListener {
            finish()
        }

        // Go to login
        login?.setOnClickListener {
            startActivity(Intent(this@GetStartedActivity, LoginSignUpActivity::class.java))
            this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }


    }

}

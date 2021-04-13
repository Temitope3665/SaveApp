package com.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth

class ForgetPasswordActivity : AppCompatActivity() {

    private var inputEmail: EditText? = null
    private var resetButton: Button? = null
    private var createAccount: TextView? = null
    private var progressBar: ProgressBar? = null
    private var backBtn: ImageView? = null
    private var cardView: CardView? = null

    private var firebaseAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)

        inputEmail = findViewById(R.id.enter_email_field)
        resetButton = findViewById(R.id.reset_btn)
        createAccount = findViewById(R.id.creat_account)
        progressBar = findViewById(R.id.progress_bar)
        backBtn = findViewById(R.id.go_back)
        cardView = findViewById(R.id.progressBar_cardview)

        firebaseAuth = FirebaseAuth.getInstance()

        // Reset Password here
        resetButton?.setOnClickListener {
            ResetUserAccount()
        }

        // create account
        createAccount?.setOnClickListener {
            startActivity(Intent(this@ForgetPasswordActivity, GetStartedActivity::class.java ))
        }

        // Back Activity
        backBtn?.setOnClickListener {
            startActivity(Intent(this@ForgetPasswordActivity, LoginActivity::class.java ))
            finish()
            overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right)
        }

    }

    // Reset Password Function
    private fun ResetUserAccount() {

        val email = inputEmail?.text.toString().trim()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this@ForgetPasswordActivity, "Enter a valid email address", Toast.LENGTH_SHORT).show()
        }

        else if (TextUtils.isEmpty(email)) {
            Toast.makeText(this@ForgetPasswordActivity, "Field must not be empty", Toast.LENGTH_SHORT).show()
        }

        else {

            progressBar?.visibility = View.VISIBLE
            cardView?.visibility = View.VISIBLE

            firebaseAuth?.sendPasswordResetEmail(email)?.addOnCompleteListener(object : OnCompleteListener<Void> {

                override fun onComplete(task: Task<Void>) {

                    progressBar?.visibility = View.VISIBLE
                    cardView?.visibility = View.INVISIBLE

                    if (task.isSuccessful) {

                        Toast.makeText(this@ForgetPasswordActivity, "Kindly check your mail to reset your password", Toast.LENGTH_LONG).show()

                        startActivity(Intent(this@ForgetPasswordActivity, LoginActivity::class.java))
                        finish()
                        cardView?.visibility = View.INVISIBLE

                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    }

                    else {
                        val error = task.exception?.message
                        Toast.makeText(this@ForgetPasswordActivity, "Error: " + error, Toast.LENGTH_SHORT).show()
                        cardView?.visibility = View.INVISIBLE
                    }
                }

            })

        }
    }

    // on back pressed
    override fun onBackPressed() {
        return
    }
}

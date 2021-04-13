package com.myapplication

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    private var emailInput: EditText? = null
    private var passwordInput: EditText? = null
    private var forgetPassword: TextView? = null
    private var login: Button? = null
    private var signUp: TextView? = null
    private var progressBar: ProgressBar? = null
    private var cardView: CardView? = null
    private var signInWithGoogle: CardView? = null

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN: Int = 1
    private lateinit var gso: GoogleSignInOptions

    private var firebaseAuth: FirebaseAuth? = null


//    override fun onStart() {
//        super.onStart()
//
//        val user = firebaseAuth?.currentUser
//
//        if (user != null) {
//
//            startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
//
//        }
//    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailInput = findViewById(R.id.enter_email)
        passwordInput = findViewById(R.id.enter_password)
        forgetPassword = findViewById(R.id.forget_password)
        login = findViewById(R.id.login_btn)
        signUp = findViewById(R.id.sign_up)
        progressBar = findViewById(R.id.progress_bar)
        cardView = findViewById(R.id.progressBar_cardview)
        signInWithGoogle = findViewById(R.id.google_signIn)


        firebaseAuth = FirebaseAuth.getInstance()

        createRequest()



        // Forget Password Activity
        forgetPassword?.setOnClickListener {
            startActivity(Intent(this@LoginActivity, ForgetPasswordActivity::class.java))
        }


        // Sign Up Activity
        signUp?.setOnClickListener {
            startActivity(Intent(this@LoginActivity, GetStartedActivity::class.java))
        }


        // Google Sign
        signInWithGoogle?.setOnClickListener {
            signInWithGoogle()
        }




        // Login Activity
        login?.setOnClickListener {
            LoginUser()
        }


        // Remember email
        emailInput?.setAutofillHints(View.AUTOFILL_HINT_USERNAME)

    }

    private fun createRequest() {
        // Configure Google Sign In
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun signInWithGoogle() {

        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)

        progressBar?.visibility = View.VISIBLE
        cardView?.visibility = View.VISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception=task.exception

            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account)
            }
            catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
                progressBar?.visibility = View.INVISIBLE
                cardView?.visibility = View.INVISIBLE
            }

        }

    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        firebaseAuth?.signInWithCredential(credential)?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val intent= Intent(this@LoginActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish()

                    progressBar?.visibility = View.INVISIBLE
                    cardView?.visibility = View.INVISIBLE

                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this@LoginActivity, "Login Failed: ", Toast.LENGTH_SHORT).show()

                    progressBar?.visibility = View.INVISIBLE
                    cardView?.visibility = View.INVISIBLE
                }
            }
    }



    // Login Via Email and Password
    private fun LoginUser() {

        val emailId = emailInput?.text.toString().trim()
        val passwordId = passwordInput?.text.toString().trim()

        if (TextUtils.isEmpty(emailId)) {
            Toast.makeText(this@LoginActivity, "Input a valid email", Toast.LENGTH_SHORT).show()
        }

        else if (TextUtils.isEmpty(passwordId)) {
            Toast.makeText(this@LoginActivity, "Password error, input a valid password", Toast.LENGTH_SHORT).show()
        }

        else if (TextUtils.isEmpty(emailId) && TextUtils.isEmpty(passwordId)) {
            Toast.makeText(this@LoginActivity, "Fields must not be empty", Toast.LENGTH_SHORT).show()
        }

        else {

            progressBar?.visibility = View.VISIBLE
            cardView?.visibility = View.VISIBLE

            firebaseAuth?.signInWithEmailAndPassword(emailId, passwordId)?.addOnCompleteListener(object : OnCompleteListener<AuthResult> {

                override fun onComplete(task: Task<AuthResult>) {

                    if (task.isSuccessful) {

                        progressBar?.visibility = View.INVISIBLE
                        cardView?.visibility = View.INVISIBLE

                        val firebaseUser: FirebaseUser = firebaseAuth!!.currentUser!!

                        if (firebaseUser.isEmailVerified) {
                            Toast.makeText(this@LoginActivity, "You are logged in successfully", Toast.LENGTH_SHORT).show()

                            startActivity(Intent(this@LoginActivity, HomeActivity::class.java))

                            cardView?.visibility = View.INVISIBLE
                        }

                        else {

                            cardView?.visibility = View.INVISIBLE
                            Toast.makeText(this@LoginActivity, "Account not verified, sign up again and verify your account", Toast.LENGTH_LONG).show()
                        }

                    }

                    else {

                        val error = task.exception?.message
                        Toast.makeText(this@LoginActivity, "Error: " + error, Toast.LENGTH_SHORT).show()

                        cardView?.visibility = View.INVISIBLE

                    }

                }
            })
        }
    }

    override fun onBackPressed() {
//        val intBack = Intent(this@LoginActivity, LoginSignUpActivity::class.java)
//        startActivity(intBack)
//        finish()
        finish()
    }
}

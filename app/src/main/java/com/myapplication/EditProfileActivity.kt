package com.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class EditProfileActivity : AppCompatActivity() {

    private var firstName: EditText? = null
    private var lastName: EditText? = null
    private var gender: EditText? = null
    private var phoneNo: EditText? = null
    private var saveBtn: Button? = null
    private var cancelBtn: Button? = null
    private var cardView: CardView? = null
    private var progressBar: ProgressBar? = null


    private var firebaseAuth: FirebaseAuth? = null
    private var firebaseDatabase: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        firstName = findViewById(R.id.first_name)
        lastName = findViewById(R.id.last_name)
        gender = findViewById(R.id.gender)
        phoneNo = findViewById(R.id.contact)
        saveBtn = findViewById(R.id.save)
        cancelBtn = findViewById(R.id.cancel)
        cardView = findViewById(R.id.progressBar_cardview)
        progressBar = findViewById(R.id.progress_bar)

        firebaseAuth = FirebaseAuth.getInstance()

        firebaseDatabase = FirebaseDatabase.getInstance("https://save-93248-default-rtdb.firebaseio.com/").reference.child("User").child(firebaseAuth!!.currentUser!!.uid)

        saveBtn?.setOnClickListener {

            saveUserInfo()
        }

        // Cancel Option
        cancelBtn?.setOnClickListener {
            finish()
        }



    }

    private fun saveUserInfo() {



        val inputFirstName = firstName?.text.toString().trim()
        val inputLastName = lastName?.text.toString().trim()
        val inputGender = gender?.text.toString().trim()
        val inputContact = phoneNo?.text.toString().trim()


        if (TextUtils.isEmpty(inputFirstName)) {
            Toast.makeText(this@EditProfileActivity, "Field can not be empty", Toast.LENGTH_SHORT).show()
        }

        else if (TextUtils.isEmpty(inputLastName)) {
            Toast.makeText(this@EditProfileActivity, "Field can not be empty", Toast.LENGTH_SHORT).show()
        }

        else if (TextUtils.isEmpty(inputGender)) {
            Toast.makeText(this@EditProfileActivity, "Field can not be empty", Toast.LENGTH_SHORT).show()
        }

        else if (TextUtils.isEmpty(inputContact)) {
            Toast.makeText(this@EditProfileActivity, "Field can not be empty", Toast.LENGTH_SHORT).show()
        }

        else {

            val userInfo = HashMap<String, Any>()

            userInfo.put("firstName", inputFirstName)
            userInfo.put("lastName", inputLastName)
            userInfo.put("gender", inputGender)
            userInfo.put("contact", inputContact)

            progressBar?.visibility = View.VISIBLE
            cardView?.visibility = View.VISIBLE

            firebaseDatabase?.updateChildren(userInfo)?.addOnCompleteListener(object : OnCompleteListener<Void> {

                override fun onComplete(task: Task<Void>) {
                    if (task.isSuccessful) {
                        Toast.makeText(this@EditProfileActivity, "Information Updated successfully", Toast.LENGTH_SHORT).show()
                        progressBar?.visibility = View.INVISIBLE
                        cardView?.visibility = View.INVISIBLE
                        startActivity(Intent(this@EditProfileActivity, OpionsMenu::class.java))
                    }

                    else {
                        val error = task.exception?.message
                        Toast.makeText(this@EditProfileActivity, "Error: " + error, Toast.LENGTH_SHORT).show()
                        progressBar?.visibility = View.INVISIBLE
                        cardView?.visibility = View.INVISIBLE
                    }
                }

            })

        }

    }


}

package com.myapplication

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_login.*
import java.io.IOException
import java.net.URI
import java.util.*
import kotlin.collections.HashMap

class OpionsMenu : AppCompatActivity() {

    private var backBtn: ImageView? = null
    private var logout: CardView? = null
    private var progressBar: ProgressBar? = null
    private var cardView: CardView? = null
    private var firstName: TextView? = null
    private var lastName: TextView? = null
    private var gender: TextView? = null
    private var contact: TextView? = null
    private var editProfile: CardView? = null
    private var profilePic: ImageView? = null
    private var imageUri: Uri? = null
    private var tapOption: TextView? = null
    private var storage: FirebaseStorage? = null
    private var ImageRef: StorageReference? = null


    private var firebaseAuth: FirebaseAuth? = null
    private var firebaseUser: FirebaseUser? = null
    private var firebaseDatabase: DatabaseReference?  = null

    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opions_menu)

        backBtn = findViewById(R.id.go_back)
        logout = findViewById(R.id.logout_layout)
        progressBar = findViewById(R.id.progress_bar)
        cardView = findViewById(R.id.progressBar_cardview)
        firstName = findViewById(R.id.first_name_display)
        lastName = findViewById(R.id.last_name_display)
        gender = findViewById(R.id.gender_display)
        contact = findViewById(R.id.contact_display)
        editProfile = findViewById(R.id.edit_profile_layout)
        profilePic = findViewById(R.id.profile_picture)
        tapOption = findViewById(R.id.tap_option)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance("https://save-93248-default-rtdb.firebaseio.com/").reference.child("User").child(firebaseAuth!!.currentUser!!.uid)
        firebaseUser = firebaseAuth!!.currentUser
        storage = FirebaseStorage.getInstance()
        ImageRef = storage?.reference?.child("Images")



        // Showing User Information
        firebaseDatabase?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(task: DataSnapshot) {
                if (task.exists()) {
                    val firstNameShow = task.child("firstName").value as String
                    val lastNameShow = task.child("lastName").value as String
                    val genderShow = task.child("gender").value as String
                    val contactShow = task.child("contact").value as String

                    val imageShow = task.child("profilePicture").value as String

                    Picasso.get().load(imageShow).into(profilePic)

                    firstName?.text = firstNameShow
                    lastName?.text = lastNameShow
                    gender?.text = genderShow
                    contact?.text = contactShow

                    //profilePic?.setImageResource(imageShow)

                }
            }

            override fun onCancelled(task: DatabaseError) {

            }

        })

        editProfile?.setOnClickListener {
            startActivity(Intent(this@OpionsMenu, EditProfileActivity::class.java))
        }

        // Profile Picture Set
        profilePic?.setOnClickListener {
            pickImageFromGallery()

        }

        // Using the tap option
        tapOption?.setOnClickListener {
            pickImageFromGallery()
        }


        // Go back option
        backBtn?.setOnClickListener {
            finish()
        }

        // Logout Option
        logout?.setOnClickListener {
            firebaseAuth?.signOut()
            val intLogout = Intent(this@OpionsMenu, LoginActivity::class.java)

//            intLogout.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//            intLogout.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK

            //intLogout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK) || (Intent.FLAG_ACTIVITY_CLEAR_TASK) //make sure user cant go back

            startActivity(intLogout)
            finishAffinity()

        }

    }


    private fun pickImageFromGallery() {
        val gallery = Intent()
        gallery.type = "image/*"
        gallery.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(gallery, 111)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == RESULT_OK && data!!.data != null) {
            imageUri = data.data

            profilePic?.setImageURI(imageUri)


            Toast.makeText(this@OpionsMenu, "Uploading...", Toast.LENGTH_SHORT).show()

            // Uploading Image to Firebase
            UploadImageToFirebase()

            //

//            try {
//                val image = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
//                profilePic?.setImageBitmap(image)
//            }
//
//            catch (error:IOException) {
//                Toast.makeText(this@OpionsMenu, "Error: " + error, Toast.LENGTH_SHORT).show()
//            }
        }
    }

    private fun UploadImageToFirebase() {

        if (imageUri != null) {

            progressBar?.visibility = View.VISIBLE
            cardView?.visibility = View.VISIBLE

            val ref = ImageRef?.child(UUID.randomUUID().toString())

            ref?.putFile(imageUri!!)?.addOnCompleteListener(object : OnCompleteListener<UploadTask.TaskSnapshot>{
                override fun onComplete(task: Task<UploadTask.TaskSnapshot>) {
                    if (task.isSuccessful) {

                        ref.downloadUrl.addOnCompleteListener(object : OnCompleteListener<Uri> {
                            override fun onComplete(task: Task<Uri>) {
                                if (task.isSuccessful) {
                                    val downloadUrl = task.result
                                    val url = downloadUrl.toString()

                                    val uploadImage = HashMap<String, Any>()
                                    uploadImage.put("profilePicture", url)

                                    firebaseDatabase!!.updateChildren(uploadImage).addOnCompleteListener(object : OnCompleteListener<Void> {
                                        override fun onComplete(task: Task<Void>) {
                                            if (task.isSuccessful) {
                                                Toast.makeText(this@OpionsMenu, "Image uploaded successfully", Toast.LENGTH_LONG).show()

                                                progressBar?.visibility = View.INVISIBLE
                                                cardView?.visibility = View.INVISIBLE

                                            }

                                            else {
                                                val error = task.exception?.message
                                                Toast.makeText(this@OpionsMenu, "Error: " + error, Toast.LENGTH_SHORT).show()
                                            }
                                        }

                                    })

                                }

                                else {
                                    val error = task.exception?.message
                                    Toast.makeText(this@OpionsMenu, "Error: " + error, Toast.LENGTH_SHORT).show()
                                    progressBar?.visibility = View.INVISIBLE
                                    cardView?.visibility = View.INVISIBLE
                                }
                            }
                        })

                    }

                    else {
                        val error = task.exception?.message
                        Toast.makeText(this@OpionsMenu, "Error: " + error, Toast.LENGTH_SHORT).show()
                        progressBar?.visibility = View.INVISIBLE
                        cardView?.visibility = View.INVISIBLE
                    }
                }
            })
        }
    }

    override fun onBackPressed() {
        return
    }
}

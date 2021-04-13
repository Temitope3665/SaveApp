package com.myapplication

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class HomeActivity : AppCompatActivity() {

    private var profilePictureOption: ImageView? = null
    private var userName: TextView? = null
    private var myImagesCardView: CardView? = null
    private var myDocumentCardView: CardView? = null
    private var myVideoCardView: CardView? = null
    private var myAudioCardView: CardView? = null
    private var addNewFile: ImageView? = null


    private var storage: FirebaseStorage? = null


    private var firebaseAuth: FirebaseAuth? = null
    private var firebaseUser: FirebaseUser? = null
    private var firebaseDatabase: DatabaseReference?  = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        profilePictureOption = findViewById(R.id.user_profile_image_option)
        userName = findViewById(R.id.user_name_text)
        myImagesCardView = findViewById(R.id.display_my_images)
        myDocumentCardView = findViewById(R.id.display_my_document)
        myVideoCardView = findViewById(R.id.display_my_video)
        myAudioCardView = findViewById(R.id.display_my_audio)
        addNewFile = findViewById(R.id.add_option)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance("https://save-93248-default-rtdb.firebaseio.com/").reference.child("User").child(firebaseAuth!!.currentUser!!.uid)
        firebaseUser = firebaseAuth!!.currentUser
        storage = FirebaseStorage.getInstance()


        // go to images
        myImagesCardView?.setOnClickListener {
            val intImages = Intent(this@HomeActivity, MyImagesActivity::class.java)
            startActivity(intImages)
        }

        // Add option onClick
        addNewFile?.setOnClickListener {
            val intItemUpload = Intent(this@HomeActivity, ItemToUploadActivity::class.java)
            startActivity(intItemUpload)
        }




        // Set profile picture and last name to the home
        firebaseDatabase?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(task: DataSnapshot) {
                if (task.exists()) {

                    val imageShow = task.child("profilePicture").value as String
                    val lastNameShow = task.child("lastName").value as String

                    Picasso.get().load(imageShow).into(profilePictureOption)

                    userName?.text = lastNameShow

                }
            }

            override fun onCancelled(task: DatabaseError) {

            }

        })


        // Option onCLick - Profile Picture clicked
        profilePictureOption?.setOnClickListener {
            val int = Intent(this@HomeActivity, OpionsMenu::class.java)
            startActivity(int)
        }

    }


    override fun onBackPressed() {
        Toast.makeText(this@HomeActivity, "You cannot go back, kindly sign out", Toast.LENGTH_SHORT).show()
        return
    }

}

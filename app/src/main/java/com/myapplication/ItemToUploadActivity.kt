package com.myapplication

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

class ItemToUploadActivity : AppCompatActivity() {

    private var addImageHere: CardView? = null
    private var addDocumentHere: CardView? = null
    private var addVideoHere: CardView? = null
    private var addAudioHere: CardView? = null
    private var goBackOption: ImageView? = null
    private var progressBar: ProgressBar? = null
    private var cardView: CardView? = null
    private var viewFiles: TextView? = null

    private lateinit var uri: Uri

    private var firebaseAuth: FirebaseAuth? = null
    private var firebaseUser: FirebaseUser? = null
    private var firebaseStorage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private var firebaseDatabase: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_to_upload)

        addImageHere = findViewById(R.id.add_image_here)
        addDocumentHere = findViewById(R.id.add_document_here)
        addVideoHere = findViewById(R.id.add_video_here)
        addAudioHere = findViewById(R.id.add_audio_here)
        progressBar = findViewById(R.id.progress_bar)
        cardView = findViewById(R.id.progressBar_cardview)
        viewFiles = findViewById(R.id.view_files)

        goBackOption = findViewById(R.id.go_back)


        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth!!.currentUser
        firebaseStorage = FirebaseStorage.getInstance()
        storageReference = firebaseStorage?.reference?.child("Uploads")
        firebaseDatabase =
            FirebaseDatabase.getInstance("https://save-93248-default-rtdb.firebaseio.com/").reference.child(
                "User"
            ).child(firebaseAuth!!.currentUser!!.uid)



        // View files
        viewFiles?.setOnClickListener {
            finish()
        }

        // Go back option
        goBackOption?.setOnClickListener {
            finish()
        }


        // Onclick options for all
        addImageHere?.setOnClickListener {
            addImageFromPhone()
        }

        addDocumentHere?.setOnClickListener {
            addDocumentFromPhone()
        }

        addAudioHere?.setOnClickListener {
            addAudioFromPhone()
        }

        addVideoHere?.setOnClickListener {
            addVideoFromPhone()
        }


    }

    // Add Image Function
    private fun addImageFromPhone() {
        val gallery = Intent()
        gallery.type = "image/*"
        gallery.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(gallery, 111)

        progressBar?.visibility = View.VISIBLE
        cardView?.visibility = View.VISIBLE
    }

    // Add Document Function
    private fun addDocumentFromPhone() {
        val document = Intent()
        document.type = "docx/*" + "pdf/*"
        document.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(document, 112)
    }

    // Add Audio Function
    private fun addAudioFromPhone() {
        val audio = Intent()
        audio.type = "audio/*"
        audio.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(audio, 113)

        progressBar?.visibility = View.VISIBLE
        cardView?.visibility = View.VISIBLE
    }

    private fun addVideoFromPhone() {
        val video = Intent()
        video.type = "video/*"
        video.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(video, 114)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 111 && resultCode == RESULT_OK && data!!.data != null) {
            uri = data.data!!
            Toast.makeText(this@ItemToUploadActivity, "Uploading...", Toast.LENGTH_SHORT).show()

            uploadImageToFirebase()
        } else if (requestCode == 112 && resultCode == RESULT_OK && data!!.data != null) {
            uri = data.data!!
            Toast.makeText(this@ItemToUploadActivity, "Uploading...", Toast.LENGTH_SHORT).show()

            uploadDocumentToFirebase()
        } else if (requestCode == 113 && resultCode == RESULT_OK && data!!.data != null) {
            uri = data.data!!
            Toast.makeText(this@ItemToUploadActivity, "Uploading...", Toast.LENGTH_SHORT).show()

            uploadAudioToFirebase()
        } else if (requestCode == 114 && resultCode == RESULT_OK && data!!.data != null) {
            uri = data.data!!
            Toast.makeText(this@ItemToUploadActivity, "Uploading...", Toast.LENGTH_SHORT).show()

            uploadVideoToFirebase()
        }
    }



    private fun uploadImageToFirebase() {
        if (uri != null) {

            progressBar?.visibility = View.VISIBLE
            cardView?.visibility = View.VISIBLE

            val reference = storageReference?.child(uri.lastPathSegment.toString())

            reference!!.putFile(uri).addOnCompleteListener(object : OnCompleteListener<UploadTask.TaskSnapshot> {
                    override fun onComplete(task: Task<UploadTask.TaskSnapshot>) {
                        if (task.isSuccessful) {

                            val downloadUrl = task.result
                            val url = downloadUrl.toString()

                            val uploadPicture = HashMap<String, Any>()

                            uploadPicture.put("myImages", url)

                            firebaseDatabase?.updateChildren(uploadPicture)?.addOnCompleteListener(object : OnCompleteListener<Void> {
                                override fun onComplete(task: Task<Void>) {
                                    if (task.isSuccessful) {
                                        Toast.makeText(this@ItemToUploadActivity, "Image uploaded successfully", Toast.LENGTH_LONG).show()

                                        progressBar?.visibility = View.INVISIBLE
                                        cardView?.visibility = View.INVISIBLE
                                    }

                                    else {
                                        val error = task.exception?.message
                                        Toast.makeText(this@ItemToUploadActivity, "Error: " + error, Toast.LENGTH_LONG).show()
                                    }
                                }

                            })
                        }

                        else {
                            val error = task.exception?.message
                            Toast.makeText(this@ItemToUploadActivity, "Error: " + error, Toast.LENGTH_LONG).show()
                        }
                    }
                })
        }
    }

    private fun uploadDocumentToFirebase() {
        if (uri != null) {

            progressBar?.visibility = View.VISIBLE
            cardView?.visibility = View.VISIBLE

            val reference = storageReference?.child(uri.lastPathSegment.toString())

            reference!!.putFile(uri).addOnCompleteListener(object : OnCompleteListener<UploadTask.TaskSnapshot> {
                override fun onComplete(task: Task<UploadTask.TaskSnapshot>) {
                    if (task.isSuccessful) {

                        val downloadDocumentUrl = task.result
                        val documentUrl = downloadDocumentUrl.toString()

                        val uploadDocument = HashMap<String, Any>()

                        uploadDocument.put("myDocument", documentUrl)

                        firebaseDatabase!!.updateChildren(uploadDocument).addOnCompleteListener(object : OnCompleteListener<Void> {
                            override fun onComplete(task: Task<Void>) {
                                if (task.isSuccessful) {
                                    Toast.makeText(this@ItemToUploadActivity, "Document uploaded successfully", Toast.LENGTH_LONG).show()

                                    progressBar?.visibility = View.INVISIBLE
                                    cardView?.visibility = View.INVISIBLE
                                }

                                else {
                                    val error = task.exception?.message
                                    Toast.makeText(this@ItemToUploadActivity, "Error: " + error, Toast.LENGTH_LONG).show()
                                }
                            }

                        })

                    }

                    else {
                        val error = task.exception?.message
                        Toast.makeText(this@ItemToUploadActivity, "Error: " + error, Toast.LENGTH_LONG).show()
                    }
                }

            })
        }
    }

    private fun uploadAudioToFirebase() {
        if (uri != null) {

            progressBar?.visibility = View.VISIBLE
            cardView?.visibility = View.VISIBLE

            val reference = storageReference?.child(uri.lastPathSegment.toString())

            reference!!.putFile(uri).addOnCompleteListener(object : OnCompleteListener<UploadTask.TaskSnapshot> {
                override fun onComplete(task: Task<UploadTask.TaskSnapshot>) {
                    if (task.isSuccessful) {

                        val downloadAudioUrl = task.result
                        val audioUrl = downloadAudioUrl.toString()

                        val uploadAudio = HashMap<String, Any>()

                        uploadAudio.put("myAudio", audioUrl)

                        firebaseDatabase!!.updateChildren(uploadAudio).addOnCompleteListener(object : OnCompleteListener<Void> {
                            override fun onComplete(task: Task<Void>) {
                                if (task.isSuccessful) {
                                    Toast.makeText(this@ItemToUploadActivity, "Audio uploaded successfully", Toast.LENGTH_LONG).show()

                                    progressBar?.visibility = View.INVISIBLE
                                    cardView?.visibility = View.INVISIBLE
                                }

                                else {
                                    val error = task.exception?.message
                                    Toast.makeText(this@ItemToUploadActivity, "Error: " + error, Toast.LENGTH_LONG).show()
                                }
                            }

                        })

                    }

                    else {
                        val error = task.exception?.message
                        Toast.makeText(this@ItemToUploadActivity, "Error: " + error, Toast.LENGTH_LONG).show()
                    }
                }

            })
        }
    }

    private fun uploadVideoToFirebase() {
        if (uri != null) {

            progressBar?.visibility = View.VISIBLE
            cardView?.visibility = View.VISIBLE

            val reference = storageReference?.child(uri.lastPathSegment.toString())

            reference!!.putFile(uri).addOnCompleteListener(object : OnCompleteListener<UploadTask.TaskSnapshot> {
                override fun onComplete(task: Task<UploadTask.TaskSnapshot>) {
                    if (task.isSuccessful) {

                        val downloadVideoUrl = task.result
                        val videoUrl = downloadVideoUrl.toString()

                        val uploadVideo = HashMap<String, Any>()

                        uploadVideo.put("myDocument", videoUrl)

                        firebaseDatabase!!.updateChildren(uploadVideo).addOnCompleteListener(object : OnCompleteListener<Void> {
                            override fun onComplete(task: Task<Void>) {
                                if (task.isSuccessful) {
                                    Toast.makeText(this@ItemToUploadActivity, "Video uploaded successfully", Toast.LENGTH_LONG).show()

                                    progressBar?.visibility = View.INVISIBLE
                                    cardView?.visibility = View.INVISIBLE
                                }

                                else {
                                    val error = task.exception?.message
                                    Toast.makeText(this@ItemToUploadActivity, "Error: " + error, Toast.LENGTH_LONG).show()
                                }
                            }
                        })
                    }

                    else {
                        val error = task.exception?.message
                        Toast.makeText(this@ItemToUploadActivity, "Error: " + error, Toast.LENGTH_LONG).show()
                    }
                }

            })
        }
    }
}

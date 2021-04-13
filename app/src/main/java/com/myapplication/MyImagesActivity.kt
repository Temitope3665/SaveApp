package com.myapplication

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class MyImagesActivity : AppCompatActivity(), MyImageRecyclerAdapter.onItemClickListener{

    private var recyclerView: RecyclerView? = null

    lateinit var lists: ArrayList<MyImageCustomClass>

    private var firebaseAuth: FirebaseAuth? = null
    private var firebaseDatabase: DatabaseReference? = null

    private var storage: FirebaseStorage? = null
    private var ref: StorageReference? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_images)


        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance("https://save-93248-default-rtdb.firebaseio.com/").reference.child(
            "User"
        ).child(firebaseAuth!!.currentUser!!.uid)

        //firebaseDatabase = FirebaseDatabase.getInstance().getReference("User")
        storage = FirebaseStorage.getInstance()
        ref = storage?.reference?.child("Uploads")


        lists = ArrayList<MyImageCustomClass>()

        recyclerView = findViewById(R.id.recycler_view)

        showData()

        val adapter = MyImageRecyclerAdapter(lists, this)

        recyclerView?.layoutManager = GridLayoutManager(this@MyImagesActivity, 3)

        recyclerView?.adapter = adapter


        adapter.notifyDataSetChanged()







    }

    override fun onStart() {
        super.onStart()
        showData()
    }


    private fun showData() {

//        firebaseDatabase?.child("User")?.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    for (task in dataSnapshot.children) {
//                        val data: MyImageCustomClass? = task.getValue(MyImageCustomClass::class.java)
//                        lists.add(data!!)
//                    }
////                    adapter = MyAdapter(listData)
////                    rv.setAdapter(adapter)
//                }
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {}
//        })

        firebaseDatabase?.child("User")?.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(task: DataSnapshot) {
                if (task.exists()) {
                    val data: MyImageCustomClass? = task.getValue(MyImageCustomClass::class.java)
                    lists.add(data!!)
                    Toast.makeText(this@MyImagesActivity, "Display Image now", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(task: DatabaseError) {
                Toast.makeText(this@MyImagesActivity, "Cancelled", Toast.LENGTH_SHORT).show()
            }

        })

    }


    override fun itemClick(data: MyImageCustomClass, position: Int) {
        Toast.makeText(applicationContext, "Open Image", Toast.LENGTH_SHORT).show()
    }

    override fun deleteClick(data: MyImageCustomClass, position: Int) {
        Toast.makeText(applicationContext, "Delete Clicked", Toast.LENGTH_SHORT).show()
    }

    override fun downloadClick(data: MyImageCustomClass, position: Int) {
        Toast.makeText(applicationContext, "Download clicked", Toast.LENGTH_SHORT).show()
    }

}

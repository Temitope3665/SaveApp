package com.myapplication

import android.content.Context
import android.icu.number.NumberFormatter.with
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.my_images_custom_item.view.*

class MyImageRecyclerAdapter (data:ArrayList<MyImageCustomClass>, var clickListener: onItemClickListener) : RecyclerView.Adapter<MyImageRecyclerAdapter.ViewHolder>() {

    internal val data: List<MyImageCustomClass>
        init {
            this.data = data
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context).inflate(R.layout.my_images_custom_item, parent, false)
        return ViewHolder(layoutInflater)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.initialize(data.get(position), clickListener)

        //val imageItem = data[position]

        //holder.showImage.setImageResource(imageItem.displayImage)

        //Picasso.get().load(imageItem.displayImage).into(holder.showImage)

    }

    override fun getItemCount(): Int {
        return data.size
    }


    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private var firebaseAuth: FirebaseAuth? = null
        private var firebaseDatabase: DatabaseReference? = null

        val showImage: ImageView
        val deleteImage: ImageView
        val downloadImage: ImageView

        init {
            showImage = itemView.findViewById(R.id.show_image_one)
            deleteImage = itemView.findViewById(R.id.delete_option)
            downloadImage = itemView.findViewById(R.id.download_option)
        }

        fun initialize (item: MyImageCustomClass, action: onItemClickListener) {
            //showImage.setImageResource(item.displayImage)
//            deleteImage.setImageResource(item.deleteOption)
//            downloadImage.setImageResource(item.downloadOption)

            firebaseAuth = FirebaseAuth.getInstance()
            firebaseDatabase = FirebaseDatabase.getInstance("https://save-93248-default-rtdb.firebaseio.com/").reference.child("User").child(firebaseAuth!!.currentUser!!.uid)

            firebaseDatabase?.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(task: DataSnapshot) {
                    if(task.exists()) {

                        val imageShow = task.child("myImages").value as String

                        Picasso.get().load(imageShow).into(showImage)

                    }
                }

                override fun onCancelled(task: DatabaseError) {

                }

            })

            showImage.setImageResource(item.displayImage)

            showImage.setOnClickListener {
                action.itemClick(item, adapterPosition)
            }

            deleteImage.setOnClickListener {
                action.deleteClick(item, adapterPosition)
            }

            downloadImage.setOnClickListener {
                action.downloadClick(item, adapterPosition)
            }
        }


    }


    interface onItemClickListener {
        fun itemClick (data: MyImageCustomClass, position: Int)
        fun deleteClick (data: MyImageCustomClass, position: Int)
        fun downloadClick (data: MyImageCustomClass, position: Int)
    }

}
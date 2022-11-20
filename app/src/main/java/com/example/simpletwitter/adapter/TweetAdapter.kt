package com.example.simpletwitter.adapter

import android.content.Intent
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.recyclerview.widget.RecyclerView
import com.example.simpletwitter.adapter.viewHolder.addViewHolder
import com.example.simpletwitter.adapter.viewHolder.ticketViewHolder
import com.example.simpletwitter.databinding.AddTicketBinding
import com.example.simpletwitter.databinding.TweetTicketBinding
import com.example.simpletwitter.model.PostInfo
import com.example.simpletwitter.model.Ticket
import com.example.simpletwitter.model.getType
import com.example.simpletwitter.ui.MainActivity.Companion.downloadURL
import com.example.simpletwitter.ui.MainActivity.Companion.uid
import com.google.firebase.database.FirebaseDatabase

class TweetAdapter(val activity: AppCompatActivity, private val listTweets: List<Ticket>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ADD: Int = 1

    private val TICKET: Int = 2

    private var database = FirebaseDatabase.getInstance()

    private var myRef = database.reference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ADD -> addViewHolder(
                AddTicketBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            ).apply {
                galleryButton.setOnClickListener {
                    loadImage(activity)
                }
                postButton.setOnClickListener {
                    myRef.child("post").push().setValue(PostInfo(uid, etPost.text.toString(), downloadURL))
                    etPost.text.clear()
                }
            }
            else -> ticketViewHolder(
                TweetTicketBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    }

    override fun getItemViewType(position: Int): Int = when (listTweets.get(position).getType()) {
        "add" -> ADD
        else -> TICKET
    }

    override fun getItemCount() = listTweets.size

    private fun loadImage(activity: AppCompatActivity) {
        val PICK_IMAGES = 1
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        activity.startActivityForResult(intent, PICK_IMAGES)
    }
}

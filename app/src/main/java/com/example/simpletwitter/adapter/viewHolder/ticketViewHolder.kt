package com.example.simpletwitter.adapter.viewHolder

import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.simpletwitter.databinding.TweetTicketBinding

class ticketViewHolder(binding: TweetTicketBinding) : RecyclerView.ViewHolder(binding.root) {

    val txtTweet: TextView = binding.txtTweet
    val txtUserName: TextView = binding.txtUserName
    val tweetPic: ImageView = binding.tweetPicture
}

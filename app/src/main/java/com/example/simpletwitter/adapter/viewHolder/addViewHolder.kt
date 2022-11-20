package com.example.simpletwitter.adapter.viewHolder

import android.widget.EditText
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.example.simpletwitter.databinding.AddTicketBinding

class addViewHolder(binding: AddTicketBinding) : RecyclerView.ViewHolder(binding.root) {

    val postButton: ImageButton = binding.send
    val galleryButton: ImageButton = binding.gallery
    val etPost: EditText = binding.etPost
}

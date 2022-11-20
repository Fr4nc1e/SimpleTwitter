package com.example.simpletwitter.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.simpletwitter.databinding.AddTicketBinding
import com.example.simpletwitter.databinding.TweetTicketBinding
import com.example.simpletwitter.model.Ticket

class TweetAdapter(private val listTweets: List<Ticket>) : RecyclerView.Adapter<TweetAdapter.ViewHolder>() {

    private val ADD: Int = 1
    private val TICKET: Int = 2

    inner class ViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = when (viewType) {
            ADD -> AddTicketBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            else -> TweetTicketBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        }

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    }

    override fun getItemViewType(position: Int): Int = when (listTweets.get(position).tweetPersonUID) {
        "add" -> ADD
        else -> TICKET
    }

    override fun getItemCount() = listTweets.size
}

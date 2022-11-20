package com.example.simpletwitter.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simpletwitter.adapter.TweetAdapter
import com.example.simpletwitter.databinding.ActivityMainBinding
import com.example.simpletwitter.model.Ticket

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var listTweets: ArrayList<Ticket> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listTweets.add(Ticket("0", "him", "url", "add"))
        listTweets.add(Ticket("0", "him", "url", "ticket"))
        listTweets.add(Ticket("0", "him", "url", "ticket"))
        listTweets.add(Ticket("0", "him", "url", "ticket"))
        listTweets.add(Ticket("0", "him", "url", "ticket"))
        listTweets.add(Ticket("0", "him", "url", "ticket"))

        val layoutManager = LinearLayoutManager(this)
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = layoutManager
        val adapter = TweetAdapter(listTweets)
        recyclerView.adapter = adapter
    }
}

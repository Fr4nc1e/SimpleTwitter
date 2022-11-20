package com.example.simpletwitter.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simpletwitter.adapter.TweetAdapter
import com.example.simpletwitter.databinding.ActivityMainBinding
import com.example.simpletwitter.model.Ticket
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val listTweets: ArrayList<Ticket> = arrayListOf()

    private lateinit var email: String

    private var database = FirebaseDatabase.getInstance()

    private var myRef = database.reference

    private lateinit var adapter: TweetAdapter

    companion object {
        lateinit var uid: String
        lateinit var downloadURL: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        email = intent.getStringExtra("email").toString()
        uid = intent.getStringExtra("uid").toString()

        val layoutManager = LinearLayoutManager(this)
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = layoutManager
        adapter = TweetAdapter(this, listTweets)
        recyclerView.adapter = adapter
        loadPost(adapter)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val PICK_IMAGES = 1
        if (requestCode == PICK_IMAGES && data != null && resultCode == RESULT_OK) {
            val selectedImage = data.data
            val filePathColum = arrayOf(MediaStore.Images.Media.DATA)
            val cursor =
                selectedImage?.let {
                    contentResolver
                        .query(
                            it,
                            filePathColum,
                            null,
                            null,
                            null
                        )
                }
            cursor?.moveToFirst()
            val columIndex = cursor?.getColumnIndex(filePathColum[0])
            val picturePath = columIndex?.let { cursor.getString(it) }
            cursor?.close()
            uploadImage(BitmapFactory.decodeFile(picturePath))
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun uploadImage(bitmap: Bitmap) {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.getReferenceFromUrl("gs://simpletwitter-5cb1a.appspot.com")
        val df = SimpleDateFormat("ddMMyyHHmmss")
        val dataObj = Date()
        val imagePath = email.substring(1, 4) + "." + df.format(dataObj) + ".jpg"
        val imageRef = storageRef.child("imagePost/$imagePath")

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask = imageRef.putBytes(data)
        uploadTask.addOnFailureListener {
            Toast.makeText(this, "Fail to upload", Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener {
            downloadURL = it.storage.downloadUrl.toString()
        }
    }

    private fun loadPost(adapter: TweetAdapter) {
        myRef.child("post")
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        listTweets.clear()
                        listTweets.add(Ticket("0", "him", "url", "add"))
                        val td = snapshot.value as HashMap<String, Any>
                        for (key in td.keys) {
                            val post = td[key] as HashMap<String, Any>
                            listTweets.add(Ticket(key, post["text"] as String, post["postImage"] as String, post["userId"] as String))
                        }
                        adapter.notifyDataSetChanged()
                    } catch (e: java.lang.Exception) {
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }
}

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
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var listTweets: ArrayList<Ticket> = arrayListOf()

    private lateinit var email: String

    companion object {
        lateinit var uid: String
        lateinit var downloadURL: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // If anything wrong, test here!
        email = intent.getStringExtra("email").toString()
        uid = intent.getStringExtra("uid").toString()

        listTweets.add(Ticket("0", "him", "url", "add"))
        listTweets.add(Ticket("0", "him", "url", "ticket"))
        listTweets.add(Ticket("0", "him", "url", "ticket"))
        listTweets.add(Ticket("0", "him", "url", "ticket"))
        listTweets.add(Ticket("0", "him", "url", "ticket"))
        listTweets.add(Ticket("0", "him", "url", "ticket"))

        val layoutManager = LinearLayoutManager(this)
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = layoutManager
        val adapter = TweetAdapter(this, listTweets)
        recyclerView.adapter = adapter
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
            val picturePath = cursor!!.getString(columIndex!!)
            cursor.close()
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
}

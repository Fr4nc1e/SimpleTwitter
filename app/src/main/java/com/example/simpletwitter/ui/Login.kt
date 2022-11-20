package com.example.simpletwitter.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.simpletwitter.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val READIMAGE: Int by lazy {
        1
    }

    private val PICK_IMAGES: Int by lazy {
        1
    }

    private lateinit var mAuth: FirebaseAuth

    private var database = FirebaseDatabase.getInstance()

    private var myRef = database.reference

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

        binding.personImage.setOnClickListener {
            check()
        }

        binding.btLogin.setOnClickListener {
            loginToFirebase(binding.etEmail.text.toString(), binding.etPassWd.text.toString())
        }
    }

    private fun loginToFirebase(email: String, passWd: String) {
        mAuth.createUserWithEmailAndPassword(email, passWd).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                Toast.makeText(this, "Successful Login", Toast.LENGTH_SHORT).show()
                saveImageInFirebase()
            } else {
                Toast.makeText(this, "Fail Login", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun saveImageInFirebase() {
        val currentUser = mAuth.currentUser
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.getReferenceFromUrl("gs://simpletwitter-5cb1a.appspot.com")
        val df = SimpleDateFormat("ddMMyyHHmmss")
        val dataObj = Date()
        val imagePath = currentUser?.email.toString().substring(1, 4) + "." + df.format(dataObj) + ".jpg"
        val imageRef = storageRef.child("images/$imagePath")
        binding.personImage.apply {
            isDrawingCacheEnabled = true
            buildDrawingCache()
        }

        val bitmap = binding.personImage.drawable.toBitmap()
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask = imageRef.putBytes(data)
        uploadTask.addOnFailureListener {
            Toast.makeText(this, "Fail to upload", Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener {
            val downloadURL = it.storage.downloadUrl.toString()
            myRef.child("Users").child(currentUser!!.uid).child("email").setValue(currentUser.email)
            myRef.child("Users").child(currentUser.uid).child("ProfileImage").setValue(downloadURL)
            loadTweets()
        }
    }

    override fun onStart() {
        super.onStart()
        loadTweets()
    }

    private fun loadTweets() {
        val currentUser = mAuth.currentUser

        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("email", currentUser.email)
            intent.putExtra("uid", currentUser.uid)
            startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun check() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat
                .checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    READIMAGE
                )
                return
            }
        }

        loadImage()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            READIMAGE -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadImage()
            } else {
                Toast.makeText(
                    this,
                    "Can not access the images.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun loadImage() {
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(intent, PICK_IMAGES)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
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
            binding.personImage.setImageBitmap(BitmapFactory.decodeFile(picturePath))
            Glide
                .with(this)
                .load(picturePath)
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .into(binding.personImage)
        }
    }
}

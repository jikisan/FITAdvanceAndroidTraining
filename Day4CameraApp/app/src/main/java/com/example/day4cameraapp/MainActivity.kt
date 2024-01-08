package com.example.day4cameraapp

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.View.OnClickListener
import androidx.activity.result.contract.ActivityResultContracts
import com.example.day4cameraapp.databinding.ActivityMainBinding
import java.io.FileNotFoundException
import java.io.InputStream

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var camerUri: Uri
    var x = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraPermission()

        val camera = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->

//            //THUMBNAIL
//            val data = result.data
//            val image = data!!.extras?.get("data") as Bitmap

            val inputStream =
                try {applicationContext.contentResolver.openInputStream(camerUri)}
                catch ( e:FileNotFoundException) {}

            val actual_image = BitmapFactory.decodeStream(inputStream as InputStream)
            binding.imageView.setImageBitmap(actual_image)
        }

        binding.btnCamera.setOnClickListener {

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val cv = ContentValues()
            cv.put(MediaStore.Images.Media.DISPLAY_NAME, "Image$x")
            cv.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            cv.put(MediaStore.Images.Media.DESCRIPTION, "Image from Camera App")
            camerUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv)!!
            x++

            intent.putExtra(MediaStore.EXTRA_OUTPUT, camerUri)
            camera.launch(intent)
        }


        val gallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result ->

            if(result.resultCode == RESULT_OK) {
                val data = result.data
                val uri = data?.data

                binding.imageView.setImageURI(uri)
            }
        }

        binding.btnGallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setType("image/*")
            gallery.launch(intent)
        }

    }


    fun cameraPermission(){
        requestPermissions(arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
    }
}
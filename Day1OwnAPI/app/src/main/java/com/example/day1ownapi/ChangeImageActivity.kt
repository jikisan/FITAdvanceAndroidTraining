package com.example.day1ownapi

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toBitmap
import com.android.volley.toolbox.Volley
import com.example.day1ownapi.databinding.ActivityChangeImageBinding
import com.example.day1ownapi.databinding.ActivityCreateUserBinding
import com.example.day1ownapi.volley.AppController
import com.example.day1ownapi.volley.VolleyMultipartRequest
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class ChangeImageActivity : AppCompatActivity() {

    lateinit var binding: ActivityChangeImageBinding
    lateinit var helper: HelperClass

    var id:String? = null
    var username:String? = null
    var image:String? = null

    fun getIntentDetails(){
        id = intent.extras?.getLong("id", 0).toString()
        username = intent.extras?.getString("username", "")
        image = intent.extras?.getString("image", "")
    }

    fun displayImage(imageUrl: String){

        val networkImage = binding.imageView
//        networkImage.setDefaultImageResId(R.drawable.ic_launcher_foreground)
//        if(!image.isNullOrEmpty()) networkImage.setImageUrl(image, AppController.getInstance().imageLoader)

        Picasso
            .get()
            .load(image)
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(binding.imageView)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        helper = HelperClass(this)
        getIntentDetails()
        displayImage(image!!)

        val gallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->

            when(result.resultCode){
                RESULT_OK -> {
                    binding.btnUpload.isEnabled = true
                    val data = result.data
                    val uri = data?.data
                    Picasso
                        .get()
                        .load(uri)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .into(binding.imageView)



                }

            }
        }

        binding.btnSelect.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setType("image/*")
            gallery.launch(intent)
        }

        binding.btnUpload.setOnClickListener {
            val bitmap = binding.imageView.drawable.toBitmap()
            binding.progressBar.visibility = View.VISIBLE
            uploadImage(bitmap)
        }
    }

    fun getFileDataBytes(bitmap : Bitmap) : ByteArray {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
        return baos.toByteArray()
    }

    fun uploadImage(bitmap: Bitmap) {
        val api = "${getString(R.string.api)}/upload.php"

        val request = object : VolleyMultipartRequest(Method.POST, api,
            {response ->
                binding.progressBar.visibility = View.GONE
                val jsonObject = JSONObject(String(response.data))
                val uploadStatus = jsonObject.getString("result")
                val id = jsonObject.getString("id")
                val path = jsonObject.getString("imagePath")
                helper.displayAlert("$id - $path", uploadStatus)
                finish()
            },
            {error ->
                binding.progressBar.visibility = View.GONE

            })
            {
                override fun getParams(): MutableMap<String, String>? {

                    val params:MutableMap<String, String> = HashMap()
                    params.put("id", id!!)
                    params.put("username", username!!)
                    return params
                }

                override fun getByteData(): MutableMap<String, DataPart> {
                    val params:MutableMap<String, DataPart> = HashMap()
                    val dataPart = DataPart("$username$id.jpg", getFileDataBytes(bitmap))
                    params.put("image", dataPart)
                    return params
                }


            }
            Volley.newRequestQueue(this).add(request)


    }

}



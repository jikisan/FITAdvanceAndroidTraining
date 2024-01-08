package com.example.day1stackoverflowapi

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.example.day1stackoverflowapi.databinding.ActivityMainBinding
import com.example.day1stackoverflowapi.volley.AppController
import com.google.gson.Gson


data class Owner(
    val display_name :String,
    val profile_image :String
)

data class Questions(
    val title :String,
    val link : String,
    val owner :Owner
)

data class AllQuestions (
    val items :ArrayList<Questions>
)


class MainActivity : AppCompatActivity() {

    val api = "https://api.stackexchange.com/2.2/questions?order=desc&sort=creation&site=stackoverflow"
    lateinit var binding: ActivityMainBinding
    lateinit var adapter: CustomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getQuestions()

        binding.listView.setOnItemClickListener { parent, view, position, id ->

            val link = parent.getItemAtPosition(position).toString()
            val uri = Uri.parse(link)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
    }


    fun getQuestions(){

        val jsonRequest = JsonObjectRequest(Request.Method.GET, api, null, { json->

            val gson = Gson()
            val allQuestions = gson.fromJson(json.toString(), AllQuestions::class.java)
            adapter = CustomAdapter(allQuestions, this)
            binding.listView.adapter = adapter

        }, {error->
            Log.d("Volley Error", error.localizedMessage)
        })

        AppController
            .getInstance()
            .addToRequestQueue(jsonRequest, "json_obj_req")

    }
}
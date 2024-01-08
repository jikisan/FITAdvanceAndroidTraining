package com.example.day1ownapi

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.SearchView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.day1ownapi.databinding.ActivityDashboardBinding
import com.example.day1ownapi.databinding.ActivityMainBinding
import com.example.day1ownapi.volley.AppController
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject

data class User(
    val id :String,
    val user :String,
    val pw :String,
    val fname :String,
    val image :String
)
data class AllUsers(
    val users: ArrayList<User>
)

class DashboardActivity : AppCompatActivity() {

    lateinit var binding: ActivityDashboardBinding
    lateinit var preference: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preference = getSharedPreferences("login", Context.MODE_PRIVATE)

        binding.btnLogout.setOnClickListener {
            val edit = preference.edit()
            edit.remove("isLogged")
            edit.clear()
            edit.commit()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val emptyJson = JSONObject()
        emptyJson.put( "keyword", "")
        viewAllUsers(emptyJson)

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
               return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                val keyword = JSONObject()
                keyword.put( "keyword", newText)
                viewAllUsers(keyword)
                return false
            }

        })

        binding.listView.setOnItemLongClickListener { parent, view, position, id ->

            val userId = parent.getItemIdAtPosition(position)
            val userName = parent.getItemAtPosition(position).toString()
            val fullname = adapter.getFullName(position)
            val password = adapter.getPassword(position)
            val profileImage = adapter.getProfileImage(position)

            val alert = AlertDialog.Builder(this)

            alert.setTitle("Action")
            alert.setItems(arrayOf("Edit", "Change Image", "Delete")){
                    dialog, index ->

                when(index){
                    0 -> {
                        //Edit
                        val intent = Intent(this, CreateUserActivity::class.java,)
                        intent.putExtra("id", userId)
                        intent.putExtra("username", userName)
                        intent.putExtra("fullname", fullname)
                        intent.putExtra("password", password)
                        startActivity(intent)
                    }
                    1 -> {
                        //Change Image
                        val intent = Intent(this, ChangeImageActivity::class.java)
                        intent.putExtra("id", userId)
                        intent.putExtra("username", userName)
                        intent.putExtra("image", profileImage)
                        startActivity(intent)
                    }
                    else -> {
                        //Delete
                        val confirm = AlertDialog.Builder(this)
                        confirm.setTitle("Confirmation")
                        confirm.setMessage("Proceed to delete user $userId?")
                        confirm.setPositiveButton("Okay"){
                            dialog, index ->
                            deleteUser(userId, position)
                        }
                        confirm.setNegativeButton("Cancel", null)
                        confirm.create().show()

                    }
                }
            }
            alert.create().show()

            false
        }
    }

    lateinit var allUsers :AllUsers
    lateinit var adapter :CustomAdapter

    fun viewAllUsers(jsonObj : JSONObject?){
        val api = "${getString(R.string.api)}/view_users.php"
        val jsonRequest = JsonObjectRequest(Request.Method.POST, api, jsonObj,
            { json ->
                val gson = Gson()
                allUsers = gson.fromJson(json.toString(), AllUsers::class.java)
                adapter = CustomAdapter(allUsers, this)
                binding.listView.adapter = adapter
            },
            {error ->

            })

        AppController.getInstance().addToRequestQueue(jsonRequest, "json_obj_req")
    }

    @SuppressLint("SuspiciousIndentation")
    fun deleteUser(userId :Long, position :Int) {
        val api = "${getString(R.string.api)}/delete.php?id=$userId"
        val stringRequest = StringRequest(Request.Method.GET, api,
            { response ->
                when(response){
                    "deleted" -> {
                        allUsers.users.removeAt(position)
                        adapter.notifyDataSetChanged()
                    }
                }
            },
            { error ->

            })
            Volley.newRequestQueue(this).add(stringRequest)
    }

    override fun onResume() {
        super.onResume()

        viewAllUsers(JSONObject().put("keyword", ""))
    }
}
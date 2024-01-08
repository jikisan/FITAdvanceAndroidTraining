package com.example.day2sqliteactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView.OnQueryTextListener
import com.example.day2sqliteactivity.databinding.ActivityDashboardBinding
import com.example.day2sqliteactivity.databinding.ActivityMainBinding
import com.example.day2sqliteactivity.databinding.ActivitySignUpBinding

data class User(
    val username: String,
    val password: String,
    val fullname: String,
    val id: Int
)

class DashboardActivity : AppCompatActivity() {

    lateinit var binding : ActivityDashboardBinding
    lateinit var customAdapter: CustomAdapter
    lateinit var dbManager: DBManager
    lateinit var helper: Helper

    var allUsers: ArrayList<User> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        helper = Helper(this)
        dbManager = DBManager(this)

        viewAllUsers(null)

        binding.searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewAllUsers(newText)
                return false
            }


        })
    }

    override fun onResume() {
        super.onResume()
        viewAllUsers(null)
    }

    fun viewAllUsers(keyword: String?){

        if(keyword.isNullOrEmpty()) { allUsers = dbManager.viewAllUsers()}
        else { allUsers = dbManager.searchUsers(keyword)}

        customAdapter = CustomAdapter(allUsers, this)
        binding.listView.adapter = customAdapter
    }


}
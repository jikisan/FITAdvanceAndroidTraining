package com.example.day2sqliteactivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.day2sqliteactivity.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding
    lateinit var dbManager: DBManager
    lateinit var helper: Helper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        helper = Helper(this)
        dbManager = DBManager(this)


        binding.btnLogin.setOnClickListener {
            val username = binding.txtUsername.text.toString()
            val password = binding.txtPassword.text.toString()
            val isLogged = dbManager.login(username, password)

            if(isLogged) {
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
            }
            else {
                helper.alert("Error", "Credentials are incorrect")
            }
        }

        binding.btnSignup.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

//
//        val username = "kyle"
//
//        if(dbManager.checkUserIfExists(username)){
//            Log.d("SQL INSERT: ", "USER ALREADY EXIST")
//        }
//        else {
//            if(dbManager.insertUser(username, "1234", "Kyle Santerna")) Log.d("SQL INSERT: ", "SUCCESS")
//            else Log.d("SQL INSERT", "FAILED")
//        }


    }
}
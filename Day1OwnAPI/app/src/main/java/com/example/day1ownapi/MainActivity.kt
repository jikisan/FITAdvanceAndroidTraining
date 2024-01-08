package com.example.day1ownapi

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.day1ownapi.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var helper: HelperClass
    lateinit var preference: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        helper = HelperClass(this)
        preference = getSharedPreferences("login", Context.MODE_PRIVATE)

        binding.btnLogin.setOnClickListener{
            checkCredentials(
                binding.txtUsername.text.toString(),
                binding.txtPassword.text.toString()
            )
        }

        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, CreateUserActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        val isLogged = preference.getBoolean("isLogged", false)

        if(isLogged){
            intentToLogin()
            finish()
        }
    }

    fun intentToLogin(){
        val editor = preference.edit()
        editor.putBoolean("isLogged", true)
        editor.putString("username", binding.txtUsername.text.toString())
        editor.commit()

        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)

    }

    fun checkCredentials(username :String, password :String){

        val api = "${getString(R.string.api)}/login.php"

        val request = object : StringRequest(
                Method.POST,
                api,
                {response ->
                    when(response){
                        "success" -> {intentToLogin()}
                        "error" -> { helper.displayAlert("Error", "Invalid Username/Password")}
                        else -> {helper.displayAlert("Error", "Username/Password is empty")}
                    }},
                 {error->
                    Log.d("Volley Error:", error.localizedMessage)})
                {
                    override fun getParams(): MutableMap<String, String>? {

                    val params: MutableMap<String, String> = HashMap()
                    params.put("username", username)
                    params.put("password", password)

                    return params
                    }
                }
        Volley.newRequestQueue(this).add(request)

    }

}
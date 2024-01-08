package com.example.day1ownapi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.day1ownapi.databinding.ActivityCreateUserBinding
import com.example.day1ownapi.databinding.ActivityMainBinding

class CreateUserActivity : AppCompatActivity() {

    lateinit var binding: ActivityCreateUserBinding
    lateinit var helper: HelperClass

    var id:String? = null
    var username:String? = null
    var password:String? = null
    var fullname:String? = null
    var checkbox:Int = 0

    fun getUserDetails(){
        id = intent.extras?.getLong("id", 0).toString()
        username = intent.extras?.getString("username", "")
        password = intent.extras?.getString("password", "")
        fullname = intent.extras?.getString("fullname", "")
    }

    fun isUserUpdate():Boolean {
        if(!username.isNullOrEmpty()) { return true }

        return false
    }

    fun setFields() {
        if(isUserUpdate()) {
            binding.cbChangePW.visibility = View.VISIBLE
//            binding.lblOldPw.visibility = View.VISIBLE
//            binding.txtOldPassword.visibility = View.VISIBLE

            binding.lblTitle.text = "Update User $fullname?"
            binding.btnRSignup.text = "Update User $id"
            binding.lblPW.text = "New Password"

            binding.textRUsername.setText(username)
            binding.txtRFullname.setText(fullname)

            binding.txtRPassword.visibility = View.GONE
            binding.lblPW.visibility = View.GONE
        }
        else {}
    }

    fun enablePassword(isEnabled: Boolean) {

        var visibility = 0

        if(isEnabled) visibility = View.VISIBLE
        else visibility = View.GONE

        binding.lblOldPw.visibility = visibility
        binding.lblOldPw.visibility = visibility
        binding.txtOldPassword.visibility = visibility
        binding.lblPW.visibility = visibility
        binding.txtRPassword.visibility = visibility


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        helper = HelperClass(this)
        getUserDetails()
        setFields()

        binding.cbChangePW.setOnCheckedChangeListener { buttonView, isChecked ->
            enablePassword(isChecked)

            if(isChecked) { checkbox = 1 }
            else { checkbox = 0 }
        }

        binding.btnRSignup.setOnClickListener {



            if(isUserUpdate()) {
                updateUser(id!!, password!!)

            }
            else {

                val user = binding.textRUsername.text.toString()
                val fn = binding.txtRFullname.text.toString()
                var pw = binding.txtRPassword.text.toString()

                createUser(user, pw, fn)
            }

        }
    }

    fun createUser(username :String, password :String, fullname :String){

        val api = "${getString(R.string.api)}/insert.php"
        val request = object : StringRequest(Method.POST, api,
            {response ->

                when(response){
                    "saved" -> {finish()}
                    else -> {helper.displayAlert("Error", response)}
                }},

            {error ->

            }){

            override fun getParams(): MutableMap<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                params.put("username", username)
                params.put("password", password)
                params.put("fullname", fullname)
                return params

            }
        }
        Volley.newRequestQueue(this).add(request)
    }

    fun updateUser(id :String, oldpwmd5 :String){
        val api = "${getString(R.string.api)}/updateuser.php"
        val request = object : StringRequest(Method.POST, api,
            {response ->

                when(response){
                    "updated" -> { finish() }
                    else -> {helper.displayAlert("Error", response)}
                }},

            {error ->

            }){

            override fun getParams(): MutableMap<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                params.put("id", id)
                params.put("fullname", binding.txtRFullname.text.toString())
                params.put("username", binding.textRUsername.text.toString())
                params.put("oldpw", binding.txtOldPassword.text.toString())
                params.put("oldpwmd5", oldpwmd5)
                params.put("checkbox", checkbox.toString())
                params.put("password", binding.txtRPassword.text.toString())

               Log.d("PARAMS", params.toString())

                return params

            }
        }
        Volley.newRequestQueue(this).add(request)
    }

}
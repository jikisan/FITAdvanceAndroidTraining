package com.example.day2sqliteactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.day2sqliteactivity.databinding.ActivityMainBinding
import com.example.day2sqliteactivity.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    lateinit var binding : ActivitySignUpBinding
    lateinit var dbManager: DBManager
    lateinit var helper: Helper

    var id : String? = null
    var username : String? = null
    var fullname : String? = null
    var password : String? = null

    fun getIntentDetails(){
        id = intent.extras?.getString("id", "")
        username = intent.extras?.getString("username", "")
        fullname = intent.extras?.getString("fullname", "")
        password = intent.extras?.getString("password", "")
    }

    fun isUserUpdate():Boolean {
        if(!username.isNullOrEmpty()) { return true }

        return false
    }

    fun setFields() {
        if(isUserUpdate()) {
            binding.lblTitle.text = "Update User $fullname?"
            binding.btnCreate.text = "Update User $id"
            binding.lblPW.text = "New Password"

            binding.txtRUsername.setText(username)
            binding.txtRFullname.setText(fullname)
            binding.txtRpassword.setText(password)

            binding.lblPW.visibility = View.GONE
        }
    }

    fun enablePassword(isEnabled: Boolean) {

        var visibility = 0

        if(isEnabled) visibility = View.VISIBLE
        else visibility = View.GONE

        binding.lblOldPw.visibility = visibility
        binding.txtOldPassword.visibility = visibility
        binding.lblPW.visibility = visibility
        binding.txtRpassword.visibility = visibility


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        helper = Helper(this)
        dbManager = DBManager(this)

        getIntentDetails()
        setFields()

        binding.cbChangePW.setOnCheckedChangeListener { buttonView, isChecked ->
            enablePassword(isChecked)
        }

        binding.btnCreate.setOnClickListener {

            val username = binding.txtRUsername.text.toString()
            val password = binding.txtRpassword.text.toString()
            val fullname = binding.txtRFullname.text.toString()

            if(isUserUpdate()) {
                updateUser(username, password, fullname, id!!)
            }
            else  {
                createUser(username, password, fullname)
            }

        }
    }

    fun createUser(username: String, password: String, fullname: String){

        if(!username.isEmpty() && !password.isEmpty() && !fullname.isEmpty()) {

            val isExist = dbManager.checkUserIfExists(username)

            if(isExist) {
                helper.alert("Error", "$username is already in use.")
            }
            else {
                val isSuccess = dbManager.insertUser(username, password, fullname)

                if(isSuccess) {finish()}
                else {helper.alert("Error", "Error in saving user $username")}
            }
        }
        else {
            helper.alert("Error", "All fields are required." )
        }

    }

    fun updateUser(username: String, password: String, fullname: String, id: String){

        if(!username.isEmpty() && !password.isEmpty() && !fullname.isEmpty()) {

            val isSuccess = dbManager.updateUser(username, password, fullname, id)

            if(isSuccess) {finish()}
            else {helper.alert("Error", "Error in updating user $username")}
        }
        else {
            helper.alert("Error", "All fields are required." )
        }

    }
}
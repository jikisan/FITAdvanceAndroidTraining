package com.example.day1ownapi

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.day1ownapi.databinding.RowBinding
import com.example.day1ownapi.volley.AppController

class CustomAdapter(val allUsers: AllUsers, val context: Context) : BaseAdapter() {

    override fun getCount(): Int {
        return allUsers.users.size
    }

    override fun getItem(position: Int): Any {
        return allUsers.users[position].user
    }

    override fun getItemId(position: Int): Long {
        return allUsers.users[position].id.toLong()
    }

    fun getFullName(position: Int) : String {
        return allUsers.users[position].fname
    }

    fun getPassword(position: Int) : String {
        return allUsers.users[position].pw
    }

    fun getProfileImage(position: Int) : String {
        return allUsers.users[position].image
    }

    override fun getView(position : Int, convertView: View?, parent: ViewGroup?): View {

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = RowBinding.inflate(inflater, parent, false);
        val user = allUsers.users[position]
        val networkImage = binding.imageView

        networkImage.setDefaultImageResId(R.drawable.ic_launcher_foreground)
        networkImage.setImageUrl(user.image, AppController.getInstance().imageLoader)

        binding.lblId.text = "USER ID: ${user.id}"
        binding.lblUsername.text = "USER NAME: ${user.user}"
        binding.lblPassword.text = "FULL NAME: ${user.fname}"

        return binding.root
    }
}
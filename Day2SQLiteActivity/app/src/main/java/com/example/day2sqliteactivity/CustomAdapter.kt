package com.example.day2sqliteactivity

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.day2sqliteactivity.databinding.RowBinding
import com.google.android.material.snackbar.Snackbar

class CustomAdapter(val users: ArrayList<User>, val context: Context) : BaseAdapter() {

    override fun getCount(): Int {
        return users.size
    }

    override fun getItem(position: Int): Any {
        return users[position].username
    }

    override fun getItemId(position: Int): Long {
        return users[position].id.toLong()
    }

    override fun getView(position: Int, convertVIew: View?, parent: ViewGroup?): View {

        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = RowBinding.inflate(layoutInflater)
        val user = users[position]

        val dbManager = DBManager(context)

        binding.lblId.text = "${user.id}"
        binding.lblUsername.text = user.username
        binding.lblFullname.text = user.fullname


        binding.btnEdit.setOnClickListener {
            val intent = Intent(context, SignUpActivity::class.java)
            intent.putExtra("id", binding.lblId.text.toString())
            intent.putExtra("username", binding.lblUsername.text.toString())
            intent.putExtra("fullname", binding.lblFullname.text.toString())
            intent.putExtra("password", user.password.toString())
            context.applicationContext.startActivity(intent)

        }

        binding.btnDelete.setOnClickListener {
            if(dbManager.deleteUser("${user.id }")) {
                users.removeAt(position)
                notifyDataSetChanged()
                Snackbar.make(binding.root, "${user.username} has been deleted.", Snackbar.LENGTH_SHORT).show()
            }
            else {
                Snackbar.make(binding.root, "Unable to delete ${user.username}", Snackbar.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

}
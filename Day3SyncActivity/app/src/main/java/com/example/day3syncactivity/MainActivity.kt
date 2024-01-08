package com.example.day3syncactivity

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.day3syncactivity.databinding.ActivityMainBinding

data class Item(
    val sync: Int,
    val item_name: String,
    val id: Int)

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var helper: HelperClass
    lateinit var dbManager: DBManager
    lateinit var adapter: CustomAdapter

    fun viewAllItems(){
        val allItems = dbManager.viewAllItems()
        adapter = CustomAdapter(this, allItems)
        binding.listView.adapter = adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        helper = HelperClass(this)
        dbManager = DBManager(this)
        setContentView(binding.root)

        viewAllItems()
//        Log.d("Network State", "${helper.isNetworkAvailable()} ")

        binding.btnAddItem.setOnClickListener {
            val item_name = binding.txtItem.text.toString()

            if(item_name.isEmpty()) {
                helper.displayAlert("Error","Item name is required")
            }
            else {
                val insertMessage = dbManager.inserItem(item_name)
                helper.displayAlert("Insert Status", insertMessage)
                viewAllItems()
            }
        }


        binding.btnSync.setOnClickListener {
            val sync = dbManager.syncToAPI()

            if(sync) {
                helper.displayAlert("Success", "Database has been Synced")
                viewAllItems()
            }
            else{
                helper.displayAlert("Information", "Database is already updated")
            }
        }


        binding.swipeRefresh.setOnRefreshListener {

            val sync = dbManager.syncToAPI()
            binding.swipeRefresh.isRefreshing = false

            if(sync) {
                helper.displayAlert("Success", "Database has been Synced")
                viewAllItems()
            }
            else{
                helper.displayAlert("Information", "Database is already updated")
            }

        }
    }

}
package com.example.day3syncactivity

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build

class HelperClass(val context: Context) {

    var api = "http://10.25.216.36/android_sync"

    fun isNetworkAvailable() : Boolean {

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities = connectivityManager.getNetworkCapabilities((connectivityManager.activeNetwork))
            if(capabilities != null) { return true }
        }
        else {
            val activeNetwork = connectivityManager.activeNetworkInfo
            if(activeNetwork != null && activeNetwork.isConnected) { return true}
        }

        return false
    }

    fun displayAlert(title :String, message :String){
        val alert = AlertDialog.Builder(context)
        alert.setTitle(title)
        alert.setMessage(message)
        alert.setNeutralButton("OK", null)
        alert.create().show()
    }

}
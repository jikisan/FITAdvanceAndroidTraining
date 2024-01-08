package com.example.day1ownapi

import android.app.AlertDialog
import android.content.Context

class HelperClass(val context: Context) {

    fun displayAlert(title :String, message :String){
        val alert = AlertDialog.Builder(context)
        alert.setTitle(title)
        alert.setMessage(message)
        alert.setNeutralButton("OK", null)
        alert.create().show()
    }


}
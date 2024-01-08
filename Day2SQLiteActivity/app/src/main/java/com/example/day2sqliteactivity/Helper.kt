package com.example.day2sqliteactivity

import android.app.AlertDialog
import android.content.Context

class Helper(val context: Context) {

    fun alert(title: String, message: String) {

        val alert = AlertDialog.Builder(context)
        alert.setTitle(title)
        alert.setMessage(message)
        alert.setNeutralButton("OK", null)
        alert.create().show()
    }
}
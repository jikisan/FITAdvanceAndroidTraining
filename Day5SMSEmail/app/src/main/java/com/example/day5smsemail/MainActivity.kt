package com.example.day5smsemail

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.view.View
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.day5smsemail.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var receiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        receiver = registerReceiver(object : BroadcastReceiver() {
//            override fun onReceive(p0: Context?, p1: Intent?) {
//                TODO("Not yet implemented")
//            }
//
//        } )
//


        binding.btnSms1.setOnClickListener(this)
        binding.btnSms2.setOnClickListener(this)
        binding.btnEmail1.setOnClickListener(this)
        binding.btnEmail2.setOnClickListener(this)

        requestPermissions(arrayOf(android.Manifest.permission.SEND_SMS), 0)

        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                when(resultCode) {
                    RESULT_OK -> {showSnackBar("Message sent!.")}
                    else -> {showSnackBar("Message not sent!.")}
                }
            }

        } , IntentFilter("SMS_SENT"))
    }

    override fun onClick(p0: View?) {

        val to = binding.txtTo.text.toString()
        val cc = binding.txtCC.text.toString()
        val bcc = binding.txtBCC.text.toString()
        val subject = binding.txtSubject.text.toString()
        val message = binding.txtMessage.text.toString()

        when(p0?.id){

            R.id.btnSms1 -> {
                val sms = SmsManager.getDefault()
                val sentIntent = PendingIntent.getBroadcast(this, 101, Intent("SMS_SENT"), FLAG_MUTABLE)
                sms.sendTextMessage(to, null, message, sentIntent, null)
            }
            R.id.btnSms2 -> {
                val uri = Uri.parse("smsto: $to")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.putExtra("sms_body", message)
                startActivity(intent)
            }
            R.id.btnEmail1 -> {
                val api = "https://portal.fitacademy.ph/android_training_mail.php"
                val stringRequest = object : StringRequest(Request.Method.POST, api,
                    {response ->
                        showSnackBar(response)
                    },
                    {error ->

                    })
                    {
                        override fun getParams(): MutableMap<String, String>? {
                            val params : MutableMap<String, String> = HashMap()
                            params.put("email", to)
                            params.put("cc", cc)
    //                        params.put("bcc", bcc)
                            params.put("subject", subject)
                            params.put("body", message)
                            params.put("fullname", "Kyle Santerna")

                            return params
                        }
                    }
                Volley.newRequestQueue(this).add(stringRequest)
            }
            else -> {
                val uri = Uri.parse("mailto: ?cc=$cc")
                val intent = Intent(Intent.ACTION_SENDTO, uri)
                intent.putExtra(Intent.EXTRA_EMAIL, to.split(";").toTypedArray())
//                intent.putExtra(Intent.EXTRA_CC, arrayOf(cc))
                intent.putExtra(Intent.EXTRA_BCC, bcc.split(";").toTypedArray())
                intent.putExtra(Intent.EXTRA_SUBJECT, subject)
                intent.putExtra(Intent.EXTRA_TEXT, message)
                intent.setPackage("com.google.android.gm")
                startActivity(intent)
            }
        }
    }

    fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

}
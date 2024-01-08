package com.example.day4qrcodes

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import com.example.day4qrcodes.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var binding: ActivityMainBinding
    lateinit var scannerLauncher: ActivityResultLauncher<ScanOptions>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestPermission()

        binding.btnZxing.setOnClickListener(this)
        binding.btnJourney.setOnClickListener(this)
        binding.btnScan.setOnClickListener(this)
        binding.btnSave.setOnClickListener(this)
        binding.btnClear.setOnClickListener(this)

        scannerLauncher = registerForActivityResult(ScanContract()) {
            result ->
            Snackbar.make(binding.root, "${result.contents}", Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onClick(v: View?) {

        when(v?.id) {
            R.id.btnZxing -> {
                val qrCode = createQR(binding.txtToQr.text.toString())
                binding.imageView.setImageBitmap(qrCode)
                saveImage(qrCode,  binding.txtToQr.text.toString())
            }
            R.id.btnScan -> {
               val options = ScanOptions()
                scannerLauncher.launch(options)
            }
            R.id.btnSave -> {

                saveImage(binding.signaturePad.signatureBitmap,  "my_signature")
            }
            R.id.btnClear -> {
                binding.signaturePad.clear()
            }
            else -> {
                //JourneyApps
                val encoder = BarcodeEncoder()
                val bitmap = encoder.encodeBitmap(
                    binding.txtToQr.text.toString(), BarcodeFormat.QR_CODE, 500, 500)
                binding.imageView.setImageBitmap(bitmap)
                saveImage(bitmap,  binding.txtToQr.text.toString())
            }
        }
    }

    fun createQR(stringToImage: String) : Bitmap {
        var bitmap : Bitmap
        val map = HashMap<EncodeHintType, Any>()
        map[EncodeHintType.CHARACTER_SET] = "utf-8"
        map[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.M
        map[EncodeHintType.QR_VERSION] = 10 // (1-40)
        map[EncodeHintType.MARGIN] = 2 // pixels

        val matrix = MultiFormatWriter().encode(stringToImage, BarcodeFormat.QR_CODE, 500, 500, map)
        val matrixHeight = matrix.height
        val matrixWidth = matrix.width

        val pixels = IntArray(matrixWidth * matrixHeight)

        for(y in 0 until matrixHeight) {
            val offset = y * matrixWidth

            for (x in 0 until matrixWidth) {
                pixels[offset + x ] =
                    if (matrix.get(x,y)) { Color.BLACK}
                    else {Color.WHITE}
            }
        }

        bitmap = Bitmap.createBitmap(matrixWidth, matrixHeight, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, matrixWidth, 0, 0, matrixWidth, matrixHeight)

        return bitmap
    }

    fun saveImage(bitmap : Bitmap, fileName : String) {
        val path =
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
            }
            else {
                Environment.getExternalStorageDirectory().absolutePath
            }

        val directory = "$path/QRCodeNov"
        val folder = File(directory)

        if(!folder.exists()) { folder.mkdirs() }
        val imageFile = File("$directory", "$fileName.jpg")
        val outputStream = FileOutputStream(imageFile)
        val status = bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
        val message = if(status) {"SAVED"} else "NOT SAVED"

        val alert = AlertDialog.Builder(this)
        alert.setTitle("QR Code")
        alert.setMessage(message)
        alert.setNeutralButton("OK", null)
        alert.create().show()

    }

    fun requestPermission(){
        requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
    }
}
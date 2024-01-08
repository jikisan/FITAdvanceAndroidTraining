package com.example.day5fingerprint

import android.os.Bundle
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.day5fingerprint.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import java.util.concurrent.Executor

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo : PromptInfo
    private lateinit var biometricManager: BiometricManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        executor = ContextCompat.getMainExecutor(this)
        biometricManager = BiometricManager.from(this)
        biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                showSnackbar("User has been authenticated!")
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                showSnackbar("$errString $errorCode")
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                showSnackbar("Authentication failed!")
            }
        })

        promptInfo = PromptInfo.Builder()
            .setTitle("Scan Fingerprint")
            .setNegativeButtonText("Cancel")
            .build()

        binding.btnFingerprint.setOnClickListener {
            when(biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
                BiometricManager.BIOMETRIC_SUCCESS -> {
                    biometricPrompt.authenticate(promptInfo)
                }
                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                    showSnackbar("Hardware unavailable")
                }
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                    showSnackbar("No fingerprint enrolled")
                }
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                    showSnackbar("Device has no fingerprint scanner")
                }
                else -> {showSnackbar("Error")}

            }
        }
    }

    fun showSnackbar(message: String){
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }



}
package com.example.day5speech

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.day5speech.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var textToSpeech: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSpeak.setOnClickListener {
            textToSpeech.speak(binding.txtToSpeech.text.toString(), TextToSpeech.QUEUE_FLUSH, null, null)
        }

        val result = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
            val data = result.data
            val resultIntent = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val text = resultIntent?.get(0)
            binding.lblResult.text = text

            if(text!!.contains("Hello jarvis", true)) {
                textToSpeech.speak("Hello Kyle", TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
        binding.btnMic.setOnClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak")
            result.launch(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        textToSpeech = TextToSpeech(this) {
            status ->
            if(status != TextToSpeech.ERROR) {
                textToSpeech.language = Locale.ENGLISH
            }
            else {
                Toast.makeText(this, "Not Available", Toast.LENGTH_LONG).show()
            }
        }
    }
}
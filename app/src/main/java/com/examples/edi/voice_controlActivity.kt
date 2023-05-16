package com.examples.edi

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.RecognizerIntent
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import java.io.IOException
import java.util.*

class voice_controlActivity : AppCompatActivity() {

    var counter = 0
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice_control)
        var voice = findViewById<Button>(R.id.voice_btn)
        voice.setOnClickListener { askSpeechInput() }
    }
    private fun askSpeechInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Say Something")
        startActivityForResult(intent,101)
}
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
        val voiceCommand = findViewById<TextView>(R.id.textView8)
        voiceCommand.text = result?.get(0).toString()
        Log.d("commands", "$result")
//        sendCommand("S")
        val text = voiceCommand.text
        Log.d("text", "$text")

        if ("$text" == "forward") {
            sendCommand("F")
        } else if ("$text" == "backward") {
            sendCommand("B")
        } else if ("$text" == "left") {
            sendCommand("L")
        } else if ("$text" == "right") {
            sendCommand("R")
        } else if ("$text" == "stop") {
            sendCommand("S")
        } else {
            sendCommand("S")

        }

        startTimeCounter()
    }

    private fun startTimeCounter() {
        val voiceCommand = findViewById<TextView>(R.id.textView8)
        object : CountDownTimer(2000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                voiceCommand.text = counter.toString()
                counter++
            }

            override fun onFinish() {
                voiceCommand.text = "S"
                sendCommand("S")
            }
        }.start()    }

    private fun sendCommand(input: String) {
        if (MainActivity.m_bluetoothSocket != null) {
            try {
                MainActivity.m_bluetoothSocket!!.outputStream.write(input.toByteArray())
                Log.d("data", "send")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
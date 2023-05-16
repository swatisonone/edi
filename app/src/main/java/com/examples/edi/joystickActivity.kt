package com.examples.edi

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.vitpunerobortics.joysticklibary.JoystickView
import java.io.IOException

class joystickActivity : AppCompatActivity() {

    private var joystick: JoystickView? = null
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_joystick)

        var anti = findViewById<Button>(R.id.aclk)
        var clk = findViewById<Button>(R.id.clk)
        var pre_command = ""
        val voiceCommand = findViewById<TextView>(R.id.voiceCommand)
        joystick = findViewById<View>(R.id.joystickView) as JoystickView

        anti?.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                Log.d("button", "Enter")
//                if (p1?.action ==MotionEvent.ACTION_BUTTON_PRESS){
//                    Log.d("button", "pressed")
//                }
//                else if(p1?.getAction()==MotionEvent.ACTION_BUTTON_RELEASE){
//                    Log.d("button", "off")
//                }
                when (p1?.action) {
                    MotionEvent.ACTION_UP -> {
                        Log.d("button", "pressed")
                        anti.setTextColor(Color.parseColor("#FF000000"))
                        voiceCommand.setText("S")
                        sendCommand("S")
                    }
                    MotionEvent.ACTION_DOWN -> {
                        Log.d("button", "released")
                        anti.setTextColor(Color.parseColor("#8E8C8C"))
                        voiceCommand.setText("A")
                        sendCommand("A")
                    }
                }
                return true
            }
        }
        )
//
//
        clk?.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                when (p1?.action) {
                    MotionEvent.ACTION_UP -> {
                        Log.d("button", "pressed")
                        clk.setTextColor(Color.parseColor("#FF000000"))
                        voiceCommand.setText("S")
                        sendCommand("S")
                    }
                    MotionEvent.ACTION_DOWN -> {
                        Log.d("button", "released")
                        clk.setTextColor(Color.parseColor("#8E8C8C"))
                        voiceCommand.setText("C")
                        sendCommand("C")
                    }
                }
                return true
            }
        }
        )

        joystick!!.setOnMoveListener(object : JoystickView.OnMoveListener {
            var command = "";

            override fun onMove(angle: Int, strength: Int) {
//                angleTextView!!.text = " $angle"
//                powerTextView!!.text = " $strength"

                if ((angle >= 320 || angle <= 50) && (strength > 30)) {
//                    directionTextView!!.setText("Right")
                    command = "R"
                    voiceCommand.setText("R")
//                    sendCommand("R")
                } else if ((angle < 120 && angle > 60) && (strength > 30)) {
//                    directionTextView!!.setText("Forward")
                    command = "F"
                    voiceCommand.setText("F")
//                    sendCommand("F")
                } else if ((angle < 210 && angle > 120) && (strength > 30)) {
//                    directionTextView!!.setText("Left")
                    command = "L"
                    voiceCommand.setText("L")
//                   sendCommand("L")
                } else if ((angle < 320 && angle > 240) && (strength > 30)) {
//                    directionTextView!!.setText("Backward")
                    command = "B"
                    voiceCommand.setText("B")
//                    sendCommand("B")
                } else {
                    command = "S"
                    voiceCommand.setText("S")
//                    directionTextView!!.setText("Stop")
//                    sendCommand("s")
                }



                if (pre_command == voiceCommand.text) {
                    Log.d("pre command", "previous commanad")
                } else {
                    sendCommand(command)
                    pre_command = "$command"
                    Log.d(
                        "command", "onMove: $pre_command" +
                                " "
                    )
                }

            }

        }, JoystickView.DEFAULT_LOOP_INTERVAL)
    }

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
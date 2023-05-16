package com.examples.edi

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var con: TextView
    companion object {
        var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var m_bluetoothSocket: BluetoothSocket? = null
        lateinit var m_progress: ProgressDialog
        lateinit var m_bluetoothAdapter: BluetoothAdapter
        var m_isConnected: Boolean = false
        lateinit var m_address: String
        var bool = false;
    }
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        m_address = intent.getStringExtra(BlutoothActivity.EXTRA_ADDRESS).toString()
        var joystick = findViewById<Button>(R.id.joystick)
        var gyaro = findViewById<Button>(R.id.gyro)
        var add = findViewById<FloatingActionButton>(R.id.add)
        var voice = findViewById<Button>(R.id.voice)
        var con = findViewById<TextView>(R.id.connection)
        var dis = findViewById<Button>(R.id.Discon)

        dis.setOnClickListener{
            disconnect()
        }

        if(m_isConnected){
            con.text = "Connected"

        }
        else{
            con.text = "Disconnected"
        }
        joystick.setOnClickListener {
            var intent = Intent(this, joystickActivity::class.java)
            startActivity((intent))
        }
        gyaro.setOnClickListener {
            var intent = Intent(this, gyroscopeActivity::class.java)
            startActivity((intent))
        }
        add.setOnClickListener {
            var intent = Intent(this, BlutoothActivity::class.java)
            startActivity((intent))
        }


        voice.setOnClickListener {
            var intent = Intent(this, voice_controlActivity::class.java)
            startActivity((intent))
        }
    }

    private fun sendCommand(input: String) {

        if (m_bluetoothSocket != null) {
            try {

                m_bluetoothSocket!!.outputStream.write(input.toByteArray())
                Log.d("data", "send")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun disconnect() {
        if (m_bluetoothSocket != null) {
            try {
                m_bluetoothSocket!!.close()
                m_bluetoothSocket = null
                m_isConnected = false
//                val intent = Intent(this, MainActivity ::class.java)
//                startActivity(intent)
                Toast.makeText(this, "Disconnect", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        finish()
    }

    class ConnectToDevice(c: Context) : AsyncTask<Void, Void, String?>() {
        private var connectSuccess: Boolean = true
        private val context: Context

        init {
            this.context = c
        }

        override fun onPreExecute() {
            super.onPreExecute()

            m_progress = ProgressDialog.show(context, "Connecting...", "please wait")
        }

        override fun doInBackground(vararg p0: Void?): String? {
            try {
                if (m_bluetoothSocket == null || !m_isConnected) {
                    m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    val device: BluetoothDevice = m_bluetoothAdapter.getRemoteDevice(m_address)
                    if (ContextCompat.checkSelfPermission(
                            this.context,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            this.context as Activity, arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.BLUETOOTH_CONNECT
                            ), 1001
                        )
                    }
                    m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(m_myUUID)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                    m_bluetoothSocket?.connect()
                }
            } catch (e: IOException) {
                connectSuccess = false
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: String?) {
           super.onPostExecute(result)
            if (!connectSuccess) {
                Log.i("data", "couldn't connect")
                Toast.makeText(this.context, "Disonnect", Toast.LENGTH_SHORT).show()


            } else {
                m_isConnected = true
                Toast.makeText(this.context, "Connected", Toast.LENGTH_SHORT).show()

            }
            m_progress?.dismiss()

        }
    }
}
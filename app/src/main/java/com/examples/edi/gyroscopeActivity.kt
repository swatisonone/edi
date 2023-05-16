package com.examples.edi

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import java.io.IOException

class gyroscopeActivity : AppCompatActivity(), SensorEventListener {

    private  var tvGyro:ArrayList<TextView> = ArrayList()
    private var btnStart: Button? =null
    private var btnStop: Button? =null

    private var idGyro:ArrayList<Int> = arrayListOf(R.id.textView2,R.id.textView3,R.id.textView4)
    private lateinit var sensorManager: SensorManager
    private lateinit var sensorGyro: Sensor
    private lateinit var rotationVectorSensor: Sensor

    private var gyroData:SensorData? = null


    class SensorData(var x1: Float, var x2: Float, var x3: Float,timestamp: Long)


    private var gyroX:Float = 0f
    private var gyroY:Float = 0f
    private var gyroZ:Float = 0f
    var X=0
    var Y=0
    var Z=0

//    private var rotationMatrix : FloatArray = FloatArray(16)
//    private var remappedRotationMatrix : FloatArray = FloatArray(16)
//    private var orientation : FloatArray = FloatArray(16)


    private var timeGyro : Long =0
    //    lateinit var pre_command :String
    override fun onCreate(savedInstanceState: Bundle?) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gyroscope)

        initView()
        initSensors()

    }

    private fun initView() {
        val angle = findViewById<TextView>(R.id.textView6)
        for (i in idGyro){
            tvGyro.add(findViewById(i))
        }
        btnStart = findViewById(R.id.start)
        btnStop = findViewById(R.id.stop)

        btnStart?.setOnClickListener { registerListener()
            btnStop?.isEnabled = true
            btnStart?.isEnabled = false
        }
        btnStop?.setOnClickListener {
            unregisterListener()
            sendCommand("S")
            angle.setText("S")
            btnStop?.isEnabled = false
            btnStart?.isEnabled = true
        }
    }

    private fun initSensors() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        if (sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {
            sensorGyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
            rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        }
    }

    private fun registerListener(){
        gyroX = 0F
        gyroY = 0F
        gyroZ = 0F
        if (sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)!=null){
            sensorManager.registerListener(this,sensorGyro, SensorManager.SENSOR_DELAY_NORMAL)
//            sensorManager.registerListener(this,rotationVectorSensor,SensorManager.SENSOR_DELAY_NORMAL)
        }

    }
    private fun unregisterListener() {
        X=0
        Y=0
        Z=0
        gyroX = 0F
        gyroY = 0F
        gyroZ = 0F
        sensorManager.unregisterListener(this,sensorGyro)
        sensorManager.unregisterListener(this,rotationVectorSensor)
        sendCommand("S")
//        var intent = Intent(this,gyroscopeActivity::class.java)
//        startActivity(intent)
    }
    @SuppressLint("SetTextI18n")
    var pre_command = ""
    @SuppressLint("SetTextI18n")
    private fun getGyroData(e: SensorEvent?){
        val angle = findViewById<TextView>(R.id.textView6)

        if(gyroData == null){
            timeGyro = System.currentTimeMillis()
            gyroData = SensorData(e!!.values[0], e!!.values[1], e!!.values[2],e!!.timestamp)

        }
        else{

            var time = (System.currentTimeMillis() - timeGyro)/(1000f)
            gyroData!!.x1 = e!!.values[0]
            gyroData!!.x2 = e!!.values[1]
            gyroData!!.x3 = e!!.values[2]
            gyroX += gyroData!!.x1 * time
            gyroY += gyroData!!.x2 * time
            gyroZ += gyroData!!.x3 * time
        }
        tvGyro[0].text = "x1 : ${"%.2f".format(gyroData!!.x1*(180.0/Math.PI))}/s \t\tgyroX:${"%.2f".format(gyroX * (180.0 / Math.PI))}"
        tvGyro[1].text = "x2 : ${"%.2f".format(gyroData!!.x2*(180.0/Math.PI))}/s \t\tgyroY:${"%.2f".format(gyroY * (180.0 / Math.PI))}"
        tvGyro[2].text = "x3 : ${"%.2f".format(gyroData!!.x3*(180.0/Math.PI))}/s \t\tgyroZ:${"%.2f".format(gyroZ * (180.0 / Math.PI))}"
        timeGyro = System.currentTimeMillis()

        X = (gyroX * (180.0 / Math.PI)).toInt()
        Y = (gyroY * (180.0 / Math.PI)).toInt()
        Z = (gyroZ * (180.0 / Math.PI)).toInt()
        var command =""
        if (X>20){
            command = "R"
            angle.setText("R")
        }
        else if(X < -20){
            command="L"
            angle.setText("L")

        }
        else if(Y < -30){
            command = "B"
            angle.setText("B")
        }
        else if(Y > 20){
            command = "F"
            angle.setText("F")
        }
        else if (Z>20){
            command = "A"
            angle.setText("A")
        }
        else if (Z<-20){
            command = "C"
            angle.setText("C")
        }
        else{
            command = "S"
            angle.setText("S")
        }

        if (pre_command == angle.text){
            Log.d("pre command", "previous commanad")
        }
        else{
            sendCommand(command)
            pre_command = "$command"
        }
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        getGyroData(p0)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        if (p0 == sensorGyro) {
            when (p1) {
                0 -> {
                    println("Unreliable")

                }
                1 -> {
                    println("Low Accuracy")
                }
                2 -> {
                    println("Medium Accuracy")
                }
                3 -> {
                    println("High Accuracy")
                }
            }
        }
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

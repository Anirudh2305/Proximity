package com.example.torchon

import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi


// This app will turn on flash light and make the screen red when something detected by proximity sensor. Else flash will be off and screen will be green.

        class MainActivity : Activity(), SensorEventListener {

            private lateinit var sensorManager: SensorManager
            private var proximity: Sensor? = null

            private lateinit var cameraManager: CameraManager
            private lateinit var cameraID: String


            public override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_main)

                // Get an instance of the sensor service, and use that to get an instance of
                // a particular sensor.
                sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
                proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

                val isFlashAvailable = applicationContext.packageManager
                        .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)


                cameraManager =getSystemService(CAMERA_SERVICE) as CameraManager
                cameraID = cameraManager.cameraIdList[0]

                if (proximity == null) {
                    Toast.makeText(this, "No proximity sensor available", Toast.LENGTH_LONG).show()
                    finish()  // close app
                }

                if (!isFlashAvailable) {
                    Toast.makeText(this,"Flashlight not available",Toast.LENGTH_LONG).show()
                    finish()
                }

            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                // Do something here if sensor accuracy changes.
            }


            @RequiresApi(Build.VERSION_CODES.M)  // requires API 23 and above for camera2
            override fun onSensorChanged(event: SensorEvent) {
                val distance = event.values[0]
                if ((distance < proximity!!.maximumRange)) {
                    window.decorView.setBackgroundColor(Color.RED)
                    cameraManager.setTorchMode(cameraID, true)
                }
                else {
                    window.decorView.setBackgroundColor(Color.GREEN)
                    cameraManager.setTorchMode(cameraID, false)
                }

            }

            override fun onResume() {
                // Register a listener for the sensor.
                super.onResume()
                    sensorManager.registerListener(this, proximity, SensorManager.SENSOR_DELAY_NORMAL)
            }

            override fun onPause() {
                // Be sure to unregister the sensor when the activity pauses.
                super.onPause()
                sensorManager.unregisterListener(this)
                Toast.makeText(this, "Sensor not being used anymore", Toast.LENGTH_LONG).show()
            }
        }






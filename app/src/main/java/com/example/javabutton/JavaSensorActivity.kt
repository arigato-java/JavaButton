package com.example.javabutton

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

// Shake your Java
open class JavaSensorActivity:JavaBaseActivity() {
    var JAVA_MIN_ACCEL = 600f
    var JAVA_MAX_ACCEL = 1000f
    var evenShake = true
    var enableShakeModulation = true
    var counterShake: Long = 0

    val aSensorListener=object: SensorEventListener{
        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

        }

        override fun onSensorChanged(event: SensorEvent?) {
            if(event==null){return}
            val x: Float
            val y: Float
            val z: Float
            x = event.values[0]
            y = event.values[1]
            z = event.values[2]

            var acceleration = x * x + y * y + z * z
            if (acceleration > JAVA_MIN_ACCEL) {
                if (evenShake) {
                    val pitch: Float
                    acceleration = Math.min(acceleration, JAVA_MAX_ACCEL)
                    if (enableShakeModulation) {
                        pitch = 0.8f + 1.2f * (acceleration - JAVA_MIN_ACCEL) / (JAVA_MAX_ACCEL - JAVA_MIN_ACCEL)
                    } else {
                        pitch = 1f
                    }
                    playJava(pitch)
                    counterShake++
                }
                evenShake = !evenShake
            }

        }
    }
    override fun onResume(){
        super.onResume()
        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if(accelerometer!=null) {
            sensorManager?.registerListener(aSensorListener, accelerometer, SensorManager.SENSOR_STATUS_ACCURACY_LOW)
        }
    }
    override fun onPause(){
        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensorManager?.unregisterListener(aSensorListener)
        super.onPause()
    }
}

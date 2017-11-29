package com.example.javabutton

import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent

// DJ Java
open class JavaDJActivity:JavaSensorActivity() {
    var counterDJ: Long = 0
    private val gestureListener=object:GestureDetector.OnGestureListener{
        var v_min = 4000f
        var v_max = 9000f

        override fun onDown(arg0: MotionEvent): Boolean {
            return false
        }

        override fun onFling(evt0: MotionEvent, evt1: MotionEvent,
                             velocityX: Float, velocityY: Float): Boolean {
            val velocity = Math.sqrt((velocityX * velocityX + velocityY * velocityY).toDouble()).toFloat()
            if (velocity >= v_min) {
                val v2 = Math.min(v_max, velocity)
                val pitch = (v2 - v_min) / (v_max - v_min) * 1.3f + 0.7f
                playJava(pitch)
                counterDJ++
                return true
            }
            return false
        }

        override fun onLongPress(arg0: MotionEvent) {
        }

        override fun onScroll(arg0: MotionEvent, arg1: MotionEvent, arg2: Float,
                              arg3: Float): Boolean {
            return false
        }

        override fun onShowPress(arg0: MotionEvent) {
        }

        override fun onSingleTapUp(arg0: MotionEvent): Boolean {
            return false
        }
    }
    private var gDetector:GestureDetector? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gDetector=GestureDetector(this,gestureListener)
    }
    fun setGestureSpeedRange(minSpeed:Float,maxSpeed:Float){
        gestureListener.v_min=minSpeed
        gestureListener.v_max=maxSpeed
    }
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gDetector?.onTouchEvent(event) ?: false
    }

}
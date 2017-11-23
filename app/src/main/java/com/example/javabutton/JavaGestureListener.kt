package com.example.javabutton

import android.view.GestureDetector
import android.view.MotionEvent

class JavaGestureListener(private val a: MainActivity) : GestureDetector.OnGestureListener {
    private var v_min: Float = 0.toFloat()
    private var v_max: Float = 0.toFloat()

    init {
        v_min = 4000f
        v_max = 9000f
    }

    fun setMinMax(v_min: Float, v_max: Float) {
        this.v_min = v_min
        this.v_max = v_max
    }

    override fun onDown(arg0: MotionEvent): Boolean {
        // TODO Auto-generated method stub
        return false
    }

    override fun onFling(evt0: MotionEvent, evt1: MotionEvent,
                         velocityX: Float, velocityY: Float): Boolean {
        val velocity = Math.sqrt((velocityX * velocityX + velocityY * velocityY).toDouble()).toFloat()
        if (velocity >= v_min) {
            val v2 = Math.min(v_max, velocity)
            val pitch = (v2 - v_min) / (v_max - v_min) * 1.3f + 0.7f
            a.playJava(pitch)
            a.incrementDJCounter()
            //javaPool.play(javaSoundId, 1.0f, 1.0f, 1, 0, pitch);
            return true
        }
        return false
    }

    override fun onLongPress(arg0: MotionEvent) {
        // TODO Auto-generated method stub

    }

    override fun onScroll(arg0: MotionEvent, arg1: MotionEvent, arg2: Float,
                          arg3: Float): Boolean {
        // TODO Auto-generated method stub
        return false
    }

    override fun onShowPress(arg0: MotionEvent) {
        // TODO Auto-generated method stub

    }

    override fun onSingleTapUp(arg0: MotionEvent): Boolean {
        // TODO Auto-generated method stub
        return false
    }

}

package com.example.javabutton

import android.view.KeyEvent

object JavaPiano {
    private val keyCodes = intArrayOf(KeyEvent.KEYCODE_Q, KeyEvent.KEYCODE_W, KeyEvent.KEYCODE_E, KeyEvent.KEYCODE_R, KeyEvent.KEYCODE_T, KeyEvent.KEYCODE_Y, KeyEvent.KEYCODE_U, KeyEvent.KEYCODE_I, KeyEvent.KEYCODE_O, KeyEvent.KEYCODE_P, KeyEvent.KEYCODE_1, KeyEvent.KEYCODE_2, KeyEvent.KEYCODE_3, KeyEvent.KEYCODE_4, KeyEvent.KEYCODE_5, KeyEvent.KEYCODE_6, KeyEvent.KEYCODE_7, KeyEvent.KEYCODE_8, KeyEvent.KEYCODE_9, KeyEvent.KEYCODE_0)
    private val javaCodes = floatArrayOf(.5f, .625f, .75f, .875f, 1f, 1.125f, 1.25f, 1.5f, 1.75f, 2f)
    fun JavaPianoCalcCode(keyCode: Int): Float {
        var i = 0
        while (i < keyCodes.size) {
            if (keyCodes[i] == keyCode) {
                if (i >= javaCodes.size) {
                    i = i % javaCodes.size
                }
                return javaCodes[i]
            }
            i++
        }
        return -1f
    }
}

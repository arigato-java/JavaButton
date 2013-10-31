package com.example.javabutton;

import android.view.KeyEvent;

public class JavaPiano {
	private static final int[] keyCodes={
		KeyEvent.KEYCODE_Q,KeyEvent.KEYCODE_W,KeyEvent.KEYCODE_E,KeyEvent.KEYCODE_R,
		KeyEvent.KEYCODE_T,KeyEvent.KEYCODE_Y,KeyEvent.KEYCODE_U,KeyEvent.KEYCODE_I,
		KeyEvent.KEYCODE_O,KeyEvent.KEYCODE_P,
		KeyEvent.KEYCODE_1,KeyEvent.KEYCODE_2,KeyEvent.KEYCODE_3,KeyEvent.KEYCODE_4,
		KeyEvent.KEYCODE_5,KeyEvent.KEYCODE_6,KeyEvent.KEYCODE_7,KeyEvent.KEYCODE_8,
		KeyEvent.KEYCODE_9,KeyEvent.KEYCODE_0};
	private static final float[] javaCodes={
		.5f,.625f,.75f,.875f,1.f,1.125f,1.25f,1.5f,1.75f,2.f
	};
	static float JavaPianoCalcCode(int keyCode) {
		for(int i=0; i<keyCodes.length; i++) {
			if(keyCodes[i]==keyCode) {
				if(i>=javaCodes.length) { i=i%javaCodes.length; }
				return javaCodes[i];
			}
		}
		return -1.f;
	}
}

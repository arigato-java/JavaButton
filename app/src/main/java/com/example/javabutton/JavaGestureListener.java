package com.example.javabutton;

import android.view.GestureDetector;
import android.view.MotionEvent;

public class JavaGestureListener implements GestureDetector.OnGestureListener {
	private float v_min,v_max;
	private MainActivity a;
	public JavaGestureListener(MainActivity a) {
		this.a=a;
		v_min=4000.f;
		v_max=9000.f;
	}
	public void setMinMax(float v_min,float v_max) {
		this.v_min=v_min;
		this.v_max=v_max;
	}
	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent evt0, MotionEvent evt1,
			float velocityX, float velocityY) {
		float velocity=(float) Math.sqrt(velocityX*velocityX+velocityY*velocityY);
		if(velocity>=v_min) {
			float v2=Math.min(v_max, velocity);
			float pitch=(v2-v_min)/(v_max-v_min)*1.3f+0.7f;
			a.playJava(pitch);
			a.incrementDJCounter();
			//javaPool.play(javaSoundId, 1.0f, 1.0f, 1, 0, pitch);
			return true;
		}
		return false;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	
}

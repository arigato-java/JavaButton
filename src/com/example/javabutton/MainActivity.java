package com.example.javabutton;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.res.Resources;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
//import android.widget.Toast;
import android.media.AudioManager;
import android.media.SoundPool;

public class MainActivity extends Activity implements SensorEventListener, GestureDetector.OnGestureListener {
    private SensorManager sensorManager;
    private Sensor accelerometer;
    protected final float JAVA_MIN_ACCEL=600.f;
    protected final float JAVA_MAX_ACCEL=1000.f;
    private SoundPool javaPool;
    private int maxJavaSounds=4;
    private int javaSoundId;
    private boolean evenShake=true;
    private GestureDetector gDetector;
	private float v_max;
	private float v_min;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        javaPool=new SoundPool(maxJavaSounds,AudioManager.STREAM_MUSIC,0);
        javaSoundId=javaPool.load(this, R.raw.java22, 1);
        
        Resources r=getResources();
        v_max=r.getDimensionPixelSize(R.dimen.fling_velocity_max);
        v_min=r.getDimensionPixelSize(R.dimen.fling_velocity_min);
        
        gDetector=new GestureDetector(this,this);
        
        sensorManager= (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer= sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_STATUS_ACCURACY_LOW);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	javaPool.release();
    }
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    */
    public void JavaButtonClick(View v) {
        //Toast.makeText(this,"Java",Toast.LENGTH_SHORT).show();
    	javaPool.play(javaSoundId, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x, y, z;
        x=event.values[0];
        y=event.values[1];
        z=event.values[2];

        float acceleration=x*x + y*y + z*z;
        if (acceleration > JAVA_MIN_ACCEL) {
        	if(evenShake) {
        		acceleration=Math.min(acceleration, JAVA_MAX_ACCEL);
            	float pitch=0.8f+1.2f*(acceleration-JAVA_MIN_ACCEL)/(JAVA_MAX_ACCEL-JAVA_MIN_ACCEL);
            	javaPool.play(javaSoundId, 1.0f, 1.0f, 1, 0, pitch);
            }
        	evenShake=!evenShake;
        }
     }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gDetector.onTouchEvent(event);
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){}

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
			javaPool.play(javaSoundId, 1.0f, 1.0f, 1, 0, pitch);
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

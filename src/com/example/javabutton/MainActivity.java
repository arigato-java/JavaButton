package com.example.javabutton;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
//import android.widget.Toast;
import android.media.AudioManager;
import android.media.SoundPool;

public class MainActivity extends Activity implements SensorEventListener, OnSharedPreferenceChangeListener {
    private SensorManager sensorManager;
    private Sensor accelerometer;
    protected float JAVA_MIN_ACCEL=600.f;
    protected float JAVA_MAX_ACCEL=1000.f;
    private SoundPool javaPool;
    private int maxJavaSounds=4;
	private int javaSoundId;
    private boolean evenShake=true;
    private GestureDetector gDetector;
	private JavaGestureListener gestureListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        javaPool=new SoundPool(maxJavaSounds,AudioManager.STREAM_MUSIC,0);
        javaSoundId=javaPool.load(this, R.raw.java22, 1);
		
		gestureListener=new JavaGestureListener(this);
		gDetector=new GestureDetector(this,gestureListener);

        sensorManager= (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer= sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		SharedPreferences shrPrefs=PreferenceManager.getDefaultSharedPreferences(this);
		shrPrefs.registerOnSharedPreferenceChangeListener(this);
		loadPreferences(shrPrefs);
    }
    
    private void loadPreferences(SharedPreferences shrPrefs) {
    	float density=getResources().getDisplayMetrics().density;
    	float x,y,z,w;
    	float v_min,v_max;
    	x=Float.valueOf(shrPrefs.getString("pref_djJava_min", "2000"));
       y=Float.valueOf(shrPrefs.getString("pref_djJava_max", "5000"));
       z=Math.max(1.f, Math.min(x, y));
		v_min=density*z;
		w=Math.max(v_min+1.f, Math.max(x, y));
		v_max=density*w;
		gestureListener.setMinMax(v_min, v_max);
		
       x=Float.valueOf(shrPrefs.getString("pref_shakeJava_min","600"));
       y=Float.valueOf(shrPrefs.getString("pref_shakeJava_max", "1000"));
       JAVA_MIN_ACCEL=Math.max(1.f, Math.min(x,y));
       JAVA_MAX_ACCEL=Math.max(JAVA_MIN_ACCEL+1.f, Math.max(x, y));
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
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	int itemId=item.getItemId();
    	if(itemId==R.id.action_settings) {
    		Intent intent=new Intent(this,SettingsActivity.class);
    		startActivity(intent);
    		return true;
    	}
    	return super.onMenuItemSelected(featureId,item);
    }
    
	public void JavaButtonClick(View v) {
		playJava(1.f);
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
            	playJava(pitch);
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
	public void onSharedPreferenceChanged(SharedPreferences shrP, String arg1) {
		loadPreferences(shrP);
	}
	
	public void playJava(float pitch) {
    	javaPool.play(javaSoundId, 1.0f, 1.0f, 1, 0, pitch);
	}
}

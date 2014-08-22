package com.example.javabutton;

import java.util.ArrayList;
import java.util.List;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ResolveInfo;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ShareActionProvider;
import android.widget.Toast;
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
	private int javaSoundId,eyepadSoundId;
    private boolean evenShake=true;
    private GestureDetector gDetector;
	private JavaGestureListener gestureListener;
	private boolean enableLucky=false;
	private java.util.Random random;
	private boolean enableShakeModulation=true;
	private long counterDJ, counterPress, counterShake, counterVoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		random=new java.util.Random();

        javaPool=new SoundPool(maxJavaSounds,AudioManager.STREAM_MUSIC,0);
        javaSoundId=javaPool.load(this, R.raw.java22, 1);
		eyepadSoundId=javaPool.load(this,R.raw.eyepad, 1);
		
		gestureListener=new JavaGestureListener(this);
		gDetector=new GestureDetector(this,gestureListener);

        sensorManager= (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer= sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		SharedPreferences shrPrefs=PreferenceManager.getDefaultSharedPreferences(this);
		shrPrefs.registerOnSharedPreferenceChangeListener(this);
    }
    
    private void loadPreferences(SharedPreferences shrPrefs) {
    	float density=getResources().getDisplayMetrics().density;
    	float x,y,z,w;
    	float v_min,v_max;
		try {
			x=Float.valueOf(shrPrefs.getString("pref_djJava_min", "2000"));
			y=Float.valueOf(shrPrefs.getString("pref_djJava_max", "5000"));
		} catch (Exception e) {
			x=2000.f;
			y=5000.f;
		}
       z=Math.max(1.f, Math.min(x, y));
		v_min=density*z;
		w=Math.max(v_min+1.f, Math.max(x, y));
		v_max=density*w;
		gestureListener.setMinMax(v_min, v_max);
		
		try {
			x=Float.valueOf(shrPrefs.getString("pref_shakeJava_min","600"));
			y=Float.valueOf(shrPrefs.getString("pref_shakeJava_max", "1000"));
		} catch (Exception e) {
			x=600.f;
			y=1000.f;
		}
       JAVA_MIN_ACCEL=Math.max(1.f, Math.min(x,y));
       JAVA_MAX_ACCEL=Math.max(JAVA_MIN_ACCEL+1.f, Math.max(x, y));
		
		enableLucky=shrPrefs.getBoolean("pref_lucky_enable", false);
		enableShakeModulation=shrPrefs.getBoolean("pref_shakeJava_modulation_enable", true);
		
		counterPress=shrPrefs.getLong(SettingsActivity.pref_counterPress, 0l);
		counterDJ=shrPrefs.getLong(SettingsActivity.pref_counterDJ, 0l);
		counterShake=shrPrefs.getLong(SettingsActivity.pref_counterShake, 0l);
		counterVoice=shrPrefs.getLong(SettingsActivity.pref_counterVoice, 0l);
	}

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_STATUS_ACCURACY_LOW);
		loadPreferences(PreferenceManager.getDefaultSharedPreferences(this));
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(this);
		PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
		saveCounters();
		super.onPause();
	}
	private void saveCounters() {
		SharedPreferences.Editor e=PreferenceManager.getDefaultSharedPreferences(this).edit();
		e.putLong(SettingsActivity.pref_counterShake, counterShake);
		e.putLong(SettingsActivity.pref_counterDJ, counterDJ);
		e.putLong(SettingsActivity.pref_counterPress, counterPress);
		e.putLong(SettingsActivity.pref_counterVoice, counterVoice);
		e.commit();
	}

    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	javaPool.release();
    }
	private boolean isSpeechRecognizerAvailable() {
		try {
			Intent recognitionIntent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
			List<ResolveInfo> activities=getPackageManager().queryIntentActivities(recognitionIntent, 0);
			if(activities.size()>0) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
		if(Build.VERSION.SDK_INT>=14) {
			setupShareAction(menu);
		}
		if(!isSpeechRecognizerAvailable()) {
			MenuItem micAction=menu.findItem(R.id.action_mic);
			micAction.setVisible(false);
		}
		return true;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private boolean setupShareAction(Menu menu) {
		MenuItem shareItem=menu.findItem(R.id.menu_item_share);
		ShareActionProvider sap=(ShareActionProvider)shareItem.getActionProvider();
		if(sap!=null) {
			Intent shareIntent=new Intent(Intent.ACTION_SEND);
			shareIntent.setType(org.apache.http.protocol.HTTP.PLAIN_TEXT_TYPE);
			shareIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.java_message));
			
			sap.setShareIntent(shareIntent);
		}

        return true;
    }

    @Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	int itemId=item.getItemId();
    	if(itemId==R.id.action_settings) {
    		Intent intent=new Intent(this,SettingsActivity.class);
    		startActivity(intent);
		} else if(itemId==R.id.action_mic) {
			startSpeechRecognition();
		} else {
			return super.onMenuItemSelected(featureId,item);
		}
		return true;
	}

    public void JavaButtonClick(View v) {
		playJava(1.f);
		counterPress++;
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
				float pitch;
        		acceleration=Math.min(acceleration, JAVA_MAX_ACCEL);
				if(enableShakeModulation) {
					pitch=0.8f+1.2f*(acceleration-JAVA_MIN_ACCEL)/(JAVA_MAX_ACCEL-JAVA_MIN_ACCEL);
				} else {
					pitch=1.f;
				}
            	playJava(pitch);
				counterShake++;
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
		int sndId;
		if(enableLucky) {
			int r=random.nextInt(32);
			sndId=(r==0)?eyepadSoundId:javaSoundId;
		} else {
			sndId=javaSoundId;
		}
		javaPool.play(sndId, 1.0f, 1.0f, 1, 0, pitch);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(event.getAction()==KeyEvent.ACTION_DOWN) {
			if(keyCode==KeyEvent.KEYCODE_SPACE || 
					keyCode==KeyEvent.KEYCODE_BUTTON_A ||
					keyCode==KeyEvent.KEYCODE_BUTTON_L1 ||
					keyCode==KeyEvent.KEYCODE_BUTTON_L2 ||
					keyCode==KeyEvent.KEYCODE_BUTTON_R1 ||
					keyCode==KeyEvent.KEYCODE_BUTTON_R2) {
				counterPress++;
				playJava(1.f);
				return true;
			} else {
				float pitch=JavaPiano.JavaPianoCalcCode(keyCode);
				if(pitch>0.f) {
					playJava(pitch);
					return true;
				}
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private static final int VOICE_RECOGNITION_REQUEST_CODE=0x06a103;
	private void startSpeechRecognition() {
		Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
		intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
		startActivityForResult(intent,VOICE_RECOGNITION_REQUEST_CODE);
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==VOICE_RECOGNITION_REQUEST_CODE && resultCode==RESULT_OK) {
			ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			if(matches.size()>0) {
				try {
					String javaStr=matches.get(0).toLowerCase(java.util.Locale.US);
					if("java".equals(javaStr)) {
						counterVoice++;
						playJava(1.f);
						SharedPreferences shrP=PreferenceManager.getDefaultSharedPreferences(this);
						SharedPreferences.Editor e=shrP.edit();
						e.putLong(SettingsActivity.pref_counterVoice,counterVoice);
						e.commit();
					} else {
						Toast.makeText(this, javaStr, Toast.LENGTH_SHORT).show();
					}
				} catch (NullPointerException e) {
				}
			}
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	public void incrementDJCounter() {
		counterDJ++;
	}
}

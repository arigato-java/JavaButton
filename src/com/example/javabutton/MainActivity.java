package com.example.javabutton;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.net.Uri;

public class MainActivity extends Activity implements SensorEventListener {
    final Uri voiceuri= Uri.parse("android.resource://com.example.javabutton/raw/java22");
    JavaPlayer jp=new JavaPlayer(voiceuri);
    private SensorManager sensorManager;
    private Sensor accelerometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    	jp.play(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x, y, z;
        x=event.values[0];
        y=event.values[1];
        z=event.values[2];

        float acceleration=x*x + y*y + z*z;
        if (acceleration > 1000)
            jp.play(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){}
}

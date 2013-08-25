package com.example.javabutton;

import android.media.AsyncPlayer;
import android.media.AudioManager;
import android.os.Bundle;
import android.app.Activity;
//import android.view.Menu;
import android.view.View;
import android.net.Uri;

public class MainActivity extends Activity {
    AsyncPlayer ap;
    final Uri voiceuri= Uri.parse("android.resource://com.example.javabutton/raw/java22");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ap=new AsyncPlayer("javavoice");
        setContentView(R.layout.activity_main);
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
        ap.play(this,voiceuri,false, AudioManager.STREAM_MUSIC);
    }

}

package com.example.javabutton;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

public class JavaPlayerThread extends Thread implements MediaPlayer.OnCompletionListener {
	private MediaPlayer mPlayer=null;
	private LinkedBlockingQueue<Integer> cmdQ;
	private Uri jUri;
	private Context nextContext;
	private Object nextContextLock=new Object();
	private final int JAVA_STOP=0;
	private final int JAVA_PLAY=1;
	private final int JAVA_PASS=0xff;
	public JavaPlayerThread(Uri javaUri) {
		cmdQ=new LinkedBlockingQueue<Integer>();
		jUri=javaUri;
	}
	public void run() {
		while(true) {
			int cmdVal;
			try {
				Integer cmd;
				cmd=cmdQ.take(); // This blocks when queue is empty
				cmdVal=cmd.intValue();
			} catch (InterruptedException e) {
				cmdVal=JAVA_PASS;
			}
			if(cmdVal==JAVA_STOP) {
				try {
					if(mPlayer!=null && mPlayer.isPlaying()) {
						mPlayer.stop();
						mPlayer.release();
					}
				} catch (IllegalStateException e) { } // already released
			}
			if(cmdVal==JAVA_PLAY) {
				MediaPlayer mPlayer2;
				Context ctx;
				synchronized(nextContextLock) {
					ctx=nextContext;
				}
				mPlayer2=new MediaPlayer();
				mPlayer2.setAudioStreamType(AudioManager.STREAM_MUSIC);
				mPlayer2.setLooping(false);
				mPlayer2.setOnCompletionListener(this);
				try {
					mPlayer2.setDataSource(ctx,jUri);
					mPlayer2.prepare();
				} catch (IOException e) {
					Log.w("JavaPlayer","mPlayer change datasource failed.");
				}
				// stop mPlayer
				try {
					if(mPlayer!=null && mPlayer.isPlaying()) {
						mPlayer.stop();
						mPlayer.release();
					}
				} catch (IllegalStateException e) { } // already released
				mPlayer2.start();
				mPlayer=mPlayer2;
			}
		}
	}
	public void enqueuePlay() {
		cmdQ.add(Integer.valueOf(JAVA_PLAY));
	}
	public void enqueueStop() {
		cmdQ.add(Integer.valueOf(JAVA_STOP));
	}
	public void enqueueChangeContext(Context ctx) {
		synchronized(nextContextLock) {
			nextContext=ctx;
		}
	}
	@Override
	public void onCompletion(MediaPlayer mp) {
		mp.release();
	}
}

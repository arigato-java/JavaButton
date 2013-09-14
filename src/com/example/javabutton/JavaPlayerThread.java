package com.example.javabutton;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

public class JavaPlayerThread extends Thread implements MediaPlayer.OnCompletionListener {
	private MediaPlayer mPlayer;
	private LinkedBlockingQueue<Integer> cmdQ;
	private Uri jUri;
	private Context nextContext;
	private Object nextContextLock=new Object();
	private final int JAVA_STOP=0;
	private final int JAVA_PLAY=1;
	private final int JAVA_CHANGE_CONTEXT=2;
	public JavaPlayerThread(Uri javaUri) {
		cmdQ=new LinkedBlockingQueue<Integer>();
		mPlayer=new MediaPlayer();
		mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mPlayer.setLooping(false);
		mPlayer.setOnCompletionListener(this);
		jUri=javaUri;
	}
	public void run() {
		Integer cmd;
		while(true) {
			try {
				cmd=cmdQ.take(); // This blocks when queue is empty
			} catch (InterruptedException e) {
				cmd=Integer.valueOf(0xff);
			}
			int cmdVal=cmd.intValue();
			if(cmdVal==JAVA_STOP || cmdVal==JAVA_PLAY) {
				if(mPlayer.isPlaying()) {
					mPlayer.stop();
					mPlayer.release();
				}
			}
			if(cmdVal==JAVA_PLAY) {
				mPlayer.start();
			} else if(cmdVal==JAVA_CHANGE_CONTEXT) {
				Context ctx;
				synchronized(nextContextLock) {
					ctx=nextContext;
				}
				try {
					mPlayer.setDataSource(ctx,jUri);
					mPlayer.prepare();
				} catch (IOException e) {
					Log.w("JavaPlayer","mPlayer change datasource failed.");
				}
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
		cmdQ.add(Integer.valueOf(JAVA_CHANGE_CONTEXT));
	}
	@Override
	public void onCompletion(MediaPlayer mp) {
		mp.release();
	}
}

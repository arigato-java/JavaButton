package com.example.javabutton;

import android.content.Context;
import android.net.Uri;

public class JavaPlayer {
	private JavaPlayerThread jThread;
	public JavaPlayer(Uri javaUri) {
		jThread=new JavaPlayerThread(javaUri);
		jThread.start();
	}
	public void play() {
		jThread.enqueuePlay();
	}
	public void stop() {
		jThread.enqueueStop();
	}
	public void setContext(Context ctx) {
		jThread.enqueueChangeContext(ctx);
	}
	public void play(Context ctx) {
		setContext(ctx);
		play();
	}
}

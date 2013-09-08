package com.example.javabutton;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.media.AsyncPlayer;
import android.media.AudioManager;
import android.net.Uri;
import android.widget.RemoteViews;

public class JavaButtonWidgetProvider extends AppWidgetProvider {
	public static final String JAVA_ACTION="com.example.javabutton.javabuttonwidget.JAVA_ACTION";
    final Uri voiceuri= Uri.parse("android.resource://com.example.javabutton/raw/java22");
    AsyncPlayer ap=new AsyncPlayer("javavoice");

	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(JAVA_ACTION)) {
			ap.play(context,voiceuri,false, AudioManager.STREAM_MUSIC);
		}
		super.onReceive(context, intent);
	}
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;
        for(int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.javabutton_appwidget);
            Intent javaIntent=new Intent(context,JavaButtonWidgetProvider.class);
            javaIntent.setAction(JAVA_ACTION);
            javaIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]); // Not used
            PendingIntent javaPendingIntent=PendingIntent.getBroadcast(context,0,javaIntent,
            		PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.redButton, javaPendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}

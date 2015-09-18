package com.example.javabutton;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

public class JavaButtonWidgetProvider extends AppWidgetProvider {
    final Uri voiceuri= Uri.parse("android.resource://com.example.javabutton/raw/java22");
    JavaPlayer jp=new JavaPlayer(voiceuri);
	protected String getJavaActionName() { return "com.example.javabutton.javabuttonwidget.JAVA_ACTION"; }
	protected int getJavaboLayoutId() { return R.layout.javabutton_appwidget; }
	protected int getJavaBotanId() { return R.id.redButton; }

	public void onReceive(Context context, Intent intent) {
		SharedPreferences shrP=PreferenceManager.getDefaultSharedPreferences(context);
		if(getJavaActionName().equals(intent.getAction())) {
			jp.play(context);
			SharedPreferences.Editor e=shrP.edit();
			long javaCounter=shrP.getLong(SettingsActivity.pref_counterPress, 0l);
			// prevent long overflow roll-over
			long nextjavaCounter=Math.max(javaCounter+1,javaCounter);
			e.putLong(SettingsActivity.pref_counterPress, nextjavaCounter);
			e.commit();
		}
		super.onReceive(context, intent);
	}
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;
        for(int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];
			RemoteViews views = new RemoteViews(context.getPackageName(), getJavaboLayoutId());
            Intent javaIntent=new Intent(context,this.getClass());
			javaIntent.setAction(getJavaActionName());
            javaIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]); // Not used
            PendingIntent javaPendingIntent=PendingIntent.getBroadcast(context,0,javaIntent,
            		PendingIntent.FLAG_UPDATE_CURRENT);
			views.setOnClickPendingIntent(getJavaBotanId(), javaPendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}

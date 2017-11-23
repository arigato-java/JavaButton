package com.example.javabutton

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.preference.PreferenceManager
import android.widget.RemoteViews

open class JavaButtonWidgetProvider : AppWidgetProvider() {
    internal val voiceuri = Uri.parse("android.resource://com.example.javabutton/raw/java22")
    internal var jp = JavaPlayer(voiceuri)
    protected open val javaActionName: String
        get() = "com.example.javabutton.javabuttonwidget.JAVA_ACTION"
    protected open val javaboLayoutId: Int
        get() = R.layout.javabutton_appwidget
    protected open val javaBotanId: Int
        get() = R.id.redButton

    override fun onReceive(context: Context, intent: Intent) {
        val shrP = PreferenceManager.getDefaultSharedPreferences(context)
        if (javaActionName == intent.action) {
            jp.play(context)
            val e = shrP.edit()
            val javaCounter = shrP.getLong(SettingsActivity.pref_counterPress, 0L)
            // prevent long overflow roll-over
            val nextjavaCounter = Math.max(javaCounter + 1, javaCounter)
            e.putLong(SettingsActivity.pref_counterPress, nextjavaCounter)
            e.commit()
        }
        super.onReceive(context, intent)
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val N = appWidgetIds.size
        for (i in 0 until N) {
            val appWidgetId = appWidgetIds[i]
            val views = RemoteViews(context.packageName, javaboLayoutId)
            val javaIntent = Intent(context, this.javaClass)
            javaIntent.action = javaActionName
            javaIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]) // Not used
            val javaPendingIntent = PendingIntent.getBroadcast(context, 0, javaIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT)
            views.setOnClickPendingIntent(javaBotanId, javaPendingIntent)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

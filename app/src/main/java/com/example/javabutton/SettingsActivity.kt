package com.example.javabutton

import java.text.NumberFormat

import android.annotation.TargetApi
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Build
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceActivity
import android.preference.PreferenceManager
import android.view.MenuItem

class SettingsActivity : PreferenceActivity(), OnSharedPreferenceChangeListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            addUpToActionbar()
        }
        addPreferencesFromResource(R.xml.preferences)
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private fun addUpToActionbar() {
        actionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val menuId = item.itemId
        if (menuId == android.R.id.home) {
            onBackPressed()
            return true
        } else {
            return super.onOptionsItemSelected(item)
        }
    }

    public override fun onResume() {
        super.onResume()
        val shrP = PreferenceManager.getDefaultSharedPreferences(this)
        shrP.registerOnSharedPreferenceChangeListener(this)
        // Update summary fields
        val prefFields = arrayOf(pref_counterDJ, pref_counterPress, pref_counterShake, pref_counterVoice)
        for (p in prefFields) {
            onSharedPreferenceChanged(shrP, p)
        }
    }

    public override fun onPause() {
        val shrP = PreferenceManager.getDefaultSharedPreferences(this)
        shrP.unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences,
                                           key: String) {
        if (pref_counterDJ == key || pref_counterPress == key ||
                pref_counterShake == key || pref_counterVoice == key) {
            val nf = NumberFormat.getInstance()
            val p = findPreference(key)
            p.summary = nf.format(sharedPreferences.getLong(key, 0L))
        }
    }

    companion object {
        val pref_counterDJ = "DJJavaCounter"
        val pref_counterPress = "PressCounter"
        val pref_counterShake = "ShakeCounter"
        val pref_counterVoice = "VoiceCounter"
    }

}

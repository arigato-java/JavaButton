package com.example.javabutton;

import java.text.NumberFormat;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.MenuItem;

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	public static final String pref_counterDJ="DJJavaCounter", 
			pref_counterPress="PressCounter",
			pref_counterShake="ShakeCounter",
			pref_counterVoice="VoiceCounter";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(android.os.Build.VERSION.SDK_INT>=11) {
			addUpToActionbar();
		}
		addPreferencesFromResource(R.xml.preferences);
	}
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void addUpToActionbar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int menuId=item.getItemId();
		if(menuId==android.R.id.home) {
			onBackPressed();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}
	@Override
	public void onResume() {
		super.onResume();
		SharedPreferences shrP=PreferenceManager.getDefaultSharedPreferences(this);
		shrP.registerOnSharedPreferenceChangeListener(this);
		// Update summary fields
		final String[] prefFields={pref_counterDJ, pref_counterPress, pref_counterShake, pref_counterVoice};
		for(String p: prefFields) {
			onSharedPreferenceChanged(shrP,p);
		}
	}
	@Override
	public void onPause() {
		SharedPreferences shrP=PreferenceManager.getDefaultSharedPreferences(this);
		shrP.unregisterOnSharedPreferenceChangeListener(this);
		super.onPause();
	}
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if(pref_counterDJ.equals(key) || pref_counterPress.equals(key) ||
				pref_counterShake.equals(key) || pref_counterVoice.equals(key)) {
			NumberFormat nf=NumberFormat.getInstance();
			Preference p=findPreference(key);
			p.setSummary(nf.format(sharedPreferences.getLong(key,0l)));
		}
	}
	
}

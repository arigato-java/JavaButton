package com.example.javabutton;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

public class SettingsActivity extends PreferenceActivity {
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
}

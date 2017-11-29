package com.example.javabutton

import android.content.ClipDescription
import android.os.Bundle
import android.preference.PreferenceManager
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ShareActionProvider

class MainActivity : JavaRecognitionActivity(), OnSharedPreferenceChangeListener {
    private var counterPress: Long = 0
    private var shareIntent: Intent? = null
    private var meigen: Array<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
    }

    private fun loadPreferences(shrPrefs: SharedPreferences) {
        val density = resources.displayMetrics.density
        var x: Float
        var y: Float
        val z: Float
        val w: Float
        val v_min: Float
        val v_max: Float
        try {
            x = java.lang.Float.valueOf(shrPrefs.getString("pref_djJava_min", "2000"))!!
            y = java.lang.Float.valueOf(shrPrefs.getString("pref_djJava_max", "5000"))!!
        } catch (e: Exception) {
            x = 2000f
            y = 5000f
        }

        z = Math.max(1f, Math.min(x, y))
        v_min = density * z
        w = Math.max(v_min + 1f, Math.max(x, y))
        v_max = density * w
        setGestureSpeedRange(v_min,v_max)

        try {
            x = java.lang.Float.valueOf(shrPrefs.getString("pref_shakeJava_min", "600"))!!
            y = java.lang.Float.valueOf(shrPrefs.getString("pref_shakeJava_max", "1000"))!!
        } catch (e: Exception) {
            x = 600f
            y = 1000f
        }

        JAVA_MIN_ACCEL = Math.max(1f, Math.min(x, y))
        JAVA_MAX_ACCEL = Math.max(JAVA_MIN_ACCEL + 1f, Math.max(x, y))

        enableLucky = shrPrefs.getBoolean("pref_lucky_enable", false)
        enableShakeModulation = shrPrefs.getBoolean("pref_shakeJava_modulation_enable", true)
    }

    private fun resetCounters() {
        counterPress = 0L
        counterDJ = 0L
        counterShake = 0L
        counterVoice = 0L
    }

    override fun onResume() {
        super.onResume()
        resetCounters()
        loadPreferences(PreferenceManager.getDefaultSharedPreferences(this))
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this)
        saveCounters()
        super.onPause()
    }

    private fun saveCounters() {
        val shrPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        val e = shrPrefs.edit()
        val oldcounterPress = shrPrefs.getLong(SettingsActivity.pref_counterPress, 0L)
        val oldcounterDJ = shrPrefs.getLong(SettingsActivity.pref_counterDJ, 0L)
        val oldcounterShake = shrPrefs.getLong(SettingsActivity.pref_counterShake, 0L)
        val oldcounterVoice = shrPrefs.getLong(SettingsActivity.pref_counterVoice, 0L)
        // prevent long overflow and roll-over
        val newCounterPress = Math.max(oldcounterPress, oldcounterPress + counterPress)
        val newCounterDJ = Math.max(oldcounterDJ, oldcounterDJ + counterDJ)
        val newCounterShake = Math.max(oldcounterShake, oldcounterShake + counterShake)
        val newCounterVoice = Math.max(oldcounterVoice, oldcounterVoice + counterVoice)
        e.putLong(SettingsActivity.pref_counterShake, newCounterShake)
        e.putLong(SettingsActivity.pref_counterDJ, newCounterDJ)
        e.putLong(SettingsActivity.pref_counterPress, newCounterPress)
        e.putLong(SettingsActivity.pref_counterVoice, newCounterVoice)
        e.commit()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        setupShareAction(menu)
        if (!isSpeechRecognizerAvailable()) {
            val micAction = menu.findItem(R.id.action_mic)
            micAction.isVisible = false
        }
        return true
    }

    private fun setupShareAction(menu: Menu): Boolean {
        val shareItem = menu.findItem(R.id.menu_item_share)
        val sap = shareItem.actionProvider as ShareActionProvider
        shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent!!.type = ClipDescription.MIMETYPE_TEXT_PLAIN
        sap.setShareIntent(shareIntent)
        meigen = resources.getStringArray(R.array.meigen)
        refreshMeigen()
        return true
    }

    private fun refreshMeigen() {
        shareIntent?.removeExtra(Intent.EXTRA_TEXT)
        shareIntent?.putExtra(Intent.EXTRA_TEXT, meigen!![random.nextInt(meigen!!.size)])
    }

    override fun onMenuItemSelected(featureId: Int, item: MenuItem): Boolean {
        val itemId = item.itemId
        if (itemId == R.id.action_settings) {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        } else if (itemId == R.id.action_mic) {
            startSpeechRecognition()
        } else {
            return super.onMenuItemSelected(featureId, item)
        }
        return true
    }

    fun JavaButtonClick(v: View) {
        playJava(1f)
        counterPress++
        refreshMeigen()
    }


    override fun onSharedPreferenceChanged(shrP: SharedPreferences, arg1: String) {
        loadPreferences(shrP)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_SPACE ||
                    keyCode == KeyEvent.KEYCODE_BUTTON_A ||
                    keyCode == KeyEvent.KEYCODE_BUTTON_L1 ||
                    keyCode == KeyEvent.KEYCODE_BUTTON_L2 ||
                    keyCode == KeyEvent.KEYCODE_BUTTON_R1 ||
                    keyCode == KeyEvent.KEYCODE_BUTTON_R2) {
                counterPress++
                playJava(1f)
                return true
            } else {
                val pitch = JavaPiano.JavaPianoCalcCode(keyCode)
                if (pitch > 0f) {
                    playJava(pitch)
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}

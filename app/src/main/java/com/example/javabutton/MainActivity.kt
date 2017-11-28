package com.example.javabutton

import android.content.ClipDescription
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.speech.RecognizerIntent
import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.content.pm.ResolveInfo
import android.view.GestureDetector
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.ShareActionProvider
import android.widget.Toast
//import android.widget.Toast;
import android.media.AudioManager
import android.media.SoundPool

// Amazon GameCircle
import com.amazon.ags.api.*
import java.util.*

class MainActivity : Activity(), SensorEventListener, OnSharedPreferenceChangeListener {
    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null
    protected var JAVA_MIN_ACCEL = 600f
    protected var JAVA_MAX_ACCEL = 1000f
    private var javaPool: SoundPool? = null
    private val maxJavaSounds = 4
    private var javaSoundId: Int = 0
    private var eyepadSoundId: Int = 0
    private var evenShake = true
    private var gDetector: GestureDetector? = null
    private var gestureListener: JavaGestureListener? = null
    private var enableLucky = false
    private val random = java.util.Random()
    private var enableShakeModulation = true
    private var counterDJ: Long = 0
    private var counterPress: Long = 0
    private var counterShake: Long = 0
    private var counterVoice: Long = 0
    private var shareIntent: Intent? = null
    private var meigen: Array<String>? = null
    private val isSpeechRecognizerAvailable: Boolean
        get() {
            try {
                val recognitionIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                val activities = packageManager.queryIntentActivities(recognitionIntent, 0)
                if (activities.size > 0) {
                    return true
                }
            } catch (e: Exception) {
                return false
            }

            return false
        }

    // Amazon GameCircle Start
    private val agsCallback=object:AmazonGamesCallback{
        override fun onServiceNotReady(p0: AmazonGamesStatus?) {

        }
        override fun onServiceReady(p0: AmazonGamesClient?) {
            agsClient=p0
        }
    }
    private val agsGameFeatures= EnumSet.of(
            AmazonGamesFeature.Achievements,
            AmazonGamesFeature.Leaderboards,
            AmazonGamesFeature.Whispersync
    )
    private var agsClient: AmazonGamesClient? = null
    // Amazon GameCircle End

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        javaPool = SoundPool(maxJavaSounds, AudioManager.STREAM_MUSIC, 0)
        javaSoundId = javaPool!!.load(this, R.raw.java22, 1)
        eyepadSoundId = javaPool!!.load(this, R.raw.eyepad, 1)

        gestureListener = JavaGestureListener(this)
        gDetector = GestureDetector(this, gestureListener)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

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
        gestureListener!!.setMinMax(v_min, v_max)

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
        // Initialize Amazon GameCircle
        AmazonGamesClient.initialize(this,agsCallback,agsGameFeatures)

        resetCounters()
        sensorManager!!.registerListener(this, accelerometer, SensorManager.SENSOR_STATUS_ACCURACY_LOW)
        loadPreferences(PreferenceManager.getDefaultSharedPreferences(this))
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        sensorManager!!.unregisterListener(this)
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this)
        saveCounters()
        super.onPause()
        AmazonGamesClient.release()
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
        javaPool!!.release()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        if (Build.VERSION.SDK_INT >= 14) {
            setupShareAction(menu)
        }
        if (!isSpeechRecognizerAvailable) {
            val micAction = menu.findItem(R.id.action_mic)
            micAction.isVisible = false
        }
        return true
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
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
        shareIntent!!.removeExtra(Intent.EXTRA_TEXT)
        shareIntent!!.putExtra(Intent.EXTRA_TEXT, meigen!![random.nextInt(meigen!!.size)])
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

    override fun onSensorChanged(event: SensorEvent) {
        val x: Float
        val y: Float
        val z: Float
        x = event.values[0]
        y = event.values[1]
        z = event.values[2]

        var acceleration = x * x + y * y + z * z
        if (acceleration > JAVA_MIN_ACCEL) {
            if (evenShake) {
                val pitch: Float
                acceleration = Math.min(acceleration, JAVA_MAX_ACCEL)
                if (enableShakeModulation) {
                    pitch = 0.8f + 1.2f * (acceleration - JAVA_MIN_ACCEL) / (JAVA_MAX_ACCEL - JAVA_MIN_ACCEL)
                } else {
                    pitch = 1f
                }
                playJava(pitch)
                counterShake++
            }
            evenShake = !evenShake
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gDetector!!.onTouchEvent(event)
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    override fun onSharedPreferenceChanged(shrP: SharedPreferences, arg1: String) {
        loadPreferences(shrP)
    }

    fun playJava(pitch: Float) {
        val sndId: Int
        if (enableLucky) {
            val r = random.nextInt(32)
            sndId = if (r == 0) eyepadSoundId else javaSoundId
        } else {
            sndId = javaSoundId
        }
        javaPool!!.play(sndId, 1.0f, 1.0f, 1, 0, pitch)
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

    private fun startSpeechRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH)
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (matches.size > 0) {
                try {
                    val javaStr = matches[0].toLowerCase(java.util.Locale.US)
                    if ("java" == javaStr) {
                        counterVoice++
                        playJava(1f)
                        val shrP = PreferenceManager.getDefaultSharedPreferences(this)
                        val e = shrP.edit()
                        e.putLong(SettingsActivity.pref_counterVoice, counterVoice)
                        e.commit()
                    } else {
                        Toast.makeText(this, javaStr, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: NullPointerException) {
                }

            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun incrementDJCounter() {
        counterDJ++
    }

    companion object {
        private val VOICE_RECOGNITION_REQUEST_CODE = 0x06a103
    }
}

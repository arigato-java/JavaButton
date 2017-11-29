package com.example.javabutton

import android.app.Activity
import android.content.Intent
import android.preference.PreferenceManager
import android.speech.RecognizerIntent
import android.widget.Toast

open class JavaRecognitionActivity:JavaDJActivity() {
    var counterVoice: Long = 0
    fun isSpeechRecognizerAvailable():Boolean {
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
    fun startSpeechRecognition() {
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

    companion object {
        private val VOICE_RECOGNITION_REQUEST_CODE = 0x06a103
    }

}

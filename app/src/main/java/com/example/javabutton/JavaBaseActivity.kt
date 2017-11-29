package com.example.javabutton

import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle

open class JavaBaseActivity: AgsActivity() {
    private var javaPool: SoundPool? =null
    private val numJavaChannels = 4
    private var javaSoundId = -1
    private var eyepadSoundId = -1
    var enableLucky = false
    val random = java.util.Random()

    override fun onResume(){
        super.onResume()
        // prepare a sound pool
        val audioAttrsBuilder=AudioAttributes.Builder()
        audioAttrsBuilder.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        audioAttrsBuilder.setUsage(AudioAttributes.USAGE_GAME)
        val soundPoolBuilder=SoundPool.Builder()
        soundPoolBuilder.setMaxStreams(numJavaChannels)
        soundPoolBuilder.setAudioAttributes(audioAttrsBuilder.build())
        javaPool=soundPoolBuilder.build()
        javaSoundId=javaPool?.load(this, R.raw.java22, 1) ?: -1
        eyepadSoundId=javaPool?.load(this,R.raw.eyepad,1) ?: -1
    }
    override fun onPause(){
        javaPool?.release()
        super.onPause()
    }
    fun playJava(pitch: Float) {
        val sndId: Int
        if (enableLucky) {
            val r = random.nextInt(32)
            sndId = if (r == 0) eyepadSoundId else javaSoundId
        } else {
            sndId = javaSoundId
        }
        javaPool?.play(sndId, 1.0f, 1.0f, 1, 0, pitch)
    }
}

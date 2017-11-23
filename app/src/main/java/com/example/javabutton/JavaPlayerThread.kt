package com.example.javabutton

import java.io.IOException
import java.util.concurrent.LinkedBlockingQueue

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log

class JavaPlayerThread(private val jUri: Uri) : Thread(), MediaPlayer.OnCompletionListener {
    private var mPlayer: MediaPlayer? = null
    private val cmdQ: LinkedBlockingQueue<Int>
    private var nextContext: Context? = null
    private val nextContextLock = Any()
    private val JAVA_STOP = 0
    private val JAVA_PLAY = 1
    private val JAVA_PASS = 0xff

    init {
        cmdQ = LinkedBlockingQueue()
    }

    override fun run() {
        while (true) {
            var cmdVal: Int
            try {
                val cmd: Int?
                cmd = cmdQ.take() // This blocks when queue is empty
                cmdVal = cmd!!.toInt()
            } catch (e: InterruptedException) {
                cmdVal = JAVA_PASS
            }

            if (cmdVal == JAVA_STOP) {
                try {
                    if (mPlayer != null && mPlayer!!.isPlaying) {
                        mPlayer!!.stop()
                        mPlayer!!.release()
                    }
                } catch (e: IllegalStateException) {
                }
                // already released
            }
            if (cmdVal == JAVA_PLAY) {
                val mPlayer2: MediaPlayer
                val ctx: Context=synchronized(nextContextLock){nextContext!!}
                mPlayer2 = MediaPlayer()
                mPlayer2.setAudioStreamType(AudioManager.STREAM_MUSIC)
                mPlayer2.isLooping = false
                mPlayer2.setOnCompletionListener(this)
                try {
                    mPlayer2.setDataSource(ctx, jUri)
                    mPlayer2.prepare()
                } catch (e: IOException) {
                    Log.w("JavaPlayer", "mPlayer change datasource failed.")
                }

                // stop mPlayer
                try {
                    if (mPlayer != null && mPlayer!!.isPlaying) {
                        mPlayer!!.stop()
                        mPlayer!!.release()
                    }
                } catch (e: IllegalStateException) {
                }
                // already released
                mPlayer2.start()
                mPlayer = mPlayer2
            }
        }
    }

    fun enqueuePlay() {
        cmdQ.add(Integer.valueOf(JAVA_PLAY))
    }

    fun enqueueStop() {
        cmdQ.add(Integer.valueOf(JAVA_STOP))
    }

    fun enqueueChangeContext(ctx: Context) {
        synchronized(nextContextLock) {
            nextContext = ctx
        }
    }

    override fun onCompletion(mp: MediaPlayer) {
        mp.release()
    }
}

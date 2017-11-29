package com.example.javabutton

import android.app.Activity
// Amazon GameCircle
import com.amazon.ags.api.*
import java.util.*

// Activity with Amazon GameCircle-related procedures
open class AgsActivity: Activity() {
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

    override fun onResume() {
        super.onResume()
        // Initialize Amazon GameCircle
        AmazonGamesClient.initialize(this,agsCallback,agsGameFeatures)
    }
    override fun onPause() {
        super.onPause()
        AmazonGamesClient.release()
    }
}

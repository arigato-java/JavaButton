package com.example.javabutton

import android.app.Activity
import android.view.View
// Amazon GameCircle
import com.amazon.ags.api.*
import com.amazon.ags.api.achievements.AchievementsClient
import java.util.*

// Activity with Amazon GameCircle-related procedures
open class AgsActivity: Activity() {
    private val LeaderboardId="javabo_press1"
    val AchievementName_JavaPress1="javabutton1"

    private val agsCallback=object:AmazonGamesCallback{
        override fun onServiceNotReady(p0: AmazonGamesStatus?) {

        }
        override fun onServiceReady(p0: AmazonGamesClient?) {
            agsClient=p0
            acClient=agsClient?.achievementsClient
        }
    }
    private val agsGameFeatures= EnumSet.of(
            AmazonGamesFeature.Achievements,
            AmazonGamesFeature.Leaderboards,
            AmazonGamesFeature.Whispersync
    )
    private var agsClient: AmazonGamesClient? = null
    private var acClient: AchievementsClient? = null

    override fun onResume() {
        super.onResume()
        // Initialize Amazon GameCircle
        AmazonGamesClient.initialize(this,agsCallback,agsGameFeatures)
    }
    override fun onPause() {
        super.onPause()
        AmazonGamesClient.release()
    }

    // Leaderboards
    open fun openLeaderboards(v: View){
        val lbClient=agsClient?.leaderboardsClient
        lbClient?.showLeaderboardsOverlay()
    }
    fun uploadScore(score: Long){
        val lbClient=agsClient?.leaderboardsClient
        lbClient?.submitScore(LeaderboardId,score)
    }

    // Achievements
    open fun openAchievements(v:View){
        acClient?.showAchievementsOverlay()
    }
    open fun unlockAchievement(achievementName: String, progress:Float=100.0f){
        acClient?.updateProgress(achievementName,progress)
    }
}

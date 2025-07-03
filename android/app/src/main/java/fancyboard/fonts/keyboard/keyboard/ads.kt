package fancyboard.fonts.keyboard.keyboard

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.edit
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import fancyboard.fonts.keyboard.data.lockedFonts
import java.util.Date

private var interstitialAd: InterstitialAd? = null
private var interstitialAdLoadedAt = -1L
var interstitialAdLoadStatus = "none"

fun loadInterstitialAdIfStale(context: Context) {
    if (System.currentTimeMillis() - interstitialAdLoadedAt > 60 * 60 * 1000) {
        interstitialAd = null
        loadInterstitialAd(context)
    }
}

fun loadInterstitialAd(context: Context, onLoad: (() -> Unit)? = null) {
    val adRequest = AdRequest.Builder().build()
    interstitialAdLoadStatus = "loading"

    val adUnitId =
        if (context.isDebug()) "ca-app-pub-3940256099942544/1033173712"
        else "ca-app-pub-1558100192494221/7529377288"

    InterstitialAd.load(context, adUnitId, adRequest, object : InterstitialAdLoadCallback() {
        override fun onAdLoaded(ad: InterstitialAd) {
            // Called when the ad is successfully loaded
            interstitialAd = ad
            Log.d("AdMob", "Interstitial ad loaded")
            interstitialAdLoadedAt = System.currentTimeMillis()
            interstitialAdLoadStatus = "loaded"
            onLoad?.invoke()
        }

        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
            interstitialAd = null
            interstitialAdLoadStatus = "failed"
            Log.d("AdMob", "Failed to load interstitial ad: ${loadAdError.message}")
        }
    })
}

fun showInterstitialAd(context: Activity, onViewComplete: () -> Unit) {
    // Don't retain for more than 1 hours
    if (System.currentTimeMillis() - interstitialAdLoadedAt > 60 * 60 * 1000 || interstitialAd == null) {
        onViewComplete() // Don't annoy people
        return
    }

    val ad = interstitialAd ?: return
    interstitialAd = null
    interstitialAdLoadedAt = -1
    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
        override fun onAdDismissedFullScreenContent() {
            super.onAdDismissedFullScreenContent()
            onViewComplete()
        }

        override fun onAdFailedToShowFullScreenContent(p0: AdError) {
            super.onAdFailedToShowFullScreenContent(p0)
            onViewComplete()
        }
    }
    ad.show(context)
}

private var rewardedAd: RewardedAd? = null
private var rewardedAdLoadedAt = -1L
var rewardedAdLoadStatus = "none"

fun loadRewardedAdIfStale(context: Context){
    if(System.currentTimeMillis()-rewardedAdLoadedAt>=60*60*1000){
        rewardedAd=null
        loadRewardedAd(context)
    }
}

fun loadRewardedAd(context: Context) {
    rewardedAdLoadStatus = "loading"
    val adRequest = AdRequest.Builder().build()

    val adId =
        if (context.isDebug()) "ca-app-pub-3940256099942544/5224354917"
        else "ca-app-pub-1558100192494221/5081562948"

    RewardedAd.load(
        context,
        adId,
        adRequest,
        object : RewardedAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedAd) {
                Log.d("AdMob", "Rewarded ad was loaded.")
                rewardedAd = ad
                rewardedAdLoadStatus = "loaded"
                rewardedAdLoadedAt = System.currentTimeMillis()
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.e("AdMob", "Failed to load rewarded ad: ${adError.message}")
                rewardedAdLoadStatus = "failed"
                rewardedAd = null
            }
        }
    )
}

fun showRewardedAd(activity: Activity, onViewComplete: () -> Unit) {
    if (System.currentTimeMillis() - rewardedAdLoadedAt > 60 * 60 * 1000 || rewardedAd == null) {
        onViewComplete()
        return
    }

    val ad = rewardedAd ?: return
    rewardedAd = null
    rewardedAdLoadedAt = -1

    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
        override fun onAdDismissedFullScreenContent() {
            super.onAdDismissedFullScreenContent()
            onViewComplete()

        }

        override fun onAdFailedToShowFullScreenContent(p0: AdError) {
            super.onAdFailedToShowFullScreenContent(p0)
            onViewComplete()
        }
    }

    ad.show(activity) {}
}

fun shouldShowUnlockAd(context: Context): Boolean {
    val preferences = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
    var lastAdWatchedAt = preferences.getLong("lastAdWatchedAt", -1)
    Log.d(
        "shouldShowUnlockAd",
        "lastAdWatchedAt: ${Date(lastAdWatchedAt)}  will show after ${1 - (System.currentTimeMillis() - lastAdWatchedAt) / 86_400_000f}days"
    )
    if (lastAdWatchedAt == -1L) {
        // First use
        // Show ad after 5 minutes of first use
        preferences.edit {
            putLong(
                "lastAdWatchedAt",
                System.currentTimeMillis() - 86_400_000 + 5 * 60_000
            )
        }
        return false
    }

    if (System.currentTimeMillis() - lastAdWatchedAt >= 86_400_000) {
        // Don't load ad here because IME service is a different process
        // loadInterstitialAd(context)
        return true
    }

    return false
}

fun unlockKeyboardFor(context: Context, hours: Int) {
    val milli = hours * 60 * 60 * 1000
    val prefs = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
    prefs.edit(commit = true) {
        putLong("lastAdWatchedAt", System.currentTimeMillis() - 86_400_000 + milli)
    }
}

fun hasUnlockedFont(context: Context, name: String): Boolean {
    if (lockedFonts.contains(name)) {
        val prefs = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        if(prefs.getBoolean("font__unlocks__$name", false)==false){
            // Don't load ad here because IME service is a different process
            // loadRewardedAd(context)
            return false
        }
    }

    return true
}

fun unlockFont(context: Context, name: String){
    val prefs = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
    prefs.edit(commit = true) { putBoolean("font__unlocks__$name", true) }
    val intent = Intent(KeyboardService.ACTION_KEYBOARD_BROADCAST).apply {
        putExtra("type", "font_unlocked")
        putExtra("font", name)
    }
    context.sendBroadcast(intent)
}
package fancyboard.fonts.keyboard

import android.app.Activity
import android.app.Application
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.appopen.AppOpenAd
import fancyboard.fonts.keyboard.keyboard.isDebug
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyApplication : Application() {
    private var appOpenAd: AppOpenAd? = null
    override fun onCreate() {
        super.onCreate()
        Log.d("MyApplication", "Application created!!")
    }

    fun loadAd(onComplete: () -> Unit) {
        if (appOpenAd != null) {
            onComplete()
            return
        }

        val adId = if (isDebug()) "ca-app-pub-3940256099942544/9257395921"
        else "ca-app-pub-1558100192494221/7026868309"
        val request = AdRequest.Builder().build()
        AppOpenAd.load(
            this, adId, request,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    Log.d("MyApplication", "App OpenAd has been loaded")
                    appOpenAd = ad
                    onComplete()
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.d("MyApplication", loadAdError.message)
                }
            })
    }

    fun showAdIfAvailable(activity: Activity, onComplete: () -> Unit) {
        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d("MyApplication", "Ad dismissed fullscreen content.")
                appOpenAd = null
                onComplete()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.d("MyApplication", adError.message)
                appOpenAd = null
                onComplete()
            }

            override fun onAdShowedFullScreenContent() {
                // Called when fullscreen content is shown.
                Log.d("MyApplication", "Ad showed fullscreen content.")
            }
        }

        appOpenAd?.show(activity)
    }
}
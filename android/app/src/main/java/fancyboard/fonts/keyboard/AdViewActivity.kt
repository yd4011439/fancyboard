package fancyboard.fonts.keyboard

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import fancyboard.fonts.keyboard.keyboard.interstitialAdLoadStatus
import fancyboard.fonts.keyboard.keyboard.loadInterstitialAdIfStale
import fancyboard.fonts.keyboard.keyboard.loadRewardedAdIfStale
import fancyboard.fonts.keyboard.keyboard.rewardedAdLoadStatus
import fancyboard.fonts.keyboard.keyboard.showInterstitialAd
import fancyboard.fonts.keyboard.keyboard.showRewardedAd
import fancyboard.fonts.keyboard.keyboard.unlockFont
import fancyboard.fonts.keyboard.keyboard.unlockKeyboardFor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AdViewActivity : ComponentActivity() {
    lateinit var message: TextView
    lateinit var exitButton: Button
    lateinit var progress: View
    lateinit var loadingAd: TextView
    var mFontName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ad_view_activity)

        message = findViewById(R.id.message)
        exitButton = findViewById(R.id.exit_btn)
        progress = findViewById(R.id.progress)
        loadingAd = findViewById(R.id.loading_ad)

        val fontName = intent.getStringExtra("font")

        if (fontName == null) {
            // Show 24 hrs unlock ad
            CoroutineScope(Dispatchers.Main).launch {
                loadInterstitialAdIfStale(this@AdViewActivity)
                while (interstitialAdLoadStatus == "loading") {
                    delay(1000)
                }

                if (interstitialAdLoadStatus == "loaded") {
                    showInterstitialAd(this@AdViewActivity) {
                        unlockKeyboardFor(context = this@AdViewActivity, 24)
                        finish()
                    }
                } else {
                    failedLoadingInterstitialAd()
                }
            }
        } else {
            mFontName = fontName
            CoroutineScope(Dispatchers.Main).launch {
                loadRewardedAdIfStale(this@AdViewActivity)
                while (rewardedAdLoadStatus == "loading") {
                    delay(1000)
                }

                if (rewardedAdLoadStatus == "loaded") {
                    showRewardedAd(this@AdViewActivity) {
                        unlockFont(this@AdViewActivity, fontName)
                        finish()
                    }
                } else {
                    failedLoadingRewardedAd()
                }
            }
        }
    }

    fun failedLoadingInterstitialAd() {
        unlockKeyboardFor(context = this@AdViewActivity, 1)
        loadingAd.visibility = View.GONE
        progress.visibility = View.GONE
        exitButton.visibility = View.VISIBLE
        message.visibility = View.VISIBLE
        exitButton.setOnClickListener {
            finish()
        }
    }

    fun failedLoadingRewardedAd() {
        loadingAd.visibility = View.GONE
        progress.visibility = View.GONE
        exitButton.visibility = View.VISIBLE
        message.visibility = View.VISIBLE
        message.text =
            "There was an error while loading the ad. You can unlock the $mFontName font by watching ad later."
        exitButton.setOnClickListener {
            finish()
        }
    }
}
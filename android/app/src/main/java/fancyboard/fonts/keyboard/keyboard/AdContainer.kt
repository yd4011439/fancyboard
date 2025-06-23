package fancyboard.fonts.keyboard.keyboard

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("ViewConstructor")
class AdContainer(context: Context, shouldLoadAds: Boolean=true): FrameLayout(context) {
    val adHeightDp:Int
    init {
        val adId =
            if(context.isDebug()) "ca-app-pub-3940256099942544/9214589741"
            else "ca-app-pub-1558100192494221/5107852792"
        val adSize = context.fullWidthBannerSize()
        adHeightDp = adSize.height

        Log.d("AdContainer", "AdSize: ${adSize.width}x${adSize.height}")

        if(shouldLoadAds){
            val adView = AdView(context)
            adView.setAdSize(adSize)
            adView.adUnitId = adId
            layoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, context.dpToPx(adSize.height))
            addView(adView)
            MobileAds.initialize(context) {
                CoroutineScope(Dispatchers.Main).launch{
                    val adRequest = AdRequest.Builder().build()
                    adView.loadAd(adRequest)
                }
            }
        }
    }
}
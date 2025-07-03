package fancyboard.fonts.keyboard.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun AdmobBanner(modifier: Modifier = Modifier, adId: String, adSize: AdSize? = null) {
    val context = LocalContext.current
    val currentScreenWidth = LocalConfiguration.current.screenWidthDp

    AndroidView(
        modifier = modifier,
        factory = { context1 ->
            AdView(context1).apply {
                val mAdSize =
                    adSize ?: AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                        context,
                        currentScreenWidth
                    )
                adUnitId = adId
                setAdSize(mAdSize)
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}
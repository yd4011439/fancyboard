package fancyboard.fonts.keyboard.ui.component

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.ViewGroup.LayoutParams
import android.widget.FrameLayout
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import fancyboard.fonts.keyboard.CreateThemeScreen
import fancyboard.fonts.keyboard.keyboard.KeyboardTheme
import fancyboard.fonts.keyboard.keyboard.KeyboardView
import fancyboard.fonts.keyboard.keyboard.getAllThemes
import fancyboard.fonts.keyboard.keyboard.getDefaultTheme
import fancyboard.fonts.keyboard.keyboard.setDefaultTheme
import fancyboard.fonts.keyboard.R
import fancyboard.fonts.keyboard.TestKeyboardScreen
import fancyboard.fonts.keyboard.keyboard.isDebug
import fancyboard.fonts.keyboard.rateApp
import fancyboard.fonts.keyboard.shareApp

private var interstitialAd: InterstitialAd? = null
private fun loadInterstitialAd(context: Context) {
    val adRequest = AdRequest.Builder().build()

    val adId =
        if (context.isDebug()) "ca-app-pub-3940256099942544/1033173712"
        else "ca-app-pub-1558100192494221/2139688926"

    InterstitialAd.load(context, adId, adRequest, object : InterstitialAdLoadCallback() {
        override fun onAdLoaded(ad: InterstitialAd) {
            // Called when the ad is successfully loaded
            interstitialAd = ad
            Log.d("AdMob", "Interstitial ad loaded")
        }

        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
            interstitialAd = null
            Log.d("AdMob", "Failed to load interstitial ad: ${loadAdError.message}")
        }
    })
}

@Composable
fun KeyboardViewPreview(
    settings: KeyboardTheme,
    modifier: Modifier = Modifier,
    scale: Float = 0.5f
) {
    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val density = LocalDensity.current.density
    val width = (density * screenWidthDp - 16).toInt()
    val widthDp = width / density
    val heightDp = 308f / screenWidthDp * widthDp
    val height = (density * heightDp).toInt()

    AndroidView(
        {
            FrameLayout(it).apply {
                addView(KeyboardView(it, settings, false, true).apply {
                    layoutParams = LayoutParams(width, height)
                    pivotX = 0f
                    pivotY = 0f
                    scaleX = scale
                    scaleY = scale
                })
            }
        },
        update = { (it.getChildAt(0) as KeyboardView).applyTheme(settings) },
        modifier = modifier
            .padding(8.dp)
            .width((widthDp * scale).dp)
            .height((heightDp * scale).dp)
            .clip(
                RoundedCornerShape(8.dp)
            )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeView(navHostController: NavHostController) {
    val context = LocalContext.current
    val themes by remember { mutableStateOf(getAllThemes(context)) }
    var themeToApply by remember { mutableStateOf<KeyboardTheme?>(null) }
    var defaultTheme by remember { mutableStateOf(getDefaultTheme(context)) }
    val isKeyboardDefault = rememberIsKeyboardDefault()
    val _themeToApply = themeToApply

    LaunchedEffect(Unit) {
        loadInterstitialAd(context)
    }

    if (_themeToApply != null) {
        ModalBottomSheet({ themeToApply = null }) {
            Column(Modifier.height(440.dp)) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Theme Preview", style = MaterialTheme.typography.titleMedium)
                    FilledTonalButton({
                        interstitialAd?.show(context as Activity)
                        interstitialAd = null
                        loadInterstitialAd(context)

                        defaultTheme = _themeToApply
                        setDefaultTheme(context, _themeToApply)
                        themeToApply = null
                        Toast.makeText(context, "Theme has been applied", Toast.LENGTH_SHORT).show()
                    }) {
                        Text("Apply (Ad)")
                    }
                }
                KeyboardViewPreview(_themeToApply, scale = 1f)
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                { Text("𝔽𝕒𝕟𝕔𝕪𝕓𝕠𝕒𝕣𝕕") },
                actions = {
                    TextButton({ navHostController.navigate(TestKeyboardScreen) }) {
                        Text("Test")
                    }
                    if (!isKeyboardDefault) TextButton({ askDefault(context) }) { Text("Default") }
                    IconButton({
                        defaultTheme =
                            defaultTheme.copy(enableVibration = defaultTheme.enableVibration != true)
                        Toast.makeText(
                            context,
                            "${if (defaultTheme.enableVibration == true) "Enabled" else "Disabled"} vibration",
                            Toast.LENGTH_SHORT
                        ).show()
                        setDefaultTheme(context, defaultTheme)
                    }) {
                        if (defaultTheme.enableVibration == true)
                            Icon(
                                painter = painterResource(R.drawable.baseline_vibration_24),
                                contentDescription = "Vibration Enabled"
                            )
                        else
                            Icon(
                                painter = painterResource(R.drawable.vibrate_off),
                                contentDescription = "Vibration Disabled"
                            )
                    }
                },
            )
        },
        bottomBar = {
            Column(Modifier.fillMaxWidth()) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row {
                        TextButton(onClick = { rateApp(context) }) {
                            Icon(
                                imageVector = Icons.Default.FavoriteBorder,
                                contentDescription = "Favourite icon"
                            )
                            Text("Rate", Modifier.padding(start = 8.dp))
                        }

                        TextButton(onClick = { shareApp(context) }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share icon"
                            )
                            Text("Share", Modifier.padding(start = 8.dp))
                        }
                    }

                    ElevatedButton({ navHostController.navigate(CreateThemeScreen()) }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Icon")
                        Text("Create Theme")
                    }
                }
                AdmobBanner(
                    adId =
                        if (context.isDebug()) "ca-app-pub-3940256099942544/9214589741"
                        else "ca-app-pub-1558100192494221/7341443511"
                )
            }
        }
    ) { innerPadding ->
        AskEnable()
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            themes.keys.forEach { key ->
                Text(
                    key,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Row(
                    Modifier
                        .padding(bottom = 16.dp)
                        .horizontalScroll(rememberScrollState())
                ) {
                    themes[key]!!.forEach { settings ->
                        Box(Modifier.clickable {
                            themeToApply =
                                settings.copy(enableVibration = defaultTheme.enableVibration)
                        }) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                KeyboardViewPreview(settings)
                                Text(settings.name, style = MaterialTheme.typography.labelMedium)
                            }

                            if (settings.id != null) {
                                IconButton(
                                    { navHostController.navigate(CreateThemeScreen(settings.id)) },
                                    Modifier
                                        .padding(vertical = 32.dp, horizontal = 16.dp)
                                        .clip(RoundedCornerShape(100.dp))
                                        .background(
                                            MaterialTheme.colorScheme.background
                                        )
                                        .align(Alignment.BottomEnd)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit"
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(120.dp))
        }
    }
}
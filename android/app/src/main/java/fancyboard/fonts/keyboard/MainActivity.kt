package fancyboard.fonts.keyboard

import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.bumptech.glide.Glide
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import fancyboard.fonts.keyboard.MyApplication
import fancyboard.fonts.keyboard.keyboard.isDebug
import fancyboard.fonts.keyboard.ui.component.CreateTheme
import fancyboard.fonts.keyboard.ui.component.TestKeyboard
import fancyboard.fonts.keyboard.ui.component.ThemeView
import fancyboard.fonts.keyboard.ui.component.isKeyboardEnabled
import fancyboard.fonts.keyboard.ui.theme.FancyboardFontsKeyboardTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.util.Arrays

@Serializable
object TestKeyboardScreen

@Serializable
object HomeScreen

@Serializable
data class CreateThemeScreen(val themeId: String? = null)

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        if(isDebug()){
            RequestConfiguration.Builder().setTestDeviceIds(listOf("11C862C4A0E74BF9BF66BF0BDCD6A26E", "0CCF278DD41EAEE0BA03B91F3DFF40D4"))
        }

        MobileAds.initialize(this@MainActivity) {}

        enableEdgeToEdge()
        CoroutineScope(Dispatchers.Main).launch {
            delay(10000)
            askReview(this@MainActivity)
        }
        setContent {
            val navHostController = rememberNavController()
            var showLoadingAd by remember { mutableStateOf(false) }
            var showAd by remember { mutableStateOf(isKeyboardEnabled(this@MainActivity)) }

            FancyboardFontsKeyboardTheme {
                LaunchedEffect(Unit) {
                    if (isKeyboardEnabled(this@MainActivity)) {
                        (application as? MyApplication)?.loadAd({
                            if (showAd)
                                (application as? MyApplication)?.showAdIfAvailable(this@MainActivity) {}
                            showAd = false
                        }, {
                            showAd = false
                        })
                        delay(5000)
                        showLoadingAd = true
                        delay(5000)
                        showAd = false
                    }
                }

                Surface {
                    Box(
                        Modifier
                            .padding(
                                bottom = WindowInsets.safeDrawing.asPaddingValues()
                                    .calculateBottomPadding()
                            )
                            .fillMaxSize()
                    ) {
                        NavHost(navHostController, startDestination = HomeScreen) {
                            composable<HomeScreen> {
                                ThemeView(navHostController)
                            }

                            composable<CreateThemeScreen> {
                                val data = it.toRoute<CreateThemeScreen>()
                                CreateTheme(navHostController, data.themeId)
                            }

                            composable<TestKeyboardScreen> {
                                TestKeyboard(navHostController)
                            }
                        }

                        if (showAd) {
                            Column(
                                Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.surface),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text("ᖴᗩᑎᑕYᗷOᗩᖇᗪ", style = MaterialTheme.typography.headlineLarge)
                                LinearProgressIndicator(Modifier.padding(horizontal = 24.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
package fancyboard.fonts.keyboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.google.android.gms.ads.MobileAds
import fancyboard.fonts.keyboard.MyApplication
import fancyboard.fonts.keyboard.ui.component.CreateTheme
import fancyboard.fonts.keyboard.ui.component.TestKeyboard
import fancyboard.fonts.keyboard.ui.component.ThemeView
import fancyboard.fonts.keyboard.ui.component.isKeyboardEnabled
import fancyboard.fonts.keyboard.ui.theme.FancyboardFontsKeyboardTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

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

        if (isKeyboardEnabled(this)) {
            CoroutineScope(Dispatchers.IO).launch {
                MobileAds.initialize(this@MainActivity) {
                    CoroutineScope(Dispatchers.Main).launch {
                        (application as? MyApplication)?.loadAd {
                            (application as? MyApplication)?.showAdIfAvailable(this@MainActivity) {}
                        }
                    }
                }
            }
        }

        enableEdgeToEdge()
        setContent {
            val navHostController = rememberNavController()
            FancyboardFontsKeyboardTheme {
                Surface {
                    Box(
                        Modifier.padding(
                            bottom = WindowInsets.safeDrawing.asPaddingValues()
                                .calculateBottomPadding()
                        )
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
                    }
                }
            }
        }
    }
}
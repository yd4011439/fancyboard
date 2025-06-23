package fancyboard.fonts.keyboard.ui.component

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import fancyboard.fonts.keyboard.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

fun isKeyboardEnabled(context: Context): Boolean {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val enabledInputMethods = imm.enabledInputMethodList
    val keyboardService = "${context.packageName}.keyboard.KeyboardService"
    return enabledInputMethods.any{
        keyboardService == it.serviceName
    }
}

fun isKeyboardDefault(context: Context): Boolean {
    if(Build.VERSION.SDK_INT < 34){
        val defaultIme = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.DEFAULT_INPUT_METHOD
        )
        val keyboardService = "${context.packageName}/.keyboard.KeyboardService"
        return defaultIme == keyboardService
    }

    return false
}

fun openInputMethodSettings(context: Context) {
    val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS).apply {
        setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}

fun askDefault(context: Context) {
    val imeManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imeManager.showInputMethodPicker()
}

@Composable
fun rememberIsKeyboardDefault(
    intervalMillis: Long = 1000L,
): Boolean {
    val context = LocalContext.current
    var isKeyboardDefault by remember { mutableStateOf<Boolean>(isKeyboardDefault(context)) }

    DisposableEffect(Unit) {
        val job = CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                isKeyboardDefault = isKeyboardDefault(context)
                delay(intervalMillis)
            }
        }

        onDispose {
            job.cancel()
        }
    }

    return isKeyboardDefault
}

@Composable
fun AskEnable(forceDefault: Boolean = false) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var askDefault by remember { mutableStateOf(!isKeyboardEnabled(context) || (forceDefault && Build.VERSION.SDK_INT < 34)) }
    var isKeyboardEnabled by remember { mutableStateOf(isKeyboardEnabled(context)) }
    var isKeyboardDefault by remember { mutableStateOf(isKeyboardDefault(context)) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isKeyboardEnabled = isKeyboardEnabled(context)
                isKeyboardDefault = isKeyboardDefault(context)
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    if (!isKeyboardEnabled) {
        return AlertDialog(
            {},
            icon = {
                Icon(
                    painter = painterResource(R.drawable.outline_emoji_emotions_24),
                    contentDescription = "Emoji"
                )
            },
            confirmButton = { ElevatedButton({ openInputMethodSettings(context) }) { Text("Open Settings") } },
            title = { Text("Enable Keyboard") },
            text = { Text("When you press open settings, you will be shown a list of keyboard apps. You need to find \"Fancyboard\" in the list, enable it and come back to the this app.") },
        )
    }

    if (!isKeyboardDefault && askDefault) {
        AlertDialog(
            {},
            icon = {
                Icon(
                    painter = painterResource(R.drawable.outline_emoji_emotions_24),
                    contentDescription = "Emoji"
                )
            },
            confirmButton = {
                OutlinedButton({
                    askDefault(context)
                    askDefault = false
                }) { Text("Yes") }
            },
            dismissButton = { TextButton({ askDefault = false }) { Text("Cancel") } },
            title = { Text("Make Default") },
            text = { Text("Would you like to make \"Fancyboard\" your default one?") },
        )
    }
}

package fancyboard.fonts.keyboard.keyboard

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.res.ColorStateList
import android.graphics.Point
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.MotionEvent
import android.view.SoundEffectConstants
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputConnection
import com.google.android.gms.ads.AdSize
import fancyboard.fonts.keyboard.data.upsideDownString
import kotlin.math.abs
import android.util.Log

fun View.onTouchAction(
    theme: KeyboardTheme,
    onDown: ((view: View) -> Unit)? = null,
    onUp: ((view: View) -> Unit)? = null,
) {
    var moved = false
    var startX = -1f
    var startY = -1f
    var prevTintList: ColorStateList? = null
    this.setOnTouchListener { view, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                prevTintList = view.backgroundTintList
                view.isPressed = true
                onDown?.let {
                    it(view)
                    view.backgroundTintList = ColorStateList.valueOf(theme.keyPressedColor)
                }

                startX = event.x
                startY = event.y

                moved = false
                true
            }

            MotionEvent.ACTION_UP -> {
                Log.d("onTouchAction", "Up")

                if (moved){
                    return@setOnTouchListener false
                }

                if(onDown!=null) view.backgroundTintList = ColorStateList.valueOf(theme.keyColor)
                view.isPressed = false
                view.performClick()
                view.playSoundEffect(SoundEffectConstants.CLICK)

                if (theme.enableVibration == true) {
                    view.context.vibrate()
                }
                onUp?.let { it(view) }
                true
            }

            MotionEvent.ACTION_CANCEL -> {
                Log.d("onTouchAction", "Cancel")
                view.isPressed = false
                view.backgroundTintList = prevTintList
                true
            }

            MotionEvent.ACTION_MOVE -> {
                Log.d("onTouchAction", "Move")

                view.backgroundTintList = prevTintList
                moved = onDown == null && abs(event.x - startX) > 10
                false
            }

            else -> false
        }
    }
}

fun Context.fullWidthBannerSize(): AdSize {
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = windowManager.defaultDisplay
    val outMetrics = DisplayMetrics()
    display.getMetrics(outMetrics)

    val density = outMetrics.density

    var adWidthPixels = outMetrics.widthPixels

    val adWidth = (adWidthPixels / density).toInt()
    return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
}


fun View.getRelativeCoordinates(relativeTo: View): Point {
    val targetLocation = IntArray(2)
    val relativeLocation = IntArray(2)

    this.getLocationOnScreen(targetLocation)
    relativeTo.getLocationOnScreen(relativeLocation)

    val x = targetLocation[0] - relativeLocation[0]
    val y = targetLocation[1] - relativeLocation[1]

    return Point(x, y)
}


fun Context.vibrate(duration: Long = 40) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        (getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(duration)
    }
}

fun Context.dpToPx(dp: Int): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        resources.displayMetrics
    ).toInt()
}

fun Context.isDebug(): Boolean {
    return applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
}
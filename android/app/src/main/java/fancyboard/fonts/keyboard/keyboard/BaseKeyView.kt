package fancyboard.fonts.keyboard.keyboard

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.SoundEffectConstants
import android.view.View
import android.view.inputmethod.InputConnection
import android.widget.FrameLayout
import kotlin.math.max
import android.util.Log

@SuppressLint("ViewConstructor")
open class BaseKeyView(context: Context, var theme: KeyboardTheme) : FrameLayout(context) {
    private val handler = Handler(Looper.getMainLooper())
    private var isDeleting = false
    private var deleteDuration = 200L
    private var deleteButton: View? = null

    protected val deleteRunnable = object : Runnable {
        override fun run() {
            deleteLastUnicodeChar(currentInputConnection)
            if (isDeleting) {
                if (deleteDuration != 200L) {
                    deleteButton?.isPressed = false
                    deleteButton?.performClick()
                    deleteButton?.playSoundEffect(SoundEffectConstants.CLICK)
                    deleteButton?.isPressed = true
                }
                handler.postDelayed(this, deleteDuration)
                deleteDuration = max(deleteDuration - 20, 50)
            }
        }
    }

    val currentInputConnection: InputConnection?
        get() {
            val c = context
            return if (c is KeyboardService) c.currentInputConnection
            else null
        }

    open fun applyTheme(theme: KeyboardTheme) {
        this.theme = theme
    }

    fun addDeleteAction(view: View) {
        view.onTouchAction(theme, {
            deleteButton = it
            isDeleting = true
            deleteDuration = 200
            handler.post(deleteRunnable)
        }, {
            isDeleting = false
            handler.removeCallbacks(deleteRunnable)
        })
    }

    fun addEnterAction(view: View) {
        view.onTouchAction(theme, {
            currentInputConnection?.sendKeyEvent(
                KeyEvent(
                    KeyEvent.ACTION_DOWN,
                    KeyEvent.KEYCODE_ENTER
                )
            )
        })
    }

    fun moveCursorLeft() {
        val beforeCursor =
            currentInputConnection?.getTextBeforeCursor(1000, 0) // Get enough text to be safe
        val currentCursorPosition = beforeCursor?.length ?: 0

        if (currentCursorPosition > 0) {
            val newPosition = (currentCursorPosition - 1).coerceAtLeast(0)
            val success = currentInputConnection?.setSelection(newPosition, newPosition) == true
            if (!success) {
                Log.d("BaseKeyView", "Couldn't move the cursor to the left")
            }
        }
    }
}
package fancyboard.fonts.keyboard.keyboard

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.inputmethodservice.InputMethodService
import android.text.InputType
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import fancyboard.fonts.keyboard.R

private fun setEnterIcon(imageButton: ImageButton?, action: Int) {
    when (action) {
        EditorInfo.IME_ACTION_GO -> {
            imageButton?.setImageResource(R.drawable.baseline_arrow_forward_24)
        }

        EditorInfo.IME_ACTION_SEARCH -> {
            imageButton?.setImageResource(R.drawable.baseline_search_24)
        }

        EditorInfo.IME_ACTION_SEND -> {
            imageButton?.setImageResource(R.drawable.baseline_send_24)
        }

        EditorInfo.IME_ACTION_NEXT -> {
            imageButton?.setImageResource(R.drawable.baseline_arrow_forward_24)
        }

        EditorInfo.IME_ACTION_DONE -> {
            imageButton?.setImageResource(R.drawable.baseline_check_circle_24)
        }

        EditorInfo.IME_ACTION_PREVIOUS -> {
            imageButton?.setImageResource(R.drawable.baseline_arrow_back_24)
        }

        EditorInfo.IME_ACTION_NONE -> {
            imageButton?.setImageResource(R.drawable.baseline_keyboard_return_24)
        }

        EditorInfo.IME_ACTION_UNSPECIFIED -> {
            imageButton?.setImageResource(R.drawable.baseline_keyboard_return_24)
        }

        else -> {
            imageButton?.setImageResource(R.drawable.baseline_keyboard_return_24)
        }
    }
}

class KeyboardService : InputMethodService() {
    companion object {
        const val ACTION_KEYBOARD_BROADCAST =
            "fancyboard.fonts.keyboard.ACTION_KEYBOARD_BROADCAST"
    }

    private var keyboardView: KeyboardView? = null

    private val keyboardReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ACTION_KEYBOARD_BROADCAST) {
                val type = intent.getStringExtra("type")
                if (type == "theme_change" && context != null) {
                    keyboardView?.applyTheme(getDefaultTheme(context))
                }else if(type == "font_unlocked"){
                    val fontName = intent.getStringExtra("font")
                    keyboardView?.onFontUnlocked(fontName)
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        val filter = IntentFilter(ACTION_KEYBOARD_BROADCAST)
        ContextCompat.registerReceiver(
            this, keyboardReceiver, filter,
            ContextCompat.RECEIVER_NOT_EXPORTED,
        )
    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(keyboardReceiver)
    }

    override fun onCreateInputView(): View {
        keyboardView = KeyboardView(this, getDefaultTheme(this))
        return keyboardView!!
    }

    override fun onStartInputView(info: EditorInfo, restarting: Boolean) {
        super.onStartInputView(info, restarting)

        // Check whether it is numeric keypad or not
        val inputType = info.inputType
        val inputClass = inputType and InputType.TYPE_MASK_CLASS
        val isNumber = inputClass == InputType.TYPE_CLASS_NUMBER || inputClass == InputType.TYPE_CLASS_PHONE

        keyboardView?.onStartInputView(isNumber)

        val action = info.imeOptions and EditorInfo.IME_MASK_ACTION
        val keyboardEnter = keyboardView?.findViewById<ImageButton>(R.id.key_enter)
        setEnterIcon(keyboardEnter, action)
    }

    override fun onUpdateSelection(
        oldSelStart: Int, oldSelEnd: Int, newSelStart: Int, newSelEnd: Int,
        candidatesStart: Int, candidatesEnd: Int
    ) {
        super.onUpdateSelection(
            oldSelStart,
            oldSelEnd,
            newSelStart,
            newSelEnd,
            candidatesStart,
            candidatesEnd
        )

        val ic = currentInputConnection ?: return
        val surroundingCharSequence = ic.getTextBeforeCursor(100, 0) ?: return
        val surroundingText = surroundingCharSequence.toString()
        val prefix = surroundingText.takeLastWhile { it != '\n' && it != '\t' && it != ' ' }
        keyboardView?.onSuggestion(prefix)
    }
}
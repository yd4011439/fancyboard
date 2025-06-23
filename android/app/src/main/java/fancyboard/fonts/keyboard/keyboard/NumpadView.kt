package fancyboard.fonts.keyboard.keyboard

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import fancyboard.fonts.keyboard.R

class NumpadView(context: Context, theme: KeyboardTheme): BaseKeyView(context, theme) {
    val keys: List<View>
    init {
        inflate(context, R.layout.numpad_view, this)
        keys = findAllButtons(this)
    }

    fun setupListeners(){

    }
}
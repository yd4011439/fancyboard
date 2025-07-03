package fancyboard.fonts.keyboard.keyboard

import android.content.Context
import android.view.View
import android.widget.Button
import fancyboard.fonts.keyboard.R

class NumpadView(context: Context, theme: KeyboardTheme) : BaseKeyView(context, theme) {
    val keys: List<View>

    init {
        inflate(context, R.layout.numpad_view, this)
        keys = findAllButtons(this)
    }

    override fun applyTheme(theme: KeyboardTheme) {
        super.applyTheme(theme)
        for (key in keys) {
            applyButtonTheme(key, theme)
            if (key is Button) {
                key.onTouchAction(theme, {
                    val label = (it as Button).text.toString()
                    currentInputConnection?.commitText(label, 1)
                    (parent?.parent?.parent as? KeyboardView)?.showKeyPreview(it, label)
                }, {
                    (parent?.parent?.parent as? KeyboardView)?.hideKeyPreview()
                })
            }
        }

        addDeleteAction(findViewById(R.id.backspace))
    }
}
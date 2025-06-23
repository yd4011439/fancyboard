package fancyboard.fonts.keyboard.keyboard

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.view.View
import android.widget.ImageButton
import fancyboard.fonts.keyboard.MainActivity
import fancyboard.fonts.keyboard.R

@SuppressLint("ViewConstructor")
class SelectorView(context: Context, theme: KeyboardTheme) : BaseKeyView(context, theme) {
    val ids = arrayOf(
        R.id.alphabet_select,
        R.id.symbol_select,
        R.id.face_select,
        R.id.emoji_select,
        R.id.art_select,
        R.id.backspace
    )
    var selected = R.id.alphabet_select
    var onChange: ((id: Int) -> Unit)? = null
    val backspace: ImageButton

    init {
        inflate(context, R.layout.selector_view, this)
        backspace = findViewById(R.id.backspace)
    }

    override fun applyTheme(theme: KeyboardTheme) {
        super.applyTheme(theme)
        for (id in ids) {
            val view = findViewById<View>(id)
            applyButtonTheme(view, theme)

            if (id == selected) {
                view.backgroundTintList = ColorStateList.valueOf(theme.keyPressedColor)
            }

            if(view==backspace){
                addDeleteAction(backspace)
                continue
            }

            view.onTouchAction(theme, {},  {
                it.backgroundTintList = ColorStateList.valueOf(theme.keyPressedColor)
                if(selected == view.id) return@onTouchAction

                val prev = this@SelectorView.findViewById<View>(selected)
                prev.backgroundTintList = ColorStateList.valueOf(theme.keyColor)
                selected = view.id

                if(selected == R.id.alphabet_select){
                    backspace.visibility = GONE
                }else{
                    backspace.visibility = VISIBLE
                }

                onChange?.invoke(id)
            })
        }

        val themeChange = findViewById<ImageButton>(R.id.change_theme)
        themeChange.backgroundTintList = ColorStateList.valueOf(theme.keyColor)
        themeChange.imageTintList = ColorStateList.valueOf(theme.textColor)
        themeChange.setOnClickListener {
            val intent = Intent(this.context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            this.context.startActivity(intent)
        }
    }
}
package fancyboard.fonts.keyboard.keyboard

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import fancyboard.fonts.keyboard.MainActivity
import fancyboard.fonts.keyboard.R
import fancyboard.fonts.keyboard.data.english.englishWords
import fancyboard.fonts.keyboard.data.english.getPrefixMatches
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

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
    val suggestedTexts: LinearLayout
    private var searchJob: Job? = null
    val unCollapse: ImageButton
    val share: ImageButton
    val separator:View

    init {
        inflate(context, R.layout.selector_view, this)
        suggestedTexts = findViewById(R.id.suggested_texts)
        backspace = findViewById(R.id.backspace)
        unCollapse = findViewById(R.id.un_collapse)
        share = findViewById(R.id.share_btn)
        separator = findViewById(R.id.separator_view)
    }

    fun onSuggestion(prefix: String) {
        searchJob?.cancel()
        if (prefix.length < 2) {
            suggestedTexts.removeAllViews()
            return
        }


        separator.visibility = GONE
        share.visibility = GONE
        for(id in ids){
            findViewById<View>(id)?.visibility = GONE
        }

        unCollapse.visibility = VISIBLE
        (suggestedTexts.parent as? View)?.visibility = VISIBLE


        // Use different thread for nicey processing
        searchJob = CoroutineScope(Dispatchers.IO).launch {
            val matches = getPrefixMatches(englishWords, prefix)

            CoroutineScope(Dispatchers.Main).launch {
                suggestedTexts.removeAllViews()
                matches.take(10).forEach { it ->

                    val text = if (!prefix[0].isLowerCase()) {
                        it[0].toUpperCase() + it.substring(1)
                    } else {
                        it
                    }

                    val textView = TextView(context)
                    textView.text = text
                    textView.textSize = 16f
                    textView.setTextColor(theme.suggestionTextColor ?: Color.BLACK)
                    val params =
                        LinearLayout.LayoutParams(
                            LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT,
                        )
                    params.gravity = Gravity.CENTER_VERTICAL
                    params.setMargins(18, 0, 0, 0)
                    textView.layoutParams = params
                    textView.setOnClickListener{
                        currentInputConnection?.deleteSurroundingText(prefix.length, 0)
                        currentInputConnection?.commitText("$text ", 1)
                    }

                    suggestedTexts.addView(textView)
                }
            }
        }
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

        applyButtonTheme(share, theme)
        share.onTouchAction(theme, {}, {
            currentInputConnection?.commitText("-`♡´-𝙳𝚘𝚠𝚗𝚕𝚘𝚊𝚍 𝔽𝕒𝕟𝕔𝕪𝕓𝕠𝕒𝕣𝕕 - https://play.google.com/store/apps/details?id=fancyboard.fonts.keyboard", 1)
        })

        applyButtonTheme(unCollapse, theme)
        unCollapse.onTouchAction(theme, {}, {
            resetSuggestion()
        })
    }

    fun resetSuggestion(){
        separator.visibility = VISIBLE
        share.visibility = VISIBLE
        for(id in ids){
            findViewById<View>(id)?.visibility = VISIBLE
        }
        backspace.visibility = GONE

        unCollapse.visibility = GONE
        (suggestedTexts.parent as? View)?.visibility = GONE
    }
}
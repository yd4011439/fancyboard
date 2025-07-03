package fancyboard.fonts.keyboard.keyboard

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import fancyboard.fonts.keyboard.R
import fancyboard.fonts.keyboard.data.fontNames
import fancyboard.fonts.keyboard.data.getLayout

enum class ShiftState {
    LOCKED,
    PRESSED,
    NONE
}

@SuppressLint("ViewConstructor")
class AlphabetView(
    context: Context,
    theme: KeyboardTheme,
    private var layout: String,
) : BaseKeyView(context, theme) {

    var shiftState = ShiftState.NONE
    var isSymbol = false

    val keys: List<View>
    val shiftKey: ImageButton
    val symbolChangeKey: Button
    var onLayoutChange: ((layout:String)->Unit)? = null
    private val fontSelect = HorizontalSelectView(context, theme, layout, fontNames){
        if(!hasUnlockedFont(context, it)) return@HorizontalSelectView "👑 $it"
        return@HorizontalSelectView it
    }

    init {
        inflate(context, R.layout.alphabet_view, this)
        fontSelect.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        findViewById<FrameLayout>(R.id.font_select).addView(fontSelect)
        shiftKey = findViewById<ImageButton>(R.id.key_shift)
        symbolChangeKey = findViewById<Button>(R.id.key_symbol)
        keys = findAllButtons(this)

        fontSelect.onItemChange = {
            onLayoutChange?.invoke(it)
            setLayout(it)
        }
    }

    private fun clearShiftState() {
        if (shiftState == ShiftState.PRESSED) {
            shiftState = ShiftState.NONE
            setupListeners()
        }
    }

    fun setupListeners() {
        val layout = getLayout(this.layout, shiftState != ShiftState.NONE, this.isSymbol)
        for (key in keys) {
            val id = key.id
            val text = layout[id] ?: continue
            if (key is Button) {
                if(this.layout=="B⃠a⃠n⃠n⃠e⃠d⃠"){
                    key.textSize = 18f
                }else{
                    key.textSize = 22f
                }

                key.text = text
                key.onTouchAction(theme, {
                    val label = (it as Button).text.toString()

                    if (this.layout == "uʍop ǝpᴉsdn") {
                        currentInputConnection?.commitText(label, 0)
                    } else {
                        currentInputConnection?.commitText(label, 1)
                    }

                    (parent?.parent?.parent as? KeyboardView)?.showKeyPreview(it, label)
                    clearShiftState()
                }, {
                    (parent?.parent?.parent as? KeyboardView)?.hideKeyPreview()
                })
            }
        }

        findViewById<View>(R.id.key_space).onTouchAction(
            theme,
            {
                if (this.layout == "uʍop ǝpᴉsdn") {
                    currentInputConnection?.commitText(" ", 0)
                } else {
                    currentInputConnection?.commitText(" ", 1)
                }
            }
        )

        findViewById<View>(R.id.key_internationalize).onTouchAction(theme, {
            val imeManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imeManager.showInputMethodPicker()
        })

        applyButtonTheme(findViewById<View>(R.id.key_internationalize), theme)

        val delete = findViewById<View>(R.id.key_delete)
        addDeleteAction(delete)

        val enter = findViewById<View>(R.id.key_enter)
        addEnterAction(enter)

        val icon = when(shiftState){
            ShiftState.LOCKED -> R.drawable.keyboard_shift_locked
            ShiftState.PRESSED -> R.drawable.keyboard_shift_filled
            ShiftState.NONE -> R.drawable.keyboard_shift_outline
        }
        shiftKey.setImageResource(icon)
        shiftKey.onTouchAction(theme, {
            shiftState = when (shiftState) {
                ShiftState.NONE -> ShiftState.PRESSED
                ShiftState.PRESSED -> ShiftState.LOCKED
                ShiftState.LOCKED -> ShiftState.NONE
            }
            setupListeners()
        })

        symbolChangeKey.textSize = 16f
        symbolChangeKey.onTouchAction(theme, {
            shiftState = ShiftState.NONE
            isSymbol = !isSymbol
            setupListeners()
        })
    }

    fun setLayout(layout: String) {
        this.layout = layout
        fontSelect.setItem(layout)
        applyTheme(this.theme)
    }

    override fun applyTheme(theme: KeyboardTheme) {
        this.theme = theme
        keys.forEach { applyButtonTheme(it, theme) }
        fontSelect.applyTheme(theme)
        setupListeners()
    }
}
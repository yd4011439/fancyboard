package fancyboard.fonts.keyboard.keyboard

import android.content.Context
import android.widget.FrameLayout

import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.content.res.ColorStateList
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.edit
import androidx.core.net.toUri
import coil3.load
import fancyboard.fonts.keyboard.R
import fancyboard.fonts.keyboard.data.allSymbolCategories
import fancyboard.fonts.keyboard.data.textArtCategories
import fancyboard.fonts.keyboard.data.textFaceCategories
import java.io.File

@SuppressLint("ViewConstructor")
open class KeyboardView(
    context: Context,
    private var theme: KeyboardTheme,
    private var isNumeric: Boolean = false,
    private var disableTouch: Boolean = false,
) : FrameLayout(context) {
    private var preferences = context.getSharedPreferences(context.packageName, MODE_PRIVATE)

    private var currentLayout = preferences.getString("defaultFont", "Normal") ?: "Normal"
    private var textFaceCategory = preferences.getString("defaultTextFace", textFaceCategories[0]) ?: textFaceCategories[0]
    private val backgroundImage: ImageView = ImageView(context)
    private val selectorView = SelectorView(context, theme)

    private val mainContainer = FrameLayout(context)
    private val alphabetView = AlphabetView(context, theme, currentLayout)
    private val symbolView = SymbolView(context, theme, allSymbolCategories[0])
    private val numpadView = NumpadView(context, theme)
    private val emojiView = EmojiView(context, theme)
    private val textFaceView = TextFaceView(context, theme, textFaceCategory)
    private val textArtView = TextArtView(context, theme, textArtCategories[0])

    private val adContainer = AdContainer(context, !disableTouch)
    private var lastBackgroundImage:String? = null

    private val keyPreviewView = inflate(context, R.layout.popup_key_preview, null) as TextView

    init {
        val heightDp = adContainer.adHeightDp + 308
        mainContainer.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, context.dpToPx(250))
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, context.dpToPx(heightDp))
        backgroundImage.layoutParams =
            LayoutParams(LayoutParams.MATCH_PARENT, context.dpToPx(heightDp))
        backgroundImage.scaleType = ImageView.ScaleType.CENTER_CROP

        val linearLayout = LinearLayout(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, context.dpToPx(heightDp))
            orientation = LinearLayout.VERTICAL
            addView(adContainer)
            addView(selectorView)
            mainContainer.addView(alphabetView)
            addView(mainContainer)
        }

        addView(backgroundImage)
        addView(linearLayout)

        keyPreviewView.layoutParams = LayoutParams(context.dpToPx(48), context.dpToPx(60))
        addView(keyPreviewView)

        alphabetView.onLayoutChange = {
            preferences.edit { putString("defaultFont", it) }
            currentLayout = it
        }

        selectorView.onChange = {
            if(it == R.id.alphabet_select){
                mainContainer.removeAllViews()
                mainContainer.addView(alphabetView)
            }else if(it==R.id.face_select){
                mainContainer.removeAllViews()
                mainContainer.addView(textFaceView)
            }else if(it==R.id.symbol_select){
                mainContainer.removeAllViews()
                mainContainer.addView(symbolView)
            }else if(it==R.id.emoji_select){
                mainContainer.removeAllViews()
                mainContainer.addView(emojiView)
            }else if(it==R.id.art_select){
                mainContainer.removeAllViews()
                mainContainer.addView(textArtView)
            }
        }

        textFaceView.onCategoryChange = {
            preferences.edit { putString("defaultTextFace", it) }
            textFaceCategory = it
        }

        applyTheme(theme)
    }

    fun showKeyPreview(view: View, letter: String) {
        keyPreviewView.text = letter
        keyPreviewView.visibility = VISIBLE

        val viewPos = view.getRelativeCoordinates(this)
        val x = viewPos.x + view.width / 2 - keyPreviewView.width / 2
        val y = viewPos.y - keyPreviewView.height - 8

        keyPreviewView.x = x.toFloat()
        keyPreviewView.y = y.toFloat()
    }

    fun hideKeyPreview() {
        keyPreviewView.visibility = GONE
    }

    fun applyTheme(theme: KeyboardTheme) {
        this.theme = theme
        backgroundImage.alpha = 1f
        if (theme.backgroundImage != null) {
            // Don't do unnecessary image loading
            if(theme.backgroundImage != lastBackgroundImage){
                val src = if(theme.backgroundImage.startsWith("images")){
                    File(context.filesDir, theme.backgroundImage).toUri()
                }else{
                    theme.backgroundImage
                }

                backgroundImage.load(src)
                lastBackgroundImage = theme.backgroundImage
            }
        } else if (theme.backgroundDrawable != null) {
            backgroundImage.setBackgroundResource(theme.backgroundDrawable)
        }else{
            backgroundImage.alpha = 0f
        }

        setBackgroundColor(this.theme.backgroundColor)
        alphabetView.applyTheme(this.theme)
        selectorView.applyTheme(this.theme)
        emojiView.applyTheme(this.theme)
        textArtView.applyTheme(this.theme)
        symbolView.applyTheme(this.theme)
        numpadView.applyTheme(this.theme)
        textFaceView.applyTheme(this.theme)
        keyPreviewView.backgroundTintList = ColorStateList.valueOf(theme.keyPressedColor)
        keyPreviewView.setTextColor(theme.textColor)
    }

    fun setNumeric(isNumeric: Boolean){
        this.isNumeric = true
        applyTheme(this.theme)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return disableTouch
    }
}
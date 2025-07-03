package fancyboard.fonts.keyboard.keyboard

import android.content.Context
import android.widget.FrameLayout

import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.res.ColorStateList
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.edit
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import fancyboard.fonts.keyboard.AdViewActivity
import fancyboard.fonts.keyboard.R
import fancyboard.fonts.keyboard.data.allSymbolCategories
import fancyboard.fonts.keyboard.data.textArtCategories
import fancyboard.fonts.keyboard.data.textFaceCategories
import fancyboard.fonts.keyboard.rateApp
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
    private var textFaceCategory =
        preferences.getString("defaultTextFace", textFaceCategories[0]) ?: textFaceCategories[0]
    private val backgroundImage: ImageView = ImageView(context)
    val selectorView = SelectorView(context, theme)

    private val mainContainer = FrameLayout(context)
    private val alphabetView = AlphabetView(context, theme, currentLayout)
    private val symbolView = SymbolView(context, theme, allSymbolCategories[0])
    private val numpadView = NumpadView(context, theme)
    private val emojiView = EmojiView(context, theme)
    private val textFaceView = TextFaceView(context, theme, textFaceCategory)
    private val textArtView = TextArtView(context, theme, textArtCategories[0])

    private val adContainer = AdContainer(context, !disableTouch)
    private var lastBackgroundImage: String? = null

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
            mainContainer.addView(if (isNumeric) numpadView else alphabetView)
            if (isNumeric) selectorView.visibility = GONE
            addView(mainContainer)
        }

        addView(backgroundImage)
        addView(linearLayout)

        keyPreviewView.layoutParams = LayoutParams(context.dpToPx(48), context.dpToPx(60))
        addView(keyPreviewView)

        alphabetView.onLayoutChange = {
            if (hasUnlockedFont(context, it)) {
                preferences.edit { putString("defaultFont", it) }
                currentLayout = it
                if (currentLayout != "Normal") {
                    selectorView.resetSuggestion()
                }
            } else {
                showUnlockFontOverlay(it)
            }
        }

        selectorView.onChange = {
            if (it == R.id.alphabet_select) {
                mainContainer.removeAllViews()
                mainContainer.addView(alphabetView)
            } else if (it == R.id.face_select) {
                mainContainer.removeAllViews()
                mainContainer.addView(textFaceView)
            } else if (it == R.id.symbol_select) {
                mainContainer.removeAllViews()
                mainContainer.addView(symbolView)
            } else if (it == R.id.emoji_select) {
                mainContainer.removeAllViews()
                mainContainer.addView(emojiView)
            } else if (it == R.id.art_select) {
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
            if (theme.backgroundImage != lastBackgroundImage) {
                val src = if (theme.backgroundImage.startsWith("images")) {
                    File(context.filesDir, theme.backgroundImage).toUri()
                } else {
                    theme.backgroundImage
                }

                Glide.with(backgroundImage).load(src).into(backgroundImage)

                lastBackgroundImage = theme.backgroundImage
            }
        } else if (theme.backgroundDrawable != null) {
            backgroundImage.setBackgroundResource(theme.backgroundDrawable)
        } else {
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

    fun onStartInputView(isNumeric: Boolean) {
        show24HrUnlockOverlay()

        if (this.isNumeric && isNumeric) return
        if (!this.isNumeric && isNumeric) {
            mainContainer.removeAllViews()
            mainContainer.addView(numpadView)
            selectorView.visibility = INVISIBLE
        } else {
            selectorView.visibility = VISIBLE
            mainContainer.removeAllViews()
            mainContainer.addView(alphabetView)
        }

        applyTheme(this.theme)
    }

    fun onFontUnlocked(name: String?) {
        if (name != null) {
            currentLayout = name
            preferences.edit { putString("defaultFont", name) }
            alphabetView.setLayout(name)
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return disableTouch
    }

    fun onSuggestion(prefix: String) {
        if (mainContainer.getChildAt(0) == alphabetView) {
            if (currentLayout == "Normal") {
                selectorView.onSuggestion(prefix)
            }
        }
    }

    fun show24HrUnlockOverlay() {
        if (shouldShowUnlockAd(context)) {
            showOverlay("Unlock Fancyboard for next 24 hr by watching a short Ad", "Watch Ad", {
                // Open activity that has the loading overlay and interstitial ad stuff
                val intent = Intent(context, AdViewActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
                context.startActivity(intent)

                // Remove this overlay after 5 seconds
                postDelayed({
                    removeView(it)
                }, 5000)
            }, "Cancel", {
                removeView(it)
            })
        } else {
            showRateUsOverlay()
        }
    }

    fun showRateUsOverlay() {
        var lastRateUsAt = preferences.getLong("lastRateUsAt", -1)
        if (lastRateUsAt == -1L) {
            preferences.edit { putLong("lastRateUsAt", System.currentTimeMillis()) }
            return
        }

        // Ask at least once two days
        if (System.currentTimeMillis() - lastRateUsAt > 2 * 86_400 * 1000) {
            preferences.edit { putLong("lastRateUsAt", System.currentTimeMillis()) }
            showOverlay("Please provide feedback by rating us.", "Rate Us", {
                rateApp(context)
            }, "Cancel", {
                hideOverlay(it)
            })
        }
    }

    fun showUnlockFontOverlay(name: String) {
        showOverlay("Unlock $name font by watching an Ad which can be 10-60s long", "Unlock", {
            val intent = Intent(context, AdViewActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            intent.putExtra("font", name)
            context.startActivity(intent)

            postDelayed({
                alphabetView.setLayout(currentLayout)
                hideOverlay(it)
            }, 5000)
        }, "Not Now", {
            alphabetView.setLayout(currentLayout)
            hideOverlay(it)
        })
    }

    fun hideOverlay(overlay: View) {
        overlay.animate().y(context.dpToPx(adContainer.adHeightDp + 308).toFloat())
            .setDuration(100)
            .withEndAction { removeView(overlay) }.start()
    }

    fun showOverlay(
        message: String,
        positive: String,
        onPositive: (v: View) -> Unit,
        cancel: String = "Cancel",
        onNegative: ((v: View) -> Unit)? = null
    ) {
        val overlay = inflate(context, R.layout.keyboard_overlay, null)
        overlay.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, context.dpToPx(308))
        overlay.y = context.dpToPx(adContainer.adHeightDp + 308).toFloat()
        val finalY = context.dpToPx(adContainer.adHeightDp).toFloat()

        overlay.setOnClickListener {  }

        overlay.animate().y(finalY).setDuration(100).start()

        val messageView = overlay.findViewById<TextView>(R.id.message)
        val positiveView = overlay.findViewById<Button>(R.id.positive_action)
        val cancelView = overlay.findViewById<Button>(R.id.cancel_action)

        if (onNegative == null) {
            cancelView.visibility = GONE
        }

        messageView.text = message
        positiveView.text = positive
        cancelView.text = cancel

        positiveView.setOnClickListener {
            onPositive(overlay)
        }

        cancelView.setOnClickListener {
            onNegative?.invoke(overlay)
        }
        addView(overlay)
    }
}
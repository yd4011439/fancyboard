package fancyboard.fonts.keyboard.keyboard

import android.content.res.ColorStateList
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import fancyboard.fonts.keyboard.R

fun findAllButtons(root: View): List<View> {
    val buttons = mutableListOf<View>()
    if (root is ViewGroup) {
        for (i in 0 until root.childCount) {
            buttons += findAllButtons(root.getChildAt(i))
        }
    } else if (root is Button || root is ImageButton) {
        buttons += root
    }

    return buttons
}

fun applyButtonTheme(view: View, theme: KeyboardTheme) {
    val backgroundTint = ColorStateList.valueOf(theme.keyColor)
    view.setBackgroundResource(R.drawable.key_background)
    view.backgroundTintList = backgroundTint
    view.elevation = 0f
    if (view is Button) {
        view.setTextColor(theme.textColor)
    } else if (view is ImageButton) {
        val foregroundTint = ColorStateList.valueOf(theme.textColor)
        view.imageTintList = foregroundTint
    }
}
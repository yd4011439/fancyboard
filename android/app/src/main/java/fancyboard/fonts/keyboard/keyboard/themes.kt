package fancyboard.fonts.keyboard.keyboard

import androidx.annotation.DrawableRes
import android.graphics.Color
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.google.gson.Gson
import fancyboard.fonts.keyboard.R
import kotlinx.serialization.Serializable
import java.io.File
import androidx.core.content.edit

private val gson = Gson()

data class KeyboardTheme(
    val id: String? = null,
    val name: String,
    val backgroundColor: Int,
    val backgroundImage: String? = null,
    @DrawableRes val backgroundDrawable: Int? = null,
    val keyColor: Int,
    val keyPressedColor: Int,
    val textColor: Int,
    val textSize: Int = 18,
    val enableVibration: Boolean? = null,
    val enableSound: Boolean? = null,
)

val lightKeyboardTheme = KeyboardTheme(
    name = "Light",
    backgroundColor = Color.parseColor("#EEEEEE"),
    backgroundImage = null,
    keyColor = Color.parseColor("#FFFFFF"),
    keyPressedColor = Color.parseColor("#DDDDDD"),
    textColor = Color.parseColor("#212121"),
)

val darkKeyboardTheme = KeyboardTheme(
    name = "Dark",
    backgroundColor = Color.BLACK,
    keyColor = Color.argb(255, 40, 40, 40),
    keyPressedColor = Color.argb(255, 70, 70, 70),
    textColor = Color.WHITE,
)

val clearKeyLight = KeyboardTheme(
    name = "Light Clear Keys",
    backgroundColor = Color.WHITE,
    backgroundImage = null,
    keyColor = Color.parseColor("#00000000"),
    keyPressedColor = Color.parseColor("#FFDDDDDD"),
    textColor = Color.BLACK,
)

val clearKeyDark = KeyboardTheme(
    name = "Dark Clear Keys",
    backgroundColor = Color.parseColor("#000000"),
    backgroundImage = null,
    keyColor = Color.parseColor("#00000000"),
    keyPressedColor = Color.parseColor("#8FDDDDDD"),
    textColor = Color.WHITE,
)

val laliGuras = KeyboardTheme(
    name = "Lali Guras",
    backgroundColor = Color.parseColor("#E0E0E0"),
    backgroundDrawable = R.drawable.lali_guras,
    keyColor = Color.parseColor("#8f000000"),
    keyPressedColor = Color.parseColor("#FFAA0000"),
    textColor = Color.parseColor("#DDDDDD"),
)

val tiger = KeyboardTheme(
    name = "Tiger",
    backgroundColor = Color.parseColor("#E0E0E0"),
    backgroundDrawable = R.drawable.tiger,
    keyColor = Color.parseColor("#8f000000"),
    keyPressedColor = Color.parseColor("#FF00AA00"),
    textColor = Color.parseColor("#DDDDDD"),
)

val himalaya = KeyboardTheme(
    name = "Himalaya",
    backgroundColor = Color.parseColor("#E0E0E0"),
    backgroundDrawable = R.drawable.mountain,
    keyColor = Color.parseColor("#8F003300"),
    keyPressedColor = Color.parseColor("#FFAA0000"),
    textColor = Color.parseColor("#DDDDDD"),
    textSize = 18,
)

val tropics = KeyboardTheme(
    name = "Tropics",
    backgroundColor = Color.parseColor("#E0E0E0"),
    backgroundDrawable = R.drawable.tropics,
    keyColor = Color.parseColor("#8F003300"),
    keyPressedColor = Color.parseColor("#8F0000AA"),
    textColor = Color.parseColor("#DDDDDD"),
    textSize = 18,
)

val love = KeyboardTheme(
    name = "Love 1",
    backgroundColor = Color.parseColor("#E0E0E0"),
    backgroundDrawable = R.drawable.love_1,
    keyColor = Color.parseColor("#8f9f1239"),
    keyPressedColor = Color.parseColor("#FF9f1239"),
    textColor = Color.parseColor("#DDDDDD"),
)

val love2 = KeyboardTheme(
    name = "Love 2",
    backgroundColor = Color.parseColor("#E0E0E0"),
    backgroundDrawable = R.drawable.love_2,
    keyColor = Color.parseColor("#8f86198f"),
    keyPressedColor = Color.parseColor("#FF86198f"),
    textColor = Color.parseColor("#DDDDDD"),
)

val love3 = KeyboardTheme(
    name = "Love 3",
    backgroundColor = Color.parseColor("#E0E0E0"),
    backgroundDrawable = R.drawable.love_3,
    keyColor = Color.parseColor("#8f3f6212"),
    keyPressedColor = Color.parseColor("#FF3f6212"),
    textColor = Color.parseColor("#DDDDDD"),
)

val love4 = KeyboardTheme(
    name = "Love 4",
    backgroundColor = Color.parseColor("#E0E0E0"),
    backgroundDrawable = R.drawable.love_4,
    keyColor = Color.parseColor("#8f000000"),
    keyPressedColor = Color.parseColor("#FFAA0000"),
    textColor = Color.parseColor("#DDDDDD"),
)

val glitch = KeyboardTheme(
    name = "Glitch",
    backgroundColor = Color.parseColor("#E0E0E0"),
    backgroundDrawable = R.drawable.glitch,
    keyColor = Color.argb(180, 0, 0, 70),
    keyPressedColor = Color.argb(200, 0, 0, 100),
    textColor = Color.parseColor("#DDDDDD"),
)


val rust = KeyboardTheme(
    name = "Rust",
    backgroundColor = Color.parseColor("#E0E0E0"),
    backgroundDrawable = R.drawable.rust,
    keyColor = Color.argb(200, 40, 0, 0),
    keyPressedColor = Color.argb(200, 60, 0, 0),
    textColor = Color.parseColor("#DDDDDD"),
)

val technology = KeyboardTheme(
    name = "Technology",
    backgroundColor = Color.parseColor("#E0E0E0"),
    backgroundDrawable = R.drawable.technology,
    keyColor = Color.parseColor("#8f000000"),
    keyPressedColor = Color.parseColor("#AF444444"),
    textColor = Color.parseColor("#DDDDDD"),
)


val themes = mutableMapOf(
    "Minimal" to arrayOf(
        lightKeyboardTheme,
        darkKeyboardTheme,
        clearKeyLight,
        clearKeyDark,
    ),

    "Nature" to arrayOf(laliGuras, tiger, himalaya, tropics),
    "Love" to arrayOf(love, love2, love3, love4),
    "Techno" to arrayOf(glitch, rust, technology),
)

private fun getPreference(context: Context): SharedPreferences {
    return context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
}

fun getDefaultTheme(context: Context): KeyboardTheme {
    val prefs = getPreference(context)
    val json = prefs.getString("defaultTheme", "null")
    val theme = gson.fromJson(json, KeyboardTheme::class.java)
    if (theme == null) return lightKeyboardTheme
    return theme
}

@SuppressLint("UseKtx")
fun setDefaultTheme(context: Context, theme: KeyboardTheme?) {
    val prefs = getPreference(context)
    val json = gson.toJson(theme)
    prefs.edit(commit = true) {putString("defaultTheme", json)}

    val intent = Intent(KeyboardService.ACTION_KEYBOARD_BROADCAST).apply {
        putExtra("type", "theme_change")
    }
    context.sendBroadcast(intent)
}

private fun saveUserThemes(context: Context, themes: Array<KeyboardTheme>) {
    val prefs = getPreference(context)
    prefs.edit { putString("userThemes", gson.toJson(themes))}
}

fun getUserThemes(context: Context): Array<KeyboardTheme> {
    val prefs = getPreference(context)
    val userThemeString = prefs.getString("userThemes", "[]")
    val userThemes = gson.fromJson(userThemeString, Array<KeyboardTheme>::class.java)
    return userThemes
}

fun deleteBackgroundImage(context: Context, settings: KeyboardTheme){
    if(settings.backgroundImage!=null && settings.backgroundImage.startsWith("images")){
        File(context.filesDir, settings.backgroundImage).delete()
    }
}

fun deleteTheme(context: Context, theme: KeyboardTheme) {
    if(getDefaultTheme(context).id==theme.id){
        setDefaultTheme(context, null)
    }

    deleteBackgroundImage(context, theme)
    val removedThemes = getUserThemes(context).filter { it.id != theme.id }.toTypedArray()
    saveUserThemes(context, removedThemes)
}

fun saveTheme(context: Context, theme: KeyboardTheme) {
    val userThemes = getUserThemes(context)
    val index = userThemes.indexOfFirst { it.id == theme.id }
    val addedThemes = if (index == -1) {
        arrayOf(*userThemes, theme)
    } else {
        userThemes[index] = theme
        userThemes
    }

    saveUserThemes(context, addedThemes)
}

fun getAllThemes(context: Context): Map<String, Array<KeyboardTheme>> {
    themes["User Made"] = getUserThemes(context)
    return themes
}
package fancyboard.fonts.keyboard.keyboard

import android.view.inputmethod.InputConnection
import fancyboard.fonts.keyboard.data.allFontString
import fancyboard.fonts.keyboard.data.upsideDownString
import kotlin.text.contains

fun deleteLastUnicodeChar(inputConnection: InputConnection?) {
    if (inputConnection == null) return

    val textBeforeCursor = inputConnection.getTextBeforeCursor(10, 0)
    val textAfterCursor = inputConnection.getTextAfterCursor(10, 0)

    // 1. Handle upside down character deletion (Unicode-safe)
    if (!textAfterCursor.isNullOrEmpty()) {
        // Skip the characters which are spaces new line or other
        var pos = 0
        while(pos<textAfterCursor.length && textAfterCursor[pos].isWhitespace()){
            pos++;
        }

        val codePoint = Character.codePointAt(textAfterCursor, pos)
        if (upsideDownString.contains(codePoint.toChar())) {
            // Need to delete the first character none the less
            val codePoint = Character.codePointAt(textAfterCursor, 0)
            val charCount = Character.charCount(codePoint)
            inputConnection.deleteSurroundingText(0, charCount)
            return
        }
    }

    if (textBeforeCursor.isNullOrEmpty()) return

    // 2. Try matching 1 to 4 characters back using binary search
    for (i in 4 downTo 1) {
        if (textBeforeCursor.length >= i) {
            val candidate = textBeforeCursor.takeLast(i)
            if (allFontString.binarySearch(candidate) >= 0) {
                inputConnection.deleteSurroundingText(i, 0)
                return
            }
        }
    }

    // 3. Fallback: delete last full Unicode character
    val codePoint = Character.codePointBefore(textBeforeCursor, textBeforeCursor.length)
    val charCount = Character.charCount(codePoint)
    inputConnection.deleteSurroundingText(charCount, 0)
}

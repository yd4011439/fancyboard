package fancyboard.fonts.keyboard.data.english

import fancyboard.fonts.keyboard.data.fancyToAscii
import fancyboard.fonts.keyboard.data.suggestionToFancy
import android.util.Log

data class WordItem(val word: String, val relevance: Int)

val englishWords = arrayOf<WordItem>(
    *englishWords0,
    *englishWords1,
    *englishWords2,
    *englishWords3,
    *englishWords4,
    *englishWords5,
    *englishWords6,
    *englishWords7,
    *englishWords8,
    *englishWords9,
    *englishWords10,
    *englishWords11,
    *englishWords12,
    *englishWords13,
    *englishWords14,
    *englishWords15,
    *englishWords16,
    *englishWords17,
    *englishWords18,
    *englishWords19,
)

fun getPrefixMatches(words: Array<WordItem>, prefix: String): List<String> {
    val actualPrefix = prefix.toLowerCase()
    val start = words.binarySearch(WordItem(actualPrefix, 0), compareBy { it.word.compareTo(actualPrefix) })
        .let { if (it < 0) -(it + 1) else it }

    val matches = mutableListOf<WordItem>()
    for (i in start until words.size) {
        if (words[i].word.startsWith(actualPrefix)) {
            matches.add(words[i])
        } else break
    }

    matches.sortBy { it.relevance }

    return matches.map { it.word }.take(10)
}
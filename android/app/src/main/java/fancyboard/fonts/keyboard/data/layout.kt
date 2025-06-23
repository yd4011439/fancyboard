package fancyboard.fonts.keyboard.data

import android.util.Log
import fancyboard.fonts.keyboard.R

val numpadLayout = mapOf<Int, String>()

val englishNormal = mapOf(
    R.id.key_q to "q",
    R.id.key_w to "w",
    R.id.key_e to "e",
    R.id.key_r to "r",
    R.id.key_t to "t",
    R.id.key_y to "y",
    R.id.key_u to "u",
    R.id.key_i to "i",
    R.id.key_o to "o",
    R.id.key_p to "p",

    R.id.key_a to "a",
    R.id.key_s to "s",
    R.id.key_d to "d",
    R.id.key_f to "f",
    R.id.key_g to "g",
    R.id.key_h to "h",
    R.id.key_j to "j",
    R.id.key_k to "k",
    R.id.key_l to "l",

    R.id.key_z to "z",
    R.id.key_x to "x",
    R.id.key_c to "c",
    R.id.key_v to "v",
    R.id.key_b to "b",
    R.id.key_n to "n",
    R.id.key_m to "m",

    R.id.key_dot to ".",
    R.id.key_space to " ",
    R.id.key_symbol to "#1!",
)

val englishShifted = mapOf(
    R.id.key_q to "Q",
    R.id.key_w to "W",
    R.id.key_e to "E",
    R.id.key_r to "R",
    R.id.key_t to "T",
    R.id.key_y to "Y",
    R.id.key_u to "U",
    R.id.key_i to "I",
    R.id.key_o to "O",
    R.id.key_p to "P",

    R.id.key_a to "A",
    R.id.key_s to "S",
    R.id.key_d to "D",
    R.id.key_f to "F",
    R.id.key_g to "G",
    R.id.key_h to "H",
    R.id.key_j to "J",
    R.id.key_k to "K",
    R.id.key_l to "L",

    R.id.key_z to "Z",
    R.id.key_x to "X",
    R.id.key_c to "C",
    R.id.key_v to "V",
    R.id.key_b to "B",
    R.id.key_n to "N",
    R.id.key_m to "M",

    R.id.key_dot to ",",
    R.id.key_space to " ",
    R.id.key_symbol to "#1!",
)

val symbolShifted = mapOf(
    R.id.key_q to "~",
    R.id.key_w to "±",
    R.id.key_e to "×",
    R.id.key_r to "÷",
    R.id.key_t to "•",
    R.id.key_y to "°",
    R.id.key_u to "`",
    R.id.key_i to "´",
    R.id.key_o to "{",
    R.id.key_p to "}",

    R.id.key_a to "©",
    R.id.key_s to "€",
    R.id.key_d to "£",
    R.id.key_f to "₹",
    R.id.key_g to "®",
    R.id.key_h to "¥",
    R.id.key_j to "_",
    R.id.key_k to "+",
    R.id.key_l to "[",

    R.id.key_z to "¡",
    R.id.key_x to "<",
    R.id.key_c to ">",
    R.id.key_v to "|",
    R.id.key_b to "'",
    R.id.key_n to "\\",
    R.id.key_m to "¿",

    R.id.key_dot to "…",
    R.id.key_space to " ",
    R.id.key_symbol to "abc",
)

val symbolNormal = mapOf(
    R.id.key_q to "1",
    R.id.key_w to "2",
    R.id.key_e to "3",
    R.id.key_r to "4",
    R.id.key_t to "5",
    R.id.key_y to "6",
    R.id.key_u to "7",
    R.id.key_i to "8",
    R.id.key_o to "9",
    R.id.key_p to "0",

    R.id.key_a to "@",
    R.id.key_s to "#",
    R.id.key_d to "$",
    R.id.key_f to "%",
    R.id.key_g to "&",
    R.id.key_h to "*",
    R.id.key_j to "-",
    R.id.key_k to "=",
    R.id.key_l to "(",

    R.id.key_z to ",",
    R.id.key_x to "!",
    R.id.key_c to ":",
    R.id.key_v to ";",
    R.id.key_b to "\"",
    R.id.key_n to "/",
    R.id.key_m to "?",

    R.id.key_dot to ".",
    R.id.key_space to " ",
    R.id.key_symbol to "abc",
)

private val keyIds = arrayOf(
    R.id.key_a, R.id.key_b, R.id.key_c, R.id.key_d, R.id.key_e,
    R.id.key_f, R.id.key_g, R.id.key_h, R.id.key_i, R.id.key_j,
    R.id.key_k, R.id.key_l, R.id.key_m, R.id.key_n, R.id.key_o,
    R.id.key_p, R.id.key_q, R.id.key_r, R.id.key_s, R.id.key_t,
    R.id.key_u, R.id.key_v, R.id.key_w, R.id.key_x, R.id.key_y,
    R.id.key_z
)

private val numberIds = arrayOf(
    R.id.key_q, R.id.key_w, R.id.key_e, R.id.key_r, R.id.key_t,
    R.id.key_y, R.id.key_u, R.id.key_i, R.id.key_o, R.id.key_p
)

private fun <K, V> addRemainingKeys(fromMap: Map<K, V>, toMap: MutableMap<K, V>) {
    for (item in fromMap) {
        if (!toMap.containsKey(item.key)) {
            toMap[item.key] = item.value
        }
    }
}

fun getLayout(name: String, isShifted: Boolean, isSymbol: Boolean): Map<Int, String> {
    val layout = layoutMap[name] ?: layoutMap["Normal"]!!

    val symbols = mutableMapOf<Int, String>()
    if (!isSymbol) {
        if (!isShifted) {
            for (index in 0 until 26) {
                symbols[keyIds[index]] = layout[index]
            }
            addRemainingKeys(englishNormal, symbols)
        } else {
            for (index in 0 until 26) {
                symbols[keyIds[index]] = layout[index + 26]
            }
            addRemainingKeys(englishShifted, symbols)
        }
    } else {
        if (!isShifted) {
            if(layout.size>52){
                for (index in 0 until 10) {
                    symbols[numberIds[index]] = layout[index+52]
                }
                addRemainingKeys(symbolNormal, symbols)
                return symbols
            }
            return symbolNormal
        } else {
            return symbolShifted
        }
    }

    return symbols
}
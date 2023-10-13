package ru.netology.nmedia.funcs

import kotlin.math.log
import kotlin.math.pow

fun countToString (x: Long): String {
    if (x == 0L) {
        return "0"
    }
    else {
        when (log(x + 0.0, 10.0).toInt()) {
            0, 1, 2 -> return x.toString()
            3, 4, 5 -> return ("%.2f".format((x + 0.0) * (10.0).pow(-3.0))) + "K"
            6, 7, 8 -> return ("%.2f".format((x + 0.0) * (10.0).pow(-6.0))) + "M"
            else -> return if (x >= 1_000_000) ">=1B" else "error"
        }
    }
}
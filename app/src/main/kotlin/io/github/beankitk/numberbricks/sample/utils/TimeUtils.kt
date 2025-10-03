package io.github.beankitk.numberbricks.sample.utils

import java.time.LocalTime
import java.time.ZoneId

fun getTime() = LocalTime.now(ZoneId.systemDefault())

fun LocalTime.toDigitList(is24Hour: Boolean = false): List<Int> {
    val hourIn24 = this.hour
    val h = if (is24Hour) hourIn24 else {
        if (hourIn24 % 12 == 0) 12 else hourIn24 % 12
    }
    val m = this.minute
    val s = this.second

    return listOf(
        h / 10, h % 10,
        m / 10, m % 10,
        s / 10, s % 10
    )
}
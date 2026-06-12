package com.timerunwhere.ui

import kotlin.math.roundToInt

fun formatDuration(millis: Long): String {
    val minutes = millis.coerceAtLeast(0L) / 60_000L
    val hours = minutes / 60
    val rest = minutes % 60
    return if (hours > 0) "${hours}h ${rest}m" else "${rest}m"
}

fun formatClock(millis: Long): String {
    val seconds = (millis.coerceAtLeast(0L) + 999L) / 1000L
    return "%02d:%02d".format(seconds / 60, seconds % 60)
}

fun percent(part: Long, total: Long): Int {
    if (total <= 0L) return 0
    return ((part.toDouble() / total.toDouble()) * 100).roundToInt()
}

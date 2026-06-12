package com.timerunwhere.data.local

import android.content.Context
import java.time.LocalDate

class FocusSessionStore(context: Context) {
    private val prefs = context.applicationContext
        .getSharedPreferences("focus_sessions", Context.MODE_PRIVATE)

    fun addCompletedFocus(durationMillis: Long) {
        if (durationMillis <= 0) return
        val key = keyFor(LocalDate.now())
        val next = prefs.getLong(key, 0L) + durationMillis
        prefs.edit().putLong(key, next).apply()
    }

    fun todayFocusMillis(): Long = prefs.getLong(keyFor(LocalDate.now()), 0L)

    private fun keyFor(date: LocalDate): String = "focus_millis_$date"
}

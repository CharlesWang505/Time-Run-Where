package com.timerunwhere.focus

import android.content.Context

class FocusStateStore(context: Context) {
    private val prefs = context.applicationContext
        .getSharedPreferences("focus_state", Context.MODE_PRIVATE)

    fun start(startAtMillis: Long, endAtMillis: Long) {
        prefs.edit()
            .putLong(KEY_START_AT, startAtMillis)
            .putLong(KEY_END_AT, endAtMillis)
            .apply()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    fun startAtMillis(): Long = prefs.getLong(KEY_START_AT, 0L)

    fun endAtMillis(): Long = prefs.getLong(KEY_END_AT, 0L)

    fun isActive(now: Long = System.currentTimeMillis()): Boolean = endAtMillis() > now

    companion object {
        private const val KEY_START_AT = "start_at"
        private const val KEY_END_AT = "end_at"
    }
}

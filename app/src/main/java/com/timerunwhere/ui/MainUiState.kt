package com.timerunwhere.ui

import com.timerunwhere.data.model.AppUsage

data class MainUiState(
    val token: String = "",
    val tokenDraft: String = "",
    val usages: List<AppUsage> = emptyList(),
    val focusMillisToday: Long = 0L,
    val hasUsageAccess: Boolean = false,
    val canDrawOverlays: Boolean = false,
    val ignoringBatteryOptimizations: Boolean = false,
    val focusRemainingMillis: Long = 0L,
    val isSyncing: Boolean = false,
    val syncStatus: String = "尚未同步"
) {
    val focusActive: Boolean get() = focusRemainingMillis > 0L
    val timeSinkMillis: Long get() = usages
        .filter { it.category.name == "TIME_SINK" }
        .sumOf { it.durationMillis }
    val productiveMillis: Long get() = usages
        .filter { it.category.name == "PRODUCTIVE" }
        .sumOf { it.durationMillis } + focusMillisToday
    val neutralMillis: Long get() = usages
        .filter { it.category.name == "NEUTRAL" }
        .sumOf { it.durationMillis }
    val totalTrackedMillis: Long get() = timeSinkMillis + productiveMillis + neutralMillis
}

package com.timerunwhere.data.model

enum class UsageCategory(val displayName: String) {
    TIME_SINK("时间黑洞"),
    PRODUCTIVE("有效利用"),
    NEUTRAL("未分类"),
    FOCUS("专注完成")
}

data class AppUsage(
    val packageName: String,
    val appName: String,
    val durationMillis: Long,
    val category: UsageCategory
)

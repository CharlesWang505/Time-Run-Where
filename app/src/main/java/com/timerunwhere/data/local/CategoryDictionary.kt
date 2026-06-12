package com.timerunwhere.data.local

import com.timerunwhere.data.model.UsageCategory

object CategoryDictionary {
    private val timeSinks = setOf(
        "com.ss.android.ugc.aweme",          // 抖音
        "com.zhiliaoapp.musically",         // TikTok
        "com.tencent.tmgp.sgame",           // 王者荣耀
        "com.tencent.KiHan",                // 金铲铲之战
        "com.smile.gifmaker",               // 快手
        "tv.danmaku.bili",                  // 哔哩哔哩
        "com.bilibili.app.in",
        "com.xingin.xhs",                   // 小红书
        "com.sina.weibo",                   // 微博
        "com.tencent.mobileqq",
        "com.tencent.mm"
    )

    private val productive = setOf(
        "com.microsoft.office.word",
        "com.microsoft.office.excel",
        "com.microsoft.office.powerpoint",
        "com.microsoft.todos",
        "com.ticktick.task",
        "com.todoist",
        "notion.id",
        "com.google.android.apps.docs.editors.docs",
        "com.google.android.apps.docs.editors.sheets",
        "com.termux",
        "com.github.android",
        "com.jetbrains.space"
    )

    fun categoryFor(packageName: String): UsageCategory = when (packageName) {
        in timeSinks -> UsageCategory.TIME_SINK
        in productive -> UsageCategory.PRODUCTIVE
        else -> UsageCategory.NEUTRAL
    }

    fun isTimeSink(packageName: String): Boolean = packageName in timeSinks
}

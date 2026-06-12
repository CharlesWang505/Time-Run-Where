package com.timerunwhere.data.usage

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import com.timerunwhere.data.local.CategoryDictionary
import com.timerunwhere.data.model.AppUsage
import java.util.Calendar

class UsageStatsCollector(private val context: Context) {
    private val usageStatsManager =
        context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    private val packageManager = context.packageManager

    fun hasUsageAccess(): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName
            )
        } else {
            @Suppress("DEPRECATION")
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    fun collectToday(): List<AppUsage> {
        if (!hasUsageAccess()) return emptyList()
        val start = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        return collectRange(start, System.currentTimeMillis())
    }

    fun currentForegroundPackage(sinceMillis: Long = 4_000L): String? {
        if (!hasUsageAccess()) return null
        val now = System.currentTimeMillis()
        val events = usageStatsManager.queryEvents(now - sinceMillis, now)
        val event = UsageEvents.Event()
        var currentPackage: String? = null
        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            when (event.eventType) {
                UsageEvents.Event.ACTIVITY_RESUMED,
                UsageEvents.Event.MOVE_TO_FOREGROUND -> currentPackage = event.packageName
                UsageEvents.Event.ACTIVITY_PAUSED,
                UsageEvents.Event.MOVE_TO_BACKGROUND -> {
                    if (currentPackage == event.packageName) currentPackage = null
                }
            }
        }
        return currentPackage
    }

    private fun collectRange(startMillis: Long, endMillis: Long): List<AppUsage> {
        val events = usageStatsManager.queryEvents(startMillis, endMillis)
        val event = UsageEvents.Event()
        val activeSince = mutableMapOf<String, Long>()
        val totals = mutableMapOf<String, Long>()

        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            val packageName = event.packageName ?: continue
            when (event.eventType) {
                UsageEvents.Event.ACTIVITY_RESUMED,
                UsageEvents.Event.MOVE_TO_FOREGROUND -> {
                    activeSince[packageName] = event.timeStamp
                }
                UsageEvents.Event.ACTIVITY_PAUSED,
                UsageEvents.Event.MOVE_TO_BACKGROUND -> {
                    val startedAt = activeSince.remove(packageName) ?: continue
                    if (event.timeStamp > startedAt) {
                        totals[packageName] = (totals[packageName] ?: 0L) + event.timeStamp - startedAt
                    }
                }
            }
        }

        activeSince.forEach { (packageName, startedAt) ->
            if (endMillis > startedAt) {
                totals[packageName] = (totals[packageName] ?: 0L) + endMillis - startedAt
            }
        }

        return totals
            .filter { (packageName, duration) ->
                duration > 1_000L &&
                    packageName != context.packageName &&
                    isLaunchableApp(packageName)
            }
            .map { (packageName, duration) ->
                AppUsage(
                    packageName = packageName,
                    appName = labelFor(packageName),
                    durationMillis = duration,
                    category = CategoryDictionary.categoryFor(packageName)
                )
            }
            .sortedByDescending { it.durationMillis }
    }

    private fun isLaunchableApp(packageName: String): Boolean =
        packageManager.getLaunchIntentForPackage(packageName) != null

    private fun labelFor(packageName: String): String =
        runCatching {
            val appInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getApplicationInfo(packageName, PackageManager.ApplicationInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                packageManager.getApplicationInfo(packageName, 0)
            }
            packageManager.getApplicationLabel(appInfo).toString()
        }.getOrDefault(packageName)
}

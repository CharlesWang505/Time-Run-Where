package com.timerunwhere.data.network

import android.content.Context
import android.provider.Settings
import com.timerunwhere.data.model.AppUsage
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate

object SyncPayloadBuilder {
    fun build(context: Context, apps: List<AppUsage>, focusMillis: Long): String {
        val items = JSONArray()
        apps.forEach { app ->
            items.put(
                JSONObject()
                    .put("packageName", app.packageName)
                    .put("appName", app.appName)
                    .put("durationMillis", app.durationMillis)
                    .put("category", app.category.name)
                    .put("categoryName", app.category.displayName)
            )
        }

        return JSONObject()
            .put("deviceId", deviceId(context))
            .put("deviceName", android.os.Build.MODEL ?: "Android")
            .put("date", LocalDate.now().toString())
            .put("generatedAtMillis", System.currentTimeMillis())
            .put("focusMillis", focusMillis)
            .put("apps", items)
            .toString()
    }

    private fun deviceId(context: Context): String =
        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            ?: "unknown"
}

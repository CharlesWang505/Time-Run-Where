package com.timerunwhere

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.timerunwhere.worker.WorkerScheduler

class TimeRunWhereApp : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
        WorkerScheduler.schedulePeriodicSync(this)
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_FOCUS,
                "专注模式",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "专注倒计时与前台应用拦截"
            }
        )
    }

    companion object {
        const val CHANNEL_FOCUS = "focus_guard"
    }
}

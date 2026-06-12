package com.timerunwhere.focus

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.timerunwhere.MainActivity
import com.timerunwhere.R
import com.timerunwhere.TimeRunWhereApp
import com.timerunwhere.data.local.CategoryDictionary
import com.timerunwhere.data.local.FocusSessionStore
import com.timerunwhere.data.usage.UsageStatsCollector

class FocusGuardService : Service() {
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var focusStateStore: FocusStateStore
    private lateinit var sessionStore: FocusSessionStore
    private lateinit var collector: UsageStatsCollector
    private lateinit var overlay: FocusOverlayController
    private var lastHomeLaunchAt = 0L

    private val tickRunnable = object : Runnable {
        override fun run() {
            tick()
            handler.postDelayed(this, POLL_INTERVAL_MS)
        }
    }

    override fun onCreate() {
        super.onCreate()
        focusStateStore = FocusStateStore(this)
        sessionStore = FocusSessionStore(this)
        collector = UsageStatsCollector(this)
        overlay = FocusOverlayController(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val requestedMinutes = intent?.getIntExtra(EXTRA_MINUTES, 0).orZero()
        if (requestedMinutes > 0) {
            val now = System.currentTimeMillis()
            focusStateStore.start(now, now + requestedMinutes * 60_000L)
        }

        if (!focusStateStore.isActive()) {
            stopSelf()
            return START_NOT_STICKY
        }

        startForeground(NOTIFICATION_ID, buildNotification())
        handler.removeCallbacks(tickRunnable)
        handler.post(tickRunnable)
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        handler.removeCallbacks(tickRunnable)
        overlay.hide()
        super.onDestroy()
    }

    private fun tick() {
        val now = System.currentTimeMillis()
        val endAt = focusStateStore.endAtMillis()
        val remaining = endAt - now
        if (remaining <= 0L) {
            completeFocus()
            return
        }

        val foreground = collector.currentForegroundPackage()
        if (foreground != null && CategoryDictionary.isTimeSink(foreground)) {
            overlay.show(remaining)
            launchHomeIfNeeded(now)
        } else {
            overlay.hide()
        }
    }

    private fun completeFocus() {
        val duration = focusStateStore.endAtMillis() - focusStateStore.startAtMillis()
        sessionStore.addCompletedFocus(duration)
        focusStateStore.clear()
        overlay.hide()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun launchHomeIfNeeded(now: Long) {
        if (now - lastHomeLaunchAt < HOME_LAUNCH_COOLDOWN_MS) return
        lastHomeLaunchAt = now
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }

    private fun buildNotification() =
        NotificationCompat.Builder(this, TimeRunWhereApp.CHANNEL_FOCUS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("时间去哪了")
            .setContentText("专注模式运行中，正在拦截时间黑洞应用")
            .setOngoing(true)
            .setContentIntent(
                PendingIntent.getActivity(
                    this,
                    0,
                    Intent(this, MainActivity::class.java),
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            .build()

    private fun Int?.orZero(): Int = this ?: 0

    companion object {
        private const val EXTRA_MINUTES = "minutes"
        private const val NOTIFICATION_ID = 2107
        private const val POLL_INTERVAL_MS = 800L
        private const val HOME_LAUNCH_COOLDOWN_MS = 2_000L

        fun start(context: Context, minutes: Int) {
            val intent = Intent(context, FocusGuardService::class.java)
                .putExtra(EXTRA_MINUTES, minutes)
            startServiceCompat(context, intent)
        }

        fun startExisting(context: Context) {
            startServiceCompat(context, Intent(context, FocusGuardService::class.java))
        }

        private fun startServiceCompat(context: Context, intent: Intent) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(context, intent)
            } else {
                context.startService(intent)
            }
        }
    }
}

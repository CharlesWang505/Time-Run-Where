package com.timerunwhere.focus

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import kotlin.math.ceil

class FocusOverlayController(private val context: Context) {
    private val appContext = context.applicationContext
    private val windowManager = appContext.getSystemService(WindowManager::class.java)
    private var rootView: View? = null
    private var countdownText: TextView? = null

    fun show(remainingMillis: Long) {
        if (!Settings.canDrawOverlays(appContext)) return
        if (rootView == null) attach()
        countdownText?.text = formatRemaining(remainingMillis)
    }

    fun hide() {
        val view = rootView ?: return
        runCatching { windowManager.removeView(view) }
        rootView = null
        countdownText = null
    }

    private fun attach() {
        val container = LinearLayout(appContext).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.rgb(220, 0, 0))
            setPadding(48, 48, 48, 48)
        }
        val title = TextView(appContext).apply {
            text = "专注期间，请关闭无关应用"
            setTextColor(Color.WHITE)
            textSize = 30f
            gravity = Gravity.CENTER
            typeface = Typeface.DEFAULT_BOLD
        }
        val subtitle = TextView(appContext).apply {
            text = "请返回桌面或打开有效利用类应用"
            setTextColor(Color.WHITE)
            alpha = 0.9f
            textSize = 18f
            gravity = Gravity.CENTER
            setPadding(0, 28, 0, 18)
        }
        countdownText = TextView(appContext).apply {
            setTextColor(Color.WHITE)
            textSize = 56f
            gravity = Gravity.CENTER
            typeface = Typeface.MONOSPACE
        }
        container.addView(title)
        container.addView(subtitle)
        container.addView(countdownText)

        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            android.graphics.PixelFormat.OPAQUE
        ).apply {
            gravity = Gravity.CENTER
        }
        windowManager.addView(container, params)
        rootView = container
    }

    private fun formatRemaining(remainingMillis: Long): String {
        val seconds = ceil(remainingMillis.coerceAtLeast(0L) / 1000.0).toLong()
        val minutes = seconds / 60
        val restSeconds = seconds % 60
        return "%02d:%02d".format(minutes, restSeconds)
    }
}

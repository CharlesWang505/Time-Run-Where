package com.timerunwhere.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.timerunwhere.ui.MainUiState
import com.timerunwhere.ui.formatClock
import com.timerunwhere.ui.theme.MutedText
import com.timerunwhere.ui.theme.ProductiveCyan
import com.timerunwhere.ui.theme.SoftWhite
import com.timerunwhere.ui.theme.TimeSinkPink

@Composable
fun FocusScreen(
    state: MainUiState,
    onStartFocus: (Int) -> Unit,
    onOpenUsageAccess: () -> Unit,
    onOpenOverlayPermission: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        ScreenTitle("专注模式", "开启后将强制拦截时间黑洞应用")
        Spacer(Modifier.height(10.dp))
        FocusTimer(
            remainingMillis = state.focusRemainingMillis,
            totalMillis = 25 * 60_000L,
            active = state.focusActive
        )
        SectionCard {
            Text("强力拦截", color = SoftWhite, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(
                text = "专注期间检测到抖音、王者荣耀等时间黑洞应用时，会弹出全屏红色悬浮窗并返回桌面。",
                color = MutedText
            )
            Spacer(Modifier.height(14.dp))
            if (!state.hasUsageAccess || !state.canDrawOverlays) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (!state.hasUsageAccess) GhostButton("使用情况访问", onOpenUsageAccess)
                    if (!state.canDrawOverlays) GhostButton("悬浮窗权限", onOpenOverlayPermission)
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    NeonButton("25 分钟", onClick = { onStartFocus(25) }, enabled = !state.focusActive)
                    GhostButton("45 分钟", onClick = { onStartFocus(45) })
                }
            }
        }
    }
}

@Composable
private fun FocusTimer(remainingMillis: Long, totalMillis: Long, active: Boolean) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(260.dp)) {
        Canvas(Modifier.fillMaxSize()) {
            val stroke = Stroke(width = 18.dp.toPx(), cap = StrokeCap.Round)
            val side = size.minDimension - 26.dp.toPx()
            val topLeft = Offset((size.width - side) / 2f, (size.height - side) / 2f)
            drawArc(
                color = androidx.compose.ui.graphics.Color(0xFF2E2E2E),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = Size(side, side),
                style = stroke
            )
            val progress = if (active) remainingMillis.toFloat() / totalMillis.toFloat() else 1f
            drawArc(
                brush = Brush.sweepGradient(listOf(ProductiveCyan, TimeSinkPink, ProductiveCyan)),
                startAngle = -90f,
                sweepAngle = 360f * progress.coerceIn(0f, 1f),
                useCenter = false,
                topLeft = topLeft,
                size = Size(side, side),
                style = stroke
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (active) formatClock(remainingMillis) else "25:00",
                color = SoftWhite,
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (active) "进行中" else "准备开始",
                color = MutedText,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

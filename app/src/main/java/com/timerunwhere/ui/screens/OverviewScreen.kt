package com.timerunwhere.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.timerunwhere.data.model.AppUsage
import com.timerunwhere.ui.MainUiState
import com.timerunwhere.ui.formatDuration
import com.timerunwhere.ui.percent
import com.timerunwhere.ui.theme.MutedText
import com.timerunwhere.ui.theme.ProductiveCyan
import com.timerunwhere.ui.theme.SoftWhite
import com.timerunwhere.ui.theme.TimeSinkPink

@Composable
fun OverviewScreen(
    state: MainUiState,
    onOpenUsageAccess: () -> Unit,
    onOpenOverlayPermission: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(22.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ScreenTitle("时间去哪了", "今日本机使用时长与专注完成记录")
        PermissionCard(state, onOpenUsageAccess, onOpenOverlayPermission)
        CategoryCard(
            title = "时间黑洞",
            duration = state.timeSinkMillis,
            total = state.totalTrackedMillis,
            colors = listOf(TimeSinkPink, Color(0xFFFF6DB2))
        )
        CategoryCard(
            title = "有效利用",
            duration = state.productiveMillis,
            total = state.totalTrackedMillis,
            colors = listOf(ProductiveCyan, Color(0xFF7DFFEF))
        )
        CategoryCard(
            title = "未分类",
            duration = state.neutralMillis,
            total = state.totalTrackedMillis,
            colors = listOf(Color(0xFF777777), Color(0xFFAAAAAA))
        )
        TopAppsCard(state.usages.take(3))
    }
}

@Composable
private fun PermissionCard(
    state: MainUiState,
    onOpenUsageAccess: () -> Unit,
    onOpenOverlayPermission: () -> Unit
) {
    if (state.hasUsageAccess && state.canDrawOverlays) return
    SectionCard {
        Text("权限未完成", color = SoftWhite, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        Text("需要使用情况访问权限进行统计，需要悬浮窗权限执行专注拦截。", color = MutedText)
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            if (!state.hasUsageAccess) GhostButton("使用情况访问", onOpenUsageAccess)
            if (!state.canDrawOverlays) GhostButton("悬浮窗", onOpenOverlayPermission)
        }
    }
}

@Composable
private fun CategoryCard(title: String, duration: Long, total: Long, colors: List<Color>) {
    SectionCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, color = SoftWhite, fontWeight = FontWeight.Bold)
            Text("${percent(duration, total)}%", color = colors.first(), fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(10.dp))
        GradientBar(
            progress = if (total > 0) duration.toFloat() / total.toFloat() else 0f,
            colors = colors
        )
        Spacer(Modifier.height(10.dp))
        Text(formatDuration(duration), color = MutedText, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun TopAppsCard(apps: List<AppUsage>) {
    SectionCard {
        Text("Top 3 应用", color = SoftWhite, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        if (apps.isEmpty()) {
            Text("授权后会显示今日前台使用时长。", color = MutedText)
        } else {
            apps.forEachIndexed { index, app ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("${index + 1}. ${app.appName}", color = SoftWhite, fontWeight = FontWeight.SemiBold)
                        Text(app.category.displayName, color = MutedText, style = MaterialTheme.typography.bodySmall)
                    }
                    Text(formatDuration(app.durationMillis), color = ProductiveCyan)
                }
            }
        }
    }
}

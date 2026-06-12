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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.timerunwhere.ui.MainUiState
import com.timerunwhere.ui.theme.MutedText
import com.timerunwhere.ui.theme.Panel
import com.timerunwhere.ui.theme.ProductiveCyan
import com.timerunwhere.ui.theme.SoftWhite

@Composable
fun SettingsScreen(
    state: MainUiState,
    onTokenChange: (String) -> Unit,
    onSaveToken: () -> Unit,
    onManualSync: () -> Unit,
    onOpenUsageAccess: () -> Unit,
    onOpenOverlayPermission: () -> Unit,
    onOpenBatterySettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(22.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ScreenTitle("系统设置", "配对、同步、权限和荣耀 MagicOS 保活")
        SectionCard {
            Text("Windows 配对 Token", color = SoftWhite, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = state.tokenDraft,
                onValueChange = onTokenChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("6 位配对码") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Panel,
                    unfocusedContainerColor = Panel,
                    focusedIndicatorColor = ProductiveCyan,
                    focusedLabelColor = ProductiveCyan
                )
            )
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                NeonButton("保存 Token", onClick = onSaveToken, enabled = state.tokenDraft.length == 6)
                GhostButton("手动同步", onClick = onManualSync)
            }
            Spacer(Modifier.height(10.dp))
            Text(state.syncStatus, color = MutedText, style = MaterialTheme.typography.bodySmall)
        }

        SectionCard {
            Text("权限状态", color = SoftWhite, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            PermissionRow("使用情况访问", state.hasUsageAccess, onOpenUsageAccess)
            PermissionRow("悬浮窗拦截", state.canDrawOverlays, onOpenOverlayPermission)
            PermissionRow("忽略电池优化", state.ignoringBatteryOptimizations, onOpenBatterySettings)
        }

        HonorGuide()
    }
}

@Composable
private fun PermissionRow(title: String, granted: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, color = SoftWhite)
        if (granted) {
            Text("已开启", color = ProductiveCyan, fontWeight = FontWeight.Bold)
        } else {
            GhostButton("去开启", onClick)
        }
    }
}

@Composable
private fun HonorGuide() {
    SectionCard {
        Text("荣耀保活指南", color = SoftWhite, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        Text("1. 打开最近任务，长按“时间去哪了”卡片并锁定。", color = MutedText)
        Spacer(Modifier.height(6.dp))
        Text("2. 手机管家 > 应用启动管理，关闭自动管理。", color = MutedText)
        Spacer(Modifier.height(6.dp))
        Text("3. 手动允许自启动、关联启动、后台活动。", color = MutedText)
        Spacer(Modifier.height(6.dp))
        Text("4. 电池设置中允许忽略电池优化，保持 WorkManager 与专注前台服务稳定。", color = MutedText)
    }
}

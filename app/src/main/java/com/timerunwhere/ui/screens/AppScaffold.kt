package com.timerunwhere.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.timerunwhere.ui.MainUiState
import com.timerunwhere.ui.theme.PureBlack

private enum class Tab(val title: String) {
    Overview("今日"),
    Focus("专注"),
    Settings("设置")
}

@Composable
fun AppScaffold(
    state: MainUiState,
    onOpenUsageAccess: () -> Unit,
    onOpenOverlayPermission: () -> Unit,
    onOpenBatterySettings: () -> Unit,
    onTokenChange: (String) -> Unit,
    onSaveToken: () -> Unit,
    onManualSync: () -> Unit,
    onStartFocus: (Int) -> Unit
) {
    var currentTab by rememberSaveable { mutableStateOf(Tab.Overview) }
    Scaffold(
        containerColor = PureBlack,
        bottomBar = {
            NavigationBar(containerColor = PureBlack) {
                NavigationBarItem(
                    selected = currentTab == Tab.Overview,
                    onClick = { currentTab = Tab.Overview },
                    icon = { Icon(Icons.Outlined.BarChart, contentDescription = null) },
                    label = { Text(Tab.Overview.title) }
                )
                NavigationBarItem(
                    selected = currentTab == Tab.Focus,
                    onClick = { currentTab = Tab.Focus },
                    icon = { Icon(Icons.Outlined.Timer, contentDescription = null) },
                    label = { Text(Tab.Focus.title) }
                )
                NavigationBarItem(
                    selected = currentTab == Tab.Settings,
                    onClick = { currentTab = Tab.Settings },
                    icon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
                    label = { Text(Tab.Settings.title) }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(PureBlack)
                .padding(padding)
        ) {
            when (currentTab) {
                Tab.Overview -> OverviewScreen(
                    state = state,
                    onOpenUsageAccess = onOpenUsageAccess,
                    onOpenOverlayPermission = onOpenOverlayPermission
                )
                Tab.Focus -> FocusScreen(
                    state = state,
                    onStartFocus = onStartFocus,
                    onOpenUsageAccess = onOpenUsageAccess,
                    onOpenOverlayPermission = onOpenOverlayPermission
                )
                Tab.Settings -> SettingsScreen(
                    state = state,
                    onTokenChange = onTokenChange,
                    onSaveToken = onSaveToken,
                    onManualSync = onManualSync,
                    onOpenUsageAccess = onOpenUsageAccess,
                    onOpenOverlayPermission = onOpenOverlayPermission,
                    onOpenBatterySettings = onOpenBatterySettings
                )
            }
        }
    }
}

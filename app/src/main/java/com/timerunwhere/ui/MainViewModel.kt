package com.timerunwhere.ui

import android.app.Application
import android.content.Context
import android.os.PowerManager
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.timerunwhere.data.local.FocusSessionStore
import com.timerunwhere.data.local.SecureTokenStore
import com.timerunwhere.data.network.SyncRepository
import com.timerunwhere.data.usage.UsageStatsCollector
import com.timerunwhere.focus.FocusGuardService
import com.timerunwhere.focus.FocusStateStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val appContext = application.applicationContext
    private val tokenStore = SecureTokenStore(appContext)
    private val usageCollector = UsageStatsCollector(appContext)
    private val focusSessionStore = FocusSessionStore(appContext)
    private val focusStateStore = FocusStateStore(appContext)
    private val syncRepository = SyncRepository(appContext)

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState

    init {
        viewModelScope.launch {
            tokenStore.tokenFlow.collect { token ->
                _uiState.update { it.copy(token = token, tokenDraft = token) }
            }
        }
        viewModelScope.launch {
            while (true) {
                refreshPermissionsAndUsage()
                delay(1_000L)
            }
        }
    }

    fun setTokenDraft(value: String) {
        _uiState.update { it.copy(tokenDraft = value.filter(Char::isDigit).take(6)) }
    }

    fun saveToken() {
        tokenStore.saveToken(_uiState.value.tokenDraft)
    }

    fun startFocus(minutes: Int) {
        FocusGuardService.start(appContext, minutes)
        refreshPermissionsAndUsage()
    }

    fun manualSync() {
        if (_uiState.value.isSyncing) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true, syncStatus = "正在发现 Windows 主机...") }
            val result = syncRepository.syncNow()
            _uiState.update {
                it.copy(
                    isSyncing = false,
                    syncStatus = result.getOrElse { error -> error.message ?: "同步失败" }
                )
            }
        }
    }

    fun refreshPermissionsAndUsage() {
        viewModelScope.launch {
            val usages = withContext(Dispatchers.IO) { usageCollector.collectToday() }
            val now = System.currentTimeMillis()
            val remaining = (focusStateStore.endAtMillis() - now).coerceAtLeast(0L)
            _uiState.update {
                it.copy(
                    usages = usages,
                    focusMillisToday = focusSessionStore.todayFocusMillis(),
                    hasUsageAccess = usageCollector.hasUsageAccess(),
                    canDrawOverlays = Settings.canDrawOverlays(appContext),
                    ignoringBatteryOptimizations = isIgnoringBatteryOptimizations(),
                    focusRemainingMillis = remaining
                )
            }
        }
    }

    private fun isIgnoringBatteryOptimizations(): Boolean {
        val manager = appContext.getSystemService(Context.POWER_SERVICE) as PowerManager
        return manager.isIgnoringBatteryOptimizations(appContext.packageName)
    }
}

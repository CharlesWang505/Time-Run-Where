package com.timerunwhere.data.network

import android.content.Context
import com.timerunwhere.data.local.FocusSessionStore
import com.timerunwhere.data.local.SecureTokenStore
import com.timerunwhere.data.usage.UsageStatsCollector

class SyncRepository(context: Context) {
    private val appContext = context.applicationContext
    private val tokenStore = SecureTokenStore(appContext)
    private val usageCollector = UsageStatsCollector(appContext)
    private val focusSessionStore = FocusSessionStore(appContext)
    private val discoveryClient = DiscoveryClient()
    private val syncClient = SyncClient()

    suspend fun syncNow(): Result<String> {
        val token = tokenStore.readToken()
            ?: return Result.failure(IllegalStateException("请先输入 6 位配对 Token"))
        if (token.length != 6) {
            return Result.failure(IllegalStateException("配对 Token 必须是 6 位数字"))
        }
        val ip = discoveryClient.discoverServerIp(token)
            ?: return Result.failure(IllegalStateException("未发现 Windows 同步服务，请确认同一 Wi-Fi 且防火墙允许 8000/8001"))
        val apps = usageCollector.collectToday()
        val payload = SyncPayloadBuilder.build(
            appContext,
            apps,
            focusSessionStore.todayFocusMillis()
        )
        return syncClient.postUsage(ip, token, payload).map { "已同步到 $ip" }
    }
}

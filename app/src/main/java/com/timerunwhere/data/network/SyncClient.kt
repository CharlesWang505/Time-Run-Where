package com.timerunwhere.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

class SyncClient {
    private val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    suspend fun postUsage(serverIp: String, token: String, jsonPayload: String): Result<String> =
        withContext(Dispatchers.IO) {
            runCatching {
                val request = Request.Builder()
                    .url("http://$serverIp:8000/api/sync")
                    .header("Authorization", "Bearer $token")
                    .post(jsonPayload.toRequestBody("application/json; charset=utf-8".toMediaType()))
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        error("HTTP ${response.code}: ${response.body?.string().orEmpty()}")
                    }
                    response.body?.string().orEmpty().ifBlank { "同步成功" }
                }
            }
        }
}

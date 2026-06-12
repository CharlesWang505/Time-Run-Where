package com.timerunwhere.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class DiscoveryClient(
    private val port: Int = 8001,
    private val timeoutMillis: Int = 4_000
) {
    suspend fun discoverServerIp(token: String): String? = withContext(Dispatchers.IO) {
        runCatching {
            DatagramSocket().use { socket ->
                socket.broadcast = true
                socket.soTimeout = timeoutMillis
                val payload = "DISCOVER:$token".toByteArray(Charsets.UTF_8)
                val request = DatagramPacket(
                    payload,
                    payload.size,
                    InetAddress.getByName("255.255.255.255"),
                    port
                )
                socket.send(request)

                val buffer = ByteArray(512)
                val response = DatagramPacket(buffer, buffer.size)
                socket.receive(response)
                parseServerIp(
                    String(response.data, 0, response.length, Charsets.UTF_8),
                    response.address.hostAddress
                )
            }
        }.getOrNull()
    }

    private fun parseServerIp(message: String, fallback: String?): String? {
        val trimmed = message.trim()
        return when {
            trimmed.startsWith("SERVER:", ignoreCase = true) ->
                trimmed.substringAfter(":").trim().ifBlank { fallback }
            trimmed.startsWith("IP:", ignoreCase = true) ->
                trimmed.substringAfter(":").trim().ifBlank { fallback }
            trimmed.matches(Regex("""\d{1,3}(\.\d{1,3}){3}""")) -> trimmed
            else -> fallback
        }
    }
}

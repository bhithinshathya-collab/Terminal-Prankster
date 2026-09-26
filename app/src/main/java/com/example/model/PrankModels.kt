package com.example.model

import android.net.Uri
import androidx.annotation.DrawableRes
import com.example.R

enum class AppScreen {
    SETUP,
    SPEECH,
    EXTRACTION,
    RESULTS,
    REVEALED
}

enum class SpeechDuration(val label: String, val seconds: Int) {
    THIRTY_SECONDS("30s", 30),
    ONE_MINUTE("1 min", 60),
    THREE_MINUTES("3 min", 180)
}

data class TargetDevice(
    val id: String,
    val title: String,
    val subtitle: String,
    val ipAddress: String,
    val port: String,
    val protocol: String,
    val exploitCve: String,
    val category: String
)

val DEFAULT_TARGETS = listOf(
    TargetDevice(
        id = "security_cam",
        title = "Security Camera (Surveillance)",
        subtitle = "RTSP Live Stream • H.264 Encoder",
        ipAddress = "192.168.1.108",
        port = "554 / RTSP",
        protocol = "ONVIF/RTSP",
        exploitCve = "CVE-2026-RTSP-CAM-BYPASS",
        category = "SURVEILLANCE"
    ),
    TargetDevice(
        id = "smart_fridge",
        title = "Smart Fridge / IoT Kitchen",
        subtitle = "Internal Pantry Cam & Storage Node",
        ipAddress = "192.168.1.144",
        port = "8080 / HTTP",
        protocol = "MQTT/WebSocket",
        exploitCve = "CVE-2026-IOT-SMART-APPLIANCE",
        category = "SMART IOT"
    ),
    TargetDevice(
        id = "friend_device",
        title = "Friend's Nearby Device",
        subtitle = "Bluetooth Low Energy & Direct Wi-Fi",
        ipAddress = "10.0.0.72",
        port = "443 / TLS",
        protocol = "BLE/P2P Direct",
        exploitCve = "CVE-2026-P2P-MEMORY-INJECT",
        category = "MOBILE"
    ),
    TargetDevice(
        id = "smart_tv_hub",
        title = "Smart TV & Home Gateway",
        subtitle = "Integrated Webcam & Microphone Node",
        ipAddress = "192.168.1.1",
        port = "8443 / SSL",
        protocol = "UPnP/DLNA",
        exploitCve = "CVE-2026-UPNP-GATEWAY-INJECT",
        category = "HOME NETWORK"
    ),
    TargetDevice(
        id = "custom_iot",
        title = "Anything with Internet Connection",
        subtitle = "Universal Port Scan & Auto-Exfiltrator",
        ipAddress = "172.16.24.99",
        port = "80 / 8080 / 22",
        protocol = "RAW TCP DUMP",
        exploitCve = "CVE-2026-UNIVERSAL-ZERO-DAY",
        category = "UNIVERSAL"
    )
)

data class MediaPayload(
    val uri: Uri? = null,
    val isVideo: Boolean = false,
    @DrawableRes val defaultDrawableRes: Int? = R.drawable.sample_cctv_feed_1789199039912,
    val fileName: String = "cam_stream_intercept_01.mp4",
    val fileSize: String = "28.4 MB"
)

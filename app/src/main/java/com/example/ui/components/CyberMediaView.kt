package com.example.ui.components

import android.widget.VideoView
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.model.MediaPayload
import com.example.ui.theme.TerminalCyan
import com.example.ui.theme.TerminalGreen
import com.example.ui.theme.TerminalRed
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CyberMediaView(
    payload: MediaPayload,
    modifier: Modifier = Modifier,
    targetName: String = "Security Camera 01",
    showWatermark: Boolean = true
) {
    val context = LocalContext.current
    val infiniteTransition = rememberInfiniteTransition(label = "cctv_hud")
    val recAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rec_blink"
    )

    val timeStamp = remember {
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Black)
            .border(1.5.dp, TerminalGreen.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
    ) {
        // Media Content
        if (payload.isVideo && payload.uri != null) {
            AndroidView(
                factory = { ctx ->
                    VideoView(ctx).apply {
                        setVideoURI(payload.uri)
                        setOnPreparedListener { mp ->
                            mp.isLooping = true
                            mp.setVolume(0.5f, 0.5f)
                            start()
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        } else if (payload.uri != null) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(payload.uri)
                    .crossfade(true)
                    .build(),
                contentDescription = "Intercepted Media Stream",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            val resId = payload.defaultDrawableRes ?: R.drawable.sample_cctv_feed_1789199039912
            Image(
                painter = painterResource(id = resId),
                contentDescription = "CCTV Intercepted Footage",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // Surveillance HUD Crosshair
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val crossSize = 24f

            // Center target
            drawLine(
                color = TerminalGreen.copy(alpha = 0.4f),
                start = Offset(cx - crossSize, cy),
                end = Offset(cx + crossSize, cy),
                strokeWidth = 2f
            )
            drawLine(
                color = TerminalGreen.copy(alpha = 0.4f),
                start = Offset(cx, cy - crossSize),
                end = Offset(cx, cy + crossSize),
                strokeWidth = 2f
            )
            drawCircle(
                color = TerminalGreen.copy(alpha = 0.25f),
                radius = 36f,
                center = Offset(cx, cy),
                style = Stroke(width = 1.5f)
            )

            // Corner brackets
            val bracketLen = 20f
            val pad = 16f
            // Top Left
            drawLine(TerminalCyan.copy(alpha = 0.5f), Offset(pad, pad), Offset(pad + bracketLen, pad), 2f)
            drawLine(TerminalCyan.copy(alpha = 0.5f), Offset(pad, pad), Offset(pad, pad + bracketLen), 2f)
            // Top Right
            drawLine(TerminalCyan.copy(alpha = 0.5f), Offset(size.width - pad, pad), Offset(size.width - pad - bracketLen, pad), 2f)
            drawLine(TerminalCyan.copy(alpha = 0.5f), Offset(size.width - pad, pad), Offset(size.width - pad, pad + bracketLen), 2f)
            // Bottom Left
            drawLine(TerminalCyan.copy(alpha = 0.5f), Offset(pad, size.height - pad), Offset(pad + bracketLen, size.height - pad), 2f)
            drawLine(TerminalCyan.copy(alpha = 0.5f), Offset(pad, size.height - pad), Offset(pad, size.height - pad - bracketLen), 2f)
            // Bottom Right
            drawLine(TerminalCyan.copy(alpha = 0.5f), Offset(size.width - pad, size.height - pad), Offset(size.width - pad - bracketLen, size.height - pad), 2f)
            drawLine(TerminalCyan.copy(alpha = 0.5f), Offset(size.width - pad, size.height - pad), Offset(size.width - pad, size.height - pad - bracketLen), 2f)
        }

        // Top CCTV Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .align(Alignment.TopStart),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(TerminalRed.copy(alpha = recAlpha))
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "● REC",
                color = TerminalRed,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = targetName.uppercase(),
                color = TerminalGreen,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "1080P • 30FPS",
                color = Color.White.copy(alpha = 0.8f),
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp
            )
        }

        // Bottom CCTV Footer
        if (showWatermark) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .align(Alignment.BottomStart),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = null,
                    tint = TerminalCyan,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "LIVE FEED • $timeStamp",
                    color = Color.White.copy(alpha = 0.9f),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "EXFIL_OK",
                    color = TerminalGreen,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            }
        }
    }
}

package com.example.ui.terminal

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundEffects
import com.example.model.MediaPayload
import com.example.model.TargetDevice
import com.example.ui.components.CyberMediaView
import com.example.ui.theme.TerminalAmber
import com.example.ui.theme.TerminalCyan
import com.example.ui.theme.TerminalGreen
import com.example.ui.theme.TerminalRed
import com.example.ui.theme.TerminalSurface
import com.example.ui.theme.TerminalSurfaceVariant
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class TerminalLog(
    val text: String,
    val color: Color = TerminalGreen
)

@Composable
fun TerminalExtractionScreen(
    target: TargetDevice,
    payload: MediaPayload,
    onExtractionComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val logs = remember { mutableStateListOf<TerminalLog>() }
    var progress by remember { mutableFloatStateOf(0f) }
    val listState = rememberLazyListState()

    val cursorTransition = rememberInfiniteTransition(label = "cursor")
    val cursorAlpha by cursorTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursor_blink"
    )

    val scriptLines = remember {
        listOf(
            TerminalLog("root@kali-box:~# ./exploit_runner.sh --target ${target.ipAddress}", TerminalCyan),
            TerminalLog("[+] Probing target: ${target.title}", TerminalGreen),
            TerminalLog("[+] Handshake port ${target.port} (${target.protocol})... OK", TerminalGreen),
            TerminalLog("[*] Infiltrating firewall via ${target.exploitCve}...", TerminalAmber),
            TerminalLog("[+] Buffer overflow injected into memory address 0x7FFF82A0", TerminalGreen),
            TerminalLog("[+] Root shell spawned! Host authentication bypassed.", TerminalCyan),
            TerminalLog("[*] SCANNING INTERNAL FILESYSTEM...", TerminalAmber),
            TerminalLog(" -> /var/media/recordings/ [FOUND]", TerminalGreen),
            TerminalLog(" -> /etc/shadow [DUMPED]", TerminalGreen),
            TerminalLog("[*] EXTRACTING FILES AND STREAM PACKETS...", TerminalAmber),
            TerminalLog("[+] Intercepting live video buffer: ${payload.fileName}", TerminalGreen),
            TerminalLog("[+] Writing decrypted chunks: chunk_001.bin -> 4.2 MB", TerminalGreen),
            TerminalLog("[+] Writing decrypted chunks: chunk_002.bin -> 12.8 MB", TerminalGreen),
            TerminalLog("[+] Writing decrypted chunks: chunk_003.bin -> 24.1 MB", TerminalGreen),
            TerminalLog("[+] File extraction status: ${payload.fileSize} assembled inside terminal!", TerminalCyan),
            TerminalLog("[✓] COMPLETE: Target files successfully exfiltrated!", TerminalGreen)
        )
    }

    LaunchedEffect(Unit) {
        for (i in scriptLines.indices) {
            delay(500)
            logs.add(scriptLines[i])
            progress = (i + 1).toFloat() / scriptLines.size
            listState.animateScrollToItem(logs.size - 1)
            SoundEffects.playHackerBeep(800f + (i * 30), 25)
        }
        delay(700)
        SoundEffects.playDataPacketChirp()
        onExtractionComplete()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Terminal Window Header
        Card(
            colors = CardDefaults.cardColors(containerColor = TerminalSurface),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, TerminalGreen.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(TerminalRed))
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(TerminalAmber))
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(TerminalGreen))
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        imageVector = Icons.Default.Terminal,
                        contentDescription = null,
                        tint = TerminalGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "TERMINAL: EXFILTRATING FILES...",
                        color = TerminalGreen,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        color = TerminalCyan,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = TerminalGreen,
                    trackColor = TerminalSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Live Media Extraction Preview (Appears as files are being extracted!)
        AnimatedVisibility(visible = progress > 0.4f) {
            Card(
                colors = CardDefaults.cardColors(containerColor = TerminalSurfaceVariant),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .border(1.dp, TerminalCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    CyberMediaView(
                        payload = payload,
                        targetName = target.title,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Overlay banner
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(TerminalCyan.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                            .border(1.dp, TerminalCyan, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "EXTRACTING RAW STREAM: ${(progress * 100).toInt()}%",
                            color = TerminalCyan,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Terminal Console Logs
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF060A0D)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .border(1.dp, TerminalGreen.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(logs) { log ->
                    Text(
                        text = log.text,
                        color = log.color,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }

                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "root@kali-box:~# ",
                            color = TerminalCyan,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                        Box(
                            modifier = Modifier
                                .size(width = 6.dp, height = 12.dp)
                                .background(TerminalGreen.copy(alpha = cursorAlpha))
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Skip / Proceed Button
        Button(
            onClick = {
                scope.launch { SoundEffects.playDataPacketChirp() }
                onExtractionComplete()
            },
            colors = ButtonDefaults.buttonColors(containerColor = TerminalGreen),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("finish_extraction_button")
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (progress >= 1f) Icons.Default.CheckCircle else Icons.Default.FastForward,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (progress >= 1f) "VIEW FINISHED RESULTS" else "SKIP TO RESULTS",
                    color = Color.Black,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

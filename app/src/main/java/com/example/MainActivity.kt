package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.model.AppScreen
import com.example.model.DEFAULT_TARGETS
import com.example.model.MediaPayload
import com.example.model.SpeechDuration
import com.example.model.TargetDevice
import com.example.notification.NotificationHelper
import com.example.ui.components.MatrixBackground
import com.example.ui.results.PrankResultsScreen
import com.example.ui.reveal.PrankRoastedScreen
import com.example.ui.speech.SpeechTimerScreen
import com.example.ui.target.TargetSetupScreen
import com.example.ui.terminal.TerminalExtractionScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

  private var onNotificationRevealTriggered: (() -> Unit)? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val shouldRevealFromNotification = intent?.getBooleanExtra(NotificationHelper.EXTRA_PRANK_REVEAL, false) == true

    setContent {
      MyApplicationTheme {
        var currentScreen by remember {
          mutableStateOf(if (shouldRevealFromNotification) AppScreen.REVEALED else AppScreen.SETUP)
        }
        var selectedTarget by remember { mutableStateOf(DEFAULT_TARGETS[0]) }
        var payload by remember { mutableStateOf(MediaPayload()) }
        var selectedDuration by remember { mutableStateOf(SpeechDuration.THIRTY_SECONDS) }

        onNotificationRevealTriggered = {
          currentScreen = AppScreen.REVEALED
        }

        BackHandler(enabled = currentScreen != AppScreen.SETUP) {
          currentScreen = when (currentScreen) {
            AppScreen.SPEECH -> AppScreen.SETUP
            AppScreen.EXTRACTION -> AppScreen.SETUP
            AppScreen.RESULTS -> AppScreen.SETUP
            AppScreen.REVEALED -> AppScreen.RESULTS
            AppScreen.SETUP -> AppScreen.SETUP
          }
        }

        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          MatrixBackground(modifier = Modifier.padding(innerPadding)) {
            AnimatedContent(
              targetState = currentScreen,
              transitionSpec = { fadeIn() togetherWith fadeOut() },
              label = "screen_transition"
            ) { screen ->
              when (screen) {
                AppScreen.SETUP -> {
                  TargetSetupScreen(
                    selectedTarget = selectedTarget,
                    onSelectTarget = { selectedTarget = it },
                    payload = payload,
                    onPayloadChanged = { payload = it },
                    selectedDuration = selectedDuration,
                    onDurationChanged = { selectedDuration = it },
                    onStartPrank = { currentScreen = AppScreen.SPEECH }
                  )
                }

                AppScreen.SPEECH -> {
                  SpeechTimerScreen(
                    target = selectedTarget,
                    duration = selectedDuration,
                    onSpeechComplete = { currentScreen = AppScreen.EXTRACTION },
                    onCancel = { currentScreen = AppScreen.SETUP }
                  )
                }

                AppScreen.EXTRACTION -> {
                  TerminalExtractionScreen(
                    target = selectedTarget,
                    payload = payload,
                    onExtractionComplete = { currentScreen = AppScreen.RESULTS }
                  )
                }

                AppScreen.RESULTS -> {
                  PrankResultsScreen(
                    target = selectedTarget,
                    payload = payload,
                    onShowReveal = { currentScreen = AppScreen.REVEALED }
                  )
                }

                AppScreen.REVEALED -> {
                  PrankRoastedScreen(
                    target = selectedTarget,
                    payload = payload,
                    onResetPrank = { currentScreen = AppScreen.SETUP }
                  )
                }
              }
            }
          }
        }
      }
    }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    if (intent.getBooleanExtra(NotificationHelper.EXTRA_PRANK_REVEAL, false)) {
      onNotificationRevealTriggered?.invoke()
    }
  }
}

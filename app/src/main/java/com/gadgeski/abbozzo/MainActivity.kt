package com.gadgeski.abbozzo

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gadgeski.abbozzo.ui.screen.CaptureScreen
import com.gadgeski.abbozzo.ui.screen.InboxScreen
import com.gadgeski.abbozzo.ui.theme.AbbozzoTheme
import com.gadgeski.abbozzo.ui.theme.BlackBackground
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val sharedText = handleSendIntent(intent)
        val startDestination = if (sharedText != null) "capture" else "inbox"

        setContent {
            AbbozzoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BlackBackground
                ) {
                    AppNavigation(
                        startDestination = startDestination,
                        sharedText = sharedText
                    )
                }
            }
        }
    }

    private fun handleSendIntent(intent: Intent): String? {
        if (Intent.ACTION_SEND == intent.action && "text/plain" == intent.type) {
            return intent.getStringExtra(Intent.EXTRA_TEXT)
        }
        return null
    }
}

@Composable
fun AppNavigation(startDestination: String, sharedText: String?) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {
        composable("inbox") {
            InboxScreen()
        }
        composable("capture") {
            CaptureScreen(
                sharedText = sharedText,
                onNavigateToInbox = {
                    navController.navigate("inbox") {
                        popUpTo("capture") { inclusive = true }
                    }
                }
            )
        }
    }
}
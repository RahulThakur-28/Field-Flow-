package com.rahul.fieldflow

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.rahul.fieldflow.core.navigation.AppNavGraph
import com.rahul.fieldflow.domain.model.AppTheme
import com.rahul.fieldflow.ui.theme.FieldFlowTheme
import dagger.hilt.android.AndroidEntryPoint
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.handleDeeplinks
import org.maplibre.android.MapLibre
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var supabaseClient: SupabaseClient

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(
            "FIELD_FLOW_STARTUP",
            "MainActivity onCreate started"
        )

        // Initialize MapLibre BEFORE any MapView is created
        MapLibre.getInstance(this)

        supabaseClient.handleDeeplinks(intent)

        enableEdgeToEdge()

        setContent {
            val theme by viewModel.theme.collectAsState()
            val isDarkTheme = when (theme) {
                AppTheme.SYSTEM -> isSystemInDarkTheme()
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
            }

            FieldFlowTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()

                AppNavGraph(
                    navController = navController
                )
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)

        Log.d(
            "FIELD_FLOW_STARTUP",
            "MainActivity onNewIntent"
        )

        supabaseClient.handleDeeplinks(intent)
    }
}

@Composable
fun Greeting(name: String) {
    Text(
        text = "Hello $name!"
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FieldFlowTheme {
        Greeting("Android")
    }
}
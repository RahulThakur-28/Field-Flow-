package com.rahul.fieldflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.rahul.fieldflow.core.navigation.AppNavGraph
import com.rahul.fieldflow.ui.theme.FieldFlowTheme
import dagger.hilt.android.AndroidEntryPoint
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.handleDeeplinks
import javax.inject.Inject

import android.util.Log

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var supabaseClient: SupabaseClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("FIELD_FLOW_STARTUP", "MainActivity onCreate started")
        supabaseClient.handleDeeplinks(intent)
        enableEdgeToEdge()
        setContent {
            FieldFlowTheme {
                val navController = rememberNavController()
                AppNavGraph(navController = navController)
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        Log.d("FIELD_FLOW_STARTUP", "MainActivity onNewIntent")
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

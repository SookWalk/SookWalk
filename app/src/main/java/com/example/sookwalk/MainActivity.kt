package com.example.sookwalk

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sookwalk.presentation.screens.map.PlacesBottomSheet
import com.example.sookwalk.presentation.viewmodel.PlacesViewModel
import com.example.sookwalk.presentation.viewmodel.ThemeViewModel
import com.example.sookwalk.ui.theme.SookWalkTheme
import com.google.android.libraries.places.api.Places
import dagger.hilt.android.AndroidEntryPoint



@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeVM: ThemeViewModel = hiltViewModel()
            val isDark by themeVM.isDark.collectAsStateWithLifecycle()

            // Define a variable to hold the Places API key.
            val apiKey = BuildConfig.PLACES_API_KEY

            // Log an error if apiKey is not set.
            if (apiKey.isEmpty() || apiKey == "DEFAULT_API_KEY") {
                Log.e("Places test", "No api key")
                finish()

            }

            // Initialize the SDK
            Places.initializeWithNewPlacesApiEnabled(applicationContext, apiKey)

            // Create a new PlacesClient instance
            val placesClient = Places.createClient(this)


            val viewModel: PlacesViewModel = hiltViewModel()
            var showSheet by remember { mutableStateOf(true) }

            SookWalkTheme (darkTheme = isDark){
                PlacesBottomSheet(
                    viewModel = viewModel,
                    onItemClick = { place ->
                        // 테스트니까 아무 처리 안 해도 됨
                        Log.d("TEST", "Clicked: ${place.displayName}")
                    },
                    onDismiss = {
                        showSheet = false
                    }
                )            }
        }
    }
}

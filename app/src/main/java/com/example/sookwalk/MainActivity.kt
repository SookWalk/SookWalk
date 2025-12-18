package com.example.sookwalk

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.sookwalk.data.remote.StepForegroundService
import com.example.sookwalk.data.repository.SettingsRepository
import com.example.sookwalk.navigation.NavGraph
import com.example.sookwalk.presentation.viewmodel.ThemeViewModel
import com.example.sookwalk.ui.theme.SookWalkTheme
import com.example.sookwalk.utils.notification.NotificationHelper
import com.google.android.gms.maps.MapsInitializer
import com.google.android.libraries.places.api.Places
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isStepGranted = permissions[Manifest.permission.ACTIVITY_RECOGNITION] ?: false

        if (isStepGranted) {
            startStepService()
        } else {
//            Toast.makeText(this, "걸음 수를 측정하려면 권한이 필요해요! 😭", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapsInitializer.initialize(applicationContext)

        val apiKey = BuildConfig.PLACES_API_KEY

        if (apiKey.isEmpty() || apiKey == "DEFAULT_API_KEY") {
            Log.e("Places test", "No api key")
            finish()
            return
        }

        if (!Places.isInitialized()) {
            Places.initializeWithNewPlacesApiEnabled(applicationContext, apiKey)
        }

        lifecycleScope.launch {
            // 저장된 설정값을 한 번 가져옴 (first() 사용)
            val isNotiOn = settingsRepository.notificationFlow.first()
            val isLocOn = settingsRepository.locationFlow.first()

            checkAndRequestPermissions(isNotiOn, isLocOn)
        }

        enableEdgeToEdge()
        setContent {
            val themeVM: ThemeViewModel = hiltViewModel()
            val isDark by themeVM.isDark.collectAsStateWithLifecycle()
            val navController = rememberNavController()

            NotificationHelper.createNotificationChannel(this)

            SookWalkTheme (
                darkTheme = isDark,
                dynamicColor = false
            ) {
                NavGraph(navController)
            }
        }
    }

    private fun checkAndRequestPermissions(userWantsNotification: Boolean, userWantsLocation: Boolean) {
        val permissionsToRequest = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.ACTIVITY_RECOGNITION)
            }
        }

        if (userWantsNotification && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        if (userWantsLocation) {
            val hasFineLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            val hasCoarseLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

            if (!hasFineLocation || !hasCoarseLocation) {
                permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
                permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            startStepService()
        }
    }

    private fun startStepService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        val intent = Intent(this, StepForegroundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }
}
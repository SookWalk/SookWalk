package com.example.sookwalk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sookwalk.presentation.viewmodel.ThemeViewModel
import com.example.sookwalk.ui.theme.SookWalkTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeVM: ThemeViewModel = hiltViewModel()
            val isDark by themeVM.isDark.collectAsStateWithLifecycle()

            SookWalkTheme (darkTheme = isDark){

            }
        }
    }
}

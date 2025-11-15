package com.example.sookwalk.presentation.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults


@OptIn(ExperimentalMaterial3Api::class)
@Composable
// TopBar 함수 호출 시, 현재 화면의 이름을 함께 넘긴다
fun TopBar(
    screenName: String,
    onMenuClick: () -> Unit // 드로어 열림/닫힘 제어를 받아올 함수
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background, // 배경색
        ),

        navigationIcon = {
            IconButton(onClick = { /* Handle settings click */ }) {
                Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back")
            }
        },

        title = {
            Text(
                text = screenName,
                style = MaterialTheme.typography.bodyLarge
            ) },

        actions = {
            IconButton(onClick = { /* Handle settings click */ }) {
                Icon(Icons.Default.NotificationsNone, contentDescription = "Notifications")
            }
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Default.Menu, contentDescription = "Navigation Drawer")
            }
        }
    )
}

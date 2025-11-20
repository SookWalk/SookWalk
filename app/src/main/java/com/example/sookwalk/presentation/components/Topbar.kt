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
import androidx.compose.ui.graphics.Color


@OptIn(ExperimentalMaterial3Api::class)
@Composable
// TopBar 함수 호출 시, 현재 화면의 이름을 함께 넘긴다
fun TopBar(
    screenName: String,
    onBack: () -> Unit, // 뒤로 가기 함수 (단방향 흐름)
    onAlarmClick: () -> Unit,
    onMenuClick: () -> Unit // 드로어 열림/닫힘 제어를 받아올 함수
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            // 배경색, 지도 화면의 경우에는 투명하게 한다.
            containerColor = if(screenName == "지도") Color.Transparent else MaterialTheme.colorScheme.background,
        ),

        // 뒤로 가기
        navigationIcon = {
            // 메인 홈에는 뒤로가기 버튼 X
            if (screenName != "메인 홈") {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back")
                }
            }
        },

        title = {
            Text(
                text = screenName,
                style = MaterialTheme.typography.bodyLarge
            ) },

        actions = {
            IconButton(onClick = onAlarmClick) {
                Icon(Icons.Default.NotificationsNone, contentDescription = "Notifications")
            }
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Default.Menu, contentDescription = "Navigation Drawer")
            }
        }
    )
}

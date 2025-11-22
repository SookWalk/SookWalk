package com.example.sookwalk.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sookwalk.presentation.components.TopBar
import com.example.sookwalk.presentation.viewmodel.SettingsViewModel
import com.example.sookwalk.ui.theme.Grey20
import com.example.sookwalk.ui.theme.Grey80
import com.example.sookwalk.utils.notification.AlarmScheduler

@Preview(showBackground = true)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
){
    val dark = viewModel.darkMode.collectAsStateWithLifecycle().value
    val notification = viewModel.notification.collectAsStateWithLifecycle().value
    val location = viewModel.location.collectAsStateWithLifecycle().value
    val context = LocalContext.current

    LaunchedEffect(notification){
        if (notification){
            // 알림 스케줄러 등록
            AlarmScheduler.scheduleEveryday8AMAlarm(context)
            // TODO:goal 스케줄링 등록
        } else {
            // 알림 스케줄러 취소
            AlarmScheduler.cancelEveryday8AMAlarm(context)
            // TODO:goal 스케줄링 취소

        }
    }

    Scaffold(
        topBar = {TopBar("설정",  {})},
    ){ innerPadding ->
        Surface (
            modifier = Modifier.padding(innerPadding)
                                .fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ){
            Column (
                modifier = Modifier.padding(16.dp)
                    .fillMaxSize(),
            ) {
                Column(){
                    settingTitle("알림")
                    settingRow("알림 on/off", notification, viewModel::toggleNotification)
                    Spacer(modifier = Modifier.height(8.dp))
                    settingTitle("지도 설정")
                    settingRow("위치 추적 on/off", location, viewModel::toggleLocation)
                    Spacer(modifier = Modifier.height(8.dp))
                    settingTitle("화면 설정")
                    settingRow("다크 모드", dark, viewModel::toggleDarkMode)
                }
                Spacer(modifier = Modifier.weight(1f))
                settingVersion("1.0.0")
            }
        }
    }
}

@Composable
fun settingTitle(title: String){
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )
    HorizontalDivider(
        thickness = 1.dp,
        color = Grey80,
        )
}

@Composable
fun settingRow(settingText: String, checked: Boolean,
               onCheckedChange: (Boolean) -> Unit){
    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(settingText)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun settingVersion(ver: String){
    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Text("버전 정보 ${ver}")
        TextButton(onClick = {}){
            Text(
                text = "로그아웃",
                color = Grey20
            )
        }
    }
}



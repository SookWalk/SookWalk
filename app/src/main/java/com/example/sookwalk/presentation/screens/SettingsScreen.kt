package com.example.sookwalk.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sookwalk.presentation.viewmodel.SettingsViewModel
import com.example.sookwalk.presentation.components.TopBar

@Preview(showBackground = true)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
){
    val dark = viewModel.darkMode.collectAsStateWithLifecycle().value
    val notification = viewModel.notification.collectAsStateWithLifecycle().value
    val location = viewModel.location.collectAsStateWithLifecycle().value

    Scaffold(
        topBar = {TopBar("설정",  {})},

    ){ innerPadding ->
        Surface (
            modifier = Modifier.padding(innerPadding)
                                .fillMaxSize()
        ){
            Column () {
                settingTitle("알림")
                settingCard("알림 on/off", notification, viewModel::toggleNotification)
                Spacer(modifier = Modifier.width(8.dp))
                settingTitle("지도 설정")
                settingCard("위치 추적 on/off", location, viewModel::toggleLocation)
                Spacer(modifier = Modifier.width(8.dp))
                settingTitle("화면 설정")
                settingCard("다크 모드", dark, viewModel::toggleDarkMode)
                settingVersion("1.0.0")
            }
        }
    }
}

@Composable
fun settingTitle(title: String){
    Text(
        text = title,

    )
    HorizontalDivider(modifier = Modifier.width(1.dp))
}

@Composable
fun settingCard(settingText: String, checked: Boolean,
                onCheckedChange: (Boolean) -> Unit){
    Card(){
        Row (){
            Text(settingText)
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}


@Composable
fun settingVersion(ver: String){
    Row (){
        Text("버전 정보 ${ver}")
        TextButton(onClick = {}){
            Text(
                text = "로그아웃"
            )
        }
    }
}



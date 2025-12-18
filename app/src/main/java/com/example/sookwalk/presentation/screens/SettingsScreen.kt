package com.example.sookwalk.presentation.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.sookwalk.presentation.components.BottomNavBar
import com.example.sookwalk.presentation.components.TopBar
import com.example.sookwalk.presentation.viewmodel.SettingsViewModel
import com.example.sookwalk.ui.theme.Grey20
import com.example.sookwalk.ui.theme.Grey80
import com.example.sookwalk.utils.notification.AlarmScheduler
import androidx.compose.ui.platform.LocalLifecycleOwner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    navController: NavController,
    onBack: () -> Unit, // 뒤로 가기 함수 (단방향 흐름)
    onAlarmClick: () -> Unit,
    onMenuClick: () -> Unit // 드로어 열림/닫힘 제어를 받아올 함수
){
    val dark = settingsViewModel.darkMode.collectAsStateWithLifecycle().value
    val notification = settingsViewModel.notification.collectAsStateWithLifecycle().value
    val location = settingsViewModel.location.collectAsStateWithLifecycle().value
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // 1. 화면이 다시 보일 때(Resume), 실제 시스템 권한과 DataStore 상태를 동기화
    // (사용자가 설정 앱에서 권한을 끄고 돌아왔을 경우 대비)
    DisposableEffect(
        lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                syncPermissions(context, settingsViewModel)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // 2. 권한 요청 런처 (알림)
    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                settingsViewModel.toggleNotification(true)
            } else {
                // 거절 시 스위치 끄기 (DataStore 업데이트)
                settingsViewModel.toggleNotification(false)
                // 필요 시 여기에 Toast나 Snackbar 추가 가능
            }
        }
    )

    // 3. 권한 요청 런처 (위치)
    val locationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val isGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            if (isGranted) {
                settingsViewModel.toggleLocation(true)
            } else {
                settingsViewModel.toggleLocation(false)
            }
        }
    )

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
        topBar = {
            TopBar("설정",
                onBack, onAlarmClick, onMenuClick
            )
                 },
        bottomBar = {
            BottomNavBar(navController)
        }
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
                Column {
                    // --- 알림 설정 ---
                    settingTitle("알림")
                    settingRow(
                        settingText = "알림 on/off",
                        checked = notification,
                        onCheckedChange = { isChecked ->
                            if (isChecked) {
                                // 켜려고 할 때 -> 권한 먼저 확인
                                checkNotificationPermission(context, notificationLauncher) {
                                    settingsViewModel.toggleNotification(true)
                                }
                            } else {
                                // 끌 때 -> 그냥 끔
                                settingsViewModel.toggleNotification(false)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // --- 지도 설정 ---
                    settingTitle("지도 설정")
                    Column {
                        settingRow(
                            settingText = "위치추적 on/off",
                            checked = location,
                            onCheckedChange = { isChecked ->
                                if (isChecked) {
                                    // 켜려고 할 때 -> 권한 먼저 확인
                                    checkLocationPermission(context, locationLauncher) {
                                        settingsViewModel.toggleLocation(true)
                                    }
                                } else {
                                    settingsViewModel.toggleLocation(false)
                                }
                            }
                        )
                        Text(
                            text = "앱을 실행하지 않은 상태에서도 사용자의 위치를 추적합니다",
                            style = MaterialTheme.typography.bodySmall,
                            color = Grey80,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // --- 화면 설정 ---
                    settingTitle("화면 설정")
                    settingRow("다크 모드", dark, settingsViewModel::toggleDarkMode)
                }
                Spacer(modifier = Modifier.weight(1f))
                settingVersion("1.0.0")
            }
        }
    }
}

/**
 * 시스템 설정(권한 상태)과 앱 내 설정(DataStore)을 동기화하는 함수
 */
fun syncPermissions(context: Context, viewModel: SettingsViewModel) {
    // 1. 알림 권한 확인 (Android 13+)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val hasNotiPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        // 권한은 없는데 스위치가 켜져있다면 -> 끔
        if (!hasNotiPermission) {
            // ViewModel 내부 로직상 false로 설정하면 스케줄러도 취소되므로 안전함
            viewModel.toggleNotification(false)
        }
    }

    // 2. 위치 권한 확인
    val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    if (!hasFine && !hasCoarse) {
        viewModel.toggleLocation(false)
    }
}

/**
 * 알림 권한 체크 및 요청
 */
fun checkNotificationPermission(
    context: Context,
    launcher: ManagedActivityResultLauncher<String, Boolean>,
    onGranted: () -> Unit
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            onGranted()
        } else {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    } else {
        // 안드로이드 12 이하에서는 권한 불필요 (기본 허용)
        onGranted()
    }
}

/**
 * 위치 권한 체크 및 요청
 */
fun checkLocationPermission(
    context: Context,
    launcher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>,
    onGranted: () -> Unit
) {
    val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    if (hasFine || hasCoarse) {
        onGranted()
    } else {
        launcher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
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



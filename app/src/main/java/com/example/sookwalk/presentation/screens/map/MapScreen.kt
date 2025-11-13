import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.* // Material 3 import
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.sookwalk.screens.TopBar
import com.example.sookwalk.ui.theme.SookWalkTheme
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import android.Manifest
import androidx.compose.ui.Alignment


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onMenuClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // 위치 권한 상태
    var hasFine by remember { mutableStateOf(false) }
    var hasCoarse by remember { mutableStateOf(false) }

    // 권한 요청 런처
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        hasFine = result[Manifest.permission.ACCESS_FINE_LOCATION] == true
        hasCoarse = result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }


    // 최초 권한 체크 & 요청
    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    // 카메라 상태
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(SEOUL, 12f)
    }

    // FusedLocationProvider
    val fused = remember { LocationServices.getFusedLocationProviderClient(context) }

    // 마지막 위치 한 번 읽어 카메라 이동
    suspend fun moveToLastKnownLocation(): Boolean {
        val hasLocationPermission =
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (!hasLocationPermission) return false
        return try {
            val loc = fused.lastLocation.await()
            if (loc != null) {
                val me = LatLng(loc.latitude, loc.longitude)
                cameraPositionState.animate(
                    update = CameraUpdateFactory.newLatLngZoom(me, 16f),
                    durationMs = 600
                )
                true
            } else {
                false
            }
        } catch (_: Exception) {
            false
        }
    }

    // 지도 스타일 옵션 (선택)
//    val mapStyle: MapStyleOptions? = remember {
//        // raw/map_style.json 이 있다면 스타일 적용, 없으면 null
//        runCatching {
//            MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style)
//        }.getOrNull()
//    }

    // 지도 프로퍼티/Ui 설정
    val isLocationEnabled = hasFine || hasCoarse
//    val mapProperties by remember(isLocationEnabled, mapStyle) {
//        mutableStateOf(
//            MapProperties(
//                isMyLocationEnabled = isLocationEnabled,
//                mapStyleOptions = mapStyle
//            )
//        )
//    }
    val uiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                myLocationButtonEnabled = false,   // 기본 내 위치 버튼 숨김
                zoomControlsEnabled = false
            )
        )
    }

    Scaffold(
        topBar = {
            TopBar(
                screenName = "지도",
                onMenuClick = onMenuClick
            )
        },
        bottomBar = { ChallengeCreateBottomNavigation() },
        containerColor = MaterialTheme.colorScheme.background,

        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                // 리센터(현재 위치로 이동)
                ExtendedFloatingActionButton(
                    onClick = {
                        scope.launch { moveToLastKnownLocation() }
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    icon = { Icon(Icons.Default.MyLocation, contentDescription = "현재 위치로 이동") },
                    text = { Text("현재 위치로", fontWeight = FontWeight.SemiBold) }
                )
                Spacer(Modifier.height(12.dp))
            }
        }

    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
//                properties = mapProperties,
                uiSettings = uiSettings,
                cameraPositionState = cameraPositionState
            ) {
                // 예시 마커 (서울 시청)
                Marker(
                    state = rememberUpdatedMarkerState(position = SEOUL),
                    title = "서울",
                    snippet = "예시 마커"
                )
            }

            // 권한이 없을 때 안내 배너
            if (!isLocationEnabled) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(12.dp),
                    tonalElevation = 6.dp,
                    shape = MaterialTheme.shapes.large
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "현재 위치를 보려면 위치 권한이 필요합니다.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.width(12.dp))
                        TextButton(onClick = {
                            // 다시 권한 요청
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }) {
                            Text("권한 요청")
                        }
                    }
                }
            }
        }
    }
}
private val SEOUL = LatLng(37.5665, 126.9780)





// --- 하단 네비게이션 ---
@Composable
fun ChallengeCreateBottomNavigation() {

}

// --- 프리뷰 ---
@Preview(showBackground = true)
@Composable
fun ChallengeCreateScreenPreview() {
    SookWalkTheme(dynamicColor = false) {
        MapScreen(onMenuClick = {})
    }
}
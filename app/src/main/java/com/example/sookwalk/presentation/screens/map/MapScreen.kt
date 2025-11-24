package com.example.sookwalk.presentation.screens.map

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.sookwalk.presentation.components.BottomNavBar
import com.example.sookwalk.presentation.components.TopBar
import com.example.sookwalk.presentation.viewmodel.MapViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel,
    navController: NavController,
    onBack: () -> Unit,
    onAlarmClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()


    var showFavoritesSheet by remember { mutableStateOf(false) }
    val favoritesSheetState = rememberModalBottomSheetState()

    var showPlacesSheet by remember { mutableStateOf(false) }
    // skipPartiallyExpanded = true를 하면 시트가 처음부터 완전히 펼쳐집니다.
    val placesSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)


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

    // 마지막 위치 한 번 읽어 카메라 이동 (임시 구현)
    suspend fun moveToLastKnownLocation(): Boolean { return false }

    // UI Settings
    val uiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                myLocationButtonEnabled = false,
                zoomControlsEnabled = false
            )
        )
    }

    val isLocationEnabled = hasFine || hasCoarse

    Scaffold(
        bottomBar = { BottomNavBar(navController = rememberNavController()) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Box(
            Modifier.fillMaxSize()
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                uiSettings = uiSettings,
                cameraPositionState = cameraPositionState
            ) {
                Marker(
                    state = rememberUpdatedMarkerState(position = SEOUL),
                    title = "서울",
                    snippet = "예시 마커"
                )
            }

            var query by remember { mutableStateOf("") }
            var active by remember { mutableStateOf(false) }
            Box(modifier = Modifier.padding(top = 90.dp)) {
                MapSearchBar(
                    query = query,
                    onQueryChange = { query = it },
                    onSearch = { /* 검색 */ },
                    active = active,
                    onActiveChange = { active = it }
                )
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .fillMaxWidth()
                    .padding(bottom = padding.calculateBottomPadding() + 20.dp, end = 20.dp, start = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FloatingActionButton(
                    onClick = { scope.launch { moveToLastKnownLocation() } },
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.secondary,
                ) {
                    Icon(Icons.Default.MyLocation, contentDescription = "현재 위치로 이동")
                }

                FloatingActionButton(
                    onClick = { showFavoritesSheet = true },
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.secondary,
                ) {
                    Icon(Icons.Default.Star, contentDescription = "Favorites", tint = Color.White)
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
            ) {
                TopBar(
                    screenName = "지도",
                    onBack = onBack,
                    onMenuClick = onMenuClick,
                    onAlarmClick = onAlarmClick
                )
            }

            if (showFavoritesSheet) {
                FavoritesBottomSheet(
                    sheetState = favoritesSheetState,
                    onDismiss = { showFavoritesSheet = false },
                    onAddClick = { /* 추가 로직 */ },
                    onCategoryClick = {
                        showFavoritesSheet = false
                        showPlacesSheet = true
                    }
                )
            }

            if (showPlacesSheet) {
                PlacesBottomSheet(
                    sheetState = placesSheetState,
                    onDismissRequest = { showPlacesSheet = false }
                )
            }
        }
    }
}

private val SEOUL = LatLng(37.5665, 126.9780)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    active: Boolean,
    onActiveChange: (Boolean) -> Unit
) {
    DockedSearchBar(
        query = query,
        onQueryChange = onQueryChange,
        onSearch = { onSearch(query) },
        active = active,
        onActiveChange = onActiveChange,
        placeholder = { Text("Search text") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = null
            )
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null
            )
        },
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        colors = SearchBarDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surface,
            dividerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(28.dp)
    ) { /* Search suggestions 넣고 싶으면 여기 */ }
}

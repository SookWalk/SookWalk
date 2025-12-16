package com.example.sookwalk.presentation.screens.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
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
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.sookwalk.data.local.entity.map.SavedPlaceEntity
import com.example.sookwalk.data.local.entity.map.SearchHistoryEntity
import com.example.sookwalk.presentation.components.BottomNavBar
import com.example.sookwalk.presentation.components.TopBar
import com.example.sookwalk.presentation.viewmodel.MapViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel(),
    navController: NavController,
    onBack: () -> Unit,
    onAlarmClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Data Observation
    val categories by viewModel.favoriteCategories.collectAsState()
    val places by viewModel.selectedCategoryPlaces.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val suggestions by viewModel.searchSuggestions.collectAsState()

    // UI States
    var showFavoritesSheet by remember { mutableStateOf(false) }
    var showPlacesSheet by remember { mutableStateOf(false) }
    var showSearchSheet by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }

    val favoritesSheetState = rememberModalBottomSheetState()

    // Map & Location Setup
    val SOOKMYUNG = LatLng(37.546, 126.964)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(SOOKMYUNG, 15f)
    }
    val fused = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Permissions
    var hasFine by remember { mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) }
    var hasCoarse by remember { mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) }

    var placeToSave by remember { mutableStateOf<SavedPlaceEntity?>(null) }

    val searchHistory by viewModel.searchHistory.collectAsState()

    // Helper Function
    @SuppressLint("MissingPermission")
    suspend fun moveToCurrentLocation(): Boolean {
        if (!hasFine && !hasCoarse) return false
        return try {
            val loc = fused.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).await()
            if (loc != null) {
                cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(LatLng(loc.latitude, loc.longitude), 16f), 600)
                true
            } else false
        } catch (e: Exception) { false }
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { perms ->
        hasFine = perms[Manifest.permission.ACCESS_FINE_LOCATION] == true
        hasCoarse = perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (hasFine || hasCoarse) scope.launch { moveToCurrentLocation() }
    }

    LaunchedEffect(Unit) {
        viewModel.syncData()

        if (!hasFine && !hasCoarse) permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        else moveToCurrentLocation()
    }

    val mapProperties = remember(hasFine, hasCoarse) { MapProperties(isMyLocationEnabled = hasFine || hasCoarse) }
    val uiSettings by remember { mutableStateOf(
        MapUiSettings(
            myLocationButtonEnabled = false,
            zoomControlsEnabled = false
        )
    ) }

    Scaffold(
        bottomBar = { BottomNavBar(navController = navController) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Box(Modifier.fillMaxSize()) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                properties = mapProperties,
                uiSettings = uiSettings,
                cameraPositionState = cameraPositionState
            )

            // Search Bar
            var query by remember { mutableStateOf("") }
            var active by remember { mutableStateOf(false) }

            Box(modifier = Modifier.padding(top = 90.dp)) {
                MapSearchBar(
                    query = query,
                    onQueryChange = {
                        query = it
                        viewModel.onQueryChange(it)
                    },
                    onSearch = {
                        active = false
                        viewModel.onSearchFinished()
                        viewModel.search(it)
                        showSearchSheet = true
                    },
                    active = active,
                    onActiveChange = {
                        active = it
                        if (!it) viewModel.onSearchFinished()
                    },
                    suggestions = suggestions,
                    searchHistory = searchHistory,
                    onHistoryClick = { historyQuery ->
                        query = historyQuery
                        active = false
                        viewModel.search(historyQuery)
                        showSearchSheet = true
                    },
                    onDeleteClick = { viewModel.deleteSearchHistory(it) },
                    onSuggestionClick = { suggestion ->
                        val text = suggestion.getPrimaryText(null).toString()
                        query = text
                        active = false
                        viewModel.onSearchFinished()
                        viewModel.search(text)
                        showSearchSheet = true
                    }
                )
            }

            Row(
                modifier = Modifier.align(Alignment.BottomEnd).fillMaxWidth().padding(bottom = padding.calculateBottomPadding() + 20.dp, end = 20.dp, start = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FloatingActionButton(onClick = { scope.launch { moveToCurrentLocation() } }, shape = CircleShape, containerColor = MaterialTheme.colorScheme.secondary) {
                    Icon(Icons.Default.MyLocation, contentDescription = "Current Location", tint = Color.White)
                }
                FloatingActionButton(onClick = { showFavoritesSheet = true }, shape = CircleShape, containerColor = MaterialTheme.colorScheme.secondary) {
                    Icon(Icons.Default.Star, contentDescription = "Favorites", tint = Color.White)
                }
            }

            Column(modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth()) {
                TopBar(screenName = "지도", onBack = onBack, onMenuClick = onMenuClick, onAlarmClick = onAlarmClick)
            }

            if (showFavoritesSheet) {
                FavoritesBottomSheet(
                    sheetState = favoritesSheetState,
                    categories = categories,
                    onDismiss = { showFavoritesSheet = false },
                    onItemClick = { category ->
                        viewModel.selectCategory(category.id)
                        scope.launch { favoritesSheetState.hide() }.invokeOnCompletion {
                            showFavoritesSheet = false
                            showPlacesSheet = true
                        }
                    },
                    onAddClick = { showAddDialog = true },
                    onDeleteCategory = { viewModel.deleteCategory(it) }
                )
            }

            if (showPlacesSheet) {
                PlacesBottomSheet(
                    places = places,
                    onDismissRequest = { showPlacesSheet = false },
                    onStarClick = { place ->
                        placeToSave = place
                    }
                )
            }

            if (showSearchSheet) {
                PlacesBottomSheet(
                    places = searchResults,
                    onDismissRequest = {
                        showSearchSheet = false
                        viewModel.clearSearchResults()
                    },
                    onStarClick = { place ->
                        placeToSave = place
                    }
                )
            }

            if (placeToSave != null) {
                AddToCategoryDialog(
                    categories = categories.map{ it.category },
                    onDismiss = { placeToSave = null },
                    onConfirm = { selectedCategoryIds ->
                        viewModel.savePlaceToCategories(placeToSave!!, selectedCategoryIds)
                        placeToSave = null
                    },
                    onCreateCategory = { name, color ->
                        viewModel.addCategory(name, color)
                    }
                )
            }

            if (showAddDialog) {
                AddFavoriteDialog(
                    onDismiss = { showAddDialog = false },
                    onAdd = { name, color -> viewModel.addCategory(name, color); showAddDialog = false }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    active: Boolean,
    onActiveChange: (Boolean) -> Unit,
    suggestions: List<AutocompletePrediction>,
    searchHistory: List<SearchHistoryEntity>,
    onHistoryClick: (String) -> Unit,
    onDeleteClick: (SearchHistoryEntity) -> Unit,
    onSuggestionClick: (AutocompletePrediction) -> Unit,
) {
    DockedSearchBar(
        query = query,
        onQueryChange = onQueryChange,
        onSearch = onSearch,
        active = active,
        onActiveChange = onActiveChange,
        placeholder = { Text("장소 검색") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
            if (active) {
                Icon(
                    modifier = Modifier.clickable {
                        if (query.isNotEmpty()) {
                            onQueryChange("")
                        } else {
                            onActiveChange(false)
                        }
                    },
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close"
                )
            }
        },
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        colors = SearchBarDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        shape = RoundedCornerShape(28.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (query.isEmpty()) {
                if (searchHistory.isNotEmpty()) {
                    item {
                        Text(
                            text = "최근 검색어",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }

                items(searchHistory) { history ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onHistoryClick(history.query) }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = history.query,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "삭제",
                            tint = Color.LightGray,
                            modifier = Modifier
                                .clickable { onDeleteClick(history) }
                                .padding(4.dp)
                        )
                    }
                }
            }
            // 검색어가 있으면 -> 추천 검색어(Autocomplete) 표시
            else {
                items(suggestions) { suggestion ->
                    val mainText = suggestion.getPrimaryText(null).toString()
                    val subText = suggestion.getSecondaryText(null).toString()

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSuggestionClick(suggestion) }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = mainText,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            if (subText.isNotEmpty()) {
                                Text(
                                    text = subText,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
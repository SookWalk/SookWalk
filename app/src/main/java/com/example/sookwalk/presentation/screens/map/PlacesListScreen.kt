package com.example.sookwalk.presentation.screens.map

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.sookwalk.data.local.entity.map.SavedPlaceEntity
import com.example.sookwalk.presentation.viewmodel.MapViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlacesBottomSheet(
    places: List<SavedPlaceEntity>?,
    isSearching: Boolean = false,
    onDismissRequest: () -> Unit,
    onPlaceClick: (SavedPlaceEntity) -> Unit = {}, // (선택) 장소 클릭 시 상세 이동용
    onStarClick: (SavedPlaceEntity) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 500.dp)
        ) {
            if (isSearching || places == null) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            else if (places.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("검색 결과가 없습니다.", color = Color.Gray)
                }
            }
            else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 40.dp)
                ) {
                    items(places) { place ->
                        PlaceItemUI(
                            place = place,
                            onStarClick = { onStarClick(place) }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 0.5.dp,
                            color = Color.LightGray.copy(0.5f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PlaceItemUI(
    place: SavedPlaceEntity,
    viewModel: MapViewModel = hiltViewModel(),
    onStarClick: () -> Unit
) {
    Column(Modifier.fillMaxWidth().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(place.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.width(6.dp))
            Text(place.category, color = Color.Gray, fontSize = 14.sp)
            Spacer(Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "저장",
                tint = Color(0xFFFFC107),
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onStarClick() })

        }
        Spacer(Modifier.height(4.dp))
        Text(place.address, color = Color.Gray, fontSize = 13.sp, maxLines = 1)
        Spacer(Modifier.height(12.dp))

        PlacePhotoRow(place.placeId, viewModel)
    }
}

@Composable
fun PlacePhotoRow(placeId: String, viewModel: MapViewModel) {
    val photos by produceState<List<Bitmap>>(initialValue = emptyList(), key1 = placeId) {
        value = viewModel.getPlacePhotos(placeId)
    }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(3) { index ->
            val bitmap = photos.getOrNull(index)
            Box(
                Modifier.weight(1f).aspectRatio(1f).clip(RoundedCornerShape(8.dp)).background(Color.LightGray.copy(0.3f)),
                contentAlignment = Alignment.Center
            ) {
                if (bitmap != null) {
                    Image(bitmap.asImageBitmap(), null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}

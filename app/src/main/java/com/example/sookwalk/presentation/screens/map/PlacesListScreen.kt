package com.example.sookwalk.presentation.screens.map

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.sookwalk.presentation.viewmodel.PlacesViewModel
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest

data class Places(
    val name: String,
    val category: String,
    val distance: String,
    val address: String,
    val imageUrls: List<String>,
    val isFavorite: Boolean
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlacesBottomSheet(
    viewModel: PlacesViewModel,
    onItemClick: (Place) -> Unit,
    onDismiss: () -> Unit
) {

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )

    val uiState by viewModel.uiState.collectAsState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = {
            BottomSheetDefaults.DragHandle()
        }
    ) {

        // ⭐ 내부 스크롤 영역
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
        ) {
            items(uiState) { model ->
                PlacesItem(
                    place = model.place,
                    isFavorite = model.isFavorite,
                    onFavoriteClick = { viewModel.onFavoriteClick(model.place.id!!) },
                    onClick = { onItemClick(model.place) }
                )
                Divider(color = Color(0xFFE0E0E0))            }
        }
    }
}


@Composable
fun PlacesItem(
    place: Place,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = place.displayName ?: "",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = mapPlaceType(place.primaryType),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                place.formattedAddress?.let {
                    Text(
                        text = it,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Star
                    else Icons.Default.StarBorder,
                    contentDescription = null,
                    tint = if (isFavorite) Color(0xFFFFD600) else Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        val photos = place.photoMetadatas?.take(3) ?: emptyList()

        if (photos.isNotEmpty()) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(photos) { metadata ->
                    GooglePlacePhoto(metadata)
                }
            }
        }
    }
}

@Composable
fun GooglePlacePhoto(
    metadata: PhotoMetadata,
    modifier: Modifier = Modifier
        .size(110.dp)
        .clip(RoundedCornerShape(12.dp))
) {
    val context = LocalContext.current
    val placesClient = remember { Places.createClient(context) }

    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(metadata) {
        val request = FetchPhotoRequest.builder(metadata)
            .setMaxWidth(600)
            .setMaxHeight(600)
            .build()

        placesClient.fetchPhoto(request)
            .addOnSuccessListener { response ->
                bitmap = response.bitmap
            }
            .addOnFailureListener {
                bitmap = null
            }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap!!.asImageBitmap(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    } else {
        Box(
            modifier = modifier.background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
fun mapPlaceType(type: String?): String {
    return when (type) {
        "korean_restaurant" -> "한식"
        "restaurant" -> "식당"
        "cafe" -> "카페"
        "bar" -> "술집"
        "bakery" -> "베이커리"
        "meal_delivery" -> "배달"
        "meal_takeaway" -> "포장"
        else -> type ?: "기타"
    }
}
// ViewModel이 Composable에게 전달할 최종 데이터 모델
data class PlaceUiModel(
    val place: Place,        // Google Place 원본 객체
    val isFavorite: Boolean  // 조합이 완료된 즐겨찾기 상태
)
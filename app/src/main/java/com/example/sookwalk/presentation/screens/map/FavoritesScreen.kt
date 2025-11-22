package com.example.sookwalk.presentation.screens.map

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sookwalk.ui.theme.SookWalkTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onAddClick: () -> Unit,
){
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ){
        // 하드 코딩
        val favorites = listOf(
            FavoriteItem("숙명여대 맛집", "60", Color(0xFF1E5EFF)),
            FavoriteItem("분좋카", "45", Color(0xFFFFE841)),
            FavoriteItem("빵집", "23", Color(0xFF4CCEB5)),
            FavoriteItem("도서관", "12", Color(0xFFBDBDBD))
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ){
            favorites.forEach { item ->
                FavoriteRow(item)
                Spacer(modifier = Modifier.height(20.dp))
            }

            Text(
                text = "+ 즐겨찾기 추가",
                fontSize = 16.sp,
                color = Color(0xFF555555),
                modifier = Modifier.clickable { onAddClick() }
            )
            Spacer(Modifier.height(32.dp))
        }
    }
}
data class FavoriteItem(
    val title: String,
    val distance: String,
    val iconColor: Color
)

@Composable
fun FavoriteRow(item: FavoriteItem) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ){
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(item.iconColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.Star,
                contentDescription = item.title,
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = item.title,
                fontSize = 18.sp,
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = "Distance",
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = item.distance,
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(start = 2.dp)

                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun FavoritesBottomSheetPreview() {
    SookWalkTheme {
        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )

        FavoritesBottomSheet(
            sheetState = sheetState,
            onDismiss = {},
            onAddClick = {}
        )
    }
}
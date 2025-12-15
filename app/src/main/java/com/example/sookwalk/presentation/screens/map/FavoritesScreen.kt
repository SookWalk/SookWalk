package com.example.sookwalk.presentation.screens.map

import android.R.attr.category
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sookwalk.data.local.entity.map.CategoryWithCount
import com.example.sookwalk.data.local.entity.map.FavoriteCategoryEntity

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FavoritesBottomSheet(
    sheetState: SheetState,
    categories: List<CategoryWithCount>,
    onDismiss: () -> Unit,
    onItemClick: (FavoriteCategoryEntity) -> Unit,
    onAddClick: () -> Unit,
    onDeleteCategory: (FavoriteCategoryEntity) -> Unit
) {
    var categoryToDelete by remember { mutableStateOf<FavoriteCategoryEntity?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 30.dp)
        ) {
            Text(
                text = "+ 즐겨찾기 추가",
                fontSize = 16.sp,
                color = Color(0xFF555555),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAddClick() }
                    .padding(vertical = 8.dp)
            )

            Spacer(Modifier.height(17.dp))

            LazyColumn {
                items(categories) { itemWithCount ->
                    FavoriteRow(
                        itemWithCount = itemWithCount,
                        onClick = { onItemClick(itemWithCount.category) },
                        onLongClick = { categoryToDelete = itemWithCount.category } // 길게 누르면 삭제 대상 설정
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }

        if (categoryToDelete != null) {
            AlertDialog(
                onDismissRequest = { categoryToDelete = null },
                title = { Text(text = "즐겨찾기 삭제") },
                text = { Text(text = "'${categoryToDelete?.name}'을(를) 삭제하시겠습니까?\n포함된 장소들도 함께 삭제됩니다.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            categoryToDelete?.let { onDeleteCategory(it) }
                            categoryToDelete = null
                        }
                    ) { Text("삭제", color = Color.Red) }
                },
                dismissButton = {
                    TextButton(onClick = { categoryToDelete = null }) { Text("취소") }
                },
                containerColor = Color.White
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FavoriteRow(
    itemWithCount: CategoryWithCount,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val category = itemWithCount.category

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { onClick() },
                onLongClick = { onLongClick() }
            )
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(Color(category.iconColor)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.Star,
                contentDescription = category.name,
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = category.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = "Count",
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "${itemWithCount.placeCount}",
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(start = 2.dp)
                )
            }
        }
    }
}
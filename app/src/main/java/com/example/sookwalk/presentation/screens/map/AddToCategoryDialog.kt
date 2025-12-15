package com.example.sookwalk.presentation.screens.map

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.sookwalk.data.local.entity.map.FavoriteCategoryEntity

@Composable
fun AddToCategoryDialog(
    categories: List<FavoriteCategoryEntity>,
    onDismiss: () -> Unit,
    onConfirm: (List<Long>) -> Unit,
    onCreateCategory: (String, Long) -> Unit
) {
    val selectedIds = remember { mutableStateListOf<Long>() }

    var showCreateDialog by remember { mutableStateOf(false) }

    if (showCreateDialog) {
        AddFavoriteDialog(
            onDismiss = { showCreateDialog = false },
            onAdd = { name, color ->
                onCreateCategory(name, color)
                showCreateDialog = false
            }
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().heightIn(min = 300.dp, max = 500.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {

                Text(
                    text = "즐겨찾기에 추가",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                Column(modifier = Modifier.weight(1f, fill = false)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showCreateDialog = true }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(24.dp).background(Color(0xFFE0E0E0), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("새 즐겨찾기 만들기", color = Color.Black, fontWeight = FontWeight.Medium)
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    if (categories.isEmpty()) {
                        Text("즐겨찾기가 없습니다.", color = Color.Gray, modifier = Modifier.padding(vertical = 12.dp))
                    } else {
                        LazyColumn {
                            items(categories) { category ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (selectedIds.contains(category.id)) selectedIds.remove(category.id)
                                            else selectedIds.add(category.id)
                                        }
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = selectedIds.contains(category.id),
                                        onCheckedChange = { isChecked ->
                                            if (isChecked) selectedIds.add(category.id)
                                            else selectedIds.remove(category.id)
                                        },
                                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF004D40))
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(Modifier.size(12.dp).background(Color(category.iconColor), CircleShape))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = category.name, fontSize = 16.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("취소", color = Color.Gray) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onConfirm(selectedIds) },
                        enabled = selectedIds.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004D40))
                    ) { Text("저장") }
                }
            }
        }
    }
}
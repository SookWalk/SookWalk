package com.example.sookwalk.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.ui.graphics.vector.ImageVector

// 하단 네비게이션 아이템을 정의하는 sealed class
sealed class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: String
) {
    object Home : BottomNavItem("홈", Icons.Default.Home, Routes.HOME)
    object Goals : BottomNavItem("목표", Icons.Default.SportsScore, "goals")
    object Rank : BottomNavItem("랭크", Icons.Default.Leaderboard, Routes.RANK)
    object Map : BottomNavItem("지도", Icons.Default.Map, "map")
}
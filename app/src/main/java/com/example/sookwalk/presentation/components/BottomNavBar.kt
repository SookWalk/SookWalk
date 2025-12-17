package com.example.sookwalk.presentation.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.sookwalk.navigation.BottomNavItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Goals,
        BottomNavItem.Rank,
        BottomNavItem.Map,
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.title, modifier = Modifier.size(26.dp)) },
                label = { Text(text = item.title, style = MaterialTheme.typography.labelSmall) },
                selected = currentRoute == item.route,
                onClick = {val destinationRoute = item.route

                    // 현재 노드가 이미 선택된 노드인지 확인 (선택 사항: 같은 버튼 반복 클릭 방지)
                    if (currentRoute != destinationRoute) {
                        navController.navigate(destinationRoute) {
                            // 메인 화면(보통 홈) 하위의 모든 스택을 비워 메모리 누수와 뒤로가기 꼬임 방지
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // 같은 화면이 여러 개 쌓이지 않도록 설정
                            launchSingleTop = true
                            // 이전 상태 복원 (스크롤 위치 등)
                            restoreState = false
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    unselectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = Color.Black,
                    unselectedTextColor = MaterialTheme.colorScheme.onPrimary,
                    indicatorColor = MaterialTheme.colorScheme.secondary
                ),
            )
        }
    }
}

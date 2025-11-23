package com.example.sookwalk.presentation.screens.goal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.sookwalk.presentation.components.BottomNavBar
import com.example.sookwalk.presentation.components.TopBar
import com.example.sookwalk.presentation.viewmodel.AuthViewModel
import com.example.sookwalk.presentation.viewmodel.GoalViewModel
import com.example.sookwalk.utils.goal.convertMillisToDateString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalScreen(
    viewModel: GoalViewModel,
    navController: NavController,
    onBack: () -> Unit,
    onAlarmClick: () -> Unit,
    onMenuClick: () -> Unit,
    onAddGoalClick: (String) -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis() // 오늘 날짜 기본값
    )

    Scaffold(
        topBar = {
            TopBar(
                screenName = "목표",
                onBack = onBack,
                onMenuClick = onMenuClick,
                onAlarmClick = onAlarmClick
            )
        },
        bottomBar = { BottomNavBar(navController = rememberNavController()) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            // 1. M3 DatePicker를 사용한 캘린더 카드
            CalendarCard(state = datePickerState)
            Spacer(modifier = Modifier.height(24.dp))
            // 2. M3 ListItem을 사용한 챌린지 카드
            ChallengesCard(
                onAddClick = {
                    // 선택된 날짜(Millis)를 가져와서 포맷팅
                    val selectedDateMillis = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                    val formattedDate = convertMillisToDateString(selectedDateMillis)

                    onAddGoalClick(formattedDate)
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


// 캘린더 카드 (M3 DatePicker 사용)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarCard(
    state: DatePickerState
) {
    val datePickerState = rememberDatePickerState(
        // 초기 선택된 날짜 (Epoch Millis)
        initialSelectedDateMillis = 1755481200000L
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        DatePicker(
            state = state,
            title = null,
            headline = null,
            showModeToggle = false, // 날짜/연도 선택 토글 숨김
            colors = DatePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.primary,
                selectedDayContainerColor = Color(0xFFB2D4BD),
                selectedDayContentColor = Color.White,
                todayDateBorderColor = MaterialTheme.colorScheme.surface,
                todayContentColor = MaterialTheme.colorScheme.surface,
                dayContentColor = MaterialTheme.colorScheme.onSurface,
                yearContentColor = MaterialTheme.colorScheme.onSurface,
                currentYearContentColor = MaterialTheme.colorScheme.onSurface,
                selectedYearContentColor = MaterialTheme.colorScheme.surface,
                weekdayContentColor = Color.Gray,

                navigationContentColor = MaterialTheme.colorScheme.onSurface,
                subheadContentColor = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}


// 챌린지 카드 (M3 ListItem 사용)
@Composable
fun ChallengesCard(
    onAddClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "4256 걸음",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = onAddClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFB2D4BD),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    elevation = ButtonDefaults.buttonElevation(5.dp)
                ) {
                    Text("챌린지 추가")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 챌린지 목록 (M3 ListItem 사용)
            ChallengeListItem(title = "5000보", subtitle = "아자아자 화이팅")
            ChallengeListItem(title = "100000보", subtitle = "아자아자 화이팅")
        }
    }
}

@Composable
fun ChallengeListItem(title: String, subtitle: String) {
    ListItem(
        headlineContent = {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
        supportingContent = {
            Text(subtitle, fontSize = 12.sp, color = Color.Gray)
        },
        leadingContent = {
            // 프로필 이미지
            Box(
                modifier = Modifier
                    .size(25.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFB2D4BD))
            )
        },
        trailingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "진행 중",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                IconButton(onClick = { /* 삭제 */ }) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "삭제", tint = Color.Gray)
                }
            }
        },
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        ),
        modifier = Modifier.padding(vertical = 4.dp)
    )
}
package com.example.sookwalk.presentation.screens.goal

import android.R.attr.fontWeight
import android.R.attr.subtitle
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.sookwalk.data.local.entity.goal.GoalEntity
import com.example.sookwalk.presentation.components.BottomNavBar
import com.example.sookwalk.presentation.components.TopBar
import com.example.sookwalk.presentation.viewmodel.AuthViewModel
import com.example.sookwalk.presentation.viewmodel.GoalViewModel
import com.example.sookwalk.presentation.viewmodel.StepViewModel
import com.example.sookwalk.utils.goal.convertMillisToDateString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalScreen(
    viewModel: GoalViewModel,
    stepViewModel: StepViewModel,
    navController: NavController,
    onBack: () -> Unit,
    onAlarmClick: () -> Unit,
    onMenuClick: () -> Unit,
    onAddGoalClick: (String) -> Unit
) {
    val context = LocalContext.current

    val goals by viewModel.goalsForSelectedDate.collectAsState()

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    var currentSteps by remember { mutableIntStateOf(0) }

    val todaySteps by stepViewModel.todaySteps.collectAsState() // 실시간 오늘 걸음 수
    var displayedSteps by remember { mutableIntStateOf(0) }

    LaunchedEffect(datePickerState.selectedDateMillis, todaySteps) {
        val millis = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
        val selectedDate = java.time.Instant.ofEpochMilli(millis)
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDate()

        val today = java.time.LocalDate.now()

        viewModel.updateSelectedDate(millis)

        if (selectedDate == today) {
            displayedSteps = todaySteps
        } else {
            val formatter = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val dateStr = formatter.format(java.util.Date(millis))
            displayedSteps = stepViewModel.getStepsForDate(dateStr) // suspend 함수 호출
        }
    }
    Scaffold(
        topBar = {
            TopBar(
                screenName = "목표",
                onBack = onBack,
                onMenuClick = onMenuClick,
                onAlarmClick = onAlarmClick
            )
        },
        bottomBar = { BottomNavBar(navController = navController) },
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
                currentSteps = displayedSteps,
                goals = goals,
                onAddClick = {
                    val selectedDateMillis = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                    val formatter = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                    val formattedDate = formatter.format(java.util.Date(selectedDateMillis))
                    onAddGoalClick(formattedDate)
                },
                onDeleteClick = { goal -> viewModel.deleteGoal(context, goal) }
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
    currentSteps: Int,
    goals: List<GoalEntity>,
    onAddClick: () -> Unit,
    onDeleteClick: (GoalEntity) -> Unit
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
                    text = "$currentSteps 걸음",
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

            if (goals.isEmpty()) {
                Text(
                    "등록된 챌린지가 없습니다.",
                    modifier = Modifier.padding(8.dp),
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            } else {
                goals.forEach { goal ->
                    ChallengeListItem(goal = goal, onDelete = { onDeleteClick(goal) })
                }
            }
        }
    }
}

@Composable
fun ChallengeListItem(
    goal: GoalEntity,
    onDelete: () -> Unit
) {
    val progress = if (goal.targetSteps > 0) {
        (goal.currentSteps.toFloat() / goal.targetSteps.toFloat()).coerceIn(0f, 1f)
    } else 0f

    ListItem(
        headlineContent = {
            // 제목: 목표 걸음 수
            Text("${goal.targetSteps}보", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
        supportingContent = {
            Column {
                if (goal.memo.isNotEmpty()) {
                    Text(
                        text = goal.memo,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // 진행률 바
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = Color(0xFFB2D4BD),
                    trackColor = Color.LightGray.copy(alpha = 0.5f),
                )
            }
        },
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(25.dp)
                    .clip(CircleShape)
                    .background(if(goal.isDone) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary)
            )
        },
        trailingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // 현재 진행 상황 텍스트
                Text(
                    text = "${goal.currentSteps} / ${goal.targetSteps}",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                // 삭제 버튼
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "삭제",
                        tint = Color.Gray
                    )
                }
            }
        },
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        ),
        modifier = Modifier.padding(vertical = 4.dp)
    )
}
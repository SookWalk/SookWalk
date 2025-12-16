package com.example.sookwalk.presentation.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowLeft
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.sookwalk.R
import com.example.sookwalk.data.local.entity.goal.GoalEntity
import com.example.sookwalk.presentation.components.BottomNavBar
import com.example.sookwalk.presentation.components.TopBar
import com.example.sookwalk.presentation.viewmodel.GoalViewModel
import com.example.sookwalk.presentation.viewmodel.StepViewModel
import com.example.sookwalk.ui.theme.Black
import com.example.sookwalk.ui.theme.Grey20
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    goalViewModel: GoalViewModel,
    stepViewModel: StepViewModel,
    navController: NavController,
    onBack: () -> Unit, // 뒤로 가기 함수 (단방향 흐름)
    onAlarmClick: () -> Unit,
    onMenuClick: () -> Unit, // 드로어 열림/닫힘 제어를 받아올 함수,
    onRankingBtnClick: () -> Unit,
    onGoToGoalsClick: () -> Unit
){
    val todaySteps by stepViewModel.todaySteps.collectAsState()
    val weekGoals by goalViewModel.weekGoals.collectAsState()

    LaunchedEffect(Unit) {
        goalViewModel.setThisWeek()
        stepViewModel.loadTodaySteps()
    }

    val goalsByDate = remember(weekGoals) { expandGoalsToDates(weekGoals) }

    val today = LocalDate.now()
    val todayGoals = goalsByDate[today].orEmpty()
    val todayMainGoal = todayGoals.firstOrNull()

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopBar("메인 홈",
                onBack, onAlarmClick, onMenuClick
            )},
        bottomBar = {
            BottomNavBar(navController)
        }
    ){ innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ){
            Spacer(modifier = Modifier.height(10.dp))

            // [상단] 오늘 날짜 & 대표 목표 카드
            MainHomeCard(
                goal = todayMainGoal,
                targetSteps = todayMainGoal?.targetSteps ?: 0,
                onCardClick = onGoToGoalsClick
            )

            Spacer(modifier = Modifier.height(20.dp))

            // [중단 1] 주차별 리스트
            WeekTitleCard("주차별")

            Spacer(modifier = Modifier.height(10.dp))

            WeekHomeList(
                goalsByDate = goalsByDate
            )

            Spacer(modifier = Modifier.height(20.dp))

            // [중단 2] 오늘의 걸음 수 (원형 그래프 포함)
            WalkHomeCard(
                targetSteps = todayMainGoal?.targetSteps, // 목표 걸음 수 (설정에서 가져오거나 하드코딩)
                currentGoalSteps = todayMainGoal?.currentSteps,
                currentTotalSteps = todaySteps,
                onCardClick = onGoToGoalsClick
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.fire), // 불꽃 아이콘 리소스 필요 (예: fire.png)
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "대항전",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            // [하단] 랭킹 카드
            RankHomeCard(onRankingBtnClick)

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun MainHomeCard(
    goal: GoalEntity?,
    targetSteps: Int,
    onCardClick: () -> Unit
){
    val today = LocalDate.now()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary // 이미지의 연한 초록색
        )
    ){
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${today.year}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "${today.monthValue}-${today.dayOfMonth}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 텍스트 내용
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "TODAY TODO",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = goal?.title ?: "등록된 목표 없음", // 목표가 없으면 기본 텍스트
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )
            }

            // 화살표 아이콘
            Icon(
                imageVector = Icons.Default.ArrowRight, // 적절한 아이콘으로 변경 (Rotation 필요할 수 있음)
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = Color.DarkGray
            )
        }
    }
}

@Composable
fun MainDateCard(){
    val today = LocalDate.now()
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ){
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)
        ){
            Text("${today.dayOfYear}")
            Text("${today.monthValue} - ${today.dayOfMonth}")
        }
    }
}

data class Goal(val title: String, val startDate: LocalDate)

@Composable
fun WeekTitleCard(title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(Color(0xFFB2D4BD), shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.calendar),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = Color.Black
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun WeekHomeList(
    goalsByDate: Map<LocalDate, List<GoalEntity>>
) {
    val today = LocalDate.now()
    val weekStart = today.with(java.time.DayOfWeek.MONDAY)
    val weekDates = (0..6).map { weekStart.plusDays(it.toLong()) }
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        weekDates.forEach { date ->
            val goals = goalsByDate[date].orEmpty()
            WeekHomeCard(date = date, goals = goals)
        }
    }
}

@Composable
fun WeekHomeCard(date: LocalDate, goals: List<GoalEntity>) {
    val isToday = date == LocalDate.now()
    val backgroundColor = if (isToday) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary // 오늘만 조금 더 진하게

    // 완료된 목표 개수 계산
    val completedCount = goals.count { it.isDone }
    val totalCount = goals.size
    val isAllDone = totalCount > 0 && completedCount == totalCount

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        modifier = Modifier.width(130.dp).height(100.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 상단: 체크박스 아이콘 + 날짜
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isAllDone) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = if(isAllDone) Color.Black else Color.Gray
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${date.monthValue}.${date.dayOfMonth}(${getKoreanDayOfWeek(date)})",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }

            // 하단: 목표 내용 요약
            if (goals.isEmpty()) {
                Text(
                    text = "목표 없음",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.LightGray
                )
            } else {
                Text(
                    text = if (isAllDone) "모두 완료!" else "${totalCount}개의 목표 중\n${completedCount}개 달성",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun WalkHomeCard(
    targetSteps: Int?,
    currentGoalSteps: Int?,
    currentTotalSteps: Int,
    onCardClick: () -> Unit
) {
    // 목표가 존재하는지 확인 (null이 아니고 0보다 커야 함)
    val isGoalSet = targetSteps != null && targetSteps > 0

    // 진행률 계산 (목표가 없으면 0%)
    val progress = if (isGoalSet) {
        val stepsForCalc = currentGoalSteps ?: 0
        (stepsForCalc.toFloat() / targetSteps!!.toFloat()).coerceIn(0f, 1f)
    } else 0f

    val percentage = (progress * 100).toInt()

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 왼쪽: 이미지 및 텍스트 (weight 1f)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_walking_man),
                    contentDescription = "산책",
                    modifier = Modifier.size(60.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "오늘의 걸음수",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 오른쪽: 원형 그래프와 걸음 수 정보 (weight 1.5f -> 크게 유지)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(0.dp),
                modifier = Modifier.weight(1.5f)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // 1. 원형 프로그레스 바
                    Box(contentAlignment = Alignment.Center) {
                        // 배경 원
                        CircularProgressIndicator(
                            progress = { 1f },
                            modifier = Modifier.size(60.dp),
                            color = Color.LightGray.copy(alpha = 0.3f),
                            strokeWidth = 7.dp,
                        )

                        // 진행 원 (목표가 있을 때만 초록색 표시)
                        if (isGoalSet) {
                            CircularProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.size(60.dp),
                                color = Color(0xFF4CAF50),
                                strokeWidth = 7.dp,
                                strokeCap = StrokeCap.Round
                            )
                        }

                        // 가운데 퍼센트 텍스트 (목표 없으면 '-' 표시)
                        Text(
                            text = if (isGoalSet) "${percentage}%" else "-",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isGoalSet) Color.Black else Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // 2. 텍스트 정보
                    Column {
                        // 목표 걸음 수 텍스트 (조건부 표시)
                        Text(
                            text = if (isGoalSet) "목표: $targetSteps" else "목표 없음",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        // 현재 걸음 수 (항상 표시)
                        Text(
                            text = "$currentTotalSteps 걸음",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun RankHomeCard(onRankingBtnClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onRankingBtnClick)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.crown),
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SMWU 산책왕전",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "가장 걸음 수가 많은 송이들이 있는 학과는 어디?",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    lineHeight = 16.sp
                )
            }

            IconButton(onClick = onRankingBtnClick) {
                Icon(
                    imageVector = Icons.Default.ArrowLeft,
                    contentDescription = "이동",
                    modifier = Modifier
                        .rotate(180f) // 오른쪽 화살표로
                        .size(24.dp)
                )
            }
        }
    }
}

// ================= Utils =================

fun expandGoalsToDates(
    goals: List<GoalEntity>
): Map<LocalDate, List<GoalEntity>> {
    val map = mutableMapOf<LocalDate, MutableList<GoalEntity>>()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    goals.forEach { goal ->
        try {
            val start = LocalDate.parse(goal.startDate, formatter)
            val end = LocalDate.parse(goal.endDate, formatter)

            var d = start
            // 시작일과 종료일 사이의 모든 날짜에 이 목표를 매핑
            while (!d.isAfter(end)) {
                map.getOrPut(d) { mutableListOf() }.add(goal)
                d = d.plusDays(1)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    return map
}

fun getKoreanDayOfWeek(date: LocalDate): String {
    return date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN)
}

// 회전용 Modifier 확장 (Icon 회전 시 필요)
fun Modifier.rotate(degrees: Float) = this.then(
    Modifier.graphicsLayer(rotationZ = degrees)
)
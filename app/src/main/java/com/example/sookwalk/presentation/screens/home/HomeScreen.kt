package com.example.sookwalk.presentation.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    // TODO: 걸음 수 ViewModel
    goalViewModel: GoalViewModel,
    stepViewModel: StepViewModel,
    navController: NavController,
    onBack: () -> Unit, // 뒤로 가기 함수 (단방향 흐름)
    onAlarmClick: () -> Unit,
    onMenuClick: () -> Unit, // 드로어 열림/닫힘 제어를 받아올 함수,
    onRankingBtnClick: () -> Unit,
    ){

    // 오늘 goal을 로드시켜놓도록 한다
    //
    val goalList = remember {
        mutableStateListOf(
            Goal("명상하기", LocalDate.of(2025, 1, 1)),
            Goal("운동하기", LocalDate.of(2025, 1, 2)),
            Goal("독서하기", LocalDate.of(2025, 1, 3))
        )
    }
    val scrollState = rememberScrollState()
    val weekGoals by goalViewModel.weekGoals.collectAsState(initial = emptyList())
    // startDate가 "yyyy-MM-dd" 라고 가정 만약 endDate 기준으로 보여줄 거면 it.endDate 로 바꿔.
//    val goalsByDate: Map<String, List<GoalEntity>> = weekGoals.groupBy { it.startDate }
    val goalsByDate: Map<LocalDate, List<GoalEntity>> =
        remember(weekGoals) { expandGoalsToDates(weekGoals) }
    // 화면 들어올 때 데이터 로딩
    LaunchedEffect(Unit) {
        stepViewModel.loadTodaySteps()

    }

    Scaffold(
        topBar = {
            TopBar("메인 홈",
                onBack, onAlarmClick, onMenuClick
            )},
        bottomBar = {
            BottomNavBar(navController)
        }
    ){ innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)){
            MainHomeCard(goalList[0])
            WeekTitleCard("주차별")
            WeekHomeList(scrollState, goalViewModel)
            WalkHomeCard(1000, 2000)
            Spacer(modifier = Modifier.height(5.dp))
            RankHomeCard(onRankingBtnClick)
        }
    }
}


@Composable
fun MainHomeCard(
    goal: Goal
){
    Card(
        modifier = Modifier
            .padding(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ){
        Row (
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                MainDateCard()
                Spacer(modifier = Modifier.width(10.dp))
                Column{
                    Text(
                        text = "TODAY TODO",
                        color = Black,
                        fontWeight = Bold
                    )
                    Text(
                        text = goal.title,
                        color = Black,
                        fontWeight = Bold
                    )
                }
            }
            IconButton(onClick = {/* 해당 목표 페이지로 이동 */}) {
                Image(
                    painter = painterResource(id = R.drawable.arrow_left),
                    contentDescription = "해당 목표 페이지로 이동"
                )
            }
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
fun WeekTitleCard(title: String){
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiary
        ),
        modifier = Modifier.padding(10.dp),
    ){
        Row (
            modifier = Modifier
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ){
            Image(
                painter = painterResource(id = R.drawable.crown),
                contentDescription = "왕관 이미지",
                modifier = Modifier.size(25.dp)
            )
            Text(title)
        }
    }
}

@Composable
fun WeekHomeList(scrollState: ScrollState,
                 goalViewModel: GoalViewModel) {
    val today = LocalDate.now()
    // 이번 주 월요일
    val weekStart = today.with(java.time.DayOfWeek.MONDAY)

    // 월요일부터 일요일까지 7일 리스트
    val weekDates = (0..6).map { offset ->
        weekStart.plusDays(offset.toLong())
    }
    val weekGoals by goalViewModel.weekGoals.collectAsState(initial = emptyList())

    val goalsByDate: Map<LocalDate, List<GoalEntity>> =
        remember(weekGoals) { expandGoalsToDates(weekGoals) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)  // 👈 가로 스크롤
            .padding(horizontal = 8.dp)
    ) {
        weekDates.forEach { date ->
            val goalsOfThatDay = goalsByDate[date].orEmpty()

            WeekHomeCard(
                date = date,
                goals = goalsOfThatDay
            )
        }
    }
}

@Composable
fun WeekHomeCard(date: LocalDate, goals: List<GoalEntity>){
    Card (
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier.padding(7.dp)
    ){
        Column(
            modifier = Modifier
                .height(90.dp)
                .padding(10.dp)
        ){
            Row {
                Icon(
                    Icons.Default.CheckBoxOutlineBlank
                    , "체크 박스 아이콘"
                )
                Text("${date.monthValue} .${date.dayOfMonth}(${getKoreanDayOfWeek(date)})")
            }
            if (goals.isNotEmpty()){
                goals.forEach { goal ->
                    Text(goal.title)
                }
            } else {
                Text("목표 없음")
            }
        }
    }
}

fun getKoreanDayOfWeek(date: LocalDate): String {
    return date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.KOREAN).first().toString()
}

@Composable
fun WalkHomeCard(goalWalkCount: Int, walkCount: Int){
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
        ),
        modifier = Modifier.fillMaxWidth()
                            .padding(10.dp),
    ){
        Row(
            modifier = Modifier.fillMaxWidth()
                               .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ){
            Column{
                Image(
                    painter = painterResource(id = R.drawable.ic_walking_man),
                    contentDescription = "걷는 사람의 아이콘",
                    modifier = Modifier.size(80.dp)
                        .padding(bottom = 6.dp),
                    contentScale = ContentScale.Fit
                )
                Text(
                    text = "오늘의 걸음 수",
                    fontWeight = Bold,
                    fontSize = 14.sp
                )
            }
            WalkCountCard(2000, 1000)
        }
    }
}

@Composable
fun WalkCountCard(goalWalkCnt: Int, todayWalkCnt: Int){
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
        modifier = Modifier.fillMaxWidth()
            .padding(10.dp),
    ){
        Column(
            modifier = Modifier.padding(10.dp)
        ){
            Text(
                text = "목표 걸음 수: ${goalWalkCnt}",
                color  = Grey20
            )
            Row{
                Icon(Icons.Default.ArrowBackIosNew, "응")
                Text(
                    text ="${todayWalkCnt} 걸음",
                    fontWeight = SemiBold
                )
            }
        }
    }
}

@Composable
fun RankHomeCard(onRankingBtnClick: () -> Unit){
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier.fillMaxWidth()
                            .padding(10.dp),
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Column {
                Row {
                    Image(
                        painter = painterResource(id = R.drawable.crown),
                        contentDescription = "왕관 이미지",
                        modifier = Modifier.size(35.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text (
                        text = "SMU 산책왕전",
                        color = Black,
                        fontWeight = Bold,
                        fontSize = 18.sp
                    )
                }
                Text (
                    text = "가장 걸음 수가 많은 송이들이 있는 학과는 어디?",
                    color = Grey20,
                    fontSize = 14.sp
                )
            }
            Column {
                IconButton(onClick = onRankingBtnClick ) {
                    Image(
                        painter = painterResource(id = R.drawable.arrow_left),
                        contentDescription = "해당 목표 페이지로 이동",
                    )
                }

            }
        }
    }
}

fun expandGoalsToDates(
    goals: List<GoalEntity>
): Map<LocalDate, List<GoalEntity>> {
    val map = mutableMapOf<LocalDate, MutableList<GoalEntity>>()

    goals.forEach { goal ->
        val start = LocalDate.parse(goal.startDate) // "yyyy-MM-dd"
        val end = LocalDate.parse(goal.endDate)

        var d = start
        while (!d.isAfter(end)) { // start~end inclusive
            map.getOrPut(d) { mutableListOf() }.add(goal)
            d = d.plusDays(1)
        }
    }
    return map
}
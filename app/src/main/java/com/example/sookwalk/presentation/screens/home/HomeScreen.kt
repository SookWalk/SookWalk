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
import com.example.sookwalk.R
import com.example.sookwalk.presentation.components.TopBar
import com.example.sookwalk.ui.theme.Black
import com.example.sookwalk.ui.theme.Grey20
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    // TODO: 걸음 수 ViewModel
//    goalViewModel: GoalViewModel,
//    navController: NavController,
//    backStackEntry: NavBackStackEntry
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


    Scaffold(
        topBar = {
            TopBar("메인 홈",
                {}
                )}
    ){ innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)){
            MainHomeCard(goalList[0])
            WeekTitleCard("주차별")
            WeekHomeList(scrollState)
            WalkHomeCard(1000, 2000)
            Spacer(modifier = Modifier.height(5.dp))
            RankHomeCard()
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
                MainDateCard("${goal.startDate.monthValue}.${goal.startDate.dayOfMonth}")
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
fun MainDateCard(date: String){
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ){
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)
        ){
            Text("2025")
            Text("11-20")
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
fun WeekHomeList(scrollState: ScrollState) {
    val today = LocalDate.now()
    // 이번 주 월요일
    val weekStart = today.with(java.time.DayOfWeek.MONDAY)

    // 월요일부터 일요일까지 7일 리스트
    val weekDates = (0..6).map { offset ->
        weekStart.plusDays(offset.toLong())
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)  // 👈 가로 스크롤
            .padding(horizontal = 8.dp)
    ) {
        weekDates.forEach { date ->
            WeekHomeCard(
                date = date,
                text = "예시 텍스트" // 나중에 요일별 목표 같은 걸로 바꿔 넣으면 됨
            )
        }
    }
}

@Composable
fun WeekHomeCard(date: LocalDate, text: String){
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
            Text("목표 달성")
        }
    }
}

fun getKoreanDayOfWeek(date: LocalDate): String {
    return date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.KOREAN)
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
            WalkCountCard()
        }
    }
}

@Composable
fun WalkCountCard(){
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
                text = "목표 걸음 수: 1000",
                color  = Grey20
            )
            Row{
                Icon(Icons.Default.ArrowBackIosNew, "응")
                Text(
                    text ="300 걸음",
                    fontWeight = SemiBold
                )
            }
        }
    }
}

@Composable
fun RankHomeCard(){
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
                IconButton(onClick = {/* 해당 목표 페이지로 이동 */}) {
                    Image(
                        painter = painterResource(id = R.drawable.arrow_left),
                        contentDescription = "해당 목표 페이지로 이동",
                    )
                }

            }
        }
    }
}
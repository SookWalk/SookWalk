package com.example.sookwalk.presentation.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sookwalk.R
import com.example.sookwalk.presentation.components.TopBar
import com.example.sookwalk.ui.theme.Grey20
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(){
    val goalList = remember { mutableStateListOf<Goal>()}

    Scaffold(
        topBar = {
            TopBar("메인 홈",
                {}
                )}
    ){ innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)){
            MainHomeCard(goalList[0])

        }
    }
}


@Composable
fun MainHomeCard(
    goal: Goal
){
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ){
        Row (
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                Column{

                }
                Column{
                    Text("TODAY TODO")
                    if (goal != null){
                        Text("${goal.title}")
                    } else {
                        Text("오늘 목표 없음")
                    }
                }
            }
            IconButton(onClick = {/* 해당 목표 페이지로 이동 */}) {
                Image(
                    painter = painterResource(id = R.drawable.arrow_right),
                    contentDescription = "해당 목표 페이지로 이동"
                )
            }
        }
    }
}

data class Goal(val title: String, val startDate: LocalDate)

@Composable
fun WeekTitleCard(title: String){
    Row (){
        Icon(Icons.Default.ArrowBackIosNew, "응")
        Text(title)
    }
}


@Composable
fun WeekHomeCard(date : LocalDate, text: String){
    Column{
        Row {
            Icon(
                Icons.Default.CheckBoxOutlineBlank
                , "체크 박스 아이콘"
            )
            Text("${date.monthValue} .${date.dayOfMonth}(${getKoreanDayOfWeek(date)})")
        }
        Text(text)
    }
}

fun getKoreanDayOfWeek(date: LocalDate): String {
    return date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.KOREAN)
}

@Composable
fun WalkHomeCard(goalWalkCount: Int, walkCount: Int){
    Card(){
        Row{
            Column{
                Image(
                    painter = painterResource(id = R.drawable.ic_walking_man),
                    contentDescription = "걷는 사람의 아이콘"
                )
                Text(
                    text = "오늘의 걸음 수",
                    fontWeight = Bold,
                    fontSize = 10.sp
                    )
            }
            Column{
                Text(
                    text = "목표 걸음 수: ${goalWalkCount}",
                    color  = Grey20
                )
                Row{
                    Icon(Icons.Default.ArrowBackIosNew, "응")
                    Text(
                        text ="${walkCount} 걸음",
                        fontWeight = SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun RankHomeCard(){
    Card(

    ){
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Column {
                Row {
                    Icon(Icons.Default.ArrowBackIosNew, "응")
                    Text ("SMU 산책왕전")
                }
                Text (
                    text = "가장 걸음 수가 많은 송이들이 있는 학과는 어디?"
                )
            }
            Column {
                IconButton(onClick = {/* RankingScreen으로 화면 이동 */}){
                    Icon(Icons.Default.ArrowBackIosNew, "응")
                }

            }
        }
    }
}
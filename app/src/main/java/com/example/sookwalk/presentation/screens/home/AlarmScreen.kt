package com.example.sookwalk.presentation.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import com.example.sookwalk.R
import com.example.sookwalk.presentation.components.TopBar
import com.example.sookwalk.ui.theme.Grey80
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmScreen(){
    var alarmList = remember { mutableStateListOf<Alarm>()}

    Scaffold(
        topBar = {
            TopBar(
                screenName = "알림",
                onMenuClick = { }
            )
        }
    ){ innerPadding ->
       Column(
           modifier = Modifier
               .padding(innerPadding)
       ){
           LazyColumn(){
                items(alarmList.size){ index ->
                    val alarm = alarmList[index]
                    AlarmCard(alarm)
                }
           }
       }
    }
}

data class Alarm (val title: String, val description: String, val date: LocalDateTime, var isRead: Boolean)

@Composable
fun AlarmCard(alarm : Alarm){
    Card (){
        Row {
            Column {
                Image(
                    painter = painterResource(id = R.drawable.ic_walking_man),
                    contentDescription = "걷는 사람의 아이콘"
                )
            }
            Column {
                Text(
                    text = alarm.title,
                    fontSize = 12.sp
                )
                Text(
                    alarm.description,
                    fontSize = 10.sp
                )
                Text(
                    text ="${alarm.date.year}-${alarm.date.monthValue}-${alarm.date.dayOfMonth}" ,
                    fontSize = 12.sp,
                    color = Grey80
                )
            }
        }
    }

}
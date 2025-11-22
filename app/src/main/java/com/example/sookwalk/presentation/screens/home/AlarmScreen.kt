package com.example.sookwalk.presentation.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sookwalk.R
import com.example.sookwalk.presentation.components.TopBar
import com.example.sookwalk.ui.theme.Grey20
import com.example.sookwalk.ui.theme.Grey80
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmScreen(){
    var alarmList = remember { mutableStateListOf(
        Alarm("알림 제목 1", "설명1 알림입니다", LocalDateTime.now(), false),
        Alarm("알림 제목 2", "설명2 알림입니다", LocalDateTime.now().minusDays(1), false),
        Alarm("알림 제목 3", "설명3 알림입니다", LocalDateTime.now().minusDays(2), true),
        Alarm("알림 제목 4", "설명4 알림입니다", LocalDateTime.now().minusDays(3), true)

    )}

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
    Card (
        colors = CardDefaults.cardColors(
        if (alarm.isRead){
            MaterialTheme.colorScheme.background
        } else {
            MaterialTheme.colorScheme.primary
        }

        ),
        modifier = Modifier.fillMaxWidth()
    ){
        Row (
            modifier = Modifier.padding(8.dp)
        ){
            Image(
                painter = painterResource(id = R.drawable.ic_walking_man),
                contentDescription = "걷는 사람의 아이콘",
                modifier = Modifier.size(80.dp)
                    .padding(bottom = 6.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(4.dp))
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
                    text ="${alarm.date.year}-${alarm.date.monthValue}-${alarm.date.dayOfMonth} 09:48" ,
                    fontSize = 12.sp,
                    color = Grey20
                )
            }
        }
    }

}
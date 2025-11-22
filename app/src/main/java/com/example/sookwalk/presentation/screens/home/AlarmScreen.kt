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
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.sookwalk.R
import com.example.sookwalk.data.local.entity.notification.NotificationEntity
import com.example.sookwalk.presentation.components.BottomNavBar
import com.example.sookwalk.presentation.components.TopBar
import com.example.sookwalk.presentation.viewmodel.NotificationViewModel
import com.example.sookwalk.ui.theme.Grey20
import com.example.sookwalk.utils.notification.DateUtils.formatTimestamp
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmScreen(
    notificationViewModel: NotificationViewModel,
    navController: NavController,
    onBack: () -> Unit, // 뒤로 가기 함수 (단방향 흐름)
    onAlarmClick: () -> Unit,
    onMenuClick: () -> Unit // 드로어 열림/닫힘 제어를 받아올 함수
){
    val notificationList = notificationViewModel.notificationList.collectAsState()

    Scaffold(
        topBar = {
            TopBar(
                screenName = "알림",
                onBack, onAlarmClick, onMenuClick
            )
        },
        bottomBar = {
            BottomNavBar(navController)
        }
    ){ innerPadding ->
       Column(
           modifier = Modifier
               .padding(innerPadding)
       ){
           LazyColumn(){
                items(notificationList.value){ notificationEntity ->
                    AlarmCard(notificationEntity)
                }
           }
       }
    }
}

data class Alarm (val title: String, val description: String, val date: LocalDateTime, var isRead: Boolean)

@Composable
fun AlarmCard(notificationEntity: NotificationEntity){
    Card (
        colors = CardDefaults.cardColors(
        if (notificationEntity.isRead){
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
                    text = notificationEntity.title,
                    fontSize = 12.sp
                )
                Text(
                    notificationEntity.message,
                    fontSize = 10.sp
                )
                Text(
                    text = formatTimestamp(notificationEntity.createdAt) ,
                    fontSize = 12.sp,
                    color = Grey20
                )
            }
        }
    }

}
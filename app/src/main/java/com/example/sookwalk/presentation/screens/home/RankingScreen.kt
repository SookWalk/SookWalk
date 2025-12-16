package com.example.sookwalk.presentation.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.sookwalk.R
import com.example.sookwalk.data.enums.RankingTab
import com.example.sookwalk.data.remote.dto.RankDto
import com.example.sookwalk.presentation.components.BottomNavBar
import com.example.sookwalk.presentation.components.TopBar
import com.example.sookwalk.presentation.viewmodel.RankingViewModel
import com.example.sookwalk.ui.theme.Grey20

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankingScreen(
//        context: Context
    rankingViewModel: RankingViewModel,
    navController: NavController,
    onBack: () -> Unit, // 뒤로 가기 함수 (단방향 흐름)
    onAlarmClick: () -> Unit,
    onMenuClick: () -> Unit // 드로어 열림/닫힘 제어를 받아올 함수
){
    val dept by rankingViewModel.deptRanking.collectAsState()
    val college by rankingViewModel.collegeRanking.collectAsState()

    var selectedTab by rememberSaveable { mutableStateOf(RankingTab.COLLEGE)}

    val listToShow: List<RankDto> = when (selectedTab){
        RankingTab.COLLEGE -> college
        RankingTab.DEPARTMENT -> dept
    }

    Scaffold (
        topBar = {
            TopBar(
                "랭킹",
                onBack, onAlarmClick, onMenuClick
            )
        },
        bottomBar = {
            BottomNavBar(navController)
        }
    ){ innerPadding ->
        Column (
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ){
            Row (){
                RankingCategory(
                    category = "단과대",
                    selected = selectedTab == RankingTab.COLLEGE,
                    onClick = { selectedTab = RankingTab.COLLEGE }
                )
                RankingCategory(
                    category = "학과",
                    selected = selectedTab == RankingTab.DEPARTMENT,
                    onClick = { selectedTab = RankingTab.DEPARTMENT }
                )
            }
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ){
                items(listToShow.size){ index ->
                    val rank = listToShow[index]
                    RankingCard(rank)
                }
                item {
                    Text(
                        text = "기록은 매주 일요일에 초기화됩니다",
                        fontSize = 8.sp,
                        color = Grey20,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 10.dp),
                        textAlign = TextAlign.End
                    )
                }
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        Image(
                            painter = painterResource(id = R.drawable.ic_walking_man),
                            contentDescription = "걷는 사람의 아이콘",
                            modifier = Modifier.size(150.dp)
                                .padding(bottom = 6.dp),
                            contentScale = ContentScale.Fit
                        )
                        Text(
                            text ="힘내서 더 걸어 보아요",
                            fontSize = 25.sp,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RankingCategory(
    category: String, selected: Boolean, onClick: () -> Unit
){
    Card(
        onClick  = onClick,
        shape = RoundedCornerShape(7.dp),
        colors = CardDefaults
            .cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.padding(10.dp)
    ){
        Text(
            text = category,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
        )
    }
}

@Composable
fun RankingCard(rank: RankDto){
    Card(
        colors = CardDefaults
            .cardColors(containerColor = MaterialTheme.colorScheme.primary),
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 15.dp,vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row() {
                Text("${rank.rank}순위")
                Spacer(modifier = Modifier.width(10.dp))
                Text(rank.name)
            }
            Text(
                text ="${rank.walkCount} 걸음",
                color = Grey20
            )
        }
    }
}
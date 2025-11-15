package com.example.sookwalk.presentation.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sookwalk.presentation.components.TopBar
import com.example.sookwalk.ui.theme.Grey20

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankingScreen(

){
    var rankList = remember {mutableStateListOf<Rank>()}

    Scaffold (
        topBar ={
            TopBar(
                "랭킹",
                onMenuClick = {}
            )
        }
    ){ innerPadding ->
        Column (
            modifier = Modifier
                .padding(innerPadding)
        ){
            Row (){
                RankingCategory("단과대")
                RankingCategory("학과")
            }
            LazyColumn(){
                items(rankList.size){ index ->
                    val rank = rankList[index]
                    RankingCard(rank)
                }
            }
            Text(
                text = "기록은 매주 일요일에 초기화됩니다",
                fontSize = 8.sp,
                color = Grey20
            )
            Icon(Icons.Default.ArrowBackIosNew, "응")
            Text(
                text ="힘내서 더 걸어 보아요",
                fontSize = 25.sp,
            )
        }
    }
}

data class Rank(var id: Int, var major: String, var walkCount: Int)

@Composable
fun RankingCategory(category: String){
    Card(
        shape = RoundedCornerShape(7.dp),
        colors = CardDefaults
            .cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ){
        Text(category)
    }
}

@Composable
fun RankingCard(rank: Rank){
    Card() {
        Row() {
            Row() {
                Text("${rank.id}순위")
                Text("${rank.major}")
            }
            Text(
                text ="${rank.walkCount}",
                color = Grey20
            )
        }
    }
}
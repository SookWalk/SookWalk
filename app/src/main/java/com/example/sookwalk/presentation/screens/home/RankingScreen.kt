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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sookwalk.R
import com.example.sookwalk.presentation.components.TopBar
import com.example.sookwalk.ui.theme.Grey20
import com.google.common.math.LinearTransformation.horizontal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankingScreen(
//        context: Context
){
    var rankList = remember {
        mutableStateListOf(
            Rank(1, "컴퓨터공학과", 128430),
            Rank(2, "소프트웨어학부", 119850),
            Rank(3, "인공지능학부", 112300),
            Rank(4, "경영학과", 98720),
            Rank(5, "통계학과", 93500),
            Rank(6, "컴퓨터공학과", 128430),
            Rank(7, "소프트웨어학부", 119850),
            Rank(8, "인공지능학부", 112300),
            Rank(9, "경영학과", 98720),
            Rank(10, "통계학과", 93500)
        )
    }

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
                .fillMaxSize()
        ){
            Row (){
                RankingCategory("단과대")
                RankingCategory("학과")
            }
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ){
                items(rankList.size){ index ->
                    val rank = rankList[index]
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

data class Rank(var id: Int, var major: String, var walkCount: Int)

@Composable
fun RankingCategory(category: String){
    Card(
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
fun RankingCard(rank: Rank){
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
                Text("${rank.id}순위")
                Spacer(modifier = Modifier.width(10.dp))
                Text("${rank.major}")
            }
            Text(
                text ="${rank.walkCount} 걸음",
                color = Grey20
            )
        }
    }
}
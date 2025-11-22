package com.example.sookwalk.presentation.screens.badge

import com.example.sookwalk.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.example.sookwalk.presentation.components.BottomNavBar
import com.example.sookwalk.presentation.components.TopBar

data class BadgeInfo(
    val title: String?,
    val level: String?,
    val imageRes: Int?
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BadgeScreen(
    onMenuClick: () -> Unit
) {
    // 샘플 데이터
    val badges = listOf(
        BadgeInfo("워킹 마스터", "레벨 1/10", R.drawable.character_01),
        BadgeInfo("챌린지 고수", "레벨 2/10", R.drawable.character_01),
        BadgeInfo("추억 수집가", "레벨 0/10", R.drawable.character_01),
        BadgeInfo("챔피언 워커", "레벨 1/10", R.drawable.character_01),
        BadgeInfo("의리왕", "레벨 1/10", R.drawable.character_01),
        BadgeInfo(null, null, null),
        BadgeInfo(null, null, null),
        BadgeInfo(null, null, null),
        BadgeInfo(null, null, null) // 3열을 맞추기 위해 더미 데이터 추가
    )

    Scaffold(
        topBar = {
            TopBar(
                screenName = "뱃지",
                onBack = {},
                onAlarmClick = {},
                onMenuClick = onMenuClick
            )
        },
        bottomBar = { BottomNavBar(navController = rememberNavController()) },
        containerColor = Color.White,
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // 베스트 뱃지 영역
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
                ) {
                    Text(
                        text = "베스트 뱃지",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = null,
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(24.dp)
                    )
                }
                Box(
                    modifier = Modifier.padding(horizontal = 40.dp)
                ) {
                    BestBadgeCard()
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 하단 뱃지들 영역
            GreenGridContainer(badges = badges)
        }
    }
}

// 하단 그리드 컨테이너
@Composable
fun GreenGridContainer(badges: List<BadgeInfo>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(13.dp, 13.dp, 13.dp) // 외부 여백
            .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
            .background(MaterialTheme.colorScheme.surface) // 배경색 적용
            .padding(16.dp) // 내부 여백
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 3개씩 묶어서 Row로 배치
            badges.chunked(3).forEach { rowBadges ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    for (badge in rowBadges) {
                        Box(modifier = Modifier.weight(1f)) {
                            SmallBadgeCard(badge)
                        }
                    }
                    // 갯수가 모자란 행의 빈 공간 채우기
                    repeat(3 - rowBadges.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun BestBadgeCard() {
    Card(
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .height(330.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(22.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(0.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        contentAlignment = Alignment.TopEnd
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.character_01),
                            contentDescription = null,
                            modifier = Modifier.size(140.dp)
                        )
                        // 매달 아이콘
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = "1등",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier
                                .size(80.dp) // 뱃지 크기
                                // 위치 미세 조정
                                .offset(x = 30.dp, y = (-4).dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "워킹 마스터",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "2025.10.27",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "100000보 걸었습니다!",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun SmallBadgeCard(badge: BadgeInfo) {
    // 빈 데이터 처리
    if (badge.title == null) {
        // 빈 칸도 흰색 박스는 그려주되 내용은 비움
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {}
        return
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            badge.imageRes?.let {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = badge.title,
                    modifier = Modifier.size(48.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = badge.title ?: "",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = badge.level ?: "",
                fontSize = 10.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}
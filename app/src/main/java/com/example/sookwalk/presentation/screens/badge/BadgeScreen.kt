package com.example.sookwalk.presentation.screens.badge

import BadgeInfo
import com.example.sookwalk.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.sookwalk.presentation.components.BottomNavBar
import com.example.sookwalk.presentation.components.TopBar
import com.example.sookwalk.presentation.viewmodel.AuthViewModel
import com.example.sookwalk.presentation.viewmodel.BadgeViewModel
import com.example.sookwalk.utils.notification.DateUtils.formatTimestamp
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BadgeScreen(
    viewModel: BadgeViewModel,
    navController: NavController,
    onBack: () -> Unit,
    onAlarmClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    LaunchedEffect(Unit) {
        // 모든 초기 데이터를 한꺼번에 호출
        viewModel.getTotalSteps()
        viewModel.getTotalPlaces()
        viewModel.getTotalRanks()
        viewModel.getTotalChallenges()
    }

    // ViewModel의 데이터를 Compose 상태로 관찰
    val totalSteps by viewModel.totalSteps.collectAsState()
    val stepLevel by viewModel.stepLevel.collectAsState()
    val stepDate by viewModel.stepDate.collectAsState()

    val totalPlaces by viewModel.totalPlaces.collectAsState()
    val placeLevel by viewModel.placeLevel.collectAsState()
    val placeDate by viewModel.stepDate.collectAsState()

    val totalRanks by viewModel.totalRanks.collectAsState()
    val rankLevel by viewModel.rankLevel.collectAsState()
    val rankDate by viewModel.stepDate.collectAsState()

    val totalChallenges by viewModel.totalChallenges.collectAsState()
    val challengeLevel by viewModel.challengeLevel.collectAsState()
    val challengeDate by viewModel.stepDate.collectAsState()

    // 스낵바
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = androidx.compose.runtime.rememberCoroutineScope()

    // 뱃지 리스트
    val badges = listOf(
        BadgeInfo(
            "워킹 마스터",
            "레벨 $stepLevel/5",
            R.drawable.ic_walking,
            "\uD83C\uDFC3\u200D♂\uFE0F ${totalSteps}보 걸었습니다!",
            stepDate
        ),
        BadgeInfo(
            "챌린지 고수",
            "레벨 $challengeLevel/5",
            R.drawable.ic_challenge,
            "\uD83D\uDD25 ${totalChallenges}개의 챌린지를 완수했습니다!",
            challengeDate
        ),
        BadgeInfo(
            "추억 수집가",
            "레벨 $placeLevel/5",
            R.drawable.ic_photos,
            "\uD83D\uDCF7 ${totalPlaces}개의 장소를 저장했습니다!",
            placeDate
        ),
        BadgeInfo(
            "챔피언 워커",
            "레벨 $rankLevel/5",
            R.drawable.ic_champion,
            "\uD83D\uDC51 단과대 별 대항전에서\n${totalRanks}번 상위권에 들었습니다!",
            rankDate
        ),
        BadgeInfo(
            "의리왕",
            "레벨 1/5",
            R.drawable.ic_handshaking,
            "\uD83E\uDD70 숙워크와 함께한지 50일 되었습니다! (dummy 데이터) ",
            null
        ),
        BadgeInfo(null, null, null, "", null),
        BadgeInfo(null, null, null, "", null),
        BadgeInfo(null, null, null, "", null),
        BadgeInfo(null, null, null, "", null) // 3열을 맞추기 위해 더미 데이터 추가
    )

    // 베스트 뱃지 찾기 - 각 뱃지의 실제 레벨(숫자)을 맵핑하여 비교
    val bestBadge = badges
        .filter { it.title != null } // 더미 데이터(null) 제외
        .filter { badge ->
            // 각 뱃지의 현재 레벨 숫자를 가져옴
            val levelValue = when (badge.title) {
                "워킹 마스터" -> stepLevel
                "챌린지 고수" -> challengeLevel
                "추억 수집가" -> placeLevel
                "챔피언 워커" -> rankLevel
                else -> 0
            }
            levelValue > 0 // 레벨이 0보다 큰 것만 남김
        }
        .maxByOrNull { badge ->
            when (badge.title) {
                "워킹 마스터" -> stepLevel
                "챌린지 고수" -> challengeLevel
                "추억 수집가" -> placeLevel
                "챔피언 워커" -> rankLevel
                else -> 0
            }
        }

    // 팝업의 열림/닫힘 제어
    var selectedBadge by remember { mutableStateOf<BadgeInfo?>(null) }

    if (selectedBadge != null) {
        Dialog(onDismissRequest = { selectedBadge = null }) {
            val date = remember(selectedBadge?.date) {
                selectedBadge?.date?.let { timestamp ->
                    val sdf =
                        java.text.SimpleDateFormat("yyyy.MM.dd", java.util.Locale.getDefault())
                    sdf.format(timestamp.toDate())
                } ?: "날짜 정보 없음"
            }

            Box(
                modifier = Modifier
                    .width(260.dp)
                    .wrapContentHeight()
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    selectedBadge?.imageRes?.let {
                        Image(
                            painter = painterResource(id = it),
                            contentDescription = null,
                            modifier = Modifier.size(100.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = selectedBadge?.title ?: "",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = date,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = selectedBadge?.level ?: "",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = selectedBadge?.description ?: "",
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }

    Scaffold(

        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            ) { data ->
                Snackbar(
                    containerColor = Color.Black.copy(alpha = 0.8f),
                    contentColor = Color.White,
                    snackbarData = data
                )
            }
        },

        topBar = {
            TopBar(
                screenName = "뱃지",
                onBack = onBack,
                onAlarmClick = onAlarmClick,
                onMenuClick = onMenuClick
            )
        },
        bottomBar = { BottomNavBar(navController = navController) },
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
                    BestBadgeCard(badge = bestBadge)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 하단 뱃지들 영역
            GreenGridContainer(
                badges = badges,
                onBadgeClick = { badge ->

                    // 레벨이 0인지 확인 (예: "레벨 0/5")
                    val isLocked = badge.level?.contains("0/5") == true

                    if (isLocked) {
                        // 잠긴 경우 스낵바 실행
                        scope.launch {
                            snackbarHostState.showSnackbar("아직 획득하지 않은 뱃지입니다.")
                        }
                    } else {
                        // 획득한 경우 팝업 노출
                        selectedBadge = badge
                    }

                }
            )
        }
    }
}

// 하단 그리드 컨테이너
@Composable
fun GreenGridContainer(
    badges: List<BadgeInfo>,
    onBadgeClick: (BadgeInfo) -> Unit
) {
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
                            SmallBadgeCard(
                                badge = badge,
                                onClick = { onBadgeClick(badge) }
                            )
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
fun BestBadgeCard(badge: BadgeInfo?) {

    // 뱃지가 없을 때 보여줄 기본값 설정
    val title = badge?.title ?: "획득한 뱃지 없음"
    val description = badge?.description ?: "열심히 걸어서 뱃지를 획득해보세요!"
    val imageRes = badge?.imageRes ?: R.drawable.character_01 // 기본 이미지
    val date = remember(badge?.date) {
        badge?.date?.let { timestamp ->
            val sdf = java.text.SimpleDateFormat("yyyy.MM.dd", java.util.Locale.getDefault())
            sdf.format(timestamp.toDate())
        } ?: "날짜 정보 없음"
    }

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
                            painter = painterResource(id = imageRes),
                            contentDescription = null,
                            modifier = Modifier.size(140.dp)
                        )
                        // 최고 뱃지일 때만 메달 아이콘 표시 (레벨이 0보다 클 때만)
                        // 만약 'level' 문자열에서 숫자를 체크하기 어렵다면
                        // 단순히 badge가 null이 아닐 때 표시하도록 설정
                        if (badge?.title != null) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = "Best",
                                tint = Color(0xFFFFD700),
                                modifier = Modifier
                                    .size(80.dp)
                                    .offset(x = 30.dp, y = (-4).dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = date,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = description,
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
fun SmallBadgeCard(
    badge: BadgeInfo,
    onClick: () -> Unit
) {
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

    val isLocked = badge.level?.contains("0/5") == true

    Box( modifier = Modifier.clickable{ onClick() } ) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(0.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if(!isLocked){
                        badge.imageRes?.let {
                            Image(
                                painter = painterResource(id = it),
                                contentDescription = badge.title,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    } else {
                        // 잠겨있을 때 보여줄 아이콘 혹은 빈 공간
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = null,
                            tint = Color(0xFFE0E0E0), // 아주 연한 회색
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isLocked) "???" else (badge.title ?: ""),
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
    }
}
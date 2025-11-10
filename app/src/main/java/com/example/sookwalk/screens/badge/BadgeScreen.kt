import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WorkspacePremium // 왕관 아이콘
import androidx.compose.material3.* // Material 3 import
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sookwalk.R // 이미지 리소스 R 임포트
import com.example.sookwalk.screens.TopBar
import com.example.sookwalk.ui.theme.SookWalkTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BadgeScreen(
    onMenuClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopBar(
                screenName = "뱃지",
                onMenuClick = onMenuClick
            )
        },
        bottomBar = {
            GoalBottomNavigation()
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()) // 스크롤 가능하도록
        ) {
            // 1. 베스트 뱃지 섹션
            BestBadgeSection()
            Spacer(modifier = Modifier.height(16.dp))

            // 2. 뱃지 그리드 섹션
            BadgeGridSection()
        }
    }
}

// --- 1. 베스트 뱃지 섹션 ---
@Composable
fun BestBadgeSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // "베스트 뱃지" 타이틀
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 24.dp, bottom = 16.dp)
        ) {
            Text(
                "베스트 뱃지",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                Icons.Default.WorkspacePremium,
                contentDescription = "베스트 뱃지",
                tint = Color(0xFFFFD700) // 금색
            )
        }

        // 베스트 뱃지 카드
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.character_01),
                    contentDescription = "워킹 마스터 뱃지",
                    modifier = Modifier.size(100.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "워킹 마스터",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "2025.10.27",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "100000보 걸었습니다!",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

// --- 2. 뱃지 그리드 섹션 ---
@Composable
fun BadgeGridSection() {
    // 뱃지 데이터 (샘플)
    val badges = listOf(
        BadgeInfo("워킹 마스터", "레벨 1/10", R.drawable.character_01),
        BadgeInfo("챌린지 고수", "레벨 2/10", R.drawable.character_01),
        BadgeInfo("추억 수집가", "레벨 0/10", R.drawable.character_01),
        BadgeInfo("챔피언 워커", "레벨 1/10", R.drawable.character_01),
        BadgeInfo("의리왕", "레벨 1/10", R.drawable.character_01),
        BadgeInfo(null, null, null), // 빈 칸
        BadgeInfo(null, null, null), // 빈 칸
        BadgeInfo(null, null, null), // 빈 칸
    )

    // 3열 그리드
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp), // 그리드 영역 높이 (필요에 따라 조절)
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(badges) { badge ->
            SmallBadgeCard(badge = badge)
        }
    }
}

// 작은 뱃지 카드
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmallBadgeCard(badge: BadgeInfo) {
    OutlinedCard(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.outlinedCardColors(
            // 디자인처럼 흰색 배경
            containerColor = MaterialTheme.colorScheme.background
        ),
        border = BorderStroke(
            width = 1.dp, // 테두리 두께 (기본값)
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f) // 테두리 색상
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(100.dp), // 카드 높이 고정
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (badge.imageRes != null) {
                Image(
                    painter = painterResource(id = badge.imageRes),
                    contentDescription = badge.title,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = badge.title ?: "",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = badge.level ?: "",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// 뱃지 정보 데이터 클래스
data class BadgeInfo(
    val title: String?,
    val level: String?,
    val imageRes: Int? // 예시로 Int를 사용 (R.drawable.xxx)
)

@Preview(showBackground = true)
@Composable
fun BadgeScreenPreview() {
    SookWalkTheme(dynamicColor = false) {
        BadgeScreen(onMenuClick = {})
    }
}
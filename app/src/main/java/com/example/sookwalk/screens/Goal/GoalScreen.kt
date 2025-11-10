import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.* // Material 3 import로 변경
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sookwalk.screens.TopBar
import com.example.sookwalk.ui.theme.SookWalkTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalScreenM3(
    onMenuClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopBar(
                screenName = "목표",
                onMenuClick = onMenuClick
            )
        },
        bottomBar = { GoalBottomNavigationM3() },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            // 1. M3 DatePicker를 사용한 캘린더 카드
            CalendarCardM3()
            Spacer(modifier = Modifier.height(24.dp))
            // 2. M3 ListItem을 사용한 챌린지 카드
            ChallengesCardM3()
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


// --- 2. 캘린더 카드 (M3 DatePicker 사용) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarCardM3() {
    // M3 DatePicker의 상태를 기억합니다.
    val datePickerState = rememberDatePickerState(
        // 초기 선택된 날짜 (Epoch Millis) - 2025년 8월 17일
        initialSelectedDateMillis = 1755481200000L
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        // M3에서 공식 제공하는 캘린더 UI
        DatePicker(
            state = datePickerState,
            title = null,
            headline = null,
            showModeToggle = false, // 날짜/연도 선택 토글 숨김
            colors = DatePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.primary, // 카드 배경과 동일하게
                selectedDayContainerColor = Color(0xFFB2D4BD), // 선택된 날짜 배경 (디자인의 연두색)
                selectedDayContentColor = Color.White,
                todayDateBorderColor = MaterialTheme.colorScheme.surface, // 오늘 날짜 테두리
                todayContentColor = MaterialTheme.colorScheme.surface,
                dayContentColor = MaterialTheme.colorScheme.onSurface,
                yearContentColor = MaterialTheme.colorScheme.onSurface,
                currentYearContentColor = MaterialTheme.colorScheme.onSurface,
                selectedYearContentColor = MaterialTheme.colorScheme.surface,
                weekdayContentColor = Color.Gray,

                navigationContentColor = MaterialTheme.colorScheme.onSurface, // 'navigationIconContentColor' -> 'navigationContentColor'
                subheadContentColor = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}


// --- 3. 챌린지 카드 (M3 ListItem 사용) ---
@Composable
fun ChallengesCardM3() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 상단: 걸음 수 + 챌린지 추가 버튼
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "4256 걸음",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = { /* 챌린지 추가 */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFB2D4BD),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    elevation = ButtonDefaults.buttonElevation(5.dp)
                ) {
                    Text("챌린지 추가")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 챌린지 목록 (M3 ListItem 사용)
            ChallengeListItem(title = "5000보", subtitle = "아자아자 화이팅")
            ChallengeListItem(title = "100000보", subtitle = "아자아자 화이팅")
        }
    }
}

// M3 ListItem을 활용한 챌린지 아이템
@Composable
fun ChallengeListItem(title: String, subtitle: String) {
    ListItem(
        headlineContent = {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
        supportingContent = {
            Text(subtitle, fontSize = 12.sp, color = Color.Gray)
        },
        leadingContent = {
            // 프로필 이미지 (원형 플레이스홀더)
            Box(
                modifier = Modifier
                    .size(25.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFB2D4BD))
            )
        },
        trailingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "진행 중",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                IconButton(onClick = { /* 삭제 */ }) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "삭제", tint = Color.Gray)
                }
            }
        },
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent // 카드 배경색을 그대로 사용
        ),
        modifier = Modifier.padding(vertical = 4.dp)
    )
}


// --- 4. 하단 네비게이션 (M3 NavigationBar) ---
@Composable
fun GoalBottomNavigationM3() {
    var selectedItem by remember { mutableStateOf(1) } // "Goals"가 선택된 상태
    val items = listOf(
        BottomNavItem("Home", Icons.Default.Home),
        BottomNavItem("Goals", Icons.Default.Flag),
        BottomNavItem("Rank", Icons.Default.Leaderboard),
        BottomNavItem("Map", Icons.Default.Map)
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = Color.Black,
        tonalElevation = 0.dp
    ) {
        items.forEachIndexed { index, item ->
            val isSelected = selectedItem == index

            NavigationBarItem(
                icon = {
                    // M3에서도 아이콘을 Box로 감싸서
                    // 원하는 디자인 (원형 배경)을 구현할 수 있습니다.
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            // 선택되면 primary 색상, 아니면 투명
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else Color.Transparent
                            )
                            .padding(if (isSelected) 10.dp else 4.dp), // 패딩으로 크기 조절
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            item.icon,
                            contentDescription = item.label,
                            modifier = Modifier.size(if (isSelected) 24.dp else 28.dp),
                        )
                    }
                },
                label = { Text(item.label, fontSize = 11.sp) },
                selected = isSelected,
                onClick = { selectedItem = index },
                // M3의 기본 인디케이터(알약 모양)를 사용하지 않기 위해 비활성화
                // 대신 위에서 icon을 직접 커스텀했습니다.
                colors = NavigationBarItemDefaults.colors(
                    selectedTextColor = MaterialTheme.colorScheme.surface,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.Transparent // 인디케이터 색상을 투명하게
                ),
                alwaysShowLabel = true // 항상 라벨 표시
            )
        }
    }
}

data class BottomNavItem(val label: String, val icon: ImageVector) // 데이터 클래스는 동일


@Preview(showBackground = true)
@Composable
fun GoalScreenM3Preview() {
    SookWalkTheme(dynamicColor = false) {
        GoalScreenM3(onMenuClick = {})
    }
}
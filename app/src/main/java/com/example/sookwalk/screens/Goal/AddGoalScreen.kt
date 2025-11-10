import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.* // Material 3 import
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sookwalk.screens.TopBar
import com.example.sookwalk.ui.theme.SookWalkTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengeCreateScreen(
    onMenuClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopBar(
                screenName = "25.10.31", // 하드 코딩
                onMenuClick = onMenuClick
            )
        },
        bottomBar = { ChallengeCreateBottomNavigation() },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                "나만의 챌린지",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(vertical = 24.dp)
            )

            // 1. 걸음 수 입력 섹션
            StepsInputSection()
            Spacer(modifier = Modifier.height(24.dp))

            // 2. 날짜 지정 섹션
            DateSelectionSection()
            Spacer(modifier = Modifier.height(24.dp))

            // 3. 메모 섹션
            MemoSection()
            Spacer(modifier = Modifier.height(40.dp))

            // 4. 작성 완료 버튼
            CompletedButton()
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// --- 1. 걸음 수 입력 섹션 ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepsInputSection() {
    var text by remember { mutableStateOf("") }
    val chipOptions = listOf("1000보", "3000보", "5000보", "10000보", "20000보")
    var selectedChip by remember { mutableStateOf<String?>(null) }

    Text(
        "걸음 수 입력",
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    OutlinedTextField(
        value = text,
        onValueChange = { text = it },
        label = { Text("걸음 수 입력") },
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = Color.Gray,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = Color.Gray
        ),
        singleLine = true
    )
    Spacer(modifier = Modifier.height(16.dp))
    FlowRow( // 칩들을 유연하게 배치
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        chipOptions.forEach { chipText ->
            FilterChip(
                selected = (selectedChip == chipText),
                onClick = { selectedChip = if (selectedChip == chipText) null else chipText },
                label = { Text(chipText) },

//                colors = FilterChipDefaults.filterChipColors(
//                    selectedContainerColor = MaterialTheme.colorScheme.primary,
//                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
//                    containerColor = MaterialTheme.colorScheme.surface,
//                    labelColor = MaterialTheme.colorScheme.onSurface,
//                ),
//                border = FilterChipDefaults.filterChipBorder(
//                    selectedBorderColor = MaterialTheme.colorScheme.primary,
//                    borderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
//                )
            )
        }
    }
}

// --- 2. 날짜 지정 섹션 ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateSelectionSection() {
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    val durationOptions = listOf("하루", "일주일", "한 달")
    var selectedDuration by remember { mutableStateOf<String?>(null) }

    Text(
        "날짜 지정",
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = startDate,
            onValueChange = { startDate = it },
            label = { Text("mm/dd/yyyy") },
            readOnly = true, // 직접 입력 대신 DatePicker로 선택
            modifier = Modifier.weight(1f),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.Gray,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = Color.Gray
            ),
        )
        Text(
            " ~ ",
            modifier = Modifier.padding(horizontal = 8.dp),
            color = MaterialTheme.colorScheme.onBackground
        )
        OutlinedTextField(
            value = endDate,
            onValueChange = { endDate = it },
            label = { Text("mm/dd/yyyy") },
            readOnly = true, // 직접 입력 대신 DatePicker로 선택
            modifier = Modifier.weight(1f),
//            colors = OutlinedTextFieldDefaults.colors(
//                focusedBorderColor = MaterialTheme.colorScheme.primary,
//                unfocusedBorderColor = Color.Gray,
//                cursorColor = MaterialTheme.colorScheme.primary,
//                focusedLabelColor = MaterialTheme.colorScheme.primary,
//                unfocusedLabelColor = Color.Gray
//            ),
        )
    }
    Spacer(modifier = Modifier.height(16.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        durationOptions.forEach { durationText ->
            FilterChip(
                selected = (selectedDuration == durationText),
                onClick = { selectedDuration = if (selectedDuration == durationText) null else durationText },
                label = { Text(durationText) },
//                colors = FilterChipDefaults.filterChipColors(
//                    selectedContainerColor = MaterialTheme.colorScheme.primary,
//                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary, // 👈 'selectedContentColor'에서 수정
//                    containerColor = MaterialTheme.colorScheme.surface,
//                    labelColor = MaterialTheme.colorScheme.onSurface,
//                ),
//                border = FilterChipDefaults.filterChipBorder(
//                    selectedBorderColor = MaterialTheme.colorScheme.primary,
//                    borderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
//                ),
//                modifier = Modifier.weight(1f)
            )
        }
    }
}

// --- 3. 메모 섹션 ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoSection() {
    var memoText by remember { mutableStateOf("") }

    Text(
        "메모",
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    OutlinedTextField(
        value = memoText,
        onValueChange = { memoText = it },
        label = { Text("챌린지 관련 메모를 작성해주세요.") },
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = Color.Gray,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = Color.Gray
        ),
        singleLine = false,
        maxLines = 5
    )
}

// --- 4. 작성 완료 버튼 ---
@Composable
fun CompletedButton() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Button(
            onClick = { /* 작성 완료 로직 */ },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = RoundedCornerShape(20.dp),
            elevation = ButtonDefaults.buttonElevation(4.dp),
            modifier = Modifier.width(120.dp).height(48.dp)
        ) {
            Text("작성 완료")
        }
    }
}

// --- 하단 네비게이션 (GoalScreenM3에서 재활용) ---
@Composable
fun ChallengeCreateBottomNavigation() {
    // GoalScreenM3의 GoalBottomNavigationM3 함수를 그대로 사용
    // BottomNavItem 데이터 클래스는 이전에 정의된 것을 사용해야 합니다.
    GoalBottomNavigationM3()
}

// --- 프리뷰 ---
@Preview(showBackground = true)
@Composable
fun ChallengeCreateScreenPreview() {
    SookWalkTheme(dynamicColor = false) { // 다이나믹 컬러 비활성화하여 CustomLightColorScheme 적용
        ChallengeCreateScreen(onMenuClick = {})
    }
}
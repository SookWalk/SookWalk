package com.example.sookwalk.presentation.screens.goal

import android.R.attr.label
import android.R.attr.text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.sookwalk.presentation.components.BottomNavBar
import com.example.sookwalk.presentation.components.TopBar
import com.example.sookwalk.presentation.viewmodel.BadgeViewModel
import com.example.sookwalk.presentation.viewmodel.GoalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalScreen(
    viewModel: GoalViewModel,
    navController: NavController,
    onBack: () -> Unit,
    onAlarmClick: () -> Unit,
    onMenuClick: () -> Unit,
    initialDate: String,
    goalId: Int,
) {
    // 1. 상태 호이스팅 (모든 데이터는 여기서 관리)
    var steps by remember { mutableStateOf("") }

    // initialDate가 있으면 startDate 초기값으로 설정
    var startDate by remember { mutableStateOf(if (initialDate.isNotEmpty()) initialDate else "") }
    var endDate by remember { mutableStateOf("") }

    var memo by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopBar(
                screenName = "25.10.31", // 하드 코딩
                onBack = onBack,
                onMenuClick = onMenuClick,
                onAlarmClick = onAlarmClick
            )
        },
        bottomBar = { BottomNavBar(navController = rememberNavController()) },
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

            // 1. 걸음 수 (데이터와 함수를 넘겨줌)
            StepsInputSection(
                steps = steps,
                onStepsChanged = { steps = it }
            )
            Spacer(modifier = Modifier.height(24.dp))

            // 2. 날짜 지정
            DateSelectionSection(
                startDate = startDate,
                endDate = endDate,
                onStartDateClick = { /* 날짜 선택 다이얼로그 띄우기 */ },
                onEndDateClick = { /* 날짜 선택 다이얼로그 띄우기 */ },
                onDurationSelected = { duration ->
                    // "하루", "일주일" 칩 선택 시 endDate 자동 계산 로직
                    if (duration == "하루") endDate = startDate
                    // "일주일" 등의 로직은 날짜 계산 라이브러리 활용 필요
                }
            )
            Spacer(modifier = Modifier.height(24.dp))

            // 3. 메모
            MemoSection(
                memo = memo,
                onMemoChanged = { memo = it }
            )
            Spacer(modifier = Modifier.height(40.dp))

            // 4. 작성 완료 버튼
            CompletedButton(
                onClick = {
                    // 여기서 ViewModel에 저장 요청
                    // ex) viewModel.saveGoal(steps, startDate, endDate, memo)
                    navController.popBackStack() // 저장 후 뒤로가기
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// 1. 걸음 수 입력
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepsInputSection(
    steps: String,
    onStepsChanged: (String) -> Unit
) {
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
        value = steps,
        onValueChange = onStepsChanged,
        label = { Text("걸음 수 입력") },
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Gray,
            unfocusedBorderColor = Color.Gray,
            cursorColor = Color.Gray,
            focusedLabelColor = Color.Gray,
            unfocusedLabelColor = Color.Gray
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
    Spacer(modifier = Modifier.height(16.dp))
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(chipOptions) { chipText ->
            FilterChip(
                selected = (selectedChip == chipText),
                onClick = {
                    selectedChip = if (selectedChip == chipText) null else chipText
                    // [핵심] 칩을 누르면 입력창 값도 자동으로 바뀜 (예: "3000보" -> "3000")
                    if (selectedChip != null) {
                        // "3000보"에서 숫자만 추출해서 전달
                        onStepsChanged(chipText.replace("보", ""))
                    }
                },
                label = { Text(chipText) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                )
            )
        }
    }
}

// 2. 날짜 지정
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateSelectionSection(
    startDate: String,
    endDate: String,
    onStartDateClick: () -> Unit,
    onEndDateClick: () -> Unit,
    onDurationSelected: (String) -> Unit
) {
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
            onValueChange = {}, // 읽기 전용이라 비워둠
            label = { Text("mm/dd/yyyy") },
            readOnly = true, // 직접 입력 대신 DatePicker로 선택
            enabled = false,
            modifier = Modifier
                .weight(1f)
                .clickable { onStartDateClick() },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Gray,
                unfocusedBorderColor = Color.Gray,
                cursorColor = Color.Gray,
                focusedLabelColor = Color.Gray,
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
            onValueChange = {},
            label = { Text("mm/dd/yyyy") },
            readOnly = true, // 직접 입력 대신 DatePicker로 선택
            enabled = false,
            modifier = Modifier
                .weight(1f)
                .clickable { onEndDateClick() },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Gray,
                unfocusedBorderColor = Color.Gray,
                cursorColor = Color.Gray,
                focusedLabelColor = Color.Gray,
                unfocusedLabelColor = Color.Gray
            ),
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
                onClick = {
                    selectedDuration = if (selectedDuration == durationText) null else durationText
                    // 부모에게 어떤 기간이 선택됐는지 알림 (부모가 endDate 계산)
                    if (selectedDuration != null) {
                        onDurationSelected(durationText)
                    }
                },
                label = { Text(durationText) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                )
            )
        }
    }
}

// 3. 메모
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoSection(
    memo: String,
    onMemoChanged: (String) -> Unit
) {
    var memoText by remember { mutableStateOf("") }

    Text(
        "메모",
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    OutlinedTextField(
        value = memo,
        onValueChange = onMemoChanged,
        label = { Text("챌린지 관련 메모를 작성해주세요.") },
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Gray,
            unfocusedBorderColor = Color.Gray,
            cursorColor = Color.Gray,
            focusedLabelColor = Color.Gray,
            unfocusedLabelColor = Color.Gray
        ),
        singleLine = false,
        maxLines = 5
    )
}

// 4. 작성 완료 버튼
@Composable
fun CompletedButton(
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(50.dp),
            modifier = Modifier.width(120.dp).height(48.dp)
        ) {
            Text("작성 완료", fontSize = 17.sp)
        }
    }
}

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
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current

    // 1. 화면 진입 시 초기화
    LaunchedEffect(Unit) {
        viewModel.initInputState(initialDate)
    }

    // 2. ViewModel 상태 구독
    val stepsStr by viewModel.inputStepsStr.collectAsState()
    val startDateMillis by viewModel.inputStartDate.collectAsState()
    val endDateMillis by viewModel.inputEndDate.collectAsState()
    val memo by viewModel.inputMemo.collectAsState()

    // 3. 날짜 선택 다이얼로그 상태
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    // 날짜 포매터 (Millis -> UI String)
    val dateFormatter = java.text.SimpleDateFormat("MM/dd/yyyy", java.util.Locale.getDefault())
    val startDateStr = startDateMillis?.let { dateFormatter.format(java.util.Date(it)) } ?: ""
    val endDateStr = endDateMillis?.let { dateFormatter.format(java.util.Date(it)) } ?: ""

    Scaffold(
        topBar = {
            TopBar(
                screenName = startDateStr.ifEmpty { "목표 생성" }, // 하드 코딩
                onBack = onBack,
                onMenuClick = onMenuClick,
                onAlarmClick = onAlarmClick
            )
        },
        bottomBar = { BottomNavBar(navController = navController) },
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
                steps = stepsStr,
                onStepsChanged = { viewModel.setSteps(it) }
            )
            Spacer(modifier = Modifier.height(24.dp))

            // 2. 날짜 지정
            DateSelectionSection(
                startDate = startDateStr,
                endDate = endDateStr,
                onStartDateClick = { showStartPicker = true },
                onEndDateClick = { showEndPicker = true },
                onDurationSelected = { duration -> viewModel.setDuration(duration) }
            )
            Spacer(modifier = Modifier.height(24.dp))

            // 3. 메모
            MemoSection(
                memo = memo,
                onMemoChanged = { viewModel.setMemo(it) }
            )
            Spacer(modifier = Modifier.height(40.dp))

            // 4. 작성 완료 버튼
            CompletedButton(
                onClick = {
                    viewModel.saveGoal(context) {
                        // onSuccess: 저장이 완벽하게 성공했을 때만 실행됨
                        navController.popBackStack()
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

        }

        if (showStartPicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = startDateMillis ?: System.currentTimeMillis()
            )
            DatePickerDialog(
                onDismissRequest = { showStartPicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.setStartDate(datePickerState.selectedDateMillis)
                        showStartPicker = false
                    }) {
                        Text("확인", fontWeight = FontWeight.Bold, color = Color.Gray)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showStartPicker = false }) {
                        Text("취소", color = Color.Gray)
                    }
                }
            ) {
                DatePicker(
                    state = datePickerState,
                    colors = DatePickerDefaults.colors(
                        selectedDayContainerColor = Color.Gray,
                        selectedDayContentColor = Color.White,
                        todayDateBorderColor = Color.Gray,
                        todayContentColor = Color.Gray
                    )
                )
            }
        }

        if (showEndPicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = endDateMillis ?: startDateMillis ?: System.currentTimeMillis()
            )
            DatePickerDialog(
                onDismissRequest = { showEndPicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.setEndDate(datePickerState.selectedDateMillis)
                        showEndPicker = false
                    }) {
                        Text("확인", fontWeight = FontWeight.Bold, color = Color.Gray)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEndPicker = false }) {
                        Text("취소", color = Color.Gray)
                    }
                }
            ) {
                DatePicker(
                    state = datePickerState,
                    colors = DatePickerDefaults.colors(
                        selectedDayContainerColor = Color.Gray,
                        selectedDayContentColor = Color.White,
                        todayDateBorderColor = Color.Gray,
                        todayContentColor = Color.Gray
                    )
                )
            }
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
                    if (selectedChip != null) {
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
        Box(modifier = Modifier.weight(1f)) {
            OutlinedTextField(
                value = startDate,
                onValueChange = {},
                label = { Text("mm/dd/yyyy") },
                readOnly = true,
                enabled = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color.Gray,
                    focusedLabelColor = Color.Gray,
                    unfocusedLabelColor = Color.Gray
                ),
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { onStartDateClick() }
            )
        }

        Text(
            " ~ ",
            modifier = Modifier.padding(horizontal = 8.dp),
            color = MaterialTheme.colorScheme.onBackground
        )

        Box(modifier = Modifier.weight(1f)) {
            OutlinedTextField(
                value = endDate,
                onValueChange = {},
                label = { Text("mm/dd/yyyy") },
                readOnly = true,
                enabled = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color.Gray,
                    focusedLabelColor = Color.Gray,
                    unfocusedLabelColor = Color.Gray
                ),
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { onEndDateClick() }
            )
        }
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

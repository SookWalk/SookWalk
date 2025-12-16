package com.example.sookwalk.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sookwalk.data.local.entity.goal.GoalEntity
import com.example.sookwalk.data.repository.GoalRepository
import com.example.sookwalk.data.repository.SettingsRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class GoalViewModel @Inject constructor(
    private val goalRepository: GoalRepository,
    private val settingsRepository: SettingsRepository // 알림 설정을 위해 복구
) : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val uid: String? get() = auth.currentUser?.uid

    // =================================================================================
    // [1] 초기화 (데이터 동기화)
    // =================================================================================
    init {
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                // 로그인이 확인되면 즉시 파이어베이스에서 데이터를 가져옴
                syncData(user.uid)
            }
        }
    }

    private fun syncData(uid: String) = viewModelScope.launch {
        goalRepository.syncGoalsFromFirebase(uid)
    }

    // =================================================================================
    // [2] 리스트 화면 (GoalScreen) 상태
    // =================================================================================

    // 캘린더에서 선택된 날짜 (기본값: 오늘)
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate = _selectedDate.asStateFlow()

    // 선택된 날짜에 해당하는 목표 리스트 (DB 실시간 감지)
    // 날짜(_selectedDate)가 바뀌면 DB 쿼리도 자동으로 다시 실행됨
    @OptIn(ExperimentalCoroutinesApi::class)
    val goalsForSelectedDate: StateFlow<List<GoalEntity>> = _selectedDate
        .flatMapLatest { date ->
            val dateStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            goalRepository.getGoalsByDate(dateStr)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 날짜 선택 변경 함수 (UI 달력에서 호출)
    fun updateSelectedDate(millis: Long?) {
        millis?.let {
            val date = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
            _selectedDate.value = date
        }
    }

    // =================================================================================
    // [3] 입력 화면 (AddGoalScreen) 상태
    // =================================================================================

    // 걸음 수 (문자열)
    private val _inputStepsStr = MutableStateFlow("")
    val inputStepsStr = _inputStepsStr.asStateFlow()

    // 시작일, 종료일 (Millis)
    private val _inputStartDate = MutableStateFlow<Long?>(null)
    val inputStartDate = _inputStartDate.asStateFlow()

    private val _inputEndDate = MutableStateFlow<Long?>(null)
    val inputEndDate = _inputEndDate.asStateFlow()

    // 메모
    private val _inputMemo = MutableStateFlow("")
    val inputMemo = _inputMemo.asStateFlow()

    // 이번 주 시작일/종료일 관리
    private val _currentWeekRange = MutableStateFlow<Pair<String, String>?>(null)

    // 주간 목표 리스트 구독
    @OptIn(ExperimentalCoroutinesApi::class)
    val weekGoals: StateFlow<List<GoalEntity>> = _currentWeekRange
        .flatMapLatest { range ->
            if (range == null) flowOf(emptyList())
            else goalRepository.getGoalsOfWeek(range.first, range.second)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 이번 주 날짜 계산 및 세팅 함수
    fun setThisWeek() {
        val today = LocalDate.now()
        val monday = today.with(java.time.DayOfWeek.MONDAY)
        val sunday = monday.plusDays(6)

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        _currentWeekRange.value = Pair(monday.format(formatter), sunday.format(formatter))
    }

    // --- 입력 화면 관련 액션 함수들 ---

    fun initInputState(initialDateStr: String) {
        _inputStepsStr.value = ""
        _inputMemo.value = ""
        _inputEndDate.value = null

        if (initialDateStr.isNotEmpty()) {
            try {
                val date = LocalDate.parse(initialDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                val millis = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                _inputStartDate.value = millis
            } catch (e: Exception) {
                _inputStartDate.value = null
            }
        } else {
            _inputStartDate.value = null
        }
    }

    fun setSteps(steps: String) { _inputStepsStr.value = steps }
    fun setMemo(text: String) { _inputMemo.value = text }
    fun setStartDate(millis: Long?) { _inputStartDate.value = millis }
    fun setEndDate(millis: Long?) { _inputEndDate.value = millis }

    fun setDuration(label: String) {
        val startMillis = _inputStartDate.value ?: return
        val calendar = Calendar.getInstance().apply { timeInMillis = startMillis }
        when (label) {
            "하루" -> { }
            "일주일" -> calendar.add(Calendar.DAY_OF_YEAR, 6)
            "한 달" -> calendar.add(Calendar.MONTH, 1)
        }
        _inputEndDate.value = calendar.timeInMillis
    }

    // =================================================================================
    // [4] 데이터 조작 (저장) - [핵심 수정 부분]
    // =================================================================================

    fun saveGoal(context: Context, onSuccess: () -> Unit) = viewModelScope.launch {
        // 1. 유효성 검사: 걸음 수
        val steps = _inputStepsStr.value.toIntOrNull()
        if (steps == null || steps <= 0) {
            android.widget.Toast.makeText(context, "목표 걸음 수를 입력해주세요.", android.widget.Toast.LENGTH_SHORT).show()
            return@launch
        }

        // 2. 유효성 검사: 시작 날짜
        val startMillis = _inputStartDate.value
        if (startMillis == null) {
            android.widget.Toast.makeText(context, "시작 날짜 오류.", android.widget.Toast.LENGTH_SHORT).show()
            return@launch
        }

        // [핵심 수정 2] 종료 날짜가 지정되지 않았으면 저장을 막음!
        // 기존 코드: val endMillis = _inputEndDate.value ?: startMillis (이게 자동 하루 설정의 원인)
        val endMillis = _inputEndDate.value
        if (endMillis == null) {
            android.widget.Toast.makeText(context, "날짜를 지정해주세요.", android.widget.Toast.LENGTH_SHORT).show()
            return@launch // 뒤로가기 실행 안 됨
        }

        // 3. 로그인 체크
        val u = uid ?: run {
            android.widget.Toast.makeText(context, "로그인이 필요합니다.", android.widget.Toast.LENGTH_SHORT).show()
            return@launch
        }

        try {
            val formatter = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val startStr = formatter.format(Date(startMillis))
            val endStr = formatter.format(Date(endMillis))

            // 메모가 비어있으면 빈 문자열 그대로 저장
            val newGoal = GoalEntity(
                remoteId = "",
                title = "$steps 걸음 챌린지",
                targetSteps = steps,
                currentSteps = 0,
                startDate = startStr,
                endDate = endStr,
                memo = _inputMemo.value, // 입력 안했으면 "" 들어감
                isDone = false
            )

            // 2. DB 저장
            val localId = goalRepository.insertGoal(u, newGoal)

            // 3. [중요] 저장 성공 토스트 띄우고 "즉시" 화면 이동
            android.widget.Toast.makeText(context, "목표가 저장되었습니다.", android.widget.Toast.LENGTH_SHORT).show()
            onSuccess() // <-- 여기서 뒤로가기가 실행됨

            // 4. 알림 설정은 화면이 넘어간 뒤에 백그라운드에서 천천히 처리 (화면 이동 방해 X)
            viewModelScope.launch {
                try {
                    val isNotificationEnabled = settingsRepository.notificationFlow.first()
                    if (isNotificationEnabled) {
                        scheduleGoalNotification(context, localId.toInt(), newGoal.title, newGoal.endDate)
                    }
                } catch (e: Exception) {
                    e.printStackTrace() // 알림 설정 실패해도 앱은 정상 작동
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            android.widget.Toast.makeText(context, "저장 실패: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
        }
    }

    // 목표 삭제
    fun deleteGoal(context: Context, goal: GoalEntity) = viewModelScope.launch {
        val u = uid ?: return@launch

        // Repository 삭제 호출 (Room + Firestore)
        goalRepository.deleteGoal(u, goal)

        // 알림 취소
        cancelGoalNotification(context, goal.id)
    }

    // 메모만 수정 (리스트 화면에서 수정 시)
    fun updateMemo(goal: GoalEntity, newMemo: String) = viewModelScope.launch {
        val u = uid ?: return@launch
        goalRepository.updateGoalByMemo(u, goal, newMemo)
    }

    // =================================================================================
    // [5] 알림 관련 (AlarmManager)
    // =================================================================================

    // 알림 스케줄링 구현 (기존 코드가 없어 껍데기만 제공, AlarmManager 로직을 여기에 채워 넣으세요)
    private fun scheduleGoalNotification(context: Context, id: Int, title: String, endDate: String) {
        // TODO: AlarmManager를 사용하여 마감일(endDate)이나 매일 특정 시간에 알림 등록
        // PendingIntent ID로 'id' 사용
    }

    // 알림 취소 구현
    private fun cancelGoalNotification(context: Context, id: Int) {
        // TODO: AlarmManager.cancel() 호출
    }
}
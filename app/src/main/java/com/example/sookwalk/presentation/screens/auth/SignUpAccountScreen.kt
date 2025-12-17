package com.example.sookwalk.presentation.screens.auth

import android.R.attr.onClick
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.sookwalk.navigation.Routes
import com.example.sookwalk.presentation.components.SignUpBottomControlBar
import com.example.sookwalk.presentation.viewmodel.AuthViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.functions.functions
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpAccountScreen(
    viewModel: AuthViewModel,
    navController: NavController,
    // backStackEntry: NavBackStackEntry
) {

    var loginId by remember { mutableStateOf("") }
    val isLoginIdAvailable by viewModel.isLoginIdAvailable.collectAsState() // 아이디 사용 가능 여부
    var isAvailableIdMsg by remember { mutableStateOf("") }

    // isLoginIdAvailable 상태가 변경될 때마다 메시지를 업데이트
    LaunchedEffect(isLoginIdAvailable) {
        when (isLoginIdAvailable) {
            true -> isAvailableIdMsg = "사용 가능한 아이디입니다."
            false -> isAvailableIdMsg = "이미 존재하는 아이디입니다."
            null -> isAvailableIdMsg = "" // 초기 상태 또는 확인 전
        }
    }

    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") } // 비밀번호 확인
    var isVisible by remember { mutableStateOf(false) } // 비밀번호 가시성
    var isPasswordValid by remember { mutableStateOf(false) } // 비밀번호 조건 체크

    // 유효성 검증
    fun validatePassword(password: String): Boolean {
        val hasUpperCase = password.any { it.isUpperCase() }
        val hasLowerCase = password.any { it.isLowerCase() }
        val hasSpecialChar = password.any { !it.isLetterOrDigit() }
        val hasCorrectLength = password.length in 8..16
        return hasUpperCase && hasLowerCase && hasSpecialChar && hasCorrectLength
    }

    var email by remember { mutableStateOf("") }
    var isSookmyungEmail by remember { mutableStateOf(false) } // 숙명 구글 계정 여부
    val isEmailAvailable by viewModel.isEmailAvailable.collectAsState() // 이메일 중복 여부
    var isEmailAvailableMsg by remember { mutableStateOf("")}

    // isDuplicatedEmail 상태가 변경될 때마다 메시지를 업데이트
    LaunchedEffect(isEmailAvailable) {
        when (isEmailAvailable) {
            true -> isEmailAvailableMsg = "사용 가능한 이메일입니다."
            false -> isEmailAvailableMsg = "이미 존재하는 이메일입니다."
            null -> isEmailAvailableMsg = "" // 초기 상태 또는 확인 전
        }
    }

    var authCode by remember { mutableStateOf("") } // OTP 코드
    var isTimerRunning by remember { mutableStateOf(false) } // 타이머 동작 여부
    var timeLeft by remember { mutableStateOf(180) } // 남은 시간 (초 단위, 3분 = 180초)
    var isAuthencated by remember { mutableStateOf(false) } // 이메일 인증 여부
    var isAuthencatedMsg by remember { mutableStateOf("") }

    var moveNextEnabled by remember { mutableStateOf(false) } // 다음 페이지 이동

    // 모든 요건을 만족하면 다음 페이지로 이동한다
    if ( isLoginIdAvailable == true && password == confirmPassword && isAuthencated ) {
        moveNextEnabled = true
    }


    // isTimerRunning이 true가 되면 해당 블록이 실행
    if (isTimerRunning) {
        LaunchedEffect(key1 = timeLeft) {
            // 1초마다 timeLeft 값을 1씩 감소시킵니다.
            while (timeLeft > 0) {
                delay(1000L) // 1초 대기
                timeLeft--
            }
            // 시간이 0이 되면 타이머를 멈춥니다.
            isTimerRunning = false
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },

                navigationIcon = {
                    IconButton(onClick = {
                        // 뒤로가기 로직
                        navController?.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "뒤로가기"
                        )
                    }
                },

                // 색상 변경
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background, // 배경색 변경
                )
            )
        },

        bottomBar = {
            SignUpBottomControlBar(
                "SignUpAccount",
                {
                    // 입력 정보 viewModel에 저장
                    viewModel.updateLoginId(loginId)
                    viewModel.updatePassword(password)
                    viewModel.updateEmail(email)
                    // 회원가입 - 프로필 설정 페이지로 이동
                    navController.navigate(Routes.PROFILE)
                },
                moveNextEnabled
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = padding),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {

                item {
                    Column(modifier = Modifier.padding(8.dp)) {

                        // 아이디 입력 문구
                        Row {
                            Text("아이디 ", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                " *",
                                color = Color.Red,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        // 아이디 입력 TextField
                        TextField(
                            value = loginId,
                            onValueChange = { loginId = it },
                            modifier = Modifier
                                .fillMaxWidth(),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color(0xFFF4F4F4),
                                focusedContainerColor = Color(0xFFF4F4F4),
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                cursorColor = Color.DarkGray
                            ),
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // 버튼을 오른쪽 정렬
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = isAvailableIdMsg,
                                color = if(isLoginIdAvailable == true) MaterialTheme.colorScheme.tertiary else Color.Red,
                                style = MaterialTheme.typography.labelSmall
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = {
                                    viewModel.isLoginIdAvailable(loginId)
                                },
                                shape = RoundedCornerShape(28),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.tertiary,
                                    contentColor = Color.White
                                ),
                                contentPadding = PaddingValues(
                                    horizontal = 16.dp,
                                    vertical = 8.dp
                                )
                            ) {
                                Text("중복 확인", style = MaterialTheme.typography.bodyLarge)
                            }
                        }


                    }
                }

                // 비밀번호 입력 문구
                item {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row {
                            Text("비밀번호 ", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                " *",
                                color = Color.Red,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        // 비밀번호 입력 TextField
                        TextField(
                            value = password,
                            onValueChange = {
                                password = it
                                isPasswordValid = validatePassword(it)
                                            },
                            modifier = Modifier
                                .fillMaxWidth(),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color(0xFFF4F4F4),
                                focusedContainerColor = Color(0xFFF4F4F4),
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                cursorColor = Color.DarkGray
                            ),
                            // 입력된 비밀번호를 '*'로 변환
                            visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),

                            // 비밀번호용 키보드
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),

                            // 비밀번호 visible 여부 (눈 아이콘)
                            trailingIcon = {
                                val icon =
                                    if (isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                                IconButton(onClick = { isVisible = !isVisible }) {
                                    Icon(imageVector = icon, contentDescription = "비밀번호 보기")
                                }
                            }
                        )

                        // 비밀번호 조건 : 8자 이상 ~ 16자 이하, 대/소문자, 특수문자
                        Text(
                            text = "대소문자와 특수문자가 포함된 8~16자리의 비밀번호를 입력해주세요.",
                            color = if (password.isEmpty() || isPasswordValid) MaterialTheme.colorScheme.tertiary else Color.Red,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(4.dp),
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // 비밀번호 확인 입력 문구
                item {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row {
                            Text("비밀번호 확인 ", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                " *",
                                color = Color.Red,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        // 비밀번호 확인 TextField
                        TextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            modifier = Modifier
                                .fillMaxWidth(),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color(0xFFF4F4F4),
                                focusedContainerColor = Color(0xFFF4F4F4),
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                cursorColor = Color.DarkGray
                            ),
                            // 입력된 비밀번호를 '*'로 변환
                            visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),

                            // 비밀번호용 키보드
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),

                            // 비밀번호 visible 여부 (눈 아이콘)
                            trailingIcon = {
                                val icon =
                                    if (isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                                IconButton(onClick = { isVisible = !isVisible }) {
                                    Icon(imageVector = icon, contentDescription = "비밀번호 보기")
                                }
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 6.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        // 비밀번호 일치 여부 메시지
                        if (confirmPassword.isNotEmpty()) {
                            if (confirmPassword == password) {
                                Text(
                                    text = "비밀번호가 일치합니다.",
                                    color = MaterialTheme.colorScheme.tertiary,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(4.dp),
                                )
                            } else {
                                Text(
                                    text = "비밀번호가 일치하지 않습니다.",
                                    color = Color.Red,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(4.dp),
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                }

                // 이메일 입력 문구
                item {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row {
                            Text("숙명 구글 이메일 ", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                " *",
                                color = Color.Red,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        TextField(
                            value = email,
                            onValueChange = { newEmail ->

                                // 🚀 핵심 수정 부분: 현재 값과 새로운 입력 값이 다를 경우 상태를 리셋
                                if (email != newEmail) {
                                    // 이전에 '이미 존재하는 이메일'이라고 떴던 메시지를 지우기 위해 상태를 null로 리셋
                                    viewModel.resetEmailAvailable()
                                }
                                email = newEmail
                                isSookmyungEmail = newEmail.endsWith("@sookmyung.ac.kr")
                                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color(0xFFF4F4F4),
                                focusedContainerColor = Color(0xFFF4F4F4),
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                cursorColor = Color.DarkGray
                            ),
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            if(!isSookmyungEmail){
                                Text(
                                    text = "숙명 구글 계정만 가입 가능합니다.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.padding(4.dp)
                                )
                            } else{
                                // 이메일 중복 여부 검사 코드
                                Text(
                                    text = isEmailAvailableMsg,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if(isEmailAvailable == true)
                                        MaterialTheme.colorScheme.tertiary else Color.Red,
                                    modifier = Modifier.padding(4.dp)
                                )
                            }

                            // --- 타이머 표시 UI ---
                            if (isTimerRunning) {
                                // 분과 초를 계산
                                val minutes = timeLeft / 60
                                val seconds = timeLeft % 60
                                Text(
                                    text = String.format("%02d:%02d", minutes, seconds),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Red,
                                    modifier = Modifier.padding(4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(

                                onClick = {
                                    // 이미 해당 이메일로 계정이 있는 경우
                                    viewModel.isEmailAvailable(email)

                                    if (isEmailAvailable == true) {

                                        Firebase.auth.signInAnonymously()
                                            .addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    Log.d("Auth", "익명 로그인 성공. OTP 전송을 시작합니다.")
                                                    val functions =
                                                        Firebase.functions("asia-northeast3") // region 설정
                                                    val sendOtp =
                                                        functions.getHttpsCallable("sendOtp")
                                                    val user = Firebase.auth.currentUser

                                                    if (user != null) {
                                                        sendOtp.call(hashMapOf("email" to email))
                                                            .addOnSuccessListener { result ->

                                                                Log.d(
                                                                    "OTP",
                                                                    "OTP 전송 성공: ${result.data}"
                                                                )

                                                            }
                                                            .addOnFailureListener { e ->
                                                                Log.e(
                                                                    "OTP",
                                                                    "OTP 전송 실패: ${e.message}"
                                                                )
                                                            }
                                                    } else {
                                                        Log.e("Auth", "익명 로그인 후 user가 null입니다.")
                                                    }
                                                } else {
                                                    Log.e("Auth", "로그인 실패: ${task.exception}")

                                                }
                                            }

                                        timeLeft = 180 // 타이머를 3분으로 초기화
                                        isTimerRunning = true // 타이머 시작

                                    }
                                },
                                // 숙명 구글 계정이 입력된 경우
                                enabled = isSookmyungEmail,
                                shape = RoundedCornerShape(28),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.tertiary,
                                    contentColor = Color.White
                                ),
                                contentPadding = PaddingValues(
                                    horizontal = 16.dp,
                                    vertical = 8.dp
                                )
                            ) {
                                if(isEmailAvailable != true)
                                    Text("중복 확인", style = MaterialTheme.typography.bodyLarge)
                                else {
                                    Text("인증번호 전송", style = MaterialTheme.typography.bodyLarge)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                }

                // 인증번호 입력 문구
                item {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row {
                            Text("인증번호 ", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                " *",
                                color = Color.Red,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        TextField(
                            value = authCode,
                            onValueChange = { authCode = it },
                            modifier = Modifier
                                .fillMaxWidth(),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color(0xFFF4F4F4),
                                focusedContainerColor = Color(0xFFF4F4F4),
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                cursorColor = Color.DarkGray
                            ),
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // 버튼을 오른쪽 정렬
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                isAuthencatedMsg,
                                color = if(isAuthencated) MaterialTheme.colorScheme.tertiary else Color.Red,
                                style = MaterialTheme.typography.labelSmall
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            Button(
                                onClick = {
                                    val functions = Firebase.functions("asia-northeast3") // region 설정
                                    val verifyOtp = functions.getHttpsCallable("verifyOtp")

                                    verifyOtp.call(hashMapOf("email" to email, "otp" to authCode))
                                        .addOnSuccessListener { result ->
                                            Log.d("OTP", "인증 성공: ${result.data}")
                                            isAuthencated = true
                                            isAuthencatedMsg = "인증에 성공했습니다."
                                            isTimerRunning = false
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e("OTP", "인증 실패: ${e.message}")
                                            isAuthencated = false
                                            isAuthencatedMsg = "인증에 실패했습니다."
                                        }
                                },
                                shape = RoundedCornerShape(28),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.tertiary,
                                    contentColor = Color.White
                                ),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text("인증 확인", style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }
                }

            }
        }
    }
}

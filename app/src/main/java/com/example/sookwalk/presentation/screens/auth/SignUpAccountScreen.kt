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
    var isAvailableId by remember { mutableStateOf(false) } // 아이디 사용 가능 여부
    var isAvailableIdMsg by remember { mutableStateOf("") }

    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") } // 비밀번호 확인
    var isVisible by remember { mutableStateOf(false) } // 비밀번호 가시성

    var email by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") } // OTP 코드
    var isTimerRunning by remember { mutableStateOf(false) } // 타이머 동작 여부
    var timeLeft by remember { mutableStateOf(180) } // 남은 시간 (초 단위, 3분 = 180초)
    var isAuthencated by remember { mutableStateOf(false) } // 이메일 인증 여부

    var moveNextEnabled by remember { mutableStateOf(false) } // 다음 페이지 이동

    // 모든 요건을 만족하면 다음 페이지로 이동한다
    if (isAvailableId && isAuthencated && password == confirmPassword) {
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface),
                horizontalArrangement = Arrangement.End,
            ) {
                Button(
                    onClick = {
                        // 입력 정보 viewModel에 저장
                        viewModel.updateLoginId(loginId)
                        viewModel.updatePassword(password)
                        viewModel.updateEmail(email)
                        // 회원가입 - 프로필 설정 페이지로 이동
                        navController.navigate(Routes.PROFILE)
                    },
                    enabled = moveNextEnabled,
                    shape = RoundedCornerShape(28),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("다음", style = MaterialTheme.typography.displaySmall)
                }
            }
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
                                color = Color.Red,
                                style = MaterialTheme.typography.labelSmall
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = {
                                    viewModel.isLoginIdAvailable(loginId)

                                    if (viewModel.isLoginIdAvailable.value) {
                                        isAvailableIdMsg = "사용 가능한 아이디입니다."
                                        isAvailableId = true
                                    } else {
                                        isAvailableIdMsg = "이미 존재하는 아이디입니다."
                                    }
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
                                Text("중복 확인", style = MaterialTheme.typography.displaySmall)
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
                            onValueChange = { password = it },
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

                        // 이메일 입력 TextField
                        var email by remember { mutableStateOf("") }
                        TextField(
                            value = email,
                            onValueChange = { email = it },
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
                                    Firebase.auth.signInAnonymously()
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                Log.d("Auth", "익명 로그인 성공. OTP 전송을 시작합니다.")
                                                val functions = Firebase.functions("asia-northeast3") // region 설정
                                                val sendOtp = functions.getHttpsCallable("sendOtp")
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
                                                            Log.e("OTP",
                                                                "OTP 전송 실패: ${e.message}")
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
                                Text("인증 번호 전송", style = MaterialTheme.typography.displaySmall)
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

                        // 인증 번호 입력 TextField
                        var authCode by remember { mutableStateOf("") }
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
                            Button(
                                onClick = {
                                    val functions = Firebase.functions("asia-northeast3") // region 설정
                                    val verifyOtp = functions.getHttpsCallable("verifyOtp")

                                    verifyOtp.call(hashMapOf("email" to email, "otp" to code))
                                        .addOnSuccessListener { result ->
                                            Log.d("OTP", "인증 성공: ${result.data}")
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e("OTP", "인증 실패: ${e.message}")
                                        }
                                },
                                shape = RoundedCornerShape(28),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.tertiary,
                                    contentColor = Color.White
                                ),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text("인증 확인", style = MaterialTheme.typography.displaySmall)
                            }
                        }
                    }
                }

            }
        }
    }
}

package com.example.sookwalk.presentation.screens.auth

import android.R.attr.navigationIcon
import android.R.attr.password
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults.contentPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun SignUpAccountScreen(
    // viewModel: TodoViewModel,
    // navController: NavController,
    // backStackEntry: NavBackStackEntry
) {

    var id by remember { mutableStateOf("") }

    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isVisible by remember { mutableStateOf(false) }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },

                navigationIcon = {
                    IconButton(onClick = {
                        // 뒤로가기 로직
                        // navController?.popBackStack()
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
                    onClick = { /* 페이지 이동 로직 */ },
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
                            value = id,
                            onValueChange = { id = it },
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
                                onClick = { /* 아이디 중복 확인 로직 */ },
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
                                onClick = { /* 인증 번호 확인 로직 */ },
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

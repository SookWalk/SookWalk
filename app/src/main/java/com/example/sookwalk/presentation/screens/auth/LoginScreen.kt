package com.example.sookwalk.presentation.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.sookwalk.R
import com.example.sookwalk.navigation.Routes
import com.example.sookwalk.presentation.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    navController: NavController
) {

    var loginAvailableMsg by remember { mutableStateOf("") }

    // 아이디 입력 textfield
    var id by remember { mutableStateOf("") }

    // 비밀번호 입력 textfield
    var password by remember { mutableStateOf("") }
    var isVisible by remember { mutableStateOf(false) }

    Scaffold { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 앱 소개 문구
                Column {
                    Text(
                        "숙명에서의 한 걸음,",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "   SookWalk와 함께 시작하세요",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Image(
                    painter = painterResource(id = R.drawable.ic_walking_man),
                    contentDescription = "앱 메인 이미지",
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))


                TextField(
                    value = id,
                    onValueChange = { id = it },
                    label = { Text("아이디") },
                    modifier = Modifier
                        .fillMaxWidth(0.9f),

                    // TextField 색깔 지정
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFF4F4F4),
                        focusedContainerColor = Color(0xFFF4F4F4),
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedLabelColor = Color.Gray,
                        focusedLabelColor = Color.Gray,
                        cursorColor = Color.DarkGray
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))


                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("비밀번호") },
                    modifier = Modifier
                        .fillMaxWidth(0.9f),

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
                    },

                    // TextField 색깔 지정
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFF4F4F4),
                        focusedContainerColor = Color(0xFFF4F4F4),
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedLabelColor = Color.Gray,
                        focusedLabelColor = Color.Gray,
                        cursorColor = Color.DarkGray
                    )
                )

                Text(
                    text = loginAvailableMsg,
                    color = Color.Red,
                    modifier = Modifier.padding(8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 로그인 버튼
                Button(
                    onClick = {
                        viewModel.login(id, password)

                        if(viewModel.isLoginSuccess.value) {
                            // 홈 화면으로 이동
                            navController.navigate(Routes.HOME)
                        } else {
                            loginAvailableMsg = "아이디나 비밀번호가 잘못 되었습니다.\n" +
                                    "아이디와 비밀번호를 정확히 입력해 주세요."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.background
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.9f),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text("로그인")
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 회원 가입
                Row(
                    modifier = Modifier.fillMaxWidth(0.7f),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "저희 앱이 처음이시라면?"
                    )

                    Text(
                        "회원가입",
                        modifier = Modifier
                            .clickable { navController.navigate(Routes.ACCOUNT) }
                    )
                }
            }
        }
    }
}
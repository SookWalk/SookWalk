package com.example.sookwalk.auth

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.example.sookwalk.R
import com.example.sookwalk.ui.theme.Grey80

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun LoginScreen(
    // viewModel: TodoViewModel,
    // navController: NavController,
    // backStackEntry: NavBackStackEntry
) {

    Scaffold{ padding ->
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
                Text("숙명에서의 한 걸음,")
                Text("SookWalk와 함께 시작하세요 ")

                Spacer(modifier = Modifier.height(32.dp))

                Image(
                    painter = painterResource(id = R.drawable.main_image),
                    contentDescription = "앱 메인 이미지",
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 아이디 입력 textfield
                var id by remember { mutableStateOf("") }
                TextField(
                    value = id,
                    onValueChange = { id = it },
                    label = { Text("아이디")},
                    modifier = Modifier
                        .fillMaxWidth(0.9f),

                )

                Spacer(modifier = Modifier.height(12.dp))

                // 비밀번호 입력 textfield
                var password by remember { mutableStateOf("") }
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("비밀번호")},
                    modifier = Modifier
                        .fillMaxWidth(0.9f),
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 로그인 버튼
                Button(
                    onClick = { /* 로그인 처리 로직 */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.background
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.9f),
                    shape = RoundedCornerShape(24.dp)
                ){
                    Text("로그인")
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 회원 가입
                Row(
                    modifier = Modifier.fillMaxWidth(0.7f),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        "저희 앱이 처음이시라면?"
                    )

                    Text(
                        "회원가입",
                        modifier = Modifier
                            .clickable{ /* 회원가입 화면 이동 */ }
                    )
                }
            }
        }
    }
}
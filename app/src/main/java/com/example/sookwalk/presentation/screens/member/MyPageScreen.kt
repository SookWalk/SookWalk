package com.example.sookwalk.presentation.screens.member

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.sookwalk.R
import com.example.sookwalk.data.local.entity.user.UserEntity
import com.example.sookwalk.navigation.Routes
import com.example.sookwalk.presentation.components.TopBar
import com.example.sookwalk.presentation.viewmodel.UserViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPageScreen(
    viewModel: UserViewModel,
    navController: NavController,
    // backStackEntry: NavBackStackEntry
) {


    val context = LocalContext.current

    // ViewModel의 StateFlow를 구독하여 Room DB의 사용자 정보를 실시간으로 받습니다.
    val currentUser by viewModel.currentUser.collectAsState()

    Scaffold(
        topBar = {
            TopBar(
                screenName = "마이페이지",
                { navController.popBackStack() },
                {navController.navigate("alarm")},
                {}
            )
        }

    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(innerPadding)
                    .align(Alignment.TopCenter), // 중앙 가로, 세로는 맨 위
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // 프로필, 사용자 정보
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .aspectRatio(1f)
                    ) {

                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                // Room DB에서 가져온 URL을 사용
                                .data(
                                    currentUser?.profileImageUrl ?: R.drawable.default_profile_image
                                )
                                .crossfade(true)
                                .build(),
                            contentDescription = "Profile Image",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(id = R.drawable.default_profile_image)
                        )
                    }

                        // 닉네임, 학과 정보
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {

                            Text(
                                text = currentUser?.nickname ?: "정보 없음",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                // 사용자 학과 정보 가져오는 로직 나중에 필요
                                text = currentUser?.major ?: "정보 없음",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                // 프로필 수정 버튼
                item {
                    Button(
                        onClick = { navController.navigate(Routes.MYPAGE_EDIT) },
                        shape = RoundedCornerShape(28),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text("프로필 수정", style = MaterialTheme.typography.bodySmall)
                    }
                }

                // 구분 줄
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(modifier = Modifier.padding(8.dp))
                }

                // 계정 정보 란
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()          // LazyColumn 아이템 전체 너비 차지
                            .padding(horizontal = 12.dp) // 좌우 여백
                    ) {
                        Text(
                            "계정 정보",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(top = 16.dp, bottom = 16.dp)
                                .align(Alignment.CenterStart),
                            textAlign = TextAlign.Start, // 왼쪽 정렬
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("아이디", style = MaterialTheme.typography.bodySmall)

                            Spacer(modifier = Modifier.width(4.dp))

                            // 아이디 정보 불러오는 로직 필요
                            Text(currentUser?.loginId?:"정보 없음", style = MaterialTheme.typography.labelSmall)
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("이메일", style = MaterialTheme.typography.bodySmall)

                            Spacer(modifier = Modifier.width(4.dp))

                            // 이메일 정보 불러오는 로직 필요
                            Text(
                                currentUser?.email?: "정보 없음",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp)
                            // clickable 필요할 수 있음..
                        ) {
                            Text("비밀번호 변경", style = MaterialTheme.typography.bodySmall)
                        }

                    }
                }
            }
        }
    }
}
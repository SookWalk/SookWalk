package com.example.sookwalk.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.sookwalk.R
import com.example.sookwalk.navigation.Routes
import com.example.sookwalk.presentation.viewmodel.UserViewModel

@Composable
fun DrawerContent(
    userViewModel: UserViewModel,
    navController: NavController,
    onCloseDrawer: () -> Unit
) {

    // UserViewModel의 currentUser StateFLow 구독
    val currentUser by userViewModel.currentUser.collectAsStateWithLifecycle()

    // Column이 전체 높이를 차지하도록 fillMaxHeight() 추가
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(25.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- 상단 컨텐츠 (프로필, 마이페이지, 뱃지) ---
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // 프로필 사진
                    AsyncImage(
                        model = currentUser?.profileImageUrl,
                        placeholder = painterResource(id = R.drawable.default_profile_image),
                        error = painterResource(id = R.drawable.default_profile_image),
                        contentDescription = "프로필 이미지",
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    // 닉네임
                    Text(
                        text = currentUser?.nickname ?: "사용자",
                        style = MaterialTheme.typography.titleSmall
                    )
                }

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                Row(
                    modifier = Modifier
                        .padding(vertical = 32.dp)
                        .fillMaxWidth()
                        .clickable {
                            onCloseDrawer()
                            // 마이페이지로 이동
                            navController.navigate(Routes.MYPAGE)
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.Person, contentDescription = "profile"
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        "마이 페이지",
                    )
                }

                Row(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth()
                        .clickable {
                            // 뱃지 페이지로 이동
                            onCloseDrawer()
                            navController.navigate(Routes.BADGES)
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.Stars, contentDescription = "badge"
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("뱃지")
                }
            }

            // --- 하단 컨텐츠 (설정) ---
            Row(
                modifier = Modifier
                    .padding(vertical = 32.dp)
                    .fillMaxWidth()
                    .clickable {
                        /* 환경 설정 페이지로 이동*/
                        onCloseDrawer()
                        navController.navigate(Routes.SETTINGS)
                    },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.Settings, contentDescription = "settings"
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text("설정")
            }
        }
    }
}
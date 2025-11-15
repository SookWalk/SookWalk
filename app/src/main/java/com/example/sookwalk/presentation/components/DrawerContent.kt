package com.example.sookwalk.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DrawerContent(nickname: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row() {
            /* 프로필 사진 불러오는 코드 */
            Text(nickname)
        }

        HorizontalDivider(modifier = Modifier.fillMaxWidth())

        Row(
            modifier = Modifier
                .clickable{
                /* 마이페이지로 이동*/
            }
        ){
            Icon(
                imageVector = Icons.Default.Person, contentDescription = "profile"
            )
            Text("마이 페이지")
        }

        Row(
            modifier = Modifier
                .clickable{
                    /* 뱃지 페이지로 이동*/
                }
        ){
            Icon(
                imageVector = Icons.Default.Stars, contentDescription = "badge"
            )
            Text("뱃지")
        }

        Row(
            modifier = Modifier
                .clickable{
                    /* 환경 설정 페이지로 이동*/
                }
        ){
            Icon(
                imageVector = Icons.Default.Settings, contentDescription = "settings"
            )
            Text("설정")
        }
    }
}
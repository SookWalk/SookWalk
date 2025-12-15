package com.example.sookwalk.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpBottomControlBar(
    screenName: String,
    onNextClick: () -> Unit,
    moveNextEnabled: Boolean // 유효성 검사 통과 여부에 따라 활성화/비활성화
){
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.surface,
        actions = {
            Spacer(Modifier.weight(1f)) // 오른쪽 정렬
            Button(
                onClick = onNextClick,
                enabled = moveNextEnabled,

                shape = RoundedCornerShape(28),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                modifier = Modifier.padding(8.dp),

                ) {
                if(screenName == "SignUpAccount")
                    Text("다음")
                else
                    Text("회원가입")
            }
        }
    )
}
package com.example.sookwalk.utils.goal

// [유틸 함수] Millis -> "yyyy-MM-dd" 변환 함수
fun convertMillisToDateString(millis: Long): String {
    val formatter = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
    return formatter.format(java.util.Date(millis))
}
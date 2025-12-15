package com.example.sookwalk.data.remote.dto

import com.google.firebase.Timestamp

data class GoalDto(
    val id: String = "",
    val title: String = "",
    val targetSteps: Int,
    // 현재까지 채운 걸음 수
    val currentSteps: Int,
    // yyyy-MM-dd
    val startDate: String = "",
    // 목표 기한
    val endDate: String = "",
    val memo: String = "",
    val isDone: Boolean = false,
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
)

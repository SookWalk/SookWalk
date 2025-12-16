package com.example.sookwalk.data.local.entity.notification

object NotificationSampleData {
    fun samples(): List<NotificationEntity> {
        val now = System.currentTimeMillis()

        return listOf(
            NotificationEntity(
                goalId = 1,
                title = "🎯 목표 알림",
                message = "오늘의 목표를 시작해볼까요?",
                createdAt = now - 1000 * 60 * 10
            ),
            NotificationEntity(
                goalId = null,
                title = "🔥 연속 달성",
                message = "3일 연속 목표를 달성했어요!",
                createdAt = now - 1000 * 60 * 60
            ),
            NotificationEntity(
                goalId = 2,
                title = "⏰ 리마인더",
                message = "아직 목표가 완료되지 않았어요.",
                createdAt = now - 1000 * 60 * 60 * 5
            )
        )
    }
}
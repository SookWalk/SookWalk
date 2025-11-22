package com.example.sookwalk.utils.notification

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object DateUtils {

    // 공통 Formatter
    private val formatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    // Long → 날짜 문자열
    fun formatTimestamp(timestamp: Long): String {
        val dateTime = Instant.ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()

        return dateTime.format(formatter)
    }
}
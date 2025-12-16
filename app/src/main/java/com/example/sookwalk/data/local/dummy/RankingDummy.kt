package com.example.sookwalk.data.local.dummy

import com.example.sookwalk.data.enums.College
import com.example.sookwalk.data.enums.Department
import com.example.sookwalk.data.remote.dto.RankDto

object RankingDummy {
    fun dept(limit: Int = 10): List<RankDto> =
        Department.entries
            .map { dept ->
                RankDto(
                    id = dept.id,                 // 영어 id
                    name = dept.displayName,      // 한글
                    walkCount = (80_000..140_000).random().toLong(),
                    rank = 0
                )
            }
            .sortedByDescending { it.walkCount }
            .take(limit)
            .mapIndexed { idx, dto -> dto.copy(rank = idx + 1) }

    fun college(limit: Int = 10): List<RankDto> =
        College.entries
            .map { c ->
                RankDto(
                    id = c.id,
                    name = c.displayName,
                    walkCount = (300_000..600_000).random().toLong(),
                    rank = 0
                )
            }
            .sortedByDescending { it.walkCount }
            .take(limit)
            .mapIndexed { idx, dto -> dto.copy(rank = idx + 1) }
}
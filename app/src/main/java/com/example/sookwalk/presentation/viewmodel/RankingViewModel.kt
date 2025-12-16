package com.example.sookwalk.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sookwalk.data.enums.College
import com.example.sookwalk.data.enums.Department
import com.example.sookwalk.data.local.dummy.RankingDummy
import com.example.sookwalk.data.remote.dto.RankDto
import com.example.sookwalk.data.repository.RankingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class RankingViewModel @Inject constructor(
    private val repository: RankingRepository
): ViewModel(){

    private val deptDummy = RankingDummy.dept()
    private val collegeDummy = RankingDummy.college()

    val deptRanking: StateFlow<List<RankDto>> =
        repository.observeDeptRanking(10)
            .map { list ->
                val mapped = list.map { dto ->
                    dto.copy(name = deptNameFromId(dto.id))
                }.sortedByDescending { it.walkCount }

                val fixedRank = mapped.mapIndexed { idx, dto -> dto.copy(rank = idx + 1) }

                if (fixedRank.isEmpty()) deptDummy else fixedRank
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), deptDummy)

    val collegeRanking: StateFlow<List<RankDto>> =
        repository.observeCollegeRanking(10)
            .map { list ->
                val mapped = list.map { dto ->
                    dto.copy(name = collegeNameFromId(dto.id))
                }.sortedByDescending { it.walkCount }

                val fixedRank = mapped.mapIndexed { idx, dto -> dto.copy(rank = idx + 1) }

                if (fixedRank.isEmpty()) collegeDummy else fixedRank
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), collegeDummy)

    private fun deptNameFromId(id: String): String =
        Department.entries.firstOrNull { it.id == id }?.displayName ?: id

    private fun collegeNameFromId(id: String): String =
        College.entries.firstOrNull { it.id == id }?.displayName ?: id
}
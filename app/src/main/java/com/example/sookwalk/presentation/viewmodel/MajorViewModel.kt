package com.example.sookwalk.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sookwalk.data.repository.MajorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class MajorViewModel @Inject constructor(
    private val repository: MajorRepository): ViewModel() {

    // List<String> 타입의 MutableStateFlow를 생성
    private val _departments = MutableStateFlow<List<String>>(emptyList())

    // UI가 구독할 수 있는 읽기 전용 StateFlow를 노출
    val departments: StateFlow<List<String>> = _departments.asStateFlow()


    // 전공 리스트 가져오기
    fun getMajors() {
        viewModelScope.launch {
            try {
                // Repository에서 데이터를 가져옵니다.
                val majorLists = repository.getMajors()
                _departments.value = majorLists
            } catch (e: Exception) {
                // 에러 발생 시, 로그를 남기고 빈 목록 상태를 유지합니다.
                // (이전 답변에서 경고했듯이, 에러 상태를 별도로 UI에 알릴 방법은 없습니다.)
                Log.e("MajorViewModel", "전공 목록 로딩 실패", e)
                _departments.value = emptyList()
            }
        }
    }
}
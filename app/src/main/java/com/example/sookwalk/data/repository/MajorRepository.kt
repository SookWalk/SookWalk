package com.example.sookwalk.data.repository

import com.google.firebase.Firebase

import kotlinx.coroutines.tasks.await
import android.util.Log
import com.google.firebase.firestore.firestore
import jakarta.inject.Inject

// 전공 목록을 불러오는 데이터 계층 클래스
class MajorRepository @Inject constructor(

) {
    val db = Firebase.firestore("sookwalk")

    suspend fun getMajors(): List<String> {
        val allMajors = mutableListOf<String>()

        try {
            // 1. 'collages' 컬렉션에 있는 모든 단과대학 문서들을 가져옴
            // .await()을 사용하여 suspend 함수 내에서 비동기 작업을 동기적으로 기다립니다.
            val colleges = db.collection("colleges").get().await()

            // 2. 각 단과대학 문서에 대해 반복
            for (collegeDoc in colleges.documents) {
                // 3. 해당 단과대학의 'majors' 하위 컬렉션에 있는 모든 세부 전공들을 가져옴
                val majors = db.collection("colleges").document(collegeDoc.id)
                    .collection("majors").get().await()

                // 4. 가져온 세부 전공들의 이름을 리스트에 추가
                for (majorDoc in majors.documents) {
                    majorDoc.getString("major")?.let { majorName ->
                        allMajors.add(majorName)
                    }
                }
            }

            // 5. 완성된 전체 전공 리스트를 반환
            return allMajors.sorted() // 가나다순 정렬

        } catch (e: Exception) {
            // 에러가 발생하면 로그를 기록하고, 빈 리스트 또는 예외를 다시 던질 수 있습니다.
            Log.e("MajorRepository", "전공 목록을 불러오는 데 실패했습니다.", e)
            throw e // ViewModel에서 에러를 처리할 수 있도록 예외를 던짐
        }
    }
}
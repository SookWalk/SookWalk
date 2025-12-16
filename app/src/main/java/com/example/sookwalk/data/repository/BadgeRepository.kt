package com.example.sookwalk.data.repository

import android.util.Log
import com.example.sookwalk.data.local.dao.UserDao
import com.example.sookwalk.data.local.entity.user.UserEntity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import com.google.firebase.Timestamp

class BadgeRepository @Inject constructor(
    private val db: FirebaseFirestore
) {
    // Firebase에서 현재 유저의 uid를 가져온다
    private val uid: String
        get() = Firebase.auth.currentUser?.uid
            ?: throw IllegalStateException("로그인되지 않은 상태에서 UserRepository 접근")


    // 공통 로직을 별도의 private 함수로 분리
    private suspend fun fetchStatsField(docName: String): Pair<Int, Timestamp?> {
        return try {
            // 경로: users/{uid}/stats/{docName} (예: step, challenge 등)
            val snapshot = db.collection("users").document(uid)
                .collection("stats").document(docName)
                .get().await()

            if (snapshot.exists()) {
                val total = snapshot.getLong("total")?.toInt() ?: 0
                val date = snapshot.get("date") as? Timestamp

                Log.d("BadgeRepo", "✅ $docName 로드: $total, $date")
                Pair(total, date)
            } else {
                Pair(0, null)
            }
        } catch (e: Exception) {
            Log.e("BadgeRepo", "❌ $docName 에러: ${e.message}")
            Pair(0, null)
        }
    }

    // Firestore에서 레벨 기준 배열을 가져오는 공통 함수
    private suspend fun getLevelThresholds(fieldName: String): List<Long> {
        return try {
            val snapshot = db.collection("badges")
                .document(fieldName)
                .get()
                .await()

            // fieldName에 해당하는 필드가 [1000, 5000, 10000] 같은 숫자 배열인지 확인
            snapshot.get("level") as? List<Long> ?: emptyList()
        } catch (e: Exception) {
            Log.e("BadgeRepo", "$fieldName 기준표 로드 실패", e)
            emptyList()
        }
    }

    // 현재 값과 기준표를 비교하여 레벨(Index + 1)을 계산하는 함수
    private fun calculateLevel(currentValue: Int, thresholds: List<Long>): Int {
        if (thresholds.isEmpty()) return 0

        var level = 0
        for (i in thresholds.indices) {
            if (currentValue >= thresholds[i]) {
                level = i + 1 // 기준을 통과할 때마다 레벨 업
            } else {
                break // 기준을 통과하지 못하면 루프 종료
            }
        }
        return level
    }


    // 걸음 수
    suspend fun getTotalSteps() = fetchStatsField("step")

    // 즐겨찾기 장소 수
    suspend fun getTotalPlaces() = fetchStatsField("place")
    // 랭크 수
    suspend fun getTotalRanks() = fetchStatsField("rank")
    // 챌린지 수
    suspend fun getTotalChallenges() = fetchStatsField("challenge")



    // 수치와 레벨을 한 번에 가져오는 함수 (예: 걸음수)
    suspend fun getStepsWithLevel(): Triple<Int, Int, Timestamp?> {
        val steps = getTotalSteps().first // 기존 함수 사용
        val date = getTotalSteps().second
        val thresholds = getLevelThresholds("walkingMaster") // Firestore 배열 필드명
        val level = calculateLevel(steps, thresholds)
        val achievedThreshold = if (level > 0) thresholds[level - 1].toInt() else 0
        return Triple(achievedThreshold, level, date)
    }

    // 장소 수도 같은 방식으로 추가
    suspend fun getPlacesWithLevel(): Triple<Int, Int, Timestamp?> {
        val places = getTotalPlaces().first
        val date = getTotalPlaces().second
        val thresholds = getLevelThresholds("memoryCollector")
        val level = calculateLevel(places, thresholds)
        val achievedThreshold = if (level > 0) thresholds[level - 1].toInt() else 0
        return Triple(achievedThreshold, level, date)
    }

    // 랭크 수도 같은 방식으로 추가
    suspend fun getRanksWithLevel(): Triple<Int, Int, Timestamp?> {
        val ranks = getTotalRanks().first
        val date = getTotalRanks().second
        val thresholds = getLevelThresholds("championWalker")
        val level = calculateLevel(ranks, thresholds)
        val achievedThreshold = if (level > 0) thresholds[level - 1].toInt() else 0
        return Triple(achievedThreshold, level, date)
    }

    // 챌린지 수도 같은 방식으로 추가
    suspend fun getChallengesWithLevel(): Triple<Int, Int, Timestamp?> {
        val challenges = getTotalChallenges().first
        val date = getTotalRanks().second
        val thresholds = getLevelThresholds("challengePro")
        val level = calculateLevel(challenges, thresholds)
        val achievedThreshold = if (level > 0) thresholds[level - 1].toInt() else 0
        return Triple(achievedThreshold, level, date)
    }

}
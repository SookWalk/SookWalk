package com.example.sookwalk.data.repository

import com.example.sookwalk.data.remote.dto.RankDto
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class RankingRepository @Inject constructor(
    private val db: FirebaseFirestore
) {
    // firestore에 바로 접근 가능
    private fun deptCollection() =
        db.collection("rankings").document("current")
            .collection("departments")

    private fun collegeCollection() =
        db.collection("rankings").document("current")
            .collection("colleges")

    fun observeDeptRanking(limitCount: Long = 10): Flow<List<RankDto>> =
        callbackFlow {
            val reg = deptCollection()
                .orderBy("walkCount", Query.Direction.DESCENDING)
                .limit(limitCount)
                .addSnapshotListener { snap, e ->
                    if (e != null) {
                        close(e)
                        return@addSnapshotListener
                    }
                    if (snap == null) return@addSnapshotListener

                    val list = snap.documents.mapIndexed { index, doc ->
                        RankDto(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            walkCount = doc.getLong("walkCount") ?: 0L,
                            rank = index + 1
                        )
                    }
                    trySend(list).isSuccess
                }

            awaitClose { reg.remove() }
        }

    fun observeCollegeRanking(limitCount: Long = 10): Flow<List<RankDto>> =
        callbackFlow {
            val reg = collegeCollection()
                .orderBy("walkCount", Query.Direction.DESCENDING)
                .limit(limitCount)
                .addSnapshotListener { snap, e ->
                    if (e != null) {
                        close(e)
                        return@addSnapshotListener
                    }
                    if (snap == null) return@addSnapshotListener

                    val list = snap.documents.mapIndexed { index, doc ->
                        RankDto(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            walkCount = doc.getLong("walkCount") ?: 0L,
                            rank = index + 1
                        )
                    }
                    trySend(list).isSuccess
                }

            awaitClose { reg.remove() }
        }


}
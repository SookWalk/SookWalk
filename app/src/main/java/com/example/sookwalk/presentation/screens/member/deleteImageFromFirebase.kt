package com.example.sookwalk.presentation.screens.member

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.storage.storage

/**
 * Firebase Storage에서 현재 사용자의 프로필 이미지를 삭제하는 헬퍼 함수.
 * @param onSuccess 삭제 성공 시 호출될 콜백.
 * @param onFailure 삭제 실패 시 호출될 콜백.
 */

fun deleteImageFromFirebase(
    onSuccess: () -> Unit,
    onFailure: (exception: Exception) -> Unit
) {
    // 1. 현재 로그인된 사용자의 UID를 가져옴
    val uid = Firebase.auth.currentUser?.uid ?: run {
        onFailure(Exception("사용자가 로그인되어 있지 않아 삭제할 수 없습니다."))
        return
    }

    // 2. 삭제할 이미지의 정확한 경로를 지정하여 참조 생성
    //    이 경로는 업로드할 때 사용한 경로와 반드시 동일해야 함
    val imageRef = Firebase.storage.reference.child("images/$uid/profile.jpg")

    // 3. delete() 메서드를 호출하여 파일 삭제 시도
    imageRef.delete()
        .addOnSuccessListener {
            // 4. 삭제 성공 시, 성공 콜백을 호출
            onSuccess()
        }
        .addOnFailureListener { exception ->
            // 5. 삭제 실패 시, 실패 콜백을 호출
            // 파일이 원래 없었던 경우 'Object does not exist' 오류가 발생 가능, 실패 처리는 X
            onFailure(exception)
        }
}

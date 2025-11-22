package com.example.sookwalk.presentation.screens.member

import android.net.Uri
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.storage.storage

fun uploadImageToFirebase(
    imageUri: Uri,
    onSuccess: (downloadUrl: String) -> Unit,
    onFailure: (exception: Exception) -> Unit
) {
    // 1. 현재 로그인된 사용자의 UID를 가져옵니다.
    val uid = Firebase.auth.currentUser?.uid ?: run {
        onFailure(Exception("사용자가 로그인되어 있지 않아 업로드할 수 없습니다."))
        return
    }

    // 2. Firebase Storage의 참조를 가져옴
    val storageRef = Firebase.storage.reference

    // 3. 이미지가 저장될 최종 경로와 파일명을 지정
    // 예: images/사용자UID/profile.jpg
    val profileImageRef = storageRef.child("images/$uid/profile.jpg")

    // 4. `putFile`을 사용하여 이미지 Uri를 스토리지에 업로드
    profileImageRef.putFile(imageUri)
        .addOnSuccessListener {
            // 5. 업로드 성공 시, 해당 파일의 다운로드 URL을 가져옴
            profileImageRef.downloadUrl // 'profileImage'가 아닌 'profileImageRef' 사용
                .addOnSuccessListener { uri ->
                    // 6. 성공 콜백 함수를 통해 최종 URL을 전달
                    onSuccess(uri.toString())
                }
                .addOnFailureListener { exception ->
                    // 다운로드 URL을 가져오는 데 실패한 경우
                    onFailure(exception)
                }
        }
        .addOnFailureListener { exception ->
            // 파일 업로드 자체에 실패한 경우
            onFailure(exception)
        }
}

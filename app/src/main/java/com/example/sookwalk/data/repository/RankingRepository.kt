package com.example.sookwalk.data.repository

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class RankingRepository (
    private val db: FirebaseFirestore = Firebase.firestore
){
    // firestore에 바로 접근 가능
}
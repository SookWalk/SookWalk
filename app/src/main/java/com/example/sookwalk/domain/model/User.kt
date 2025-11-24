package com.example.sookwalk.domain.model

data class User(
    val major: String,
    val email: String,
    var nickname: String,
    var loginId: String,
    var profileImageUrl: String
)
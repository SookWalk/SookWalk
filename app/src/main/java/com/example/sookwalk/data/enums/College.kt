package com.example.sookwalk.data.enums

enum class College (
    val id: String,
    val displayName: String
){
    HUMANITIES("humanities", "문과대학"),
    SCIENCE("science", "이과대학"),
    ENGINEERING("engineering", "공과대학"),
    LIFE_SCIENCE("life_science", "생활과학대학"),
    SOCIAL_SCIENCE("social_science", "사회과학대학"),
    LAW("law", "법과대학"),
    ECONOMICS("economics", "경상대학"),
    MUSIC("music", "음악대학"),
    PHARMACY("pharmacy", "약학대학"),
    ART("art", "미술대학"),
    GLOBAL_SERVICE("global_service", "글로벌서비스학부"),
    ENGLISH_COLLEGE("english_college", "영어영문학부"),
    MEDIA("media", "미디어학부"),
    GLOBAL_CONVERGENCE("global_convergence", "글로벌융합대학");

    companion object {
        fun fromId(id: String): College =
            values().find { it.id == id }
                ?: throw IllegalArgumentException("Unknown college id: $id")
    }

    private fun collegeNameFromId(id: String): String {
        return College.entries.firstOrNull { it.id == id }?.displayName ?: id
    }

}
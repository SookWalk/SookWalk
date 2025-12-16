package com.example.sookwalk.data.enums

enum class Department (
    val id: String,
    val displayName: String,
    val college: College
){
    /* ================= 문과대학 ================= */
    KOREAN_LANGUAGE("korean_language", "한국어문학부", College.HUMANITIES),
    HISTORY_CULTURE("history_culture", "역사문화학과", College.HUMANITIES),
    FRENCH_LANGUAGE("french_language", "프랑스언어·문화학과", College.HUMANITIES),
    CHINESE_LANGUAGE("chinese_language", "중어중문학부", College.HUMANITIES),
    GERMAN_LANGUAGE("german_language", "독일언어·문화학과", College.HUMANITIES),
    JAPANESE_LANGUAGE("japanese_language", "일본학과", College.HUMANITIES),
    CULTURAL_CONTENTS("cultural_contents", "문화정보학과", College.HUMANITIES),
    CULTURAL_TOURISM("cultural_tourism", "문화관광외식학부 문화관광학전공", College.HUMANITIES),
    FOOD_SERVICE("food_service", "문화관광외식학부 푸드콘텐츠외식경영전공", College.HUMANITIES),
    EDUCATION("education", "교육학부", College.HUMANITIES),

    /* ================= 이과대학 ================= */
    CHEMISTRY("chemistry", "화학과", College.SCIENCE),
    LIFE_SYSTEM("life_system", "생명시스템학부", College.SCIENCE),
    MATHEMATICS("mathematics", "수학과", College.SCIENCE),
    STATISTICS("statistics", "통계학과", College.SCIENCE),
    SPORTS_SCIENCE("sports_science", "체육교육과", College.SCIENCE),
    DANCE("dance", "무용과", College.SCIENCE),

    /* ================= 공과대학 ================= */
    CHEMICAL_ENGINEERING("chemical_engineering", "화공생명공학부", College.ENGINEERING),
    AI_ENGINEERING("ai_engineering", "인공지능공학부", College.ENGINEERING),
    INTELLIGENT_ELECTRONICS("intelligent_electronics", "지능형전자시스템전공", College.ENGINEERING),
    MATERIALS_SCIENCE("materials_science", "신소재물리전공", College.ENGINEERING),
    COMPUTER_SCIENCE("computer_science", "컴퓨터과학전공", College.ENGINEERING),
    DATA_SCIENCE("data_science", "데이터사이언스전공", College.ENGINEERING),
    MECHANICAL_SYSTEM("mechanical_system", "기계시스템학부", College.ENGINEERING),

    /* ================= 생활과학대학 ================= */
    FAMILY_WELFARE("family_welfare", "가족자원경영학과", College.LIFE_SCIENCE),
    CHILD_WELFARE("child_welfare", "아동복지학부", College.LIFE_SCIENCE),
    CLOTHING_TEXTILES("clothing_textiles", "의류학과", College.LIFE_SCIENCE),
    FOOD_NUTRITION("food_nutrition", "식품영양학과", College.LIFE_SCIENCE),

    /* ================= 사회과학대학 ================= */
    POLITICAL_SCIENCE("political_science", "정치외교학과", College.SOCIAL_SCIENCE),
    PUBLIC_ADMIN("public_admin", "행정학과", College.SOCIAL_SCIENCE),
    ADVERTISING("advertising", "홍보광고학과", College.SOCIAL_SCIENCE),
    CONSUMER_ECONOMICS("consumer_economics", "소비자경제학과", College.SOCIAL_SCIENCE),
    SOCIAL_PSYCHOLOGY("social_psychology", "사회심리학과", College.SOCIAL_SCIENCE),

    /* ================= 법과대학 ================= */
    LAW("law", "법학부", College.LAW),

    /* ================= 경상대학 ================= */
    ECONOMICS_DEPT("economics_dept", "경제학부", College.ECONOMICS),
    BUSINESS_ADMIN("business_admin", "경영학부", College.ECONOMICS),

    /* ================= 음악대학 ================= */
    PIANO("piano", "피아노과", College.MUSIC),
    ORCHESTRA("orchestra", "관현악과", College.MUSIC),
    VOCAL("vocal", "성악과", College.MUSIC),
    COMPOSITION("composition", "작곡과", College.MUSIC),

    /* ================= 약학대학 ================= */
    PHARMACY("pharmacy", "약학부(통합6년제)", College.PHARMACY),

    /* ================= 미술대학 ================= */
    VISUAL_DESIGN("visual_design", "시각영상디자인과", College.ART),
    INDUSTRIAL_DESIGN("industrial_design", "산업디자인과", College.ART),
    ENVIRONMENT_DESIGN("environment_design", "환경디자인과", College.ART),
    CRAFT("craft", "공예과", College.ART),
    PAINTING("painting", "회화과", College.ART),

    /* ================= 글로벌 ================= */
    GLOBAL_COOPERATION("global_cooperation", "글로벌협력전공", College.GLOBAL_SERVICE),
    ENTREPRENEURSHIP("entrepreneurship", "앙트프러너십전공", College.GLOBAL_SERVICE),

    ENGLISH_LITERATURE("english_literature", "영어영문학전공", College.ENGLISH_COLLEGE),
    TESOL("tesol", "TESOL전공", College.ENGLISH_COLLEGE),

    MEDIA_SCIENCE("media_science", "미디어학전공", College.MEDIA),

    GLOBAL_CONVERGENCE("global_convergence", "글로벌융합학부", College.GLOBAL_CONVERGENCE);

    companion object {
        fun fromId(id: String): Department =
            values().find { it.id == id }
                ?: throw IllegalArgumentException("Unknown dept id: $id")
    }

    private fun deptNameFromId(id: String): String {
        return Department.entries.firstOrNull { it.id == id }?.displayName ?: id
    }
}
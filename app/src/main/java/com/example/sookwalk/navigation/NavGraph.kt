package com.example.sookwalk.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.sookwalk.presentation.screens.SettingsScreen
import com.example.sookwalk.presentation.screens.auth.LoginScreen
import com.example.sookwalk.presentation.screens.home.AlarmScreen
import com.example.sookwalk.presentation.screens.home.HomeScreen
import com.example.sookwalk.presentation.screens.home.RankingScreen
import com.example.sookwalk.presentation.viewmodel.GoalViewModel
import com.example.sookwalk.presentation.viewmodel.NotificationViewModel
import com.example.sookwalk.presentation.viewmodel.SettingsViewModel

@Composable
fun NavGraph(navController: NavHostController) {
//    val authViewModel: AuthViewModel = hiltViewModel() // 회원가입
//    val userViewModel: UserViewModel = hiltViewModel() // 마이페이지 등

    /// 그 외 각자 만든 viewModel들 추가 ( ThemeViewModel 제외 )
    // ....
    // val badgeViewModel: BadgeViewModel = hiltViewModel()
    // ....
    val settingsViewModel: SettingsViewModel = hiltViewModel() // 환경 설정
    val goalViewModel: GoalViewModel = hiltViewModel()
    val notificationViewModel: NotificationViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = Routes.LOGIN) {

        ////// 첫 화면 //////

        composable("login") {
            if (/* 로그인이 되어있을 경우 */) {
                HomeScreen(UserViewModel, navController)
            } else LoginScreen(userViewModel, navController)
        }


        ////// TopBar에서 쓰이는 경로 //////

        // 알림 페이지
        composable(Routes.ALARM) {
            AlarmScreen(notificationViewModel, navController)
        }

        // 마이 페이지
        composable(Routes.MYPAGE) {
            MyPageScreen(userViewModel, navController)
        }

        // 뱃지
        composable(Routes.BADGES) {
            BadgeScreen(viewModel, navController)
        }

        // 환경 설정
        composable(Routes.SETTINGS) {
            SettingsScreen(settingsViewModel, navController)
        }


        ////// BottomNavBar에서 쓰이는 경로 //////
        ////// 메인 홈에서 특정 버튼 선택 시 쓰이는 경로 /////

        // 메인 홈
        composable(Routes.HOME) {
            HomeScreen(userViewModel, /* 등등..? */ , navController)
        }

        // 목표
        composable("goals") {
            GoalScreen(viewModel, navController)
        }

        // 랭킹
        composable(Routes.RANK) {
            RankingScreen(viewModel, navController)
        }

        // 지도
        composable("map") {
            MapScreen(viewModel, navController)
        }


        /////// 그 외 기타 스크린 ///////

        // 회원 가입
        composable("signUpAccount") {
            SignUpAccountScreen(authViewModel, navController)
        }

        composable("signUpProfile") {
            SignUpProfileScreen(authViewModel, navController)
        }

        // 마이페이지 수정
        composable("myPageEdit") {
            MyPageEditScreen(userViewModel, navController)
        }

        // 목표 추가, 수정
        // 캘린더 내 날짜도 함께 인자로 받아야 할 것 같기도.. 수정 부탁드려요
        composable("addEditGoal?goalId={goalId}") { backStackEntry ->
            AddGoalScreen(viewModel, navController, backStackEntry)
        }

        // 장소 즐겨찾기 화면
        composable("favorites") {
            FavoritesScreen(viewModel, navController)
        }

        // 장소 리스트 스크린
        // backStackEntry 필요? 수정 부탁드려요..
        composable("placesList") { backStackEntry ->
            PlacesListScreen(viewModel, navController, backStackEntry)
        }
    }
}
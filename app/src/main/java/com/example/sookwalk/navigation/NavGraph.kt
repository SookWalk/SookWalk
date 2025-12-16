package com.example.sookwalk.navigation

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.sookwalk.presentation.components.AppRightDrawer
import com.example.sookwalk.presentation.screens.SettingsScreen
import com.example.sookwalk.presentation.screens.auth.LoginScreen
import com.example.sookwalk.presentation.screens.auth.SignUpAccountScreen
import com.example.sookwalk.presentation.screens.auth.SignUpProfileScreen
import com.example.sookwalk.presentation.screens.badge.BadgeScreen
import com.example.sookwalk.presentation.screens.goal.AddGoalScreen
import com.example.sookwalk.presentation.screens.goal.GoalScreen
import com.example.sookwalk.presentation.screens.home.AlarmScreen
import com.example.sookwalk.presentation.screens.home.HomeScreen
import com.example.sookwalk.presentation.screens.home.RankingScreen
import com.example.sookwalk.presentation.screens.map.MapScreen
import com.example.sookwalk.presentation.screens.member.MyPageEditScreen
import com.example.sookwalk.presentation.screens.member.MyPageScreen
import com.example.sookwalk.presentation.viewmodel.AuthViewModel
import com.example.sookwalk.presentation.viewmodel.BadgeViewModel
import com.example.sookwalk.presentation.viewmodel.GoalViewModel
import com.example.sookwalk.presentation.viewmodel.MajorViewModel
import com.example.sookwalk.presentation.viewmodel.MapViewModel
import com.example.sookwalk.presentation.viewmodel.NotificationViewModel
import com.example.sookwalk.presentation.viewmodel.RankingViewModel
import com.example.sookwalk.presentation.viewmodel.SettingsViewModel
import com.example.sookwalk.presentation.viewmodel.StepViewModel
import com.example.sookwalk.presentation.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun NavGraph(navController: NavHostController,modifier: Modifier = Modifier) {
    val authViewModel: AuthViewModel = hiltViewModel() // 회원가입
    val userViewModel: UserViewModel = hiltViewModel() // 마이페이지 등

    /// 그 외 각자 만든 viewModel들 추가 ( ThemeViewModel 제외 )

    val settingsViewModel: SettingsViewModel = hiltViewModel() // 환경 설정
    val goalViewModel: GoalViewModel = hiltViewModel()
    val rankingViewModel: RankingViewModel = hiltViewModel()
    val notificationViewModel: NotificationViewModel = hiltViewModel()
    val stepViewModel: StepViewModel = hiltViewModel()
    val badgeViewModel: BadgeViewModel = hiltViewModel()
    val mapViewModel: MapViewModel = hiltViewModel()
    var majorViewModel: MajorViewModel = hiltViewModel()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // 로그인 여부 체크
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

    LaunchedEffect(Unit) {
        authViewModel.checkLoginStatus()
    }

    NavHost(navController = navController, startDestination = Routes.LOGIN) {

        ////// 첫 화면 //////

        composable(Routes.LOGIN) {
            AppRightDrawer(
                drawerState = drawerState,
                userViewModel = userViewModel,
                navController = navController,
                scope = scope
            ) {
            if (isLoggedIn) {
                HomeScreen(goalViewModel, stepViewModel, navController,
                    onBack = { navController.popBackStack() },
                    onAlarmClick = {navController.navigate(Routes.NOTIFICATION)},
                    onMenuClick = { scope.launch {
                        if (drawerState.isClosed) drawerState.open() else drawerState.close()
                    }},
                    onRankingBtnClick = {navController.navigate(Routes.RANK)}
                )
            } else
                LoginScreen(authViewModel, navController)
            }
        }


        ////// TopBar에서 쓰이는 경로 //////

        // 알림 페이지
        composable(Routes.NOTIFICATION) {
            AppRightDrawer(
                drawerState = drawerState,
                userViewModel = userViewModel,
                navController = navController,
                scope = scope
            ) {
                AlarmScreen(
                    notificationViewModel, navController,
                    onBack = { navController.popBackStack() },
                    onAlarmClick = { navController.navigate(Routes.NOTIFICATION) },
                    onMenuClick = { scope.launch {
                        if (drawerState.isClosed) drawerState.open() else drawerState.close()
                    } }
                )
            }
        }

        // 마이 페이지
        composable(Routes.MYPAGE) {
            MyPageScreen(userViewModel, navController)
        }

        // 뱃지
        composable(Routes.BADGES) {
            AppRightDrawer(
                drawerState = drawerState,
                userViewModel = userViewModel,
                navController = navController,
                scope = scope
            ) {
                BadgeScreen(
                    badgeViewModel, navController,
                    onBack = { navController.popBackStack() },
                    onAlarmClick = { navController.navigate(Routes.NOTIFICATION) },
                    onMenuClick = { scope.launch {
                        if (drawerState.isClosed) drawerState.open() else drawerState.close()
                    } }
                )
            }
        }

        // 환경 설정
        composable(Routes.SETTINGS) {
            AppRightDrawer(
                drawerState = drawerState,
                userViewModel = userViewModel,
                navController = navController,
                scope = scope
            ) {
                SettingsScreen(
                    settingsViewModel, navController,
                    onBack = { navController.popBackStack() },
                    onAlarmClick = { navController.navigate(Routes.NOTIFICATION) },
                    onMenuClick = { scope.launch {
                        if (drawerState.isClosed) drawerState.open() else drawerState.close()
                    } }
                )
            }
        }


        ////// BottomNavBar에서 쓰이는 경로 //////
        ////// 메인 홈에서 특정 버튼 선택 시 쓰이는 경로 /////

        // 메인 홈
        composable(Routes.HOME) {
            AppRightDrawer(
                drawerState = drawerState,
                userViewModel = userViewModel,
                navController = navController,
                scope = scope
            ) {
                HomeScreen(
                    goalViewModel, stepViewModel, /* 등등..? */  navController,
                    onBack = { navController.popBackStack() },
                    onAlarmClick = { navController.navigate(Routes.NOTIFICATION) },
                    onMenuClick = { scope.launch {
                        if (drawerState.isClosed) drawerState.open() else drawerState.close()
                    } },
                    onRankingBtnClick = { navController.navigate(Routes.RANK) }
                )
            }
        }

        // 목표
        composable(Routes.GOALS) {
            AppRightDrawer(
                drawerState = drawerState,
                userViewModel = userViewModel,
                navController = navController,
                scope = scope
            ) {
                GoalScreen(
                    goalViewModel, navController,
                    onBack = { navController.popBackStack() },
                    onAlarmClick = { navController.navigate(Routes.NOTIFICATION) },
                    onMenuClick = { scope.launch {
                        if (drawerState.isClosed) drawerState.open() else drawerState.close()
                    } },
                    onAddGoalClick = { dateString ->
                        navController.navigate("add_goal_screen?date=$dateString")
                    }
                )
            }
        }

        // 랭킹
        composable(Routes.RANK) {
            AppRightDrawer(
                drawerState = drawerState,
                userViewModel = userViewModel,
                navController = navController,
                scope = scope
            ) {
                RankingScreen(
                    rankingViewModel, navController,
                    onBack = { navController.popBackStack() },
                    onAlarmClick = { navController.navigate(Routes.NOTIFICATION) },
                    onMenuClick = {
                        scope.launch {
                            if (drawerState.isClosed) drawerState.open() else drawerState.close()
                        }
                    }
                )
            }
        }

        // 지도
        composable(Routes.MAP) {
            AppRightDrawer(
                drawerState = drawerState,
                userViewModel = userViewModel,
                navController = navController,
                scope = scope
            ) {
                MapScreen(
                    mapViewModel, navController,
                    onBack = { navController.popBackStack() },
                    onAlarmClick = { navController.navigate(Routes.NOTIFICATION) },
                    onMenuClick = { scope.launch {
                        if (drawerState.isClosed) drawerState.open() else drawerState.close()
                    } }
                )
            }
        }

        /////// 그 외 기타 스크린 ///////

        // 회원 가입
        composable(Routes.ACCOUNT) {
            SignUpAccountScreen(authViewModel, navController)
        }

        composable(Routes.PROFILE) {
            SignUpProfileScreen(authViewModel, userViewModel, majorViewModel,navController)
        }

        // 마이페이지 수정
        composable(Routes.MYPAGE_EDIT) {
            MyPageEditScreen(userViewModel, majorViewModel, navController)
        }

        // 목표 추가, 수정
        composable(
            route = "add_goal_screen?date={date}&goalId={goalId}",
            arguments = listOf(
                navArgument("date") { type = NavType.StringType; defaultValue = "" },
                navArgument("goalId") { type = NavType.IntType; defaultValue = -1 }
            )
        ) { backStackEntry ->
            val date = backStackEntry.arguments?.getString("date") ?: ""
            val goalId = backStackEntry.arguments?.getInt("goalId") ?: -1
            AppRightDrawer(
                drawerState = drawerState,
                userViewModel = userViewModel,
                navController = navController,
                scope = scope
            ) {
                AddGoalScreen(
                    viewModel = goalViewModel,
                    navController = navController,
                    onBack = { navController.popBackStack() },
                    onAlarmClick = { navController.navigate(Routes.NOTIFICATION) },
                    onMenuClick = {
                        scope.launch {
                            if (drawerState.isClosed) drawerState.open() else drawerState.close()
                        }
                    },
                    initialDate = date, // 전달받은 날짜
                    goalId = goalId,
                )
            }
        }
    }
}
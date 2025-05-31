package com.example.pmaapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pmaapp.Screens.*
import com.example.pmaapp.ViewModels.AIPredictionViewModel
import com.example.pmaapp.screens.AddPlayerScreen
import com.example.pmaapp.SensorsLayer.SensorScreen
import com.example.pmaapp.screens.GeminiChatScreen

object AppRoutes {
    const val SIGNUP_ROUTE = "signup"
    const val SIGNIN_ROUTE = "signin"
    const val HOME_ROUTE = "home/{coachName}/{teamName}"
    const val SPLASH_ROUTE = "splash"
    const val ONBOARDING_ROUTE = "onboarding"
    const val ADDPLAYER_ROUTE = "addplayer"
    const val PLAYERS_LIST_ROUTE = "players_list"
    const val PLAYER_DETAIL_ROUTE = "player_detail/{playerId}"
    const val GEMINI_CHAT_ROUTE = "gemini_chat"
    const val AI_MODEL_SELECTION_ROUTE = "ai_model_selection"
    const val AI_PLAYER_SELECTION_ROUTE = "ai_player_selection"
    const val AI_RESULT_ROUTE = "ai_result"
    const val SENSOR_MONITORING_ROUTE = "sensor_monitoring"
}

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val aiVm: AIPredictionViewModel = viewModel()
    NavHost(
        navController = navController,
        startDestination = AppRoutes.SPLASH_ROUTE,
        modifier = modifier
    ) {
        composable(AppRoutes.SIGNUP_ROUTE) { SignupScreen(navController) }
        composable(AppRoutes.SIGNIN_ROUTE) { SigninScreen(navController) }
        composable(AppRoutes.SPLASH_ROUTE) { SplashScreen(navController) }
        composable(AppRoutes.ONBOARDING_ROUTE) { OnboardingScreen(navController) }
        composable(AppRoutes.ADDPLAYER_ROUTE) { AddPlayerScreen(navController) }
        composable(AppRoutes.PLAYERS_LIST_ROUTE) { PlayersListScreen(navController) }
        composable(
            route = AppRoutes.PLAYER_DETAIL_ROUTE,
            arguments = listOf(navArgument("playerId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("playerId") ?: -1
            PlayerDetailScreen(navController, id)
        }
        composable(
            route = AppRoutes.HOME_ROUTE,
            arguments = listOf(
                navArgument("coachName") { type = NavType.StringType },
                navArgument("teamName") { type = NavType.StringType }
            )
        ) { entry ->
            val coach = entry.arguments?.getString("coachName") ?: ""
            val team = entry.arguments?.getString("teamName") ?: ""
            HomeScreen(coachName = coach, teamName = team, navController = navController)
        }
        composable(AppRoutes.GEMINI_CHAT_ROUTE) {
            GeminiChatScreen(navController, it.arguments?.getString("prompt") ?: "")
        }

        // AI Prediction flow - Fixed with correct screens
        composable(AppRoutes.AI_MODEL_SELECTION_ROUTE) {
            AIModelSelectionScreen(navController, viewModel = aiVm)
        }
        composable(AppRoutes.AI_PLAYER_SELECTION_ROUTE) {
            AIPlayerSelectionScreen(navController, viewModel = aiVm)
        }
        composable(AppRoutes.AI_RESULT_ROUTE) {
            AIResultScreen(navController, viewModel = aiVm)
        }

        // Sensor Monitoring Screen
        composable(AppRoutes.SENSOR_MONITORING_ROUTE) {
            SensorScreen()
        }
    }
}
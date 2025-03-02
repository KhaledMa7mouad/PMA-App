package com.example.pmaapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pmaapp.Screens.HomeScreen
import com.example.pmaapp.Screens.OnboardingScreen
import com.example.pmaapp.Screens.SigninScreen
import com.example.pmaapp.Screens.SignupScreen
import com.example.pmaapp.Screens.SplashScreen
import com.example.pmaapp.navigation.AppRoutes.HOME_ROUTE
import com.example.pmaapp.navigation.AppRoutes.SIGNIN_ROUTE
import com.example.pmaapp.navigation.AppRoutes.SIGNUP_ROUTE


object AppRoutes {
    const val SIGNUP_ROUTE = "signup"
    const val SIGNIN_ROUTE = "signin"
    const val HOME_ROUTE = "home"
    const val SPLASH_ROUTE = "splash"
    const val ONBOARDING_ROUTE = "onboarding"
}

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoutes.SPLASH_ROUTE, modifier = modifier
    ) {
        composable(route = SIGNUP_ROUTE) { SignupScreen(navController = navController) }
        composable(route = SIGNIN_ROUTE) { SigninScreen(navController) }
        composable(route = HOME_ROUTE) { HomeScreen() }
        composable(route = AppRoutes.SPLASH_ROUTE) { SplashScreen(navController) }
        composable(route = AppRoutes.ONBOARDING_ROUTE) { OnboardingScreen(navController) }
    }
}
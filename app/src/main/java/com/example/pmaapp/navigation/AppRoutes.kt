// File: AppNavHost.kt
package com.example.pmaapp.navigation

//import AddPlayerScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pmaapp.Screens.HomeScreen
import com.example.pmaapp.Screens.OnboardingScreen
import com.example.pmaapp.Screens.SigninScreen
import com.example.pmaapp.Screens.SignupScreen
import com.example.pmaapp.Screens.SplashScreen

object AppRoutes {
    const val SIGNUP_ROUTE = "signup"
    const val SIGNIN_ROUTE = "signin"
    // Updated HOME_ROUTE to include two parameters: coachName and teamName.
    const val HOME_ROUTE = "home/{coachName}/{teamName}"
    const val SPLASH_ROUTE = "splash"
    const val ONBOARDING_ROUTE = "onboarding"
    const val ADDPLAYER_ROUTE = "addplayer"
}

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoutes.SPLASH_ROUTE,
        modifier = modifier
    ) {
        composable(route = AppRoutes.SIGNUP_ROUTE) { SignupScreen(navController = navController) }
        composable(route = AppRoutes.SIGNIN_ROUTE) { SigninScreen(navController) }
        composable(route = AppRoutes.SPLASH_ROUTE) { SplashScreen(navController) }
        composable(route = AppRoutes.ONBOARDING_ROUTE) { OnboardingScreen(navController) }
      //  composable(route = AppRoutes.ADDPLAYER_ROUTE) { AddPlayerScreen(navController) }
        // Here we add the arguments for coachName and teamName.
        composable(
            route = AppRoutes.HOME_ROUTE,
            arguments = listOf(
                navArgument("coachName") { type = NavType.StringType },
                navArgument("teamName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val coachName = backStackEntry.arguments?.getString("coachName") ?: ""
            val teamName = backStackEntry.arguments?.getString("teamName") ?: ""
            HomeScreen(coachName = coachName, teamName = teamName , navController = navController)
        }
    }
}

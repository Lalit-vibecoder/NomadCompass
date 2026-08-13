package com.example.nomadcompass.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.nomadcompass.ui.screens.country.CountryProfileScreen
import com.example.nomadcompass.ui.screens.country.CountryProfileViewModel
import com.example.nomadcompass.ui.screens.explore.ExploreScreen
import com.example.nomadcompass.ui.screens.explore.ExploreViewModel
import com.example.nomadcompass.ui.screens.profile.ProfileSetupScreen
import com.example.nomadcompass.ui.screens.profile.ProfileSetupViewModel
import com.example.nomadcompass.ui.screens.splash.SplashScreen
import com.example.nomadcompass.ui.screens.splash.SplashViewModel

import com.example.nomadcompass.ui.screens.planner.PlannerScreen
import com.example.nomadcompass.ui.screens.planner.PlannerViewModel

object Destinations {
    const val SPLASH = "splash"
    const val PROFILE_SETUP = "profile_setup"
    const val EXPLORE = "explore"
    const val PLANNER = "planner"
    const val PLANNER_WITH_COUNTRY = "planner/{cca3}"
    const val COUNTRY_PROFILE = "country_profile/{cca3}"

    fun countryProfileRoute(cca3: String) = "country_profile/$cca3"
    fun plannerRoute(cca3: String) = "planner/$cca3"
}

@Composable
fun NomadCompassNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Destinations.SPLASH
    ) {
        // Splash Screen
        composable(Destinations.SPLASH) {
            val viewModel: SplashViewModel = hiltViewModel()
            SplashScreen(
                viewModel = viewModel,
                onNavigateNext = { hasProfile ->
                    val destination = if (hasProfile) Destinations.EXPLORE else Destinations.PROFILE_SETUP
                    navController.navigate(destination) {
                        popUpTo(Destinations.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        // Profile Setup Screen
        composable(Destinations.PROFILE_SETUP) {
            val viewModel: ProfileSetupViewModel = hiltViewModel()
            ProfileSetupScreen(
                viewModel = viewModel,
                onContinue = {
                    navController.navigate(Destinations.EXPLORE) {
                        popUpTo(Destinations.PROFILE_SETUP) { inclusive = true }
                    }
                }
            )
        }

        // Explore Dashboard Screen
        composable(Destinations.EXPLORE) {
            val viewModel: ExploreViewModel = hiltViewModel()
            ExploreScreen(
                viewModel = viewModel,
                onCountryClick = { cca3 ->
                    navController.navigate(Destinations.countryProfileRoute(cca3))
                },
                onPlannerClick = {
                    navController.navigate(Destinations.PLANNER)
                },
                onProfileClick = {
                    navController.navigate(Destinations.PROFILE_SETUP)
                }
            )
        }

        // Planner Screen
        composable(Destinations.PLANNER) {
            val viewModel: PlannerViewModel = hiltViewModel()
            PlannerScreen(
                viewModel = viewModel,
                onExploreClick = {
                    navController.navigate(Destinations.EXPLORE) {
                        popUpTo(Destinations.EXPLORE) { inclusive = true }
                    }
                },
                onProfileClick = {
                    navController.navigate(Destinations.PROFILE_SETUP)
                }
            )
        }

        composable(
            route = Destinations.PLANNER_WITH_COUNTRY,
            arguments = listOf(navArgument("cca3") { type = NavType.StringType })
        ) {
            val viewModel: PlannerViewModel = hiltViewModel()
            PlannerScreen(
                viewModel = viewModel,
                onExploreClick = {
                    navController.navigate(Destinations.EXPLORE) {
                        popUpTo(Destinations.EXPLORE) { inclusive = true }
                    }
                },
                onProfileClick = {
                    navController.navigate(Destinations.PROFILE_SETUP)
                }
            )
        }

        // Country Profile Screen
        composable(
            route = Destinations.COUNTRY_PROFILE,
            arguments = listOf(navArgument("cca3") { type = NavType.StringType })
        ) {
            val viewModel: CountryProfileViewModel = hiltViewModel()
            CountryProfileScreen(
                viewModel = viewModel,
                onNeighborClick = { neighborCca3 ->
                    navController.navigate(Destinations.countryProfileRoute(neighborCca3))
                },
                onExploreClick = {
                    navController.navigate(Destinations.EXPLORE) {
                        popUpTo(Destinations.EXPLORE) { inclusive = true }
                    }
                },
                onPlannerClick = {
                    navController.navigate(Destinations.PLANNER)
                },
                onProfileClick = {
                    navController.navigate(Destinations.PROFILE_SETUP)
                },
                onAddTripClick = { cca3 ->
                    navController.navigate(Destinations.plannerRoute(cca3))
                }
            )
        }
    }
}

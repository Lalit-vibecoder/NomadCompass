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
import com.example.nomadcompass.ui.screens.lock.AppLockScreen
import com.example.nomadcompass.ui.screens.lock.AppLockViewModel
import com.example.nomadcompass.ui.screens.profile.ProfileSetupScreen
import com.example.nomadcompass.ui.screens.profile.ProfileSetupViewModel
import com.example.nomadcompass.ui.screens.splash.SplashScreen
import com.example.nomadcompass.ui.screens.splash.SplashViewModel

import com.example.nomadcompass.ui.screens.planner.PlannerScreen
import com.example.nomadcompass.ui.screens.planner.PlannerViewModel

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.nomadcompass.ui.components.AppBackground
import com.example.nomadcompass.ui.components.NomadBottomNavigationBar
import com.example.nomadcompass.ui.components.NomadNavTab
import com.example.nomadcompass.ui.theme.AppBackgroundState
import com.example.nomadcompass.ui.theme.LocalAppBackground

object Destinations {
    const val SPLASH = "splash"
    const val APP_LOCK = "app_lock"
    const val PROFILE_SETUP = "profile_setup"
    const val EXPLORE = "explore"
    const val PLANNER = "planner"
    const val PLANNER_WITH_COUNTRY = "planner/{cca3}"
    const val COUNTRY_PROFILE = "country_profile/{cca3}"

    fun countryProfileRoute(cca3: String) = "country_profile/$cca3"
    fun plannerRoute(cca3: String) = "planner/$cca3"
}

@Composable
fun NomadCompassNavGraph(
    profileViewModel: ProfileSetupViewModel = hiltViewModel()
) {
    val profileUiState by profileViewModel.uiState.collectAsState()
    val navController = rememberNavController()

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val isPlanner = currentRoute == Destinations.PLANNER || currentRoute == Destinations.PLANNER_WITH_COUNTRY
    val showBottomBar = currentRoute in listOf(
        Destinations.EXPLORE,
        Destinations.PLANNER,
        Destinations.PLANNER_WITH_COUNTRY,
        Destinations.COUNTRY_PROFILE
    )

    CompositionLocalProvider(
        LocalAppBackground provides AppBackgroundState(
            bgPhotoUri = profileUiState.bgPhotoUri,
            bgBlurRadius = profileUiState.bgBlurRadius
        )
    ) {
        AppBackground(
            bgPhotoUri = profileUiState.bgPhotoUri,
            blurRadius = profileUiState.bgBlurRadius
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                NavHost(
                    navController = navController,
                    startDestination = Destinations.SPLASH,
            enterTransition = {
                fadeIn(animationSpec = tween(200, easing = FastOutSlowInEasing))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(200, easing = FastOutSlowInEasing))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(200, easing = FastOutSlowInEasing))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(200, easing = FastOutSlowInEasing))
            }
        ) {
        // Splash Screen
        composable(Destinations.SPLASH) {
            val viewModel: SplashViewModel = hiltViewModel()
            SplashScreen(
                viewModel = viewModel,
                onNavigateNext = { hasProfile, isSecurityLocked ->
                    val destination = when {
                        !hasProfile -> Destinations.PROFILE_SETUP
                        isSecurityLocked -> Destinations.APP_LOCK
                        else -> Destinations.EXPLORE
                    }
                    navController.navigate(destination) {
                        popUpTo(Destinations.SPLASH) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // App Lock Security Screen
        composable(Destinations.APP_LOCK) {
            val viewModel: AppLockViewModel = hiltViewModel()
            AppLockScreen(
                viewModel = viewModel,
                onUnlocked = {
                    navController.navigate(Destinations.EXPLORE) {
                        popUpTo(Destinations.APP_LOCK) { inclusive = true }
                        launchSingleTop = true
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
                        launchSingleTop = true
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
                    navController.navigate(Destinations.countryProfileRoute(cca3)) {
                        launchSingleTop = true
                    }
                },
                onPlannerClick = {
                    navController.navigate(Destinations.PLANNER) {
                        popUpTo(Destinations.EXPLORE) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onProfileClick = {
                    navController.navigate(Destinations.PROFILE_SETUP) {
                        launchSingleTop = true
                    }
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
                        popUpTo(Destinations.EXPLORE) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onProfileClick = {
                    navController.navigate(Destinations.PROFILE_SETUP) {
                        launchSingleTop = true
                    }
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
                        popUpTo(Destinations.EXPLORE) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onProfileClick = {
                    navController.navigate(Destinations.PROFILE_SETUP) {
                        launchSingleTop = true
                    }
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
                        popUpTo(Destinations.EXPLORE) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onPlannerClick = {
                    navController.navigate(Destinations.PLANNER) {
                        popUpTo(Destinations.EXPLORE) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onProfileClick = {
                    navController.navigate(Destinations.PROFILE_SETUP) {
                        launchSingleTop = true
                    }
                },
                onAddTripClick = { cca3 ->
                    navController.navigate(Destinations.plannerRoute(cca3)) {
                        launchSingleTop = true
                    }
                }
            )
        }
    }

    AnimatedVisibility(
        visible = showBottomBar,
        enter = fadeIn(animationSpec = tween(200, easing = FastOutSlowInEasing)),
        exit = fadeOut(animationSpec = tween(200, easing = FastOutSlowInEasing)),
        modifier = Modifier.align(Alignment.BottomCenter)
    ) {
        NomadBottomNavigationBar(
            currentTab = if (isPlanner) NomadNavTab.PLANNER else NomadNavTab.EXPLORE,
            onExploreClick = {
                if (currentRoute != Destinations.EXPLORE) {
                    navController.navigate(Destinations.EXPLORE) {
                        popUpTo(Destinations.EXPLORE) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            onPlannerClick = {
                if (!isPlanner) {
                    navController.navigate(Destinations.PLANNER) {
                        popUpTo(Destinations.EXPLORE) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        )
    }
}
}
}
}

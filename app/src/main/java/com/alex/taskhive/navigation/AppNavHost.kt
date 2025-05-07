package com.alex.taskhive.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.alex.taskhive.data.TaskScheduleScreen
import com.alex.taskhive.data.UserViewModel
import com.alex.taskhive.ui.screens.assign.AssignScreen
import com.alex.taskhive.ui.screens.chat.ChatScreen
import com.alex.taskhive.ui.screens.dashboard.DashboardScreen
import com.alex.taskhive.ui.screens.register.RegisterScreen

@Composable
fun AppNavHost(navController: NavHostController, userViewModel: UserViewModel) {
    NavHost(navController = navController, startDestination = Routes.RegisterScreen) {

        composable(Routes.RegisterScreen) {
            RegisterScreen(viewModel = userViewModel, navController = navController)
        }

        composable(Routes.DashboardScreen) {
            DashboardScreen(navController = navController)
        }

        composable(Routes.TaskScheduleScreen) {
            TaskScheduleScreen(navController = navController)
        }

        // UPDATED Chat Screen route with dynamic workerId
        composable(
            route = "chat/{workerId}",
            arguments = listOf(navArgument("workerId") { type = NavType.StringType })
        ) { backStackEntry ->
            val workerId = backStackEntry.arguments?.getString("workerId") ?: ""
            ChatScreen(workerId = workerId, navController = navController)
        }

        composable(Routes.AssignScreen) {
            AssignScreen(navController = navController)
        }
    }
}

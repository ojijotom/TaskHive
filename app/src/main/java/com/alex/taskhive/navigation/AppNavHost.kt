package com.alex.taskhive.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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

        composable(Routes.ChatScreen) {
            ChatScreen(navController = navController)
        }
        composable(Routes.AssignScreen) {
            AssignScreen(navController = navController)
        }
    }
}

package com.alex.taskhive.navigation

object Routes {
    const val RegisterScreen = "register"
    const val DashboardScreen = "dashboard"
    const val TaskScheduleScreen = "task_schedule"
    const val AssignScreen = "assign_screen"

    // Define route pattern for dynamic navigation
    const val ChatScreen = "chat/{workerId}"

    // Helper function to create the actual route with workerId
    fun chatScreen(workerId: String) = "chat/$workerId"
}

package com.alex.taskhive.ui.screens.taskschedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.alex.taskhive.ui.theme.BlackBackground
import com.alex.taskhive.ui.theme.OrangePrimary
import com.alex.taskhive.ui.theme.WhiteText
import com.alex.taskhive.navigation.Routes

@Composable
fun TaskScheduleScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackBackground)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Task Scheduling",
            color = OrangePrimary,
            style = MaterialTheme.typography.headlineMedium
        )

        // Button to select a worker
        Button(
            onClick = { navController.navigate(Routes.AssignScreen) },
            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Assign Task", color = WhiteText)
        }
    }
}

@Composable
@Preview(showBackground = true)
fun TaskScheduleScreenPreview() {
    TaskScheduleScreen(navController = rememberNavController())
}

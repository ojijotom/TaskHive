package com.alex.taskhive.ui.screens.assign

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
fun AssignScreen(navController: NavController) {
    var workdays by remember { mutableStateOf(setOf<String>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackBackground)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Assign Workdays",
            color = OrangePrimary,
            style = MaterialTheme.typography.headlineMedium
        )

        val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday")

        daysOfWeek.forEach { day ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = day, color = WhiteText)
                Checkbox(
                    checked = workdays.contains(day),
                    onCheckedChange = {
                        workdays = if (it) workdays + day else workdays - day
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                // Submit the schedule and navigate back to TaskScheduleScreen or DashboardScreen
                navController.navigate(Routes.TaskScheduleScreen)
            },
            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit", color = WhiteText)
        }
    }
}

@Composable
@Preview(showBackground = true)
fun AssignScreenPreview() {
    AssignScreen(navController = rememberNavController())
}

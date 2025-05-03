package com.alex.taskhive.ui.screens.dashboard

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
fun DashboardScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackBackground)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Dashboard",
            color = OrangePrimary,
            style = MaterialTheme.typography.headlineMedium
        )

        // Worker schedule box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(OrangePrimary)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Your schedule will appear here.",
                color = WhiteText,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Button to navigate to Chat Screen
        Button(
            onClick = { navController.navigate(Routes.ChatScreen) },
            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Go to Chat", color = WhiteText)
        }
    }
}

@Composable
@Preview(showBackground = true)
fun DashboardScreenPreview() {
    DashboardScreen(navController = rememberNavController())
}

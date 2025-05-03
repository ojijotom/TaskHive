package com.alex.taskhive.ui.screens.chat

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
import com.alex.taskhive.navigation.Routes
import com.alex.taskhive.ui.theme.BlackBackground
import com.alex.taskhive.ui.theme.OrangePrimary
import com.alex.taskhive.ui.theme.WhiteText

@Composable
fun ChatScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackBackground)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Chat",
            color = WhiteText,
            style = MaterialTheme.typography.titleLarge
        )

        // Placeholder chat box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(OrangePrimary)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Chat messages will appear here.",
                color = WhiteText,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { navController.navigate(Routes.TaskScheduleScreen) },
            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Send Message", color = WhiteText)
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ChatScreenPreview() {
    ChatScreen(navController = rememberNavController())
}

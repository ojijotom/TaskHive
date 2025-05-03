package com.alex.taskhive.ui.screens.register

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.alex.taskhive.data.User
import com.alex.taskhive.data.UserViewModel
import com.alex.taskhive.navigation.Routes
import com.alex.taskhive.ui.theme.BlackBackground
import com.alex.taskhive.ui.theme.BlackText
import com.alex.taskhive.ui.theme.OrangePrimary
import com.alex.taskhive.ui.theme.WhiteText

@Composable
fun RegisterScreen(
    viewModel: UserViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("Worker") }
    var adminCount by remember { mutableStateOf(0) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackBackground)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Register",
            color = OrangePrimary,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name", color = OrangePrimary) },
            textStyle = TextStyle(color = WhiteText),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text("Select Role", color = WhiteText)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(vertical = 12.dp)
        ) {
            RadioButton(
                selected = selectedRole == "Admin",
                onClick = { selectedRole = "Admin" },
                colors = RadioButtonDefaults.colors(
                    selectedColor = OrangePrimary,
                    unselectedColor = Color.Gray
                )
            )
            Text("Admin", color = WhiteText)

            RadioButton(
                selected = selectedRole == "Worker",
                onClick = { selectedRole = "Worker" },
                colors = RadioButtonDefaults.colors(
                    selectedColor = OrangePrimary,
                    unselectedColor = Color.Gray
                )
            )
            Text("Worker", color = WhiteText)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (name.isNotBlank()) {
                    if (selectedRole == "Admin" && adminCount >= 2) {
                        Toast.makeText(context, "Only 2 admins are allowed", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.registerUser(User(name = name.trim(), role = selectedRole), context)
                        if (selectedRole == "Worker") {
                            navController.navigate(Routes.DashboardScreen)
                        } else {
                            navController.navigate(Routes.TaskScheduleScreen)
                        }
                    }
                } else {
                    Toast.makeText(context, "Please enter a name", Toast.LENGTH_SHORT).show()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit", color = BlackText)
        }
    }
}

private fun UserViewModel.registerUser(
    user: User,
    context: Context
) {
}

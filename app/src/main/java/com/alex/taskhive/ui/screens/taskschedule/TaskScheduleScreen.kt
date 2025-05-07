package com.alex.taskhive.ui.screens.taskschedule

import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.room.*
import kotlinx.coroutines.launch
import com.alex.taskhive.ui.theme.BlackBackground
import com.alex.taskhive.ui.theme.OrangePrimary
import com.alex.taskhive.ui.theme.WhiteText
import com.alex.taskhive.navigation.Routes
import androidx.lifecycle.viewmodel.compose.viewModel

// Room Database Entity
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workerId: String,
    val taskName: String,
    val date: String
)

// Room DAO
@Dao
interface TaskDao {
    @Insert
    suspend fun insertTask(task: Task)

    @Query("SELECT * FROM tasks WHERE workerId = :workerId")
    suspend fun getTasksByWorker(workerId: String): List<Task>
}

// Room Database
@Database(entities = [Task::class], version = 1)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: TaskDatabase? = null

        fun getDatabase(context: Application): TaskDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    "task_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// ViewModel
class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val taskDao = TaskDatabase.getDatabase(application).taskDao()

    // Function to assign a task
    fun assignTask(workerId: String, taskName: String, date: String) {
        val task = Task(workerId = workerId, taskName = taskName, date = date)
        viewModelScope.launch {
            taskDao.insertTask(task)
        }
    }
}

// Task Schedule Screen UI
@Composable
fun TaskScheduleScreen(navController: NavController) {
    // Use the viewModel() function to obtain the TaskViewModel instance
    val taskViewModel: TaskViewModel = viewModel()

    // Task fields
    var workerId by remember { mutableStateOf("") }
    var taskName by remember { mutableStateOf(TextFieldValue("")) }
    var date by remember { mutableStateOf(TextFieldValue("")) }
    var isTaskAssigned by remember { mutableStateOf(false) }

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

        // Worker ID input
        TextField(
            value = workerId,
            onValueChange = { workerId = it },
            label = { Text("Worker ID") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedTextColor = WhiteText,
                unfocusedTextColor = WhiteText,
                focusedIndicatorColor = OrangePrimary,
                unfocusedIndicatorColor = OrangePrimary,
                focusedLabelColor = OrangePrimary,
                unfocusedLabelColor = WhiteText,
                focusedPlaceholderColor = WhiteText,
                unfocusedPlaceholderColor = WhiteText,
                focusedContainerColor = OrangePrimary,
                unfocusedContainerColor = OrangePrimary
            )
        )


                    // Task name input
        TextField(
            value = taskName,
            onValueChange = { taskName = it },
            label = { Text("Task Name") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedTextColor = WhiteText,
                unfocusedTextColor = WhiteText,
                focusedIndicatorColor = OrangePrimary,
                unfocusedIndicatorColor = OrangePrimary,
                focusedLabelColor = OrangePrimary,
                unfocusedLabelColor = WhiteText,
                focusedPlaceholderColor = WhiteText,
                unfocusedPlaceholderColor = WhiteText,
                focusedContainerColor = OrangePrimary,
                unfocusedContainerColor = OrangePrimary
            )
        )

        // Date input
        TextField(
            value = date,
            onValueChange = { date = it },
            label = { Text("Date (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedTextColor = WhiteText,
                unfocusedTextColor = WhiteText,
                focusedIndicatorColor = OrangePrimary,
                unfocusedIndicatorColor = OrangePrimary,
                focusedLabelColor = OrangePrimary,
                unfocusedLabelColor = WhiteText,
                focusedPlaceholderColor = WhiteText,
                unfocusedPlaceholderColor = WhiteText,
                focusedContainerColor = OrangePrimary,
                unfocusedContainerColor = OrangePrimary
            )
        )

        // Button to assign the task
        Button(
            onClick = {
                if (workerId.isNotEmpty() && taskName.text.isNotEmpty() && date.text.isNotEmpty()) {
                    taskViewModel.assignTask(workerId, taskName.text, date.text)
                    isTaskAssigned = true
                    Toast.makeText(navController.context, "Task Assigned", Toast.LENGTH_SHORT).show()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Assign Task", color = WhiteText)
        }

        // Navigation Button
        Button(
            onClick = { navController.navigate(Routes.AssignScreen) },
            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Go to Assign Screen", color = WhiteText)
        }
    }
}

// Preview UI
@Composable
@Preview(showBackground = true)
fun TaskScheduleScreenPreview() {
    TaskScheduleScreen(navController = rememberNavController())
}

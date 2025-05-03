package com.alex.taskhive.data

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.room.*
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.liveData
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alex.taskhive.navigation.Routes

// --- Database Entities ---
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val role: String // "Admin" or "Worker"
)

@Entity(tableName = "schedules")
data class Schedule(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val dayOfWeek: String, // Monday to Friday
    val workDay: Boolean
)

// --- Room Database Setup ---
@Database(entities = [User::class, Schedule::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun scheduleDao(): ScheduleDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// --- Data Access Objects (DAOs) ---
@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    @Query("SELECT COUNT(*) FROM users WHERE role = 'Admin'")
    suspend fun getAdminCount(): Int // Returns the count of admins

    @Query("SELECT * FROM users WHERE role = 'Worker'")
    suspend fun getWorkers(): List<User>
}

@Dao
interface ScheduleDao {
    @Insert
    suspend fun insertSchedule(schedule: Schedule)

    @Query("DELETE FROM schedules WHERE userId = :userId AND dayOfWeek = :day")
    suspend fun clearScheduleForDay(userId: Long, day: String)

    @Query("SELECT * FROM schedules WHERE userId = :userId")
    suspend fun getScheduleForUser(userId: Long): List<Schedule>
}

// --- ViewModels ---
class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val userDao = AppDatabase.getDatabase(application).userDao()

    // Cache admin count in state
    private val _adminCount = mutableStateOf(0)
    val adminCount: State<Int> = _adminCount

    init {
        loadAdminCount()
    }

    private fun loadAdminCount() {
        viewModelScope.launch {
            _adminCount.value = userDao.getAdminCount()
        }
    }

    /**
     * Registers a user. Enforces max of 2 admins.
     * onSuccess is invoked after successful registration to navigate.
     */
    fun registerUser(
        user: User,
        context: Context,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val currentAdmins = _adminCount.value
            if (user.role == "Admin" && currentAdmins >= 2) {
                Toast.makeText(context, "Only 2 admins are allowed", Toast.LENGTH_SHORT).show()
            } else {
                userDao.insertUser(user)
                Toast.makeText(context, "User registered successfully.", Toast.LENGTH_SHORT).show()
                loadAdminCount() // refresh count
                onSuccess()
            }
        }
    }
}

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {
    private val scheduleDao = AppDatabase.getDatabase(application).scheduleDao()
    private val userDao = AppDatabase.getDatabase(application).userDao()

    fun assignWeeklySchedule(userId: Long, days: List<Pair<String, Boolean>>, context: Context) {
        viewModelScope.launch {
            for ((day, workDay) in days) {
                scheduleDao.clearScheduleForDay(userId, day)
                scheduleDao.insertSchedule(Schedule(userId = userId, dayOfWeek = day, workDay = workDay))
            }
            Toast.makeText(context, "Schedule assigned!", Toast.LENGTH_SHORT).show()
        }
    }

    fun getWorkers(): LiveData<List<User>> = liveData {
        emit(userDao.getWorkers())
    }

    fun getScheduleForUser(userId: Long): LiveData<List<Schedule>> = liveData {
        emit(scheduleDao.getScheduleForUser(userId))
    }
}

// --- UI Composables ---
@Composable
fun RegisterScreen(
    navController: NavController,
    userViewModel: UserViewModel = viewModel()
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("Worker") }
    val adminCount by userViewModel.adminCount

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Register", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        Text("Select Role")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selectedRole == "Admin",
                onClick = { selectedRole = "Admin" }
            )
            Text("Admin")

            RadioButton(
                selected = selectedRole == "Worker",
                onClick = { selectedRole = "Worker" }
            )
            Text("Worker")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (name.isBlank()) {
                    Toast.makeText(context, "Please enter a name", Toast.LENGTH_SHORT).show()
                } else if (selectedRole == "Admin" && adminCount >= 2) {
                    Toast.makeText(context, "Only 2 admins are allowed", Toast.LENGTH_SHORT).show()
                } else {
                    userViewModel.registerUser(
                        User(name = name.trim(), role = selectedRole),
                        context
                    ) {
                        if (selectedRole == "Worker") {
                            navController.navigate(Routes.DashboardScreen)
                        } else {
                            navController.navigate(Routes.TaskScheduleScreen)
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit")
        }
    }
}

@Composable
fun DashboardScreen(navController: NavController, scheduleViewModel: ScheduleViewModel = viewModel()) {
    val schedules = scheduleViewModel.getScheduleForUser(1).observeAsState(emptyList())

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Worker Dashboard", style = MaterialTheme.typography.titleLarge)

        Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
            Text("Schedule: ${schedules.value}")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = { navController.navigate("chat_screen") }, modifier = Modifier.fillMaxWidth()) {
            Text("Go to Chat")
        }
    }
}

@Composable
fun TaskScheduleScreen(
    navController: NavController,
    scheduleViewModel: ScheduleViewModel = viewModel()
) {
    val context = LocalContext.current
    val workers by scheduleViewModel.getWorkers().observeAsState(emptyList())
    var selectedWorkerId by remember { mutableStateOf<Long?>(null) }
    val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday")
    val dayStates = remember { daysOfWeek.associateWith { mutableStateOf(false) } }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Assign Tasks", style = MaterialTheme.typography.titleLarge)

        var expanded by remember { mutableStateOf(false) }
        Box {
            Button(onClick = { expanded = true }) {
                Text(
                    selectedWorkerId?.let { id -> workers.find { it.id == id }?.name } ?: "Select Worker"
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                workers.forEach { worker ->
                    DropdownMenuItem(
                        onClick = {
                            selectedWorkerId = worker.id
                            expanded = false
                        },
                        text = { Text(worker.name) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Worker Icon") }
                    )
                }
            }

            daysOfWeek.forEach { day ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = dayStates[day]?.value ?: false,
                        onCheckedChange = { dayStates[day]?.value = it }
                    )
                    Text(text = day)
                }
            }

            Button(onClick = {
                selectedWorkerId?.let { id ->
                    val assignments = daysOfWeek.map { day -> day to (dayStates[day]?.value ?: false) }
                    scheduleViewModel.assignWeeklySchedule(id, assignments, context)
                } ?: Toast.makeText(context, "Select a worker first", Toast.LENGTH_SHORT).show()
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Assign")
            }
        }
    }
}

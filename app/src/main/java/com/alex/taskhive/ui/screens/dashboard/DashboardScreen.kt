package com.alex.taskhive.ui.screens.dashboard

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.room.*
import com.alex.taskhive.navigation.Routes
import com.alex.taskhive.ui.theme.BlackBackground
import com.alex.taskhive.ui.theme.OrangePrimary
import com.alex.taskhive.ui.theme.WhiteText
import kotlinx.coroutines.launch

// Dummy Routes object
object Routes {
    const val ChatScreen = "chat_screen"
}

// ========================= ROOM DATABASE SETUP =========================
@Entity
data class Schedule(
    @PrimaryKey val workerId: Int,
    val details: String
)

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM Schedule WHERE workerId = :id")
    suspend fun getSchedule(id: Int): Schedule?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: Schedule)
}

@Database(entities = [Schedule::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scheduleDao(): ScheduleDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "task_db").build().also {
                    INSTANCE = it
                }
            }
        }
    }
}

// ========================= VIEWMODEL =========================
class DashboardViewModel(context: Context) : ViewModel() {
    private val dao = AppDatabase.getInstance(context).scheduleDao()
    var schedule by mutableStateOf<Schedule?>(null)
        private set

    fun loadSchedule(workerId: Int) {
        viewModelScope.launch {
            schedule = dao.getSchedule(workerId)
        }
    }
}
@Composable
fun DashboardScreen(
    navController: NavController
) {
    val context = LocalContext.current

    val viewModel: DashboardViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(context.applicationContext) as T
        }
    })

    val workerId = 1

    LaunchedEffect(Unit) {
        viewModel.loadSchedule(workerId)
    }

    val schedule = viewModel.schedule

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

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            colors = CardDefaults.cardColors(containerColor = OrangePrimary)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = schedule?.details ?: "No schedule available.",
                    color = WhiteText,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
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
@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    DashboardScreen(navController = rememberNavController())
}

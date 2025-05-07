package com.alex.taskhive.ui.screens.workers

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 1. User Entity (Room Database Table)
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val userId: Long = 0,
    val name: String,
    val role: String // e.g., "Worker", "Admin"
)

// 2. UserDao (Database Access Object)
@Dao
interface UserDao {

    @Insert
    suspend fun insert(user: User)

    @Query("SELECT * FROM users WHERE role = 'Worker'")
    fun getWorkers(): Flow<List<User>>

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>>
}

// 3. UserDatabase (Room Database)
@Database(entities = [User::class], version = 1)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: UserDatabase? = null

        fun getDatabase(context: Context): UserDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "user_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// 4. WorkerListViewModel
class WorkerListViewModel(private val db: UserDatabase) : ViewModel() {
    val workers: Flow<List<User>> = db.userDao().getAllUsers()
}

// 5. WorkerListViewModelFactory
class WorkerListViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return WorkerListViewModel(UserDatabase.getDatabase(context)) as T
    }
}

// 6. WorkerListScreen (Main Screen)
@Composable
fun WorkerListScreen(navController: NavController) {
    val context = LocalContext.current
    val db = UserDatabase.getDatabase(context)

    val viewModel: WorkerListViewModel = viewModel(
        factory = WorkerListViewModelFactory(context)
    )

    val workers by viewModel.workers.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Select a Worker to Chat",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (workers.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No workers found", color = MaterialTheme.colorScheme.onBackground)
            }
        } else {
            LazyColumn {
                items(workers) { worker ->
                    Button(
                        onClick = { navController.navigate("chat/${worker.userId}") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(worker.name, color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        }
    }
}


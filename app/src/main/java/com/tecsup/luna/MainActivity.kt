package com.tecsup.luna

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import com.tecsup.luna.data.local.AppDatabase
import com.tecsup.luna.data.repository.CursoRepository
import com.tecsup.luna.ui.CursoViewModel
import com.tecsup.luna.ui.CursoViewModelFactory
import com.tecsup.luna.ui.screens.FormScreen
import com.tecsup.luna.ui.screens.ListScreen
import com.tecsup.luna.ui.theme.ExamenrecuperacionTheme
import com.google.gson.Gson

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "curso-database"
        ).build()

        val repository = CursoRepository(db.cursoDao())
        val viewModelFactory = CursoViewModelFactory(repository)

        setContent {
            ExamenrecuperacionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(viewModelFactory)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(viewModelFactory: CursoViewModelFactory) {
    val navController = rememberNavController()
    val viewModel: CursoViewModel = viewModel(factory = viewModelFactory)

    NavHost(navController = navController, startDestination = "list") {
        composable("list") {
            ListScreen(
                viewModel = viewModel,
                onAddClick = { navController.navigate("form") },
                onEditClick = { curso ->
                    val cursoJson = Gson().toJson(curso)
                    navController.navigate("form?cursoId=${curso.idCurso}&cursoJson=${android.net.Uri.encode(cursoJson)}")
                }
            )
        }
        composable(
            route = "form?cursoId={cursoId}&cursoJson={cursoJson}",
            arguments = listOf(
                navArgument("cursoId") {
                    type = NavType.IntType
                    defaultValue = -1
                },
                navArgument("cursoJson") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val cursoId = backStackEntry.arguments?.getInt("cursoId")
            val cursoJson = backStackEntry.arguments?.getString("cursoJson")
            val cursoToEdit = if (cursoJson != null) {
                Gson().fromJson(cursoJson, com.tecsup.luna.data.local.Curso::class.java)
            } else {
                null
            }

            FormScreen(
                viewModel = viewModel,
                cursoId = if (cursoId == -1) null else cursoId,
                cursoToEdit = cursoToEdit,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
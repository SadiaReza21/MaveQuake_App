package com.mavequake.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.mavequake.ui.screens.CaptureScreen
import com.mavequake.ui.screens.HistoryScreen
import com.mavequake.ui.screens.HomeScreen
import com.mavequake.ui.screens.ResultsScreen
import com.mavequake.viewmodel.CaptureViewModel
import com.mavequake.viewmodel.HistoryViewModel
import com.mavequake.viewmodel.ResultsViewModel

object Routes {
    const val HOME = "home"
    const val CAPTURE = "capture"
    const val HISTORY = "history"
    const val RESULTS = "results/{assessmentId}"
    fun results(id: String) = "results/$id"
}

class ResultsViewModelFactory(private val assessmentId: String) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T =
        ResultsViewModel(assessmentId) as T
}

@Composable
fun MaveQuakeNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.HOME) {

        composable(Routes.HOME) {
            HomeScreen(
                onStartAssessment = { navController.navigate(Routes.CAPTURE) },
                onViewHistory = { navController.navigate(Routes.HISTORY) }
            )
        }

        composable(Routes.CAPTURE) {
            val viewModel: CaptureViewModel = viewModel()
            CaptureScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onSubmitted = { assessmentId ->
                    navController.navigate(Routes.results(assessmentId)) {
                        popUpTo(Routes.HOME)
                    }
                }
            )
        }

        composable(
            route = Routes.RESULTS,
            arguments = listOf(navArgument("assessmentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val assessmentId = backStackEntry.arguments?.getString("assessmentId").orEmpty()
            val viewModel: ResultsViewModel = viewModel(factory = ResultsViewModelFactory(assessmentId))
            ResultsScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }

        composable(Routes.HISTORY) {
            val viewModel: HistoryViewModel = viewModel()
            HistoryScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onOpenAssessment = { id -> navController.navigate(Routes.results(id)) }
            )
        }
    }
}

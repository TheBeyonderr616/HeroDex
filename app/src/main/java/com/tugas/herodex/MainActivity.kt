package com.tugas.herodex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tugas.herodex.ui.components.AnimatedSplashScreen
import com.tugas.herodex.ui.screens.HeroDetailScreen
import com.tugas.herodex.ui.screens.HomeScreen
import com.tugas.herodex.ui.screens.SquadScreen
import com.tugas.herodex.ui.theme.HeroDexTheme
import com.tugas.herodex.ui.viewmodel.HeroViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HeroDexTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var showSplashScreen by remember { mutableStateOf(true) }

                    if (showSplashScreen) {
                        AnimatedSplashScreen(onSplashFinished = { showSplashScreen = false })
                    } else {
                        val navController = rememberNavController()
                        val viewModel: HeroViewModel = viewModel()
                        HeroDexMainApp(navController, viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun HeroDexMainApp(navController: NavHostController, viewModel: HeroViewModel) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute == "home" || currentRoute == "squad") {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") },
                        selected = currentRoute == "home",
                        onClick = {
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Star, contentDescription = "My Squad") },
                        label = { Text("My Squad") },
                        selected = currentRoute == "squad",
                        onClick = {
                            navController.navigate("squad") {
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") {
                HomeScreen(
                    viewModel = viewModel,
                    onHeroClick = { hero ->
                        navController.navigate("detail/${hero.nama}")
                    }
                )
            }
            composable("squad") {
                SquadScreen(
                    viewModel = viewModel,
                    onHeroClick = { hero ->
                        navController.navigate("detail/${hero.nama}")
                    }
                )
            }
            composable(
                route = "detail/{heroName}",
                arguments = listOf(navArgument("heroName") { type = NavType.StringType })
            ) { backStackEntry ->
                val heroName = backStackEntry.arguments?.getString("heroName")
                val heroes by viewModel.heroes.collectAsState()
                val hero = heroes.find { it.nama == heroName }
                
                hero?.let {
                    HeroDetailScreen(
                        hero = it,
                        viewModel = viewModel,
                        onBackClick = { navController.popBackStack() },
                        onRecruitClick = { viewModel.addToSquad(it) }
                    )
                }
            }
        }
    }
}

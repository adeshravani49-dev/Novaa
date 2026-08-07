package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.GridOn
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import com.example.ui.TicTacToeViewModel
import com.example.ui.screens.GameScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StatsScreen
import com.example.ui.theme.TicTacToeTheme
import com.example.ui.theme.getThemePalette

enum class AppDestination(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    GAME("Play", Icons.Filled.GridOn, Icons.Outlined.GridOn, "nav_item_game"),
    STATS("Stats", Icons.Filled.BarChart, Icons.Outlined.BarChart, "nav_item_stats"),
    SETTINGS("Settings", Icons.Filled.Settings, Icons.Outlined.Settings, "nav_item_settings")
}

class MainActivity : ComponentActivity() {

    private val viewModel: TicTacToeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TicTacToeTheme {
                val uiState by viewModel.uiState.collectAsState()
                val palette = getThemePalette(uiState.boardTheme)

                var currentDestination by rememberSaveable { mutableStateOf(AppDestination.GAME) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar(
                            containerColor = palette.surface,
                            contentColor = palette.accent,
                            modifier = Modifier.testTag("navigation_bar")
                        ) {
                            AppDestination.entries.forEach { destination ->
                                val isSelected = currentDestination == destination
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = { currentDestination = destination },
                                    icon = {
                                        Icon(
                                            imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                                            contentDescription = destination.title
                                        )
                                    },
                                    label = { Text(destination.title) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = palette.background,
                                        selectedTextColor = palette.accent,
                                        indicatorColor = palette.accent,
                                        unselectedIconColor = palette.gridColor,
                                        unselectedTextColor = palette.gridColor
                                    ),
                                    modifier = Modifier.testTag(destination.testTag)
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(palette.background)
                    ) {
                        when (currentDestination) {
                            AppDestination.GAME -> GameScreen(
                                uiState = uiState,
                                viewModel = viewModel
                            )
                            AppDestination.STATS -> StatsScreen(
                                viewModel = viewModel
                            )
                            AppDestination.SETTINGS -> SettingsScreen(
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }
}

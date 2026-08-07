package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.MatchEntity
import com.example.ui.TicTacToeViewModel
import com.example.ui.theme.getThemePalette
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StatsScreen(
    viewModel: TicTacToeViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val palette = getThemePalette(uiState.boardTheme)

    val matches by viewModel.allMatches.collectAsState()
    val totalMatches by viewModel.totalMatchesCount.collectAsState()
    val xWins by viewModel.xWinsCount.collectAsState()
    val oWins by viewModel.oWinsCount.collectAsState()
    val draws by viewModel.drawsCount.collectAsState()

    var showClearDialog by remember { mutableStateOf(false) }

    val winRate = if (totalMatches > 0) ((xWins.toFloat() / totalMatches.toFloat()) * 100).toInt() else 0

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background)
            .padding(16.dp)
    ) {
        // Screen Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Match Statistics",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Local match history & win rates",
                    color = palette.gridColor,
                    fontSize = 12.sp
                )
            }

            if (matches.isNotEmpty()) {
                IconButton(
                    onClick = { showClearDialog = true },
                    modifier = Modifier.testTag("clear_history_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Clear History",
                        tint = palette.oColor
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Summary Metric Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard(
                title = "Total Games",
                value = totalMatches.toString(),
                accentColor = palette.accent,
                palette = palette,
                testTag = "metric_total_games",
                modifier = Modifier.weight(1f)
            )

            MetricCard(
                title = "Win Rate",
                value = "$winRate%",
                accentColor = palette.xColor,
                palette = palette,
                testTag = "metric_win_rate",
                modifier = Modifier.weight(1f)
            )

            MetricCard(
                title = "Draws",
                value = draws.toString(),
                accentColor = palette.gridColor,
                palette = palette,
                testTag = "metric_draws",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // History Log List Section Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = null,
                tint = palette.accent
            )
            Text(
                text = "Recent Matches",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (matches.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = palette.gridColor,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "No match history recorded yet.",
                        color = palette.gridColor,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Play a round to track your stats!",
                        color = palette.gridColor.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(matches) { match ->
                    MatchHistoryCard(match = match, palette = palette)
                }
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear Match History?") },
            text = { Text("Are you sure you want to delete all recorded match history? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearMatchHistory()
                        showClearDialog = false
                    },
                    modifier = Modifier.testTag("confirm_clear_button")
                ) {
                    Text("Clear All", color = palette.oColor)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel")
                }
            },
            containerColor = palette.surface
        )
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    accentColor: Color,
    palette: com.example.ui.theme.ThemePalette,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .testTag(testTag),
        colors = CardDefaults.cardColors(
            containerColor = palette.cardBackground
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                color = palette.gridColor,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                color = accentColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun MatchHistoryCard(
    match: MatchEntity,
    palette: com.example.ui.theme.ThemePalette
) {
    val resultText = when (match.result) {
        "X_WON" -> "${match.winnerName ?: "X"} Won"
        "O_WON" -> "${match.winnerName ?: "O"} Won"
        else -> "Draw"
    }

    val resultColor = when (match.result) {
        "X_WON" -> palette.xColor
        "O_WON" -> palette.oColor
        else -> palette.gridColor
    }

    val dateFormat = remember { SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()) }
    val formattedDate = remember(match.timestamp) { dateFormat.format(Date(match.timestamp)) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = palette.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "${match.playerXName} vs ${match.playerOName}",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${match.gameMode} • ${match.boardSizeLabel} • ${match.moveCount} moves",
                    color = palette.gridColor,
                    fontSize = 11.sp
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = resultText,
                    color = resultColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = formattedDate,
                    color = palette.gridColor,
                    fontSize = 10.sp
                )
            }
        }
    }
}

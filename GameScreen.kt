package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameMode
import com.example.model.GameStatus
import com.example.model.PlayerSymbol
import com.example.ui.GameUiState
import com.example.ui.TicTacToeViewModel
import com.example.ui.components.ConfettiEffect
import com.example.ui.components.GameBoard
import com.example.ui.components.ScoreBoard
import com.example.ui.theme.getThemePalette

@Composable
fun GameScreen(
    uiState: GameUiState,
    viewModel: TicTacToeViewModel,
    modifier: Modifier = Modifier
) {
    val palette = getThemePalette(uiState.boardTheme)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Row: App Title & Audio Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Tic Tac Toe",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "${uiState.gameMode.label} • ${uiState.boardSize.label}",
                        color = palette.gridColor,
                        fontSize = 12.sp
                    )
                }

                IconButton(
                    onClick = { viewModel.toggleAudio() },
                    modifier = Modifier.testTag("audio_toggle_button")
                ) {
                    Icon(
                        imageVector = if (uiState.isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                        contentDescription = "Toggle Audio",
                        tint = palette.accent
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Game Mode Switcher Row
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                GameMode.entries.forEachIndexed { index, mode ->
                    SegmentedButton(
                        selected = uiState.gameMode == mode,
                        onClick = { viewModel.setGameMode(mode) },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = GameMode.entries.size),
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = palette.surface,
                            activeContentColor = palette.accent,
                            inactiveContainerColor = palette.cardBackground,
                            inactiveContentColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.testTag("mode_tab_${mode.name.lowercase()}")
                    ) {
                        Text(
                            text = if (mode == GameMode.VS_AI) "vs AI" else "2 Players",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Scoreboard Header
            ScoreBoard(
                xName = uiState.playerXName,
                oName = uiState.playerOName,
                xWins = uiState.sessionXWins,
                oWins = uiState.sessionOWins,
                draws = uiState.sessionDraws,
                currentTurn = uiState.currentTurn,
                palette = palette,
                markerStyle = uiState.markerStyle
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Turn Status Banner / Announcement
            StatusBanner(
                gameStatus = uiState.gameStatus,
                currentTurn = uiState.currentTurn,
                playerXName = uiState.playerXName,
                playerOName = uiState.playerOName,
                isAiThinking = uiState.isAiThinking,
                palette = palette,
                markerStyle = uiState.markerStyle
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Interactive Game Grid Board
            GameBoard(
                board = uiState.board,
                boardSize = uiState.boardSize,
                gameStatus = uiState.gameStatus,
                palette = palette,
                markerStyle = uiState.markerStyle,
                onCellClick = { viewModel.makeMove(it) },
                modifier = Modifier.testTag("game_board")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom Actions: Undo & Reset
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.undoMove() },
                    enabled = uiState.moveHistory.isNotEmpty() && uiState.gameStatus is GameStatus.InProgress,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = palette.accent
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("undo_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Undo,
                        contentDescription = "Undo",
                        tint = if (uiState.moveHistory.isNotEmpty()) palette.accent else palette.gridColor
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Undo")
                }

                Button(
                    onClick = { viewModel.resetBoard() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = palette.accent,
                        contentColor = palette.background
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("reset_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Autorenew,
                        contentDescription = "New Round"
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (uiState.gameStatus is GameStatus.InProgress) "Reset" else "Next Round",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Confetti Overlay on Victory
        AnimatedVisibility(
            visible = uiState.gameStatus is GameStatus.Won,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            ConfettiEffect(palette = palette)
        }
    }
}

@Composable
private fun StatusBanner(
    gameStatus: GameStatus,
    currentTurn: PlayerSymbol,
    playerXName: String,
    playerOName: String,
    isAiThinking: Boolean,
    palette: com.example.ui.theme.ThemePalette,
    markerStyle: com.example.model.MarkerStyle
) {
    val statusText = when (gameStatus) {
        is GameStatus.Won -> {
            val winnerName = if (gameStatus.winner == PlayerSymbol.X) playerXName else playerOName
            val symbolStr = if (gameStatus.winner == PlayerSymbol.X) markerStyle.xDisplay else markerStyle.oDisplay
            "🎉 $winnerName ($symbolStr) Wins!"
        }
        GameStatus.Draw -> "🤝 It's a Draw!"
        GameStatus.InProgress -> {
            if (isAiThinking) {
                "🤖 AI is thinking..."
            } else {
                val name = if (currentTurn == PlayerSymbol.X) playerXName else playerOName
                val symbol = if (currentTurn == PlayerSymbol.X) markerStyle.xDisplay else markerStyle.oDisplay
                "Turn: $name ($symbol)"
            }
        }
    }

    val bannerColor = when (gameStatus) {
        is GameStatus.Won -> if (gameStatus.winner == PlayerSymbol.X) palette.xColor else palette.oColor
        GameStatus.Draw -> palette.gridColor
        GameStatus.InProgress -> if (currentTurn == PlayerSymbol.X) palette.xColor else palette.oColor
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .testTag("status_banner"),
        colors = CardDefaults.cardColors(
            containerColor = palette.surface
        )
    ) {
        Text(
            text = statusText,
            color = bannerColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 12.dp)
        )
    }
}

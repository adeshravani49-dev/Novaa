package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BoardSize
import com.example.model.GameStatus
import com.example.model.MarkerStyle
import com.example.model.PlayerSymbol
import com.example.ui.theme.ThemePalette

@Composable
fun GameBoard(
    board: Array<PlayerSymbol?>,
    boardSize: BoardSize,
    gameStatus: GameStatus,
    palette: ThemePalette,
    markerStyle: MarkerStyle,
    onCellClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val size = boardSize.dimension

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(24.dp))
            .background(palette.cardBackground)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (r in 0 until size) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (c in 0 until size) {
                        val index = r * size + c
                        val symbol = board[index]
                        val isWinningCell = (gameStatus is GameStatus.Won) && (index in gameStatus.winningIndices)

                        GridCell(
                            symbol = symbol,
                            isWinningCell = isWinningCell,
                            palette = palette,
                            markerStyle = markerStyle,
                            testTag = "cell_$index",
                            onClick = { onCellClick(index) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Draw animated winning line if someone won
        if (gameStatus is GameStatus.Won) {
            WinningLineCanvas(
                winningIndices = gameStatus.winningIndices,
                boardSize = boardSize,
                lineColor = if (gameStatus.winner == PlayerSymbol.X) palette.xColor else palette.oColor
            )
        }
    }
}

@Composable
private fun GridCell(
    symbol: PlayerSymbol?,
    isWinningCell: Boolean,
    palette: ThemePalette,
    markerStyle: MarkerStyle,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    val scale by animateFloatAsState(
        targetValue = if (symbol != null) 1f else 0.9f,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "CellScale"
    )

    val backgroundColor = when {
        isWinningCell -> (if (symbol == PlayerSymbol.X) palette.xColor else palette.oColor).copy(alpha = 0.25f)
        else -> palette.surface
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .testTag(testTag)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (symbol != null) {
            val text = when (symbol) {
                PlayerSymbol.X -> markerStyle.xDisplay
                PlayerSymbol.O -> markerStyle.oDisplay
            }
            val textColor = when (symbol) {
                PlayerSymbol.X -> palette.xColor
                PlayerSymbol.O -> palette.oColor
            }

            Text(
                text = text,
                color = textColor,
                fontSize = if (markerStyle == MarkerStyle.CLASSIC || markerStyle == MarkerStyle.NEON) 36.sp else 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.scale(scale)
            )
        }
    }
}

@Composable
private fun WinningLineCanvas(
    winningIndices: List<Int>,
    boardSize: BoardSize,
    lineColor: Color
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(winningIndices) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
        )
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        if (winningIndices.size < 2) return@Canvas

        val dimension = boardSize.dimension
        val cellWidth = size.width / dimension
        val cellHeight = size.height / dimension

        val firstIdx = winningIndices.first()
        val lastIdx = winningIndices.last()

        val startRow = firstIdx / dimension
        val startCol = firstIdx % dimension
        val endRow = lastIdx / dimension
        val endCol = lastIdx % dimension

        val startX = (startCol + 0.5f) * cellWidth
        val startY = (startRow + 0.5f) * cellHeight

        val targetEndX = (endCol + 0.5f) * cellWidth
        val targetEndY = (endRow + 0.5f) * cellHeight

        val currentEndX = startX + (targetEndX - startX) * progress.value
        val currentEndY = startY + (targetEndY - startY) * progress.value

        drawLine(
            color = lineColor,
            start = Offset(startX, startY),
            end = Offset(currentEndX, currentEndY),
            strokeWidth = 14f,
            cap = StrokeCap.Round
        )
    }
}

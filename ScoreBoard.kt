package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MarkerStyle
import com.example.model.PlayerSymbol
import com.example.ui.theme.ThemePalette

@Composable
fun ScoreBoard(
    xName: String,
    oName: String,
    xWins: Int,
    oWins: Int,
    draws: Int,
    currentTurn: PlayerSymbol,
    palette: ThemePalette,
    markerStyle: MarkerStyle,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Player X Card
        ScoreCard(
            label = "$xName (${markerStyle.xDisplay})",
            score = xWins,
            isActiveTurn = currentTurn == PlayerSymbol.X,
            activeColor = palette.xColor,
            palette = palette,
            testTag = "score_card_x",
            modifier = Modifier.weight(1f)
        )

        // Ties Card
        ScoreCard(
            label = "Draws",
            score = draws,
            isActiveTurn = false,
            activeColor = palette.gridColor,
            palette = palette,
            testTag = "score_card_draws",
            modifier = Modifier.weight(0.8f)
        )

        // Player O Card
        ScoreCard(
            label = "$oName (${markerStyle.oDisplay})",
            score = oWins,
            isActiveTurn = currentTurn == PlayerSymbol.O,
            activeColor = palette.oColor,
            palette = palette,
            testTag = "score_card_o",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ScoreCard(
    label: String,
    score: Int,
    isActiveTurn: Boolean,
    activeColor: androidx.compose.ui.graphics.Color,
    palette: ThemePalette,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .clip(shape)
            .background(palette.cardBackground)
            .then(
                if (isActiveTurn) Modifier.border(2.dp, activeColor, shape)
                else Modifier
            )
            .padding(vertical = 10.dp, horizontal = 8.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = label,
                color = if (isActiveTurn) activeColor else palette.gridColor,
                fontSize = 12.sp,
                fontWeight = if (isActiveTurn) FontWeight.Bold else FontWeight.Normal,
                maxLines = 1
            )
            Text(
                text = score.toString(),
                color = androidx.compose.ui.graphics.Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

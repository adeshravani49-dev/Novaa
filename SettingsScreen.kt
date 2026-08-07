package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AiDifficulty
import com.example.model.BoardSize
import com.example.model.BoardTheme
import com.example.model.MarkerStyle
import com.example.ui.TicTacToeViewModel
import com.example.ui.theme.getThemePalette

@Composable
fun SettingsScreen(
    viewModel: TicTacToeViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val palette = getThemePalette(uiState.boardTheme)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Screen Title
        Text(
            text = "Game Settings",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = "Customize themes, board size, and AI difficulty",
            color = palette.gridColor,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 1. Board Theme Selector Card
        SettingSection(title = "Visual Theme", palette = palette) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                BoardTheme.entries.forEach { theme ->
                    val isSelected = uiState.boardTheme == theme
                    Card(
                        onClick = { viewModel.setBoardTheme(theme) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .testTag("theme_card_${theme.name.lowercase()}"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) palette.surface else palette.cardBackground
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = theme.label,
                                color = if (isSelected) palette.accent else MaterialTheme.colorScheme.onBackground,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                            if (isSelected) {
                                Text(
                                    text = "Active",
                                    color = palette.accent,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Marker Symbols Style
        SettingSection(title = "Symbol Markers", palette = palette) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                MarkerStyle.entries.forEach { style ->
                    val isSelected = uiState.markerStyle == style
                    Card(
                        onClick = { viewModel.setMarkerStyle(style) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .testTag("marker_card_${style.name.lowercase()}"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) palette.surface else palette.cardBackground
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = style.label,
                                color = if (isSelected) palette.accent else MaterialTheme.colorScheme.onBackground,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                            Text(
                                text = "${style.xDisplay} vs ${style.oDisplay}",
                                color = if (isSelected) palette.accent else palette.gridColor,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Grid Board Size Selector
        SettingSection(title = "Board Size", palette = palette) {
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                BoardSize.entries.forEachIndexed { index, size ->
                    SegmentedButton(
                        selected = uiState.boardSize == size,
                        onClick = { viewModel.setBoardSize(size) },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = BoardSize.entries.size),
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = palette.surface,
                            activeContentColor = palette.accent,
                            inactiveContainerColor = palette.cardBackground,
                            inactiveContentColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.testTag("size_button_${size.dimension}")
                    ) {
                        Text(text = size.label, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4. AI Difficulty Level
        SettingSection(title = "AI Difficulty", palette = palette) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AiDifficulty.entries.forEach { diff ->
                    val isSelected = uiState.aiDifficulty == diff
                    Card(
                        onClick = { viewModel.setAiDifficulty(diff) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .testTag("difficulty_card_${diff.name.lowercase()}"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) palette.surface else palette.cardBackground
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = diff.label,
                                    color = if (isSelected) palette.accent else MaterialTheme.colorScheme.onBackground,
                                    fontWeight = FontWeight.Bold
                                )
                                if (isSelected) {
                                    Text("Selected", color = palette.accent, fontSize = 12.sp)
                                }
                            }
                            Text(
                                text = diff.description,
                                color = palette.gridColor,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 5. Sound Effects Switch
        SettingSection(title = "Audio & Sound Effects", palette = palette) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = palette.cardBackground
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Sound Effects",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Play audio tones on move placement & win",
                            color = palette.gridColor,
                            fontSize = 11.sp
                        )
                    }

                    Switch(
                        checked = !uiState.isMuted,
                        onCheckedChange = { viewModel.toggleAudio() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = palette.background,
                            checkedTrackColor = palette.accent
                        ),
                        modifier = Modifier.testTag("settings_audio_switch")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun SettingSection(
    title: String,
    palette: com.example.ui.theme.ThemePalette,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            color = palette.accent,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        content()
    }
}

package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Neon Cyberpunk Colors
val NeonBackground = Color(0xFF0D0714)
val NeonSurface = Color(0xFF1A1228)
val NeonCard = Color(0xFF251A38)
val NeonCyan = Color(0xFF00F0FF)
val NeonMagenta = Color(0xFFFF2A85)
val NeonGrid = Color(0xFF382A54)

// Minimal Slate Colors
val SlateBackground = Color(0xFF0F172A)
val SlateSurface = Color(0xFF1E293B)
val SlateCard = Color(0xFF334155)
val SlateBlue = Color(0xFF38BDF8)
val SlateRose = Color(0xFFFB7185)
val SlateGrid = Color(0xFF475569)

// Sunset Glow Colors
val SunsetBackground = Color(0xFF1A0F1D)
val SunsetSurface = Color(0xFF2A1930)
val SunsetCard = Color(0xFF3B2344)
val SunsetAmber = Color(0xFFFBBF24)
val SunsetPurple = Color(0xFFC084FC)
val SunsetGrid = Color(0xFF4A2C55)

// Emoji Party Colors
val EmojiBackground = Color(0xFF181524)
val EmojiSurface = Color(0xFF262238)
val EmojiCard = Color(0xFF35304C)
val EmojiGreen = Color(0xFF34D399)
val EmojiOrange = Color(0xFFFB923C)
val EmojiGrid = Color(0xFF494266)

data class ThemePalette(
    val background: Color,
    val surface: Color,
    val cardBackground: Color,
    val xColor: Color,
    val oColor: Color,
    val gridColor: Color,
    val accent: Color
)

fun getThemePalette(theme: com.example.model.BoardTheme): ThemePalette {
    return when (theme) {
        com.example.model.BoardTheme.NEON_CYBERPUNK -> ThemePalette(
            background = NeonBackground,
            surface = NeonSurface,
            cardBackground = NeonCard,
            xColor = NeonCyan,
            oColor = NeonMagenta,
            gridColor = NeonGrid,
            accent = NeonCyan
        )
        com.example.model.BoardTheme.MINIMAL_SLATE -> ThemePalette(
            background = SlateBackground,
            surface = SlateSurface,
            cardBackground = SlateCard,
            xColor = SlateBlue,
            oColor = SlateRose,
            gridColor = SlateGrid,
            accent = SlateBlue
        )
        com.example.model.BoardTheme.SUNSET_GLOW -> ThemePalette(
            background = SunsetBackground,
            surface = SunsetSurface,
            cardBackground = SunsetCard,
            xColor = SunsetAmber,
            oColor = SunsetPurple,
            gridColor = SunsetGrid,
            accent = SunsetAmber
        )
        com.example.model.BoardTheme.EMOJI_FUN -> ThemePalette(
            background = EmojiBackground,
            surface = EmojiSurface,
            cardBackground = EmojiCard,
            xColor = EmojiGreen,
            oColor = EmojiOrange,
            gridColor = EmojiGrid,
            accent = EmojiGreen
        )
    }
}

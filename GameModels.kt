package com.example.model

enum class PlayerSymbol {
    X, O;

    fun other(): PlayerSymbol = if (this == X) O else X
}

enum class GameMode(val label: String) {
    VS_AI("Single Player (vs AI)"),
    PASS_AND_PLAY("Two Players (Pass & Play)")
}

enum class AiDifficulty(val label: String, val description: String) {
    EASY("Easy", "Random & basic moves"),
    MEDIUM("Medium", "Smart blocks & winning moves"),
    HARD("Hard (Minimax)", "Unbeatable strategic play")
}

enum class BoardSize(val dimension: Int, val winLength: Int, val label: String) {
    THREE(3, 3, "3 x 3"),
    FOUR(4, 4, "4 x 4"),
    FIVE(5, 4, "5 x 5 (4 in a row)")
}

enum class BoardTheme(val label: String) {
    NEON_CYBERPUNK("Neon Cyberpunk"),
    MINIMAL_SLATE("Minimal Slate"),
    SUNSET_GLOW("Sunset Glow"),
    EMOJI_FUN("Emoji Party")
}

enum class MarkerStyle(val label: String, val xDisplay: String, val oDisplay: String) {
    CLASSIC("Classic X & O", "X", "O"),
    NEON("Neon Glow", "✕", "◯"),
    EMOJI_PETS("Pets", "🐶", "🐱"),
    EMOJI_SPACE("Space", "🚀", "👾"),
    EMOJI_FOOD("Food", "🍕", "🍔")
}

sealed class GameStatus {
    object InProgress : GameStatus()
    data class Won(val winner: PlayerSymbol, val winningIndices: List<Int>) : GameStatus()
    object Draw : GameStatus()
}

data class Move(
    val index: Int,
    val symbol: PlayerSymbol
)

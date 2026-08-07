package com.example.game

import com.example.model.AiDifficulty
import com.example.model.BoardSize
import com.example.model.GameStatus
import com.example.model.PlayerSymbol
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

object TicTacToeEngine {

    /**
     * Checks if the board has a win or draw condition.
     */
    fun checkGameStatus(board: Array<PlayerSymbol?>, boardSize: BoardSize): GameStatus {
        val size = boardSize.dimension
        val winLen = boardSize.winLength

        val winningLines = getPossibleWinningLines(size, winLen)

        for (line in winningLines) {
            val firstSymbol = board[line[0]] ?: continue
            var isWin = true
            for (i in 1 until line.size) {
                if (board[line[i]] != firstSymbol) {
                    isWin = false
                    break
                }
            }
            if (isWin) {
                return GameStatus.Won(firstSymbol, line)
            }
        }

        // Check for draw if no empty cells remaining
        if (board.none { it == null }) {
            return GameStatus.Draw
        }

        return GameStatus.InProgress
    }

    /**
     * Generates all winning line index combinations for a size x size grid with winLength requirement.
     */
    fun getPossibleWinningLines(size: Int, winLen: Int): List<List<Int>> {
        val lines = mutableListOf<List<Int>>()

        // Horizontal lines
        for (r in 0 until size) {
            for (c in 0..size - winLen) {
                val line = mutableListOf<Int>()
                for (k in 0 until winLen) {
                    line.add(r * size + (c + k))
                }
                lines.add(line)
            }
        }

        // Vertical lines
        for (c in 0 until size) {
            for (r in 0..size - winLen) {
                val line = mutableListOf<Int>()
                for (k in 0 until winLen) {
                    line.add((r + k) * size + c)
                }
                lines.add(line)
            }
        }

        // Main Diagonals (\)
        for (r in 0..size - winLen) {
            for (c in 0..size - winLen) {
                val line = mutableListOf<Int>()
                for (k in 0 until winLen) {
                    line.add((r + k) * size + (c + k))
                }
                lines.add(line)
            }
        }

        // Anti Diagonals (/)
        for (r in 0..size - winLen) {
            for (c in winLen - 1 until size) {
                val line = mutableListOf<Int>()
                for (k in 0 until winLen) {
                    line.add((r + k) * size + (c - k))
                }
                lines.add(line)
            }
        }

        return lines
    }

    /**
     * Finds the best AI move according to difficulty.
     */
    fun getAiMove(
        board: Array<PlayerSymbol?>,
        boardSize: BoardSize,
        aiSymbol: PlayerSymbol,
        difficulty: AiDifficulty
    ): Int? {
        val emptyIndices = board.indices.filter { board[it] == null }
        if (emptyIndices.isEmpty()) return null

        return when (difficulty) {
            AiDifficulty.EASY -> {
                // Easy: 80% random, 20% block immediate loss
                if (Random.nextFloat() < 0.2f) {
                    findWinningOrBlockingMove(board, boardSize, aiSymbol) ?: emptyIndices.random()
                } else {
                    emptyIndices.random()
                }
            }
            AiDifficulty.MEDIUM -> {
                // Medium: Take win, block loss, center if available, or random
                findWinningOrBlockingMove(board, boardSize, aiSymbol)
                    ?: getCenterOrCornerMove(board, boardSize)
                    ?: emptyIndices.random()
            }
            AiDifficulty.HARD -> {
                if (boardSize == BoardSize.THREE) {
                    // Minimax for 3x3 (Unbeatable)
                    getMinimaxMove(board, boardSize, aiSymbol)
                } else {
                    // Smart heuristic search for 4x4 / 5x5
                    getHeuristicBestMove(board, boardSize, aiSymbol)
                }
            }
        }
    }

    private fun findWinningOrBlockingMove(
        board: Array<PlayerSymbol?>,
        boardSize: BoardSize,
        aiSymbol: PlayerSymbol
    ): Int? {
        val opponent = aiSymbol.other()
        val emptyIndices = board.indices.filter { board[it] == null }

        // 1. Can AI win in 1 move?
        for (index in emptyIndices) {
            board[index] = aiSymbol
            val status = checkGameStatus(board, boardSize)
            board[index] = null
            if (status is GameStatus.Won && status.winner == aiSymbol) {
                return index
            }
        }

        // 2. Must AI block opponent's 1-move win?
        for (index in emptyIndices) {
            board[index] = opponent
            val status = checkGameStatus(board, boardSize)
            board[index] = null
            if (status is GameStatus.Won && status.winner == opponent) {
                return index
            }
        }

        return null
    }

    private fun getCenterOrCornerMove(board: Array<PlayerSymbol?>, boardSize: BoardSize): Int? {
        val size = boardSize.dimension
        val center = (size * size) / 2
        if (board[center] == null) return center

        val corners = listOf(0, size - 1, size * (size - 1), size * size - 1)
        val availableCorners = corners.filter { board[it] == null }
        if (availableCorners.isNotEmpty()) {
            return availableCorners.random()
        }
        return null
    }

    private fun getMinimaxMove(
        board: Array<PlayerSymbol?>,
        boardSize: BoardSize,
        aiSymbol: PlayerSymbol
    ): Int {
        var bestScore = Int.MIN_VALUE
        var bestMove = -1
        val emptyIndices = board.indices.filter { board[it] == null }

        for (index in emptyIndices) {
            board[index] = aiSymbol
            val score = minimax(board, boardSize, 0, false, aiSymbol, Int.MIN_VALUE, Int.MAX_VALUE)
            board[index] = null

            if (score > bestScore) {
                bestScore = score
                bestMove = index
            }
        }

        return if (bestMove != -1) bestMove else emptyIndices.random()
    }

    private fun minimax(
        board: Array<PlayerSymbol?>,
        boardSize: BoardSize,
        depth: Int,
        isMaximizing: Boolean,
        aiSymbol: PlayerSymbol,
        alpha: Int,
        beta: Int
    ): Int {
        var a = alpha
        var b = beta
        val status = checkGameStatus(board, boardSize)

        when (status) {
            is GameStatus.Won -> {
                return if (status.winner == aiSymbol) 10 - depth else depth - 10
            }
            GameStatus.Draw -> return 0
            GameStatus.InProgress -> {}
        }

        if (depth >= 7) return 0 // Depth limit for performance safety

        val emptyIndices = board.indices.filter { board[it] == null }

        if (isMaximizing) {
            var maxEval = Int.MIN_VALUE
            for (index in emptyIndices) {
                board[index] = aiSymbol
                val eval = minimax(board, boardSize, depth + 1, false, aiSymbol, a, b)
                board[index] = null
                maxEval = max(maxEval, eval)
                a = max(a, eval)
                if (b <= a) break
            }
            return maxEval
        } else {
            var minEval = Int.MAX_VALUE
            val humanSymbol = aiSymbol.other()
            for (index in emptyIndices) {
                board[index] = humanSymbol
                val eval = minimax(board, boardSize, depth + 1, true, aiSymbol, a, b)
                board[index] = null
                minEval = min(minEval, eval)
                b = min(b, eval)
                if (b <= a) break
            }
            return minEval
        }
    }

    private fun getHeuristicBestMove(
        board: Array<PlayerSymbol?>,
        boardSize: BoardSize,
        aiSymbol: PlayerSymbol
    ): Int {
        val winningOrBlocking = findWinningOrBlockingMove(board, boardSize, aiSymbol)
        if (winningOrBlocking != null) return winningOrBlocking

        // Rank remaining moves by score
        val emptyIndices = board.indices.filter { board[it] == null }
        var bestMove = emptyIndices.random()
        var maxScore = -10000

        for (index in emptyIndices) {
            val score = evaluateCellHeuristic(board, boardSize, index, aiSymbol)
            if (score > maxScore) {
                maxScore = score
                bestMove = index
            }
        }

        return bestMove
    }

    private fun evaluateCellHeuristic(
        board: Array<PlayerSymbol?>,
        boardSize: BoardSize,
        index: Int,
        aiSymbol: PlayerSymbol
    ): Int {
        val size = boardSize.dimension
        val winLen = boardSize.winLength
        val row = index / size
        val col = index % size
        var score = 0

        // Prefer center
        val centerRow = size / 2
        val centerCol = size / 2
        val distFromCenter = Math.abs(row - centerRow) + Math.abs(col - centerCol)
        score += (size - distFromCenter) * 2

        // Check potential lines passing through this cell
        val lines = getPossibleWinningLines(size, winLen).filter { index in it }
        for (line in lines) {
            var aiCount = 0
            var oppCount = 0
            for (idx in line) {
                val symbol = board[idx]
                if (symbol == aiSymbol) aiCount++
                else if (symbol == aiSymbol.other()) oppCount++
            }

            if (aiCount > 0 && oppCount == 0) {
                score += aiCount * 10
            } else if (oppCount > 0 && aiCount == 0) {
                score += oppCount * 8
            }
        }

        return score
    }
}

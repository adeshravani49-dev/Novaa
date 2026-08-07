package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundManager
import com.example.data.MatchRepository
import com.example.data.db.AppDatabase
import com.example.data.db.MatchEntity
import com.example.game.TicTacToeEngine
import com.example.model.AiDifficulty
import com.example.model.BoardSize
import com.example.model.BoardTheme
import com.example.model.GameMode
import com.example.model.GameStatus
import com.example.model.MarkerStyle
import com.example.model.Move
import com.example.model.PlayerSymbol
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GameUiState(
    val board: Array<PlayerSymbol?> = arrayOfNulls(9),
    val boardSize: BoardSize = BoardSize.THREE,
    val gameStatus: GameStatus = GameStatus.InProgress,
    val currentTurn: PlayerSymbol = PlayerSymbol.X,
    val gameMode: GameMode = GameMode.VS_AI,
    val aiDifficulty: AiDifficulty = AiDifficulty.MEDIUM,
    val userSymbol: PlayerSymbol = PlayerSymbol.X,
    val boardTheme: BoardTheme = BoardTheme.NEON_CYBERPUNK,
    val markerStyle: MarkerStyle = MarkerStyle.NEON,
    val playerXName: String = "Player X",
    val playerOName: String = "AI (Medium)",
    val sessionXWins: Int = 0,
    val sessionOWins: Int = 0,
    val sessionDraws: Int = 0,
    val isMuted: Boolean = false,
    val isAiThinking: Boolean = false,
    val moveHistory: List<Move> = emptyList()
)

class TicTacToeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MatchRepository
    val soundManager: SoundManager

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    val allMatches: StateFlow<List<MatchEntity>>
    val totalMatchesCount: StateFlow<Int>
    val xWinsCount: StateFlow<Int>
    val oWinsCount: StateFlow<Int>
    val drawsCount: StateFlow<Int>

    init {
        val dao = AppDatabase.getDatabase(application).matchDao()
        repository = MatchRepository(dao)
        soundManager = SoundManager(application)

        allMatches = repository.allMatches.stateInFlow(emptyList())
        totalMatchesCount = repository.totalMatches.stateInFlow(0)
        xWinsCount = repository.xWins.stateInFlow(0)
        oWinsCount = repository.oWins.stateInFlow(0)
        drawsCount = repository.draws.stateInFlow(0)
    }

    private fun <T> kotlinx.coroutines.flow.Flow<T>.stateInFlow(initialValue: T): StateFlow<T> {
        val flow = MutableStateFlow(initialValue)
        viewModelScope.launch {
            collect { flow.value = it }
        }
        return flow.asStateFlow()
    }

    fun makeMove(index: Int) {
        val state = _uiState.value
        if (state.gameStatus !is GameStatus.InProgress) return
        if (state.isAiThinking) return
        if (state.board[index] != null) return

        val symbol = state.currentTurn
        val newBoard = state.board.copyOf()
        newBoard[index] = symbol

        val newHistory = state.moveHistory + Move(index, symbol)
        val status = TicTacToeEngine.checkGameStatus(newBoard, state.boardSize)

        soundManager.playMoveSound()

        val nextTurn = symbol.other()

        _uiState.update {
            it.copy(
                board = newBoard,
                gameStatus = status,
                currentTurn = nextTurn,
                moveHistory = newHistory
            )
        }

        handleGameStatusCheck(status, newBoard, newHistory)

        // Trigger AI turn if applicable
        if (status is GameStatus.InProgress &&
            state.gameMode == GameMode.VS_AI &&
            nextTurn != state.userSymbol
        ) {
            triggerAiMove(newBoard, state.boardSize, nextTurn, state.aiDifficulty)
        }
    }

    private fun triggerAiMove(
        board: Array<PlayerSymbol?>,
        boardSize: BoardSize,
        aiSymbol: PlayerSymbol,
        difficulty: AiDifficulty
    ) {
        _uiState.update { it.copy(isAiThinking = true) }

        viewModelScope.launch {
            delay(400) // Realistic delay for AI move feeling

            val aiMoveIndex = TicTacToeEngine.getAiMove(board, boardSize, aiSymbol, difficulty)
            if (aiMoveIndex != null && _uiState.value.gameStatus is GameStatus.InProgress) {
                val newBoard = board.copyOf()
                newBoard[aiMoveIndex] = aiSymbol

                val newHistory = _uiState.value.moveHistory + Move(aiMoveIndex, aiSymbol)
                val status = TicTacToeEngine.checkGameStatus(newBoard, boardSize)

                soundManager.playAiMoveSound()

                _uiState.update {
                    it.copy(
                        board = newBoard,
                        gameStatus = status,
                        currentTurn = aiSymbol.other(),
                        isAiThinking = false,
                        moveHistory = newHistory
                    )
                }

                handleGameStatusCheck(status, newBoard, newHistory)
            } else {
                _uiState.update { it.copy(isAiThinking = false) }
            }
        }
    }

    private fun handleGameStatusCheck(
        status: GameStatus,
        board: Array<PlayerSymbol?>,
        history: List<Move>
    ) {
        val state = _uiState.value

        when (status) {
            is GameStatus.Won -> {
                soundManager.playWinSound(viewModelScope)

                val isX = status.winner == PlayerSymbol.X
                val newXWins = if (isX) state.sessionXWins + 1 else state.sessionXWins
                val newOWins = if (!isX) state.sessionOWins + 1 else state.sessionOWins

                _uiState.update {
                    it.copy(
                        sessionXWins = newXWins,
                        sessionOWins = newOWins
                    )
                }

                recordMatchInDb(
                    result = if (isX) "X_WON" else "O_WON",
                    winnerName = if (isX) state.playerXName else state.playerOName,
                    moveCount = history.size
                )
            }
            GameStatus.Draw -> {
                soundManager.playDrawSound()

                _uiState.update { it.copy(sessionDraws = it.sessionDraws + 1) }

                recordMatchInDb(
                    result = "DRAW",
                    winnerName = null,
                    moveCount = history.size
                )
            }
            GameStatus.InProgress -> {}
        }
    }

    private fun recordMatchInDb(result: String, winnerName: String?, moveCount: Int) {
        val state = _uiState.value
        viewModelScope.launch {
            repository.recordMatch(
                MatchEntity(
                    gameMode = state.gameMode.label,
                    difficulty = if (state.gameMode == GameMode.VS_AI) state.aiDifficulty.label else null,
                    boardSizeLabel = state.boardSize.label,
                    playerXName = state.playerXName,
                    playerOName = state.playerOName,
                    result = result,
                    winnerName = winnerName,
                    moveCount = moveCount
                )
            )
        }
    }

    fun undoMove() {
        val state = _uiState.value
        val history = state.moveHistory
        if (history.isEmpty()) return
        if (state.isAiThinking) return

        soundManager.playButtonClick()

        var movesToPop = 1
        // In VS_AI mode, undo pops both AI move and human move to return to human turn
        if (state.gameMode == GameMode.VS_AI && history.size >= 2) {
            val lastMove = history.last()
            if (lastMove.symbol != state.userSymbol) {
                movesToPop = 2
            }
        }

        val newHistory = history.dropLast(movesToPop)
        val newBoard = arrayOfNulls<PlayerSymbol?>(state.boardSize.dimension * state.boardSize.dimension)
        newHistory.forEach { move ->
            newBoard[move.index] = move.symbol
        }

        val newTurn = if (newHistory.isNotEmpty()) newHistory.last().symbol.other() else PlayerSymbol.X

        _uiState.update {
            it.copy(
                board = newBoard,
                gameStatus = GameStatus.InProgress,
                currentTurn = newTurn,
                moveHistory = newHistory
            )
        }
    }

    fun resetBoard() {
        soundManager.playButtonClick()
        val state = _uiState.value
        val totalCells = state.boardSize.dimension * state.boardSize.dimension

        _uiState.update {
            it.copy(
                board = arrayOfNulls(totalCells),
                gameStatus = GameStatus.InProgress,
                currentTurn = PlayerSymbol.X,
                moveHistory = emptyList(),
                isAiThinking = false
            )
        }

        // If VS_AI and user chosen symbol is O, AI makes the first move
        if (state.gameMode == GameMode.VS_AI && state.userSymbol == PlayerSymbol.O) {
            triggerAiMove(arrayOfNulls(totalCells), state.boardSize, PlayerSymbol.X, state.aiDifficulty)
        }
    }

    fun setGameMode(mode: GameMode) {
        soundManager.playButtonClick()
        _uiState.update {
            it.copy(
                gameMode = mode,
                playerXName = if (mode == GameMode.VS_AI) "You" else "Player 1",
                playerOName = if (mode == GameMode.VS_AI) "AI (${it.aiDifficulty.label})" else "Player 2"
            )
        }
        resetBoard()
    }

    fun setAiDifficulty(difficulty: AiDifficulty) {
        soundManager.playButtonClick()
        _uiState.update {
            it.copy(
                aiDifficulty = difficulty,
                playerOName = if (it.gameMode == GameMode.VS_AI) "AI (${difficulty.label})" else it.playerOName
            )
        }
        resetBoard()
    }

    fun setBoardSize(size: BoardSize) {
        soundManager.playButtonClick()
        _uiState.update { it.copy(boardSize = size) }
        resetBoard()
    }

    fun setBoardTheme(theme: BoardTheme) {
        soundManager.playButtonClick()
        _uiState.update { it.copy(boardTheme = theme) }
    }

    fun setMarkerStyle(style: MarkerStyle) {
        soundManager.playButtonClick()
        _uiState.update { it.copy(markerStyle = style) }
    }

    fun toggleAudio() {
        val newMuted = !_uiState.value.isMuted
        soundManager.isMuted = newMuted
        _uiState.update { it.copy(isMuted = newMuted) }
    }

    fun clearMatchHistory() {
        soundManager.playButtonClick()
        viewModelScope.launch {
            repository.clearAllHistory()
        }
    }

    override fun onCleared() {
        super.onCleared()
        soundManager.release()
    }
}

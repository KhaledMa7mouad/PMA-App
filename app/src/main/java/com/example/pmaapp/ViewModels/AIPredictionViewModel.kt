package com.example.pmaapp.ViewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pmaapp.APIs.PlayerPredictionRepository
import com.example.pmaapp.database.AppDatabase
import com.example.pmaapp.database.Player
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AIPredictionViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PlayerPredictionRepository()
    private val db = AppDatabase.getInstance(application)

    // Track selected model and players
    private val _selectedModel = MutableStateFlow<ApiModel?>(null)
    val selectedModel: StateFlow<ApiModel?> = _selectedModel

    private val _selectedPlayers = MutableStateFlow<List<Player>>(emptyList())
    val selectedPlayers: StateFlow<List<Player>> = _selectedPlayers

    // List of all players for selection
    private val _allPlayers = MutableStateFlow<List<Player>>(emptyList())
    val allPlayers: StateFlow<List<Player>> = _allPlayers

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Result data
    private val _predictionResult = MutableStateFlow<PredictionResult?>(null)
    val predictionResult: StateFlow<PredictionResult?> = _predictionResult

    // Error handling
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Navigation event
    private val _navigateToResult = MutableSharedFlow<Unit>()
    val navigateToResult: SharedFlow<Unit> = _navigateToResult

    init {
        loadPlayers()
    }

    private fun loadPlayers() {
        viewModelScope.launch {
            try {
                _allPlayers.value = db.playerDao.getAllPlayers().first()
            } catch (e: Exception) {
                _error.value = "Failed to load players: ${e.message}"
            }
        }
    }

    fun selectModel(model: ApiModel) {
        // When changing model, clear previously selected players
        if (_selectedModel.value != model) {
            _selectedPlayers.value = emptyList()
        }
        _selectedModel.value = model
        // Also clear any previous errors or results
        _error.value = null
        _predictionResult.value = null
    }

    fun selectPlayer(player: Player) {
        val currentModel = _selectedModel.value ?: return
        val currentList = _selectedPlayers.value.toMutableList()

        if (currentList.contains(player)) {
            // If player is already selected, remove them
            currentList.remove(player)
        } else {
            // For single-player models, replace the selection
            // For multi-player models, add to selection
            if (currentModel == ApiModel.PREDICT_POSITION ||
                currentModel == ApiModel.PREDICT_RATING ||
                currentModel == ApiModel.PREDICT_VALUE ||
                currentModel == ApiModel.PREDICT_WAGE) {
                // These models need exactly 1 player
                currentList.clear()
                currentList.add(player)
            } else {
                // Substitutes model can have multiple players
                currentList.add(player)
            }
        }
        _selectedPlayers.value = currentList
    }

    fun makePrediction() {
        val model = _selectedModel.value
        if (model == null) {
            _error.value = "Please select a prediction model first"
            return
        }

        val players = _selectedPlayers.value
        if (players.isEmpty()) {
            _error.value = "Please select at least one player"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = when (model) {
                    ApiModel.PREDICT_POSITION -> repository.predictPosition(players.first())
                    ApiModel.PREDICT_SUBSTITUTES -> {
                        if (players.size < 2) {
                            _error.value = "Please select at least 2 players for substitutes prediction"
                            _isLoading.value = false
                            return@launch
                        }
                        repository.predictSubstitutes(players)
                    }
                    ApiModel.PREDICT_RATING -> repository.predictRating(players.first())
                    ApiModel.PREDICT_VALUE -> repository.predictValue(players.first())
                    ApiModel.PREDICT_WAGE -> repository.predictWage(players.first())
                }
                handleResult(result, model.toPredictionType())
            } catch (e: Exception) {
                _error.value = "API Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun <T> handleResult(result: Result<T>, type: PredictionType) {
        result.fold(
            onSuccess = { response ->
                _predictionResult.value = PredictionResult(type, response as Any)
                viewModelScope.launch { _navigateToResult.emit(Unit) }
            },
            onFailure = { exception ->
                _error.value = "Prediction failed: ${exception.message}"
            }
        )
    }

    fun clearSelectedPlayers() {
        _selectedPlayers.value = emptyList()
    }

    fun clearError() {
        _error.value = null
    }

    fun clearResult() {
        _predictionResult.value = null
    }

    fun refreshPlayers() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Force fetch the latest players from the database
                _allPlayers.value = db.playerDao.getAllPlayers().first()
            } catch (e: Exception) {
                _error.value = "Failed to refresh players: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }

    }


}


// Helper extensions
fun ApiModel.toPredictionType(): PredictionType = when(this) {
    ApiModel.PREDICT_POSITION    -> PredictionType.POSITION
    ApiModel.PREDICT_SUBSTITUTES -> PredictionType.SUBSTITUTES
    ApiModel.PREDICT_RATING      -> PredictionType.RATING
    ApiModel.PREDICT_VALUE       -> PredictionType.VALUE
    ApiModel.PREDICT_WAGE        -> PredictionType.WAGE
}

enum class ApiModel(val displayName: String, val endpoint: String, val description: String) {
    PREDICT_POSITION("Best Position", "predictPos", "Predict the best position for a player"),
    PREDICT_SUBSTITUTES("Substitutes", "predictSubs", "Recommend the best substitutes"),
    PREDICT_RATING("Player Rating", "predictRating", "Predict the player's rating"),
    PREDICT_VALUE("Market Value", "predictValue", "Predict the market value of a player"),
    PREDICT_WAGE("Wage Recommendation", "predictWage", "Predict the recommended wage for a player")
}

enum class PredictionType { POSITION, SUBSTITUTES, RATING, VALUE, WAGE }

data class PredictionResult(val type: PredictionType, val data: Any)
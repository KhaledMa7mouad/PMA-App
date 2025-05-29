package com.example.pmaapp.SensorsLayer


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import kotlinx.coroutines.Job

class SensorViewModel : ViewModel() {

    // Current sensor data state
    private val _sensorData = MutableStateFlow<StatusData?>(null)
    val sensorData: StateFlow<StatusData?> = _sensorData.asStateFlow()

    // Connection status
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // ECG data points for real-time graph
    private val _ecgDataPoints = MutableStateFlow<List<Float>>(emptyList())
    val ecgDataPoints: StateFlow<List<Float>> = _ecgDataPoints.asStateFlow()

    // Historical data
    private val _sensorHistory = MutableStateFlow(SensorDataHistory())
    val sensorHistory: StateFlow<SensorDataHistory> = _sensorHistory.asStateFlow()

    // Error state
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Monitoring control
    private var monitoringJob: Job? = null
    private val _isMonitoring = mutableStateOf(false)
    val isMonitoring: State<Boolean> = _isMonitoring

    // Configuration
    private val maxEcgPoints = 50
    private val updateInterval = 100L // milliseconds

    init {
        testConnection()
    }

    fun startMonitoring() {
        if (_isMonitoring.value) return

        _isMonitoring.value = true
        _isLoading.value = true

        monitoringJob = viewModelScope.launch {
            while (_isMonitoring.value) {
                try {
                    fetchSensorData { data ->
                        data?.let {
                            updateSensorData(it)
                            _errorMessage.value = null
                            _isConnected.value = true
                        } ?: run {
                            _isConnected.value = false
                            _errorMessage.value = "Failed to fetch sensor data"
                        }
                        _isLoading.value = false
                    }
                } catch (e: Exception) {
                    _errorMessage.value = "Error: ${e.localizedMessage}"
                    _isConnected.value = false
                    _isLoading.value = false
                }

                delay(updateInterval)
            }
        }
    }

    fun stopMonitoring() {
        _isMonitoring.value = false
        monitoringJob?.cancel()
        monitoringJob = null
        _isLoading.value = false
    }

    private fun updateSensorData(data: StatusData) {
        _sensorData.value = data

        // Update ECG data points for real-time graph
        val currentPoints = _ecgDataPoints.value.toMutableList()
        currentPoints.add(data.ecg)
        if (currentPoints.size > maxEcgPoints) {
            currentPoints.removeAt(0)
        }
        _ecgDataPoints.value = currentPoints

        // Add to history
        val currentHistory = _sensorHistory.value
        val updatedEntries = currentHistory.entries + data
        _sensorHistory.value = currentHistory.copy(entries = updatedEntries)
    }

    fun testConnection() {
        viewModelScope.launch {
            _isLoading.value = true
            testSensorConnection { connected ->
                _isConnected.value = connected
                _isLoading.value = false
                if (!connected) {
                    _errorMessage.value = "Unable to connect to sensor device"
                } else {
                    _errorMessage.value = null
                }
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearHistory() {
        _sensorHistory.value = SensorDataHistory()
        _ecgDataPoints.value = emptyList()
    }

    fun exportHistoryData(): SensorDataHistory {
        return _sensorHistory.value.copy(endTime = System.currentTimeMillis())
    }

    override fun onCleared() {
        super.onCleared()
        stopMonitoring()
    }
}
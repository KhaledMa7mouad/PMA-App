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
import android.util.Log

class SensorViewModel : ViewModel() {

    companion object {
        private const val TAG = "SensorViewModel"
    }

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

    // Connection retry tracking
    private var consecutiveFailures = 0
    private val maxConsecutiveFailures = 5

    // Configuration
    private val maxEcgPoints = 50
    private var updateInterval = 1000L // Start with 1 second, will adjust based on success rate

    init {
        testConnection()
    }

    fun startMonitoring() {
        if (_isMonitoring.value) return

        Log.d(TAG, "Starting sensor monitoring")
        _isMonitoring.value = true
        _isLoading.value = true
        consecutiveFailures = 0

        monitoringJob = viewModelScope.launch {
            while (_isMonitoring.value) {
                try {
                    val startTime = System.currentTimeMillis()

                    fetchSensorData { data ->
                        val fetchTime = System.currentTimeMillis() - startTime
                        Log.d(TAG, "Fetch completed in ${fetchTime}ms")

                        data?.let {
                            updateSensorData(it)
                            _errorMessage.value = null
                            _isConnected.value = true
                            consecutiveFailures = 0

                            // Adjust update interval based on success
                            if (updateInterval > 500L) {
                                updateInterval = maxOf(500L, updateInterval - 100L)
                            }

                            Log.d(TAG, "Successfully received data: $it")
                        } ?: run {
                            handleConnectionFailure("Failed to fetch sensor data")
                        }
                        _isLoading.value = false
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error in monitoring loop", e)
                    handleConnectionFailure("Monitoring error: ${e.localizedMessage}")
                }

                delay(updateInterval)
            }
        }
    }

    private fun handleConnectionFailure(errorMsg: String) {
        consecutiveFailures++
        Log.w(TAG, "Connection failure #$consecutiveFailures: $errorMsg")

        _isConnected.value = false
        _isLoading.value = false
        _errorMessage.value = errorMsg

        // Gradually increase update interval on repeated failures
        if (consecutiveFailures > 3) {
            updateInterval = minOf(5000L, updateInterval + 500L)
            Log.d(TAG, "Increased update interval to ${updateInterval}ms due to failures")
        }

        // Stop monitoring if too many consecutive failures
        if (consecutiveFailures >= maxConsecutiveFailures) {
            Log.e(TAG, "Too many consecutive failures, stopping monitoring")
            _errorMessage.value = "Connection lost. Too many failures. Please check your sensor."
            stopMonitoring()
        }
    }

    fun stopMonitoring() {
        Log.d(TAG, "Stopping sensor monitoring")
        _isMonitoring.value = false
        monitoringJob?.cancel()
        monitoringJob = null
        _isLoading.value = false
        consecutiveFailures = 0
        updateInterval = 1000L // Reset to default
    }

    private fun updateSensorData(data: StatusData) {
        Log.d(TAG, "Updating sensor data: ECG=${data.ecg}, Temp=${data.temp}, Speed=${data.speed}")

        _sensorData.value = data

        // Update ECG data points for real-time graph
        val currentPoints = _ecgDataPoints.value.toMutableList()
        currentPoints.add(data.ecg)
        if (currentPoints.size > maxEcgPoints) {
            currentPoints.removeAt(0)
        }
        _ecgDataPoints.value = currentPoints

        // Add to history with validation
        if (data.isDataValid) {
            val currentHistory = _sensorHistory.value
            val updatedEntries = currentHistory.entries + data
            _sensorHistory.value = currentHistory.copy(entries = updatedEntries)
        }
    }

    fun testConnection() {
        Log.d(TAG, "Testing sensor connection")
        viewModelScope.launch {
            _isLoading.value = true
            testSensorConnection { connected ->
                Log.d(TAG, "Connection test result: $connected")
                _isConnected.value = connected
                _isLoading.value = false
                if (!connected) {
                    _errorMessage.value =
                        "Unable to connect to sensor device. Check IP address and network."
                } else {
                    _errorMessage.value = null
                    consecutiveFailures = 0
                }
            }
        }
    }

    fun retryConnection() {
        Log.d(TAG, "Retrying connection")
        clearError()
        consecutiveFailures = 0
        updateInterval = 1000L // Reset interval
        testConnection()

        // If monitoring was active, restart it
        if (_isMonitoring.value) {
            stopMonitoring()
            startMonitoring()
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearHistory() {
        Log.d(TAG, "Clearing sensor history")
        _sensorHistory.value = SensorDataHistory()
        _ecgDataPoints.value = emptyList()
    }

    fun exportHistoryData(): SensorDataHistory {
        return _sensorHistory.value.copy(endTime = System.currentTimeMillis())
    }

    // Debug function to get diagnostics
    fun getDiagnostics(onResult: (String) -> Unit) {
        getSensorDiagnostics { diagnostics ->
            val info = buildString {
                appendLine("=== SENSOR DIAGNOSTICS ===")
                appendLine("Connected: ${_isConnected.value}")
                appendLine("Monitoring: ${_isMonitoring.value}")
                appendLine("Loading: ${_isLoading.value}")
                appendLine("Consecutive Failures: $consecutiveFailures")
                appendLine("Update Interval: ${updateInterval}ms")
                appendLine("ECG Points: ${_ecgDataPoints.value.size}")
                appendLine("History Entries: ${_sensorHistory.value.entries.size}")
                appendLine("Last Error: ${_errorMessage.value}")
                appendLine("Server Response: $diagnostics")
            }
            onResult(info)
        }
    }

    // Alternative sync method for testing
    fun testSyncFetch() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val data = fetchSensorDataSync()
                Log.d(TAG, "Sync fetch result: $data")
                data?.let {
                    updateSensorData(it)
                    _errorMessage.value = null
                    _isConnected.value = true
                } ?: run {
                    _errorMessage.value = "Sync fetch failed"
                    _isConnected.value = false
                }
            } catch (e: Exception) {
                Log.e(TAG, "Sync fetch error", e)
                _errorMessage.value = "Sync error: ${e.localizedMessage}"
                _isConnected.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "ViewModel cleared, stopping monitoring")
        stopMonitoring()
    }
}
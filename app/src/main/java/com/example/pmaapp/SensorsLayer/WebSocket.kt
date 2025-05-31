package com.example.pmaapp.SensorsLayer

import okhttp3.*
import com.google.gson.Gson
import java.io.IOException
import android.util.Log
import java.util.concurrent.TimeUnit

class SensorDataClient {
    companion object {
        private const val TAG = "SensorDataClient"
        private const val BASE_URL = "http://192.168.9.213" // Replace with your ESP32 IP
        private const val TIMEOUT_SECONDS = 10L // Increased timeout
        private const val MAX_RETRIES = 3
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        // Add connection pooling settings
        .connectionPool(ConnectionPool(5, 5, TimeUnit.MINUTES))
        // Disable HTTP/2 if causing issues with ESP32
        .protocols(listOf(Protocol.HTTP_1_1))
        .build()

    private val gson = Gson()

    fun fetchSensorData(onResult: (StatusData?) -> Unit) {
        fetchSensorDataWithRetry(0, onResult)
    }

    private fun fetchSensorDataWithRetry(
        retryCount: Int, onResult: (StatusData?) -> Unit
    ) {
        val request = Request.Builder()
            .url("$BASE_URL/data")
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .addHeader("Connection", "close") // Force connection close to avoid keep-alive issues
            .addHeader("Cache-Control", "no-cache")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(
                    TAG, "Failed to fetch sensor data (attempt ${retryCount + 1})",
                    e
                )

                if (retryCount < MAX_RETRIES) {
                    Log.d(TAG, "Retrying... (${retryCount + 1}/$MAX_RETRIES)")
                    // Wait a bit before retrying
                    Thread.sleep(500)
                    fetchSensorDataWithRetry(retryCount + 1, onResult)
                } else {
                    onResult(null)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    if (!response.isSuccessful) {
                        Log.e(TAG, "HTTP Error: ${response.code} - ${response.message}")
                        response.close()
                        if (retryCount < MAX_RETRIES) {
                            fetchSensorDataWithRetry(retryCount + 1, onResult)
                        } else {
                            onResult(null)
                        }
                        return
                    }

                    val responseBody = response.body
                    if (responseBody == null) {
                        Log.e(TAG, "Response body is null")
                        response.close()
                        if (retryCount < MAX_RETRIES) {
                            fetchSensorDataWithRetry(retryCount + 1, onResult)
                        } else {
                            onResult(null)
                        }
                        return
                    }

                    // Check Content-Length header
                    val contentLength = response.header("Content-Length")?.toLongOrNull()
                    Log.d(TAG, "Content-Length: $contentLength")

                    // Use buffered reading to handle incomplete responses
                    val json = try {
                        responseBody.string()
                    } catch (e: IOException) {
                        Log.e(TAG, "Error reading response body", e)
                        response.close()
                        if (retryCount < MAX_RETRIES) {
                            fetchSensorDataWithRetry(retryCount + 1, onResult)
                        } else {
                            onResult(null)
                        }
                        return
                    }

                    Log.d(TAG, "Response JSON (length: ${json.length}): $json")

                    if (json.isBlank()) {
                        Log.e(TAG, "Empty response body")
                        response.close()
                        if (retryCount < MAX_RETRIES) {
                            fetchSensorDataWithRetry(retryCount + 1, onResult)
                        } else {
                            onResult(null)
                        }
                        return
                    }

                    // Validate JSON format before parsing
                    if (!isValidJson(json)) {
                        Log.e(TAG, "Invalid JSON format: $json")
                        response.close()
                        if (retryCount < MAX_RETRIES) {
                            fetchSensorDataWithRetry(retryCount + 1, onResult)
                        } else {
                            onResult(null)
                        }
                        return
                    }

                    val data = gson.fromJson(json, StatusData::class.java)
                    Log.d(TAG, "Parsed sensor data: $data")
                    onResult(data)

                } catch (e: Exception) {
                    Log.e(
                        TAG, "Error parsing JSON response (attempt ${retryCount + 1})",
                        e
                    )
                    if (retryCount < MAX_RETRIES) {
                        fetchSensorDataWithRetry(retryCount + 1, onResult)
                    } else {
                        onResult(null)
                    }
                } finally {
                    response.close()
                }
            }
        })
    }

    private fun isValidJson(json: String): Boolean {
        return try {
            json.trim().startsWith("{") && json.trim().endsWith("}")
        } catch (e: Exception) {
            false
        }
    }

    // Alternative method using synchronous call (for testing)
    fun fetchSensorDataSync(): StatusData? {
        return try {
            val request = Request.Builder()
                .url("$BASE_URL/data")
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Connection", "close")
                .addHeader("Cache-Control", "no-cache")
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val json = response.body?.string() ?: ""
                    Log.d(TAG, "Sync Response: $json")
                    if (json.isNotBlank() && isValidJson(json)) {
                        gson.fromJson(json, StatusData::class.java)
                    } else {
                        null
                    }
                } else {
                    Log.e(TAG, "Sync HTTP Error: ${response.code}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Sync fetch error", e)
            null
        }
    }

    fun testConnection(onResult: (Boolean) -> Unit) {
        val request = Request.Builder()
            .url("$BASE_URL/status")
            .addHeader("Connection", "close")
            .addHeader("Cache-Control", "no-cache")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Connection test failed", e)
                onResult(false)
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val isConnected = response.isSuccessful
                    Log.d(TAG, "Connection test result: $isConnected")

                    // Also test the data endpoint
                    if (isConnected) {
                        // Try to fetch actual data to ensure the endpoint works
                        response.close()
                        testDataEndpoint(onResult)
                    } else {
                        onResult(false)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Connection test error", e)
                    onResult(false)
                } finally {
                    // Simply close the response - no need to check if it's already closed
                    response.close()
                }
            }
        })
    }

    private fun testDataEndpoint(onResult: (Boolean) -> Unit) {
        val request = Request.Builder()
            .url("$BASE_URL/data")
            .addHeader("Connection", "close")
            .addHeader("Cache-Control", "no-cache")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Data endpoint test failed", e)
                onResult(false)
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val isWorking = response.isSuccessful &&
                            response.body?.string()?.isNotBlank() == true
                    Log.d(TAG, "Data endpoint test result: $isWorking")
                    onResult(isWorking)
                } catch (e: Exception) {
                    Log.e(TAG, "Data endpoint test error", e)
                    onResult(false)
                } finally {
                    response.close()
                }
            }
        })
    }

    // Method to check ESP32 status with diagnostic info
    fun getDiagnosticInfo(onResult: (String) -> Unit) {
        val request = Request.Builder()
            .url("$BASE_URL/info") // Add this endpoint to your ESP32 if available
            .addHeader("Connection", "close")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onResult("Connection failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val info = response.body?.string() ?: "No response body"
                    onResult("Status: ${response.code}, Info: $info")
                } catch (e: Exception) {
                    onResult("Error reading response: ${e.message}")
                } finally {
                    response.close()
                }
            }
        })
    }
}

// Singleton instance for easy access
private val sensorClient = SensorDataClient()

// Legacy function for backward compatibility
fun fetchSensorData(onResult: (StatusData?) -> Unit) {
    sensorClient.fetchSensorData(onResult)
}

// New function for connection testing
fun testSensorConnection(onResult: (Boolean) -> Unit) {
    sensorClient.testConnection(onResult)
}

// Additional utility functions
fun fetchSensorDataSync(): StatusData? {
    return sensorClient.fetchSensorDataSync()
}

fun getSensorDiagnostics(onResult: (String) -> Unit) {
    sensorClient.getDiagnosticInfo(onResult)
}
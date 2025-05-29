package com.example.pmaapp.SensorsLayer


import okhttp3.*
import com.google.gson.Gson
import java.io.IOException

import android.util.Log

class SensorDataClient {
    companion object {
        private const val TAG = "SensorDataClient"
        private const val BASE_URL = "http://192.168.103.213" // Replace with your ESP32 IP
        private const val TIMEOUT_SECONDS = 5L
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(TIMEOUT_SECONDS, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(TIMEOUT_SECONDS, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT_SECONDS, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    private val gson = Gson()

    fun fetchSensorData(onResult: (StatusData?) -> Unit) {
        val request = Request.Builder()
            .url("$BASE_URL/data")
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed to fetch sensor data", e)
                onResult(null)
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    response.body?.string()?.let { json ->
                        Log.d(TAG, "Response JSON: $json")

                        if (response.isSuccessful) {
                            val data = gson.fromJson(json, StatusData::class.java)
                            Log.d(TAG, "Parsed sensor data: $data")
                            onResult(data)
                        } else {
                            Log.e(TAG, "HTTP Error: ${response.code} - ${response.message}")
                            onResult(null)
                        }
                    } ?: run {
                        Log.e(TAG, "Empty response body")
                        onResult(null)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing JSON response", e)
                    onResult(null)
                } finally {
                    response.close()
                }
            }
        })
    }

    // Additional method for testing connection
    fun testConnection(onResult: (Boolean) -> Unit) {
        val request = Request.Builder()
            .url("$BASE_URL/status")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Connection test failed", e)
                onResult(false)
            }

            override fun onResponse(call: Call, response: Response) {
                val isConnected = response.isSuccessful
                Log.d(TAG, "Connection test result: $isConnected")
                onResult(isConnected)
                response.close()
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
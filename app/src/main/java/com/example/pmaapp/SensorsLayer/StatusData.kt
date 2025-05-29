package com.example.pmaapp.SensorsLayer


import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StatusData(
    @SerializedName("ecg")
    val ecg: Float = 0.0f,

    @SerializedName("pred")
    val pred: String = "Unknown",

    @SerializedName("speed")
    val speed: Float = 0.0f,

    @SerializedName("accel")
    val accel: Float = 0.0f,

    @SerializedName("collision")
    val collision: String = "N/A",

    @SerializedName("activity")
    val activity: String = "N/A",

    @SerializedName("temp")
    val temp: Float = 0.0f,

    // Additional fields for enhanced monitoring
    @SerializedName("timestamp")
    val timestamp: Long = System.currentTimeMillis(),

    @SerializedName("battery_level")
    val batteryLevel: Float? = null,

    @SerializedName("signal_strength")
    val signalStrength: Int? = null
) : Parcelable {

    // Computed properties for better data interpretation
    val isECGNormal: Boolean
        get() = pred.equals("Normal", ignoreCase = true)

    val isCollisionDetected: Boolean
        get() = collision.equals("Detected", ignoreCase = true)

    val speedInKmh: Float
        get() = speed * 3.6f // Convert m/s to km/h

    val tempInFahrenheit: Float
        get() = (temp * 9/5) + 32 // Convert Celsius to Fahrenheit

    val isDataValid: Boolean
        get() = ecg != 0.0f || speed != 0.0f || accel != 0.0f

    // Activity level classification
    val activityLevel: ActivityLevel
        get() = when {
            speed < 0.5f && accel < 1.0f -> ActivityLevel.RESTING
            speed < 2.0f && accel < 3.0f -> ActivityLevel.WALKING
            speed < 5.0f && accel < 8.0f -> ActivityLevel.JOGGING
            speed < 10.0f && accel < 15.0f -> ActivityLevel.RUNNING
            else -> ActivityLevel.SPRINTING
        }

    // Health status assessment
    val healthStatus: HealthStatus
        get() = when {
            !isECGNormal -> HealthStatus.ABNORMAL_ECG
            isCollisionDetected -> HealthStatus.COLLISION_DETECTED
            temp > 38.0f -> HealthStatus.HIGH_TEMPERATURE
            temp < 35.0f -> HealthStatus.LOW_TEMPERATURE
            else -> HealthStatus.NORMAL
        }
}

enum class ActivityLevel(val displayName: String, val description: String) {
    RESTING("Resting", "Minimal movement detected"),
    WALKING("Walking", "Light activity detected"),
    JOGGING("Jogging", "Moderate activity detected"),
    RUNNING("Running", "High activity detected"),
    SPRINTING("Sprinting", "Intense activity detected")
}

enum class HealthStatus(val displayName: String, val priority: Int) {
    NORMAL("Normal", 0),
    HIGH_TEMPERATURE("High Temperature", 1),
    LOW_TEMPERATURE("Low Temperature", 1),
    ABNORMAL_ECG("Abnormal ECG", 2),
    COLLISION_DETECTED("Collision Detected", 3)
}

// Data class for historical sensor data storage
@Parcelize
data class SensorDataHistory(
    val entries: List<StatusData> = emptyList(),
    val sessionId: String = "",
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long? = null
) : Parcelable {

    val duration: Long
        get() = (endTime ?: System.currentTimeMillis()) - startTime

    val averageHeartRate: Float
        get() = if (entries.isNotEmpty()) entries.map { it.ecg }.average().toFloat() else 0.0f

    val maxSpeed: Float
        get() = entries.maxOfOrNull { it.speed } ?: 0.0f

    val averageSpeed: Float
        get() = if (entries.isNotEmpty()) entries.map { it.speed }.average().toFloat() else 0.0f

    val totalDistance: Float
        get() = entries.sumOf { it.speed.toDouble() }.toFloat() * (duration / 1000f) // Rough estimation
}
package com.example.pmaapp.APIs

import com.example.pmaapp.database.Player
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class PlayerPredictionRepository {
    private val api = RetrofitClient.playerPredictionService

    suspend fun predictPosition(player: Player): Result<PredictPositionResponse> = withContext(Dispatchers.IO) {
        try {
            // Convert Player object to properly formatted features list as required by API
            val features = listOf(
                player.height.toFloat(),
                player.weight.toFloat(),
                player.bestPosition,
                player.weakFoot.toFloatOrNull() ?: 3.0f,
                player.skillMoves.toFloat(),
                player.attackingWorkRate,
                player.defensiveWorkRate,
                player.crossing.toFloat(),
                player.finishing.toFloat(),
                player.headingAccuracy.toFloat(),
                player.shortPassing.toFloat(),
                player.volleys.toFloat(),
                player.dribbling.toFloat(),
                player.curve.toFloat(),
                player.fkAccuracy.toFloat(),
                player.longPassing.toFloat(),
                player.ballControl.toFloat(),
                player.acceleration.toFloat(),
                player.sprintSpeed.toFloat(),
                player.agility.toFloat(),
                player.reactions.toFloat(),
                player.balance.toFloat(),
                player.shotPower.toFloat(),
                player.jumping.toFloat(),
                player.stamina.toFloat(),
                player.strength.toFloat(),
                player.longShots.toFloat(),
                player.aggression.toFloat(),
                player.interceptions.toFloat(),
                player.positioning.toFloat(),
                player.vision.toFloat(),
                player.penalties.toFloat(),
                player.composure.toFloat(),
                player.marking.toFloat(),
                player.standingTackle.toFloat(),
                player.slidingTackle.toFloat(),
                player.gkDiving.toFloat(),
                player.gkHandling.toFloat(),
                player.gkKicking.toFloat(),
                player.gkPositioning.toFloat(),
                player.gkReflexes.toFloat()
            )

            val response = api.predictPosition(PredictPositionRequest(features))

            if (response.isSuccessful && response.body() != null) {
                val responseBody = response.body()!!

                // Check if prediction is null or empty
                if (responseBody.prediction.isNullOrEmpty()) {
                    // If API returns null or empty prediction, use the player's current position or "Unknown"
                    val fixedResponse = PredictPositionResponse(
                        prediction = listOf(player.bestPosition ?: "Unknown"),
                        confidence = responseBody.confidence
                    )
                    Result.success(fixedResponse)
                } else {
                    Result.success(responseBody)
                }
            } else {
                Result.failure(Exception("Failed to predict position: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Rest of the repository methods remain unchanged

    suspend fun predictSubstitutes(players: List<Player>): Result<PredictSubsResponse> = withContext(Dispatchers.IO) {
        try {
            // Fix: Properly convert Player objects to PlayerSubstituteData
            val substituteDataList = players.map { player ->
                PlayerSubstituteData(
                    Name = player.name ?: "Unknown Player",
                    Weight = player.weight.toInt(),
                    Overall = calculateOverall(player), // Calculate overall rating
                    Positions = player.position ?: "",
                    BestPosition = player.bestPosition ?: "N/A",
                    PreferredFoot = if ((player.weakFoot?.toIntOrNull() ?: 3) > 3) "Both" else "Right", // Estimate preferred foot
                    WeakFoot = player.weakFoot.toIntOrNull() ?: 3,
                    SkillMoves = player.skillMoves,
                    AttackingWorkRate = player.attackingWorkRate,
                    DefensiveWorkRate = player.defensiveWorkRate,
                    Crossing = player.crossing,
                    Finishing = player.finishing,
                    HeadingAccuracy = player.headingAccuracy,
                    ShortPassing = player.shortPassing,
                    Volleys = player.volleys,
                    Dribbling = player.dribbling,
                    Curve = player.curve,
                    FKAccuracy = player.fkAccuracy,
                    LongPassing = player.longPassing,
                    BallControl = player.ballControl,
                    Acceleration = player.acceleration,
                    SprintSpeed = player.sprintSpeed,
                    Agility = player.agility,
                    Reactions = player.reactions,
                    Balance = player.balance,
                    ShotPower = player.shotPower,
                    Jumping = player.jumping,
                    Stamina = player.stamina,
                    Strength = player.strength,
                    LongShots = player.longShots,
                    Aggression = player.aggression,
                    Interceptions = player.interceptions,
                    Positioning = player.positioning,
                    Vision = player.vision,
                    Penalties = player.penalties,
                    Composure = player.composure,
                    Marking = player.marking,
                    StandingTackle = player.standingTackle,
                    SlidingTackle = player.slidingTackle
                )
            }

            val response = api.predictSubstitutes(substituteDataList)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to predict substitutes: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun predictRating(player: Player): Result<PredictRatingResponse> = withContext(Dispatchers.IO) {
        try {
            // Calculate totals using proper logic
            val passingTotal = (player.shortPassing + player.longPassing + player.vision + player.crossing + player.fkAccuracy + player.curve) / 6
            val dribblingTotal = (player.dribbling + player.ballControl + player.agility + player.balance + player.reactions) / 5
            val shootingTotal = (player.finishing + player.shotPower + player.longShots + player.volleys + player.penalties + player.positioning) / 6
            val defendingTotal = (player.interceptions + player.marking + player.standingTackle + player.slidingTackle + player.headingAccuracy) / 5
            val physicalityTotal = (player.jumping + player.stamina + player.strength + player.aggression) / 4

            // Create a map that matches exactly the structure expected by the API
            val featuresMap = mapOf(
                "features" to listOf(
                    mapOf(
                        "Reactions" to player.reactions,
                        "ValueEUR" to calculateEstimatedValue(player),
                        "PassingTotal" to passingTotal,
                        "Composure" to player.composure,
                        "DribblingTotal" to dribblingTotal,
                        "PhysicalityTotal" to physicalityTotal,
                        "ShotPower" to player.shotPower,
                        "Vision" to player.vision,
                        "LongPassing" to player.longPassing,
                        "ShootingTotal" to shootingTotal,
                        "ShortPassing" to player.shortPassing,
                        "Strength" to player.strength,
                        "BallControl" to player.ballControl,
                        "Aggression" to player.aggression,
                        "Stamina" to player.stamina,
                        "SkillMoves" to player.skillMoves,
                        "Curve" to player.curve,
                        "DefendingTotal" to defendingTotal,
                        "LongShots" to player.longShots,
                        "FKAccuracy" to player.fkAccuracy,
                        "Crossing" to player.crossing,
                        "Volleys" to player.volleys
                    )
                )
            )

            // Use a raw API call with the map to ensure exact format
            val requestJson = RetrofitClient.gson.toJson(featuresMap)

            // Updated syntax for creating MediaType and RequestBody
            val mediaType = "application/json".toMediaTypeOrNull()
            val requestBody = requestJson.toRequestBody(mediaType)

            val rawResponse = api.predictRatingRaw(requestBody)
            if (rawResponse.isSuccessful && rawResponse.body() != null) {
                val responseBody = rawResponse.body()!!.string()
                val predictRatingResponse = RetrofitClient.gson.fromJson(responseBody, PredictRatingResponse::class.java)
                Result.success(predictRatingResponse)
            } else {
                Result.failure(Exception("Failed to predict rating: ${rawResponse.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun predictValue(player: Player): Result<PredictValueResponse> = withContext(Dispatchers.IO) {
        try {
            val valueFeatures = ValueFeatures(
                Age = player.age,
                Height = player.height.toInt(),
                Weight = player.weight.toInt(),
                Overall = calculateOverall(player), // Calculate overall rating
                Potential = calculatePotential(player), // Calculate potential
                WageEUR = calculateEstimatedWage(player), // Calculate estimated wage
                ReleaseClause = calculateReleaseClause(player) // Calculate release clause
            )

            val response = api.predictValue(PredictValueRequest(listOf(valueFeatures)))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to predict value: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun predictWage(player: Player): Result<PredictWageResponse> = withContext(Dispatchers.IO) {
        try {
            val wageFeatures = WageFeatures(
                Age = player.age,
                Height = player.height.toInt(),
                Weight = player.weight.toInt(),
                Overall = calculateOverall(player),
                Potential = calculatePotential(player),
                ValueEUR = calculateEstimatedValue(player),
                ReleaseClause = calculateReleaseClause(player)
            )

            val response = api.predictWage(PredictWageRequest(listOf(wageFeatures)))
            if (response.isSuccessful && response.body() != null) {
                val responseBody = response.body()!!

                // Handle the case where predictedWage is 0 but prediction array exists
                if (responseBody.predictedWage == 0 && responseBody.prediction == null) {
                    // Try to parse the raw JSON response to extract prediction array if needed
                    val rawResponse = api.predictWageRaw(
                        RetrofitClient.gson.toJson(PredictWageRequest(listOf(wageFeatures)))
                            .toRequestBody("application/json".toMediaTypeOrNull())
                    )

                    if (rawResponse.isSuccessful) {
                        val rawBody = rawResponse.body()?.string()
                        if (rawBody != null) {
                            try {
                                val fixedResponse = RetrofitClient.gson.fromJson(rawBody, PredictWageResponse::class.java)
                                Result.success(fixedResponse)
                            } catch (e: Exception) {
                                Result.success(responseBody) // Fallback to original response
                            }
                        } else {
                            Result.success(responseBody)
                        }
                    } else {
                        Result.success(responseBody)
                    }
                } else {
                    Result.success(responseBody)
                }
            } else {
                Result.failure(Exception("Failed to predict wage: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Helper methods to calculate player metrics
    private fun calculateOverall(player: Player): Int {
        // Simple calculation based on player's attributes
        val technicalAvg = (player.crossing + player.finishing + player.headingAccuracy +
                player.shortPassing + player.volleys + player.dribbling +
                player.curve + player.fkAccuracy + player.longPassing +
                player.ballControl) / 10

        val physicalAvg = (player.acceleration + player.sprintSpeed + player.agility +
                player.reactions + player.balance + player.shotPower +
                player.jumping + player.stamina + player.strength +
                player.longShots) / 10

        val mentalAvg = (player.aggression + player.interceptions + player.positioning +
                player.vision + player.penalties + player.composure) / 6

        val defendingAvg = (player.marking + player.standingTackle + player.slidingTackle) / 3

        // Weight the averages based on position type
        val isDefender = player.bestPosition in listOf("CB", "LB", "RB", "LWB", "RWB")
        val isMidfielder = player.bestPosition in listOf("CDM", "CM", "CAM", "LM", "RM")
        val isAttacker = player.bestPosition in listOf("ST", "CF", "LW", "RW")
        val isGoalkeeper = player.bestPosition == "GK"

        return when {
            isGoalkeeper -> (player.gkDiving + player.gkHandling + player.gkKicking +
                    player.gkPositioning + player.gkReflexes) / 5
            isDefender -> (defendingAvg * 0.4 + physicalAvg * 0.3 + technicalAvg * 0.2 + mentalAvg * 0.1).toInt()
            isMidfielder -> (technicalAvg * 0.4 + mentalAvg * 0.3 + physicalAvg * 0.2 + defendingAvg * 0.1).toInt()
            isAttacker -> (technicalAvg * 0.5 + physicalAvg * 0.3 + mentalAvg * 0.2).toInt()
            else -> (technicalAvg * 0.4 + physicalAvg * 0.3 + mentalAvg * 0.2 + defendingAvg * 0.1).toInt()
        }.coerceIn(50, 99) // Ensure value is between 50 and 99
    }

    private fun calculatePotential(player: Player): Int {
        // Simple potential calculation based on age and overall
        val overall = calculateOverall(player)
        val ageFactor = when {
            player.age < 21 -> 10 // Younger players have more potential
            player.age < 25 -> 5
            player.age < 30 -> 2
            else -> 0 // Older players have less potential to grow
        }

        return (overall + ageFactor).coerceIn(overall, 99) // Potential should be at least equal to overall
    }

    private fun calculateEstimatedValue(player: Player): Long {
        // Simple value calculation
        val overall = calculateOverall(player)
        val potential = calculatePotential(player)
        val ageValueFactor = when {
            player.age < 23 -> 1.5 // Younger players are worth more
            player.age < 28 -> 1.3 // Prime age
            player.age < 32 -> 1.0
            player.age < 35 -> 0.7
            else -> 0.4 // Older players are worth less
        }

        // Base value calculation
        val baseValue = when {
            overall >= 90 -> 80_000_000L
            overall >= 85 -> 50_000_000L
            overall >= 80 -> 25_000_000L
            overall >= 75 -> 10_000_000L
            overall >= 70 -> 5_000_000L
            else -> 1_000_000L
        }

        // Potential multiplier
        val potentialMultiplier = 1 + ((potential - overall) * 0.05)

        return (baseValue * ageValueFactor * potentialMultiplier).toLong()
    }

    private fun calculateEstimatedWage(player: Player): Int {
        // Simple wage calculation based on overall rating and age
        val overall = calculateOverall(player)
        val ageWageFactor = when {
            player.age < 23 -> 0.8 // Younger players earn less
            player.age < 28 -> 1.0 // Prime age
            player.age < 32 -> 0.9
            else -> 0.7 // Older players typically earn less
        }

        // Base weekly wage calculation (in Euros)
        val baseWeeklyWage = when {
            overall >= 90 -> 250_000
            overall >= 85 -> 150_000
            overall >= 80 -> 80_000
            overall >= 75 -> 40_000
            overall >= 70 -> 20_000
            else -> 5_000
        }

        return (baseWeeklyWage * ageWageFactor).toInt()
    }

    private fun calculateReleaseClause(player: Player): Long {
        // Release clause is typically higher than market value
        val value = calculateEstimatedValue(player)
        return (value * 1.5).toLong()
    }
}
package com.example.pmaapp.APIs

import com.example.pmaapp.database.Player
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlayerPredictionRepository {
    private val api = RetrofitClient.playerPredictionService

    suspend fun predictPosition(player: Player): Result<PredictPositionResponse> = withContext(Dispatchers.IO) {
        try {
            // Convert Player object to list of features as required by API
            val features = listOf(
                player.height, player.weight, player.bestPosition, player.weakFoot, player.skillMoves,
                player.attackingWorkRate, player.defensiveWorkRate, player.crossing, player.finishing,
                player.headingAccuracy, player.shortPassing, player.volleys, player.dribbling, player.curve,
                player.fkAccuracy, player.longPassing, player.ballControl, player.acceleration, player.sprintSpeed,
                player.agility, player.reactions, player.balance, player.shotPower, player.jumping, player.stamina,
                player.strength, player.longShots, player.aggression, player.interceptions, player.positioning,
                player.vision, player.penalties, player.composure, player.marking, player.standingTackle,
                player.slidingTackle, player.gkDiving, player.gkHandling, player.gkKicking, player.gkPositioning,
                player.gkReflexes
            )

            val response = api.predictPosition(PredictPositionRequest(features))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to predict position: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun predictSubstitutes(players: List<Player>): Result<PredictSubsResponse> = withContext(Dispatchers.IO) {
        try {
            // Convert Player objects to PlayerSubstituteData
            val substituteDataList = players.map { player ->
                PlayerSubstituteData(
                    Name = player.name,
                    Weight = player.weight.toInt(),
                    Overall = 80, // This would need to be calculated or stored somewhere
                    Positions = player.position,
                    BestPosition = player.bestPosition,
                    PreferredFoot = "Right", // This would need to be added to your Player model
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
            // Calculate totals (these would need proper calculation logic)
            val passingTotal = (player.shortPassing + player.longPassing + player.vision + player.crossing + player.fkAccuracy + player.curve) / 6
            val dribblingTotal = (player.dribbling + player.ballControl + player.agility + player.balance + player.reactions) / 5
            val shootingTotal = (player.finishing + player.shotPower + player.longShots + player.volleys + player.penalties + player.positioning) / 6
            val defendingTotal = (player.interceptions + player.marking + player.standingTackle + player.slidingTackle + player.headingAccuracy) / 5
            val physicalityTotal = (player.jumping + player.stamina + player.strength + player.aggression) / 4

            val ratingFeatures = RatingFeatures(
                Reactions = player.reactions,
                ValueEUR = 50000000, // This would need to be calculated or stored
                PassingTotal = passingTotal,
                Composure = player.composure,
                DribblingTotal = dribblingTotal,
                PhysicalityTotal = physicalityTotal,
                ShotPower = player.shotPower,
                Vision = player.vision,
                LongPassing = player.longPassing,
                ShootingTotal = shootingTotal,
                ShortPassing = player.shortPassing,
                Strength = player.strength,
                BallControl = player.ballControl,
                Aggression = player.aggression,
                Stamina = player.stamina,
                SkillMoves = player.skillMoves,
                Curve = player.curve,
                DefendingTotal = defendingTotal,
                LongShots = player.longShots,
                FKAccuracy = player.fkAccuracy,
                Crossing = player.crossing,
                Volleys = player.volleys
            )

            val response = api.predictRating(PredictRatingRequest(listOf(ratingFeatures)))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to predict rating: ${response.errorBody()?.string()}"))
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
                Overall = 80, // This would need to be calculated or stored
                Potential = 85, // This would need to be added to your Player model
                WageEUR = 100000, // This would need to be calculated or stored
                ReleaseClause = 0 // This would need to be added to your Player model
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
                Overall = 80, // This would need to be calculated or stored
                Potential = 85, // This would need to be added to your Player model
                ValueEUR = 50000000, // This would need to be calculated or stored
                ReleaseClause = 0 // This would need to be added to your Player model
            )

            val response = api.predictWage(PredictWageRequest(listOf(wageFeatures)))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to predict wage: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
package com.example.pmaapp.APIs

// Position Prediction
data class PredictPositionRequest(
    val features: List<Any> // Mix of Float and String values
)

data class PredictPositionResponse(
    // Changed to match actual API response format
    val prediction: List<String>?,
    val confidence: Float = 0.85f
) {
    // Add a convenience property to maintain compatibility with existing code
    val predictedPosition: String?
        get() = prediction?.firstOrNull()
}

// Substitutes Prediction - Player data for input
data class PlayerSubstituteData(
    val Name: String,
    val Weight: Int,
    val Overall: Int,
    val Positions: String,
    val BestPosition: String,
    val PreferredFoot: String,
    val WeakFoot: Int,
    val SkillMoves: Int,
    val AttackingWorkRate: String,
    val DefensiveWorkRate: String,
    val Crossing: Int,
    val Finishing: Int,
    val HeadingAccuracy: Int,
    val ShortPassing: Int,
    val Volleys: Int,
    val Dribbling: Int,
    val Curve: Int,
    val FKAccuracy: Int,
    val LongPassing: Int,
    val BallControl: Int,
    val Acceleration: Int,
    val SprintSpeed: Int,
    val Agility: Int,
    val Reactions: Int,
    val Balance: Int,
    val ShotPower: Int,
    val Jumping: Int,
    val Stamina: Int,
    val Strength: Int,
    val LongShots: Int,
    val Aggression: Int,
    val Interceptions: Int,
    val Positioning: Int,
    val Vision: Int,
    val Penalties: Int,
    val Composure: Int,
    val Marking: Int,
    val StandingTackle: Int,
    val SlidingTackle: Int,
    val Height: Int
)

// Similar player data from API response
data class SimilarPlayer(
    val Name: String,
    val Similarity: Double
)

data class PredictSubsRequest(
    val players: List<PlayerSubstituteData>
)

data class PredictSubsResponse(
    val similar_players: List<SimilarPlayer>? = null,
    val status: String? = null
)

// Legacy data classes for backwards compatibility (can be removed if not used elsewhere)
data class SubstituteRecommendation(
    val name: String,
    val score: Float,
    val compatibility: String?
)

// Rating Prediction
data class RatingFeatures(
    val Reactions: Int,
    val ValueEUR: Long,
    val PassingTotal: Int,
    val Composure: Int,
    val DribblingTotal: Int,
    val PhysicalityTotal: Int,
    val ShotPower: Int,
    val Vision: Int,
    val LongPassing: Int,
    val ShootingTotal: Int,
    val ShortPassing: Int,
    val Strength: Int,
    val BallControl: Int,
    val Aggression: Int,
    val Stamina: Int,
    val SkillMoves: Int,
    val Curve: Int,
    val DefendingTotal: Int,
    val LongShots: Int,
    val FKAccuracy: Int,
    val Crossing: Int,
    val Volleys: Int
)

data class PredictRatingRequest(
    val features: List<RatingFeatures>
)

data class PredictRatingResponse(
    val prediction: List<Double>? = null,
    val predictedRating: Float = 0f
) {
    // Computed property to get the rating from either field
    val finalRating: Float
        get() = if (prediction?.isNotEmpty() == true) {
            prediction[0].toFloat()
        } else {
            predictedRating
        }
}

// Value Prediction
data class ValueFeatures(
    val Age: Int,
    val Height: Int,
    val Weight: Int,
    val Overall: Int,
    val Potential: Int,
    val WageEUR: Int,
    val ReleaseClause: Long
)

data class PredictValueRequest(
    val features: List<ValueFeatures>
)

data class PredictValueResponse(
    val prediction: List<Double>? = null,
    val predictedValue: Long? = null
)

// Wage Prediction
data class WageFeatures(
    val Age: Int,
    val Height: Int,
    val Weight: Int,
    val Overall: Int,
    val Potential: Int,
    val ValueEUR: Long,
    val ReleaseClause: Long
)

data class PredictWageRequest(
    val features: List<WageFeatures>
)

data class PredictWageResponse(
    val prediction: List<Double>? = null,
    val predictedWage: Int = 0
) {
    // Computed property to get the wage from either field
    val finalWage: Int
        get() = if (prediction?.isNotEmpty() == true) {
            prediction[0].toInt()
        } else {
            predictedWage
        }
}
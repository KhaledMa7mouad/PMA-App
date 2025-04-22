package com.example.pmaapp.APIs

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PlayerPredictionService {
    @POST("predictPos")
    suspend fun predictPosition(@Body request: PredictPositionRequest): Response<PredictPositionResponse>

    @POST("predictSubs")
    suspend fun predictSubstitutes(@Body request: List<PlayerSubstituteData>): Response<PredictSubsResponse>

    @POST("predictRating")
    suspend fun predictRating(@Body request: PredictRatingRequest): Response<PredictRatingResponse>

    @POST("predictValue")
    suspend fun predictValue(@Body request: PredictValueRequest): Response<PredictValueResponse>

    @POST("predictWage")
    suspend fun predictWage(@Body request: PredictWageRequest): Response<PredictWageResponse>
}
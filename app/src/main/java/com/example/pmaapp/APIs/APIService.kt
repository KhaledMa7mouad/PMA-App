package com.example.pmaapp.APIs

import retrofit2.Response
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST

interface PlayerPredictionService {
    @POST("predictPos")
    suspend fun predictPosition(@Body request: PredictPositionRequest): Response<PredictPositionResponse>

    @POST("predictSubs")
    suspend fun predictSubstitutes(@Body request: PredictSubsRequest): Response<PredictSubsResponse>

    @POST("predictRating")
    suspend fun predictRatingRaw(@Body requestBody: RequestBody): Response<ResponseBody>

    @POST("predict-wage")
    suspend fun predictWageRaw(@Body requestBody: RequestBody): Response<ResponseBody>


    @POST("predictValue")
    suspend fun predictValue(@Body request: PredictValueRequest): Response<PredictValueResponse>

    @POST("predictWage")
    suspend fun predictWage(@Body request: PredictWageRequest): Response<PredictWageResponse>
}
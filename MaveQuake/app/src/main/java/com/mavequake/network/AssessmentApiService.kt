package com.mavequake.network

import com.mavequake.network.dto.AssessmentDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

/**
 * This interface IS the API contract between the Android app and the FastAPI backend.
 * Whoever builds the backend should implement these exact three endpoints -
 * everything else (the model, the database) can change freely underneath them.
 */
interface AssessmentApiService {

    @Multipart
    @POST("api/v1/assessments")
    suspend fun submitAssessment(
        @Part nadir: MultipartBody.Part,
        @Part oblique60: MultipartBody.Part,
        @Part oblique45: MultipartBody.Part,
        @Part facade: MultipartBody.Part,
        @Part("label") label: RequestBody,
        @Part("neighborhood") neighborhood: RequestBody
    ): AssessmentDto

    @GET("api/v1/assessments")
    suspend fun listAssessments(): List<AssessmentDto>

    @GET("api/v1/assessments/{id}")
    suspend fun getAssessment(@Path("id") id: String): AssessmentDto
}

package com.mavequake.network.dto

/**
 * Shape returned by the backend for a single assessment.
 * Matches: { id, vulnerability_class, confidence, indicators: [...], gradcam_urls: {...} }
 * Field names use snake_case with @SerializedName because that's what FastAPI/Pydantic emits by default.
 */
data class AssessmentDto(
    val id: String,
    val label: String,
    val neighborhood: String,
    @com.google.gson.annotations.SerializedName("vulnerability_class")
    val vulnerabilityClass: String,
    val confidence: Float,
    val indicators: List<String> = emptyList(),
    @com.google.gson.annotations.SerializedName("gradcam_urls")
    val gradcamUrls: Map<String, String> = emptyMap(),
    @com.google.gson.annotations.SerializedName("created_at")
    val createdAt: String? = null
)

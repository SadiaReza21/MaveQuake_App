package com.mavequake.repository

import com.mavequake.data.BuildingRecord
import com.mavequake.data.VulnerabilityClass
import com.mavequake.network.AssessmentApiService
import com.mavequake.network.dto.AssessmentDto
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

/**
 * Real implementation - wire this in once the backend (see mavequake-backend/) is
 * running and reachable. Swap it in via AppContainer; nothing else changes.
 */
class RemoteAssessmentRepository(
    private val api: AssessmentApiService
) : AssessmentRepository {

    override suspend fun submitAssessment(
        nadir: File,
        oblique60: File,
        oblique45: File,
        facade: File,
        label: String,
        neighborhood: String
    ): Result<BuildingRecord> = runCatching {
        val dto = api.submitAssessment(
            nadir = nadir.toPart("nadir"),
            oblique60 = oblique60.toPart("oblique60"),
            oblique45 = oblique45.toPart("oblique45"),
            facade = facade.toPart("facade"),
            label = label.toRequestBody("text/plain".toMediaTypeOrNull()),
            neighborhood = neighborhood.toRequestBody("text/plain".toMediaTypeOrNull())
        )
        dto.toBuildingRecord()
    }

    override suspend fun getHistory(): Result<List<BuildingRecord>> = runCatching {
        api.listAssessments().map { it.toBuildingRecord() }
    }

    override suspend fun getAssessment(id: String): Result<BuildingRecord> = runCatching {
        api.getAssessment(id).toBuildingRecord()
    }

    private fun File.toPart(fieldName: String): MultipartBody.Part {
        val body = asRequestBody("image/jpeg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(fieldName, name, body)
    }

    private fun AssessmentDto.toBuildingRecord() = BuildingRecord(
        id = id,
        label = label,
        neighborhood = neighborhood,
        vulnerability = runCatching { VulnerabilityClass.valueOf(vulnerabilityClass.uppercase()) }
            .getOrDefault(VulnerabilityClass.MODERATE),
        confidence = confidence,
        indicators = indicators
    )
}

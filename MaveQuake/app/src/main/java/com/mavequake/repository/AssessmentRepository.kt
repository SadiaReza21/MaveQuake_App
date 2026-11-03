package com.mavequake.repository

import com.mavequake.data.BuildingRecord
import java.io.File

/**
 * Everything above this line (ViewModels, screens) only ever talks to this interface.
 * Swapping MockAssessmentRepository for RemoteAssessmentRepository in AppContainer
 * is the ONLY change needed when the backend/model is ready - no screen code changes.
 */
interface AssessmentRepository {
    suspend fun submitAssessment(
        nadir: File,
        oblique60: File,
        oblique45: File,
        facade: File,
        label: String,
        neighborhood: String
    ): Result<BuildingRecord>

    suspend fun getHistory(): Result<List<BuildingRecord>>

    suspend fun getAssessment(id: String): Result<BuildingRecord>
}

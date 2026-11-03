package com.mavequake.repository

import com.mavequake.data.BuildingRecord
import com.mavequake.data.MockData
import com.mavequake.data.VulnerabilityClass
import kotlinx.coroutines.delay
import java.io.File
import java.util.UUID

/**
 * Fake backend. Use this while the model/API isn't ready yet -
 * it simulates network latency and returns believable results so every
 * screen can be built and demoed end-to-end right now.
 */
class MockAssessmentRepository : AssessmentRepository {

    private val submitted = mutableListOf<BuildingRecord>().apply { addAll(MockData.records) }

    override suspend fun submitAssessment(
        nadir: File,
        oblique60: File,
        oblique45: File,
        facade: File,
        label: String,
        neighborhood: String
    ): Result<BuildingRecord> {
        delay(1500) // pretend inference is running
        val record = BuildingRecord(
            id = UUID.randomUUID().toString(),
            label = label.ifBlank { "Untitled Building" },
            neighborhood = neighborhood.ifBlank { "Unknown" },
            vulnerability = VulnerabilityClass.values().random(),
            confidence = (0.65f..0.95f).let { (60..95).random() / 100f },
            indicators = listOf("Soft-story present", "Visible facade cracks").shuffled().take((0..2).random())
        )
        submitted.add(0, record)
        return Result.success(record)
    }

    override suspend fun getHistory(): Result<List<BuildingRecord>> {
        delay(400)
        return Result.success(submitted.toList())
    }

    override suspend fun getAssessment(id: String): Result<BuildingRecord> {
        delay(300)
        val match = submitted.find { it.id == id }
        return if (match != null) Result.success(match)
        else Result.failure(NoSuchElementException("No assessment with id $id"))
    }
}

package com.mavequake.data

/**
 * Placeholder data model for a single building assessment.
 * Once the MAVE-Quake model is wired up, `vulnerability` and `confidence`
 * will come from real inference output instead of mock data.
 */
data class BuildingRecord(
    val id: String,
    val label: String,
    val neighborhood: String,
    val vulnerability: VulnerabilityClass,
    val confidence: Float,
    val indicators: List<String> = emptyList()
)

enum class VulnerabilityClass {
    LOW, MODERATE, HIGH, CRITICAL
}

/** Temporary in-memory sample data so the UI has something to render. */
object MockData {
    val records = listOf(
        BuildingRecord(
            id = "b1",
            label = "Building A - Purana Paltan",
            neighborhood = "Purana Paltan",
            vulnerability = VulnerabilityClass.HIGH,
            confidence = 0.82f,
            indicators = listOf("Soft-story present", "Visible facade cracks")
        ),
        BuildingRecord(
            id = "b2",
            label = "Building B - Mirpur",
            neighborhood = "Mirpur",
            vulnerability = VulnerabilityClass.MODERATE,
            confidence = 0.71f,
            indicators = listOf("Plan irregularity")
        ),
        BuildingRecord(
            id = "b3",
            label = "Building C - Uttara",
            neighborhood = "Uttara",
            vulnerability = VulnerabilityClass.LOW,
            confidence = 0.90f,
            indicators = emptyList()
        )
    )
}

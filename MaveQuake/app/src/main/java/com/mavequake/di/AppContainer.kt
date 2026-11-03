package com.mavequake.di

import com.mavequake.network.RetrofitClient
import com.mavequake.repository.AssessmentRepository
import com.mavequake.repository.MockAssessmentRepository
import com.mavequake.repository.RemoteAssessmentRepository

/**
 * Deliberately simple (no Hilt/Dagger) - a capstone team with a tight timeline
 * doesn't need a DI framework for one repository. Flip USE_MOCK_BACKEND to false
 * once the FastAPI backend + model are up and RetrofitClient.BASE_URL points to them.
 */
object AppContainer {

    private const val USE_MOCK_BACKEND = true

    val assessmentRepository: AssessmentRepository by lazy {
        if (USE_MOCK_BACKEND) {
            MockAssessmentRepository()
        } else {
            RemoteAssessmentRepository(RetrofitClient.apiService)
        }
    }
}

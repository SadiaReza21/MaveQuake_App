
package com.mavequake.ui.screens

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

// Replace this with your Web application OAuth Client ID.
private const val WEB_CLIENT_ID = "898064194837-iv9phtq3033ke3b0ukvarh7g5tf8vld2.apps.googleusercontent.com"

// Information returned after Google account selection.
data class GoogleUser(
    val name: String,
    val email: String,
    val idToken: String
)

suspend fun signInWithGoogle(context: Context): GoogleUser? {
    val credentialManager = CredentialManager.create(context)

    // false allows accounts that have not previously signed in to MaveQuake.
    val googleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(WEB_CLIENT_ID)
        .build()

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    // Let authentication errors reach the calling screen for handling.
    val result = credentialManager.getCredential(
        context = context,
        request = request
    )

    val credential = result.credential

    if (
        credential is CustomCredential &&
        credential.type ==
        GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
    ) {
        val googleCredential =
            GoogleIdTokenCredential.createFrom(credential.data)

        return GoogleUser(
            name = googleCredential.displayName ?: "User",
            email = googleCredential.id,
            idToken = googleCredential.idToken
        )
    }

    return null
}

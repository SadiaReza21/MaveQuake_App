package com.mavequake.ui.screens

import android.content.Context
import android.util.Log
import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.mavequake.ui.components.LanguageSwitch
import com.mavequake.ui.components.ThemeSwitch
import com.mavequake.ui.i18n.LocalStrings
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.security.MessageDigest

/** Tiny local account store. Compose state makes the app switch screens automatically. */
object SimpleAuth {
    private const val FILE = "bhobonsathi_users"

    var userName by mutableStateOf<String?>(null)
        private set

    private fun prefs(c: Context) = c.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    private fun hash(email: String, pw: String): String =
        MessageDigest.getInstance("SHA-256").digest("$email:$pw".toByteArray())
            .joinToString("") { "%02x".format(it) }

    fun init(c: Context) { userName = prefs(c).getString("session_name", null) }

    /** false = email already used */
    fun signup(c: Context, name: String, email: String, pw: String): Boolean {
        val e = email.trim().lowercase()
        val p = prefs(c)
        if (p.contains("pw_$e")) return false
        p.edit().putString("pw_$e", hash(e, pw)).putString("name_$e", name.trim())
            .putString("session_name", name.trim()).apply()
        userName = name.trim()
        return true
    }

    /** false = wrong email or password */
    fun login(c: Context, email: String, pw: String): Boolean {
        val e = email.trim().lowercase()
        val p = prefs(c)
        if (p.getString("pw_$e", null) != hash(e, pw)) return false
        val name = p.getString("name_$e", "") ?: ""
        p.edit().putString("session_name", name).apply()
        userName = name
        return true
    }

    /** Called after a successful Google sign-in. */
    fun loginWithGoogle(c: Context, name: String) {
        prefs(c).edit().putString("session_name", name).apply()
        userName = name
    }

    fun logout(c: Context) {
        prefs(c).edit().remove("session_name").apply()
        userName = null
    }
}

@Composable
fun AuthScreen() {
    val s = LocalStrings.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    fun t(en: String, bn: String) = if (s.isBangla) bn else en

    var isSignup by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var googleLoading by remember { mutableStateOf(false) }

    fun submit() {
        val cleanEmail = email.trim()
        if (isSignup && name.isBlank()) {
            error = t("Please enter your name", "অনুগ্রহ করে আপনার নাম লিখুন"); return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            error = t("Enter a valid email address", "সঠিক ইমেইল ঠিকানা দিন"); return
        }
        if (password.length < 6) {
            error = t("Password must be at least 6 characters", "পাসওয়ার্ড কমপক্ষে ৬ অক্ষরের হতে হবে"); return
        }
        if (isSignup) {
            if (!SimpleAuth.signup(context, name, cleanEmail, password)) {
                error = t("This email is already registered", "এই ইমেইল আগেই ব্যবহৃত হয়েছে")
            }
        } else {
            if (!SimpleAuth.login(context, cleanEmail, password)) {
                error = t("Incorrect email or password", "ইমেইল বা পাসওয়ার্ড ভুল")
            }
        }
    }

    fun googleSignIn() {
        if (googleLoading) return
        googleLoading = true
        error = null
        scope.launch {
            try {
                val user = signInWithGoogle(context)
                if (user != null) {
                    SimpleAuth.loginWithGoogle(context, user.name)
                } else {
                    error = t("Google sign-in failed", "গুগল সাইন-ইন ব্যর্থ হয়েছে")
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: GetCredentialCancellationException) {
                // User closed the account sheet: show nothing.
            } catch (e: NoCredentialException) {
                Log.e("GoogleSignIn", "No credential: ${e.message}", e)
                error = t(
                    "No Google account found on this device. Add one in Settings and try again.",
                    "এই ডিভাইসে কোনো গুগল অ্যাকাউন্ট পাওয়া যায়নি। সেটিংসে অ্যাকাউন্ট যোগ করে আবার চেষ্টা করুন।"
                )
            } catch (e: GetCredentialException) {
                Log.e("GoogleSignIn", "type=${e.type} msg=${e.message}", e)
                error = t("Google sign-in failed", "গুগল সাইন-ইন ব্যর্থ হয়েছে")
            } catch (e: Exception) {
                Log.e("GoogleSignIn", "Unexpected: ${e.message}", e)
                error = t("Google sign-in failed", "গুগল সাইন-ইন ব্যর্থ হয়েছে")
            } finally {
                googleLoading = false
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().statusBarsPadding().imePadding()
            .verticalScroll(rememberScrollState()).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) { LanguageSwitch() }
        Spacer(Modifier.height(8.dp))
        ThemeSwitch()
        Spacer(Modifier.height(24.dp))

        Text(s.appName, style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
        Text(
            s.fullName,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(8.dp))
        Text(
            if (isSignup) t("Create your account", "নতুন অ্যাকাউন্ট খুলুন")
            else t("Log in to continue", "চালিয়ে যেতে লগইন করুন"),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(24.dp))

        if (isSignup) {
            OutlinedTextField(
                value = name, onValueChange = { name = it; error = null },
                label = { Text(t("Full name", "পুরো নাম")) },
                singleLine = true, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
        }
        OutlinedTextField(
            value = email, onValueChange = { email = it; error = null },
            label = { Text(t("Email", "ইমেইল")) },
            singleLine = true, modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = password, onValueChange = { password = it; error = null },
            label = { Text(t("Password", "পাসওয়ার্ড")) },
            singleLine = true, visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        error?.let {
            Text(
                it, color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { submit() },
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) { Text(if (isSignup) t("Sign up", "সাইন আপ") else t("Log in", "লগইন")) }

        TextButton(onClick = { isSignup = !isSignup; error = null }) {
            Text(
                if (isSignup) t("Already have an account? Log in", "অ্যাকাউন্ট আছে? লগইন করুন")
                else t("New here? Sign up", "নতুন ব্যবহারকারী? সাইন আপ করুন")
            )
        }

        // ---- Google sign-in ----
        Spacer(Modifier.height(4.dp))
        Text(
            t("or", "অথবা"),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            onClick = { googleSignIn() },
            enabled = !googleLoading,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            if (googleLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text(t("Continue with Google", "গুগল দিয়ে চালিয়ে যান"))
            }
        }
    }
}
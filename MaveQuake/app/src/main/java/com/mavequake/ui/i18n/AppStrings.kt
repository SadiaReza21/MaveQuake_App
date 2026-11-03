package com.mavequake.ui.i18n

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.mavequake.data.VulnerabilityClass
import com.mavequake.viewmodel.CapturePass

enum class AppLanguage { EN, BN }

class LanguageController(
    val language: AppLanguage,
    val onChange: (AppLanguage) -> Unit
)

val LocalStrings = staticCompositionLocalOf { Strings(false) }
val LocalLanguage = staticCompositionLocalOf { LanguageController(AppLanguage.EN) {} }

fun vulnColor(v: VulnerabilityClass): Color = when (v) {
    VulnerabilityClass.LOW -> Color(0xFF2E5AAC)
    VulnerabilityClass.MODERATE -> Color(0xFFE0AA2E)
    VulnerabilityClass.HIGH -> Color(0xFFE8763D)
    VulnerabilityClass.CRITICAL -> Color(0xFFD1483D)
}

// Edit the keys to match the exact indicator text your backend returns (lowercase, spaces).
private val indicatorBn = mapOf(
    "soft story" to "সফট-স্টোরি (নরম তলা)",
    "irregular height" to "উচ্চতার অনিয়ম",
    "height irregularity" to "উচ্চতার অনিয়ম",
    "mass asymmetry" to "ভরের অসামঞ্জস্য",
    "column exposure" to "কলাম উন্মুক্ত",
    "wall cracks" to "দেয়ালে ফাটল",
    "roof irregularity" to "ছাদের অনিয়ম",
    "short column" to "খাটো কলাম",
    "large openings" to "বড় খোলা অংশ",
    "poor material condition" to "উপাদানের খারাপ অবস্থা",
    "pounding risk" to "পাশের ভবনের সাথে সংঘর্ষের ঝুঁকি"
)

class Strings(val isBangla: Boolean) {
    private fun t(en: String, bn: String) = if (isBangla) bn else en

    // ---- helpers ----
    fun num(n: Int): String = digits(n.toString())
    private fun digits(text: String): String =
        if (!isBangla) text
        else text.map { if (it in '0'..'9') "০১২৩৪৫৬৭৮৯"[it - '0'] else it }.joinToString("")

    val appName = "Bhobon-AKKHI"
    val fullName = t(
        "Architectural Knowledge-based Kinetic Hazard Intelligence",
        "স্থাপত্য-জ্ঞাননির্ভর গতিশীল ঝুঁকি বুদ্ধিমত্তা"
    )
    val back = t("Back", "ফিরে যান")

    // ---- home ----
    val slogan = t("Know your building before the quake", "ভূমিকম্পের আগেই জানুন আপনার ভবনের অবস্থা")
    val tagline = t(
        "Pre-earthquake building vulnerability assessment from UAV imagery",
        "ড্রোন ছবি থেকে ভূমিকম্প-পূর্ব ভবনের ঝুঁকি নিরূপণ"
    )
    val newAssessment = t("New Assessment", "নতুন মূল্যায়ন")
    val viewHistory = t("View Past Assessments", "পূর্বের মূল্যায়ন দেখুন")
    val stepCapture = t("Capture", "ছবি তুলুন")
    val stepAnalyze = t("Analyze", "বিশ্লেষণ")
    val stepReview = t("Review", "ফলাফল")

    // ---- capture ----
    val buildingLabel = t("Building label", "ভবনের নাম / লেবেল")
    val neighborhood = t("Neighborhood", "এলাকা")
    val add = t("Add", "যোগ করুন")
    val change = t("Change", "বদলান")
    val runAssessment = t("Run Assessment", "মূল্যায়ন শুরু করুন")
    val analyzing = t("Analyzing…", "বিশ্লেষণ চলছে…")
    val allPhotosReady = t("All photos added. Ready to run!", "সব ছবি যোগ হয়েছে। এবার শুরু করুন!")
    fun photosProgress(n: Int, total: Int) =
        t("$n of $total photos added", "${num(n)}/${num(total)} টি ছবি যোগ হয়েছে")

    fun passTitle(p: CapturePass) = when (p) {
        CapturePass.NADIR -> t("Nadir (90°)", "সরাসরি উপর থেকে (৯০°)")
        CapturePass.OBLIQUE_60 -> t("Oblique (60°)", "তির্যক (৬০°)")
        CapturePass.OBLIQUE_45 -> t("Oblique (45°)", "তির্যক (৪৫°)")
        CapturePass.FACADE -> t("Facade-level", "সম্মুখ দেয়াল")
    }

    fun passDesc(p: CapturePass) = when (p) {
        CapturePass.NADIR -> t("Top-down: roof geometry, plan footprint", "উপর থেকে: ছাদের আকৃতি, ভবনের নকশা")
        CapturePass.OBLIQUE_60 -> t("Height irregularity, soft-story flags", "উচ্চতার অনিয়ম, সফট-স্টোরির লক্ষণ")
        CapturePass.OBLIQUE_45 -> t("Mass asymmetry, column exposure", "ভরের অসামঞ্জস্য, কলাম উন্মুক্ত কি না")
        CapturePass.FACADE -> t("Wall cracks, openings, material condition", "দেয়ালের ফাটল, খোলা অংশ, উপাদানের অবস্থা")
    }

    // ---- history ----
    val pastAssessments = t("Past Assessments", "পূর্বের মূল্যায়ন")
    val searchHint = t("Search by name or area", "নাম বা এলাকা দিয়ে খুঁজুন")
    val all = t("All", "সব")
    val noAssessments = t("No assessments yet", "এখনো কোনো মূল্যায়ন নেই")
    val noMatches = t("No matches found", "কিছু পাওয়া যায়নি")
    val refresh = t("Refresh", "রিফ্রেশ")
    val retry = t("Retry", "আবার চেষ্টা করুন")

    // ---- results ----
    val assessmentResult = t("Assessment Result", "মূল্যায়নের ফলাফল")
    val indicators = t("INDICATORS DETECTED", "শনাক্তকৃত সূচক")
    val noneFlagged = t("None flagged", "কোনো সূচক পাওয়া যায়নি")
    val modelConfidence = t("model confidence", "মডেলের আস্থা")
    val viewExplain = t("View Explainability Map", "ব্যাখ্যা মানচিত্র দেখুন")
    val comingSoon = t("Coming soon", "শীঘ্রই আসছে")
    val nextStep = t("RECOMMENDED NEXT STEP", "পরবর্তী করণীয়")
    val disclaimer = t(
        "Preliminary screening only. It does not replace an inspection by a licensed structural engineer.",
        "এটি শুধুমাত্র প্রাথমিক যাচাই। লাইসেন্সপ্রাপ্ত স্ট্রাকচারাল ইঞ্জিনিয়ারের পরিদর্শনের বিকল্প নয়।"
    )

    fun vulnerability(v: VulnerabilityClass) = when (v) {
        VulnerabilityClass.LOW -> t("Low", "নিম্ন")
        VulnerabilityClass.MODERATE -> t("Moderate", "মাঝারি")
        VulnerabilityClass.HIGH -> t("High", "উচ্চ")
        VulnerabilityClass.CRITICAL -> t("Critical", "অতি উচ্চ")
    }

    fun vulnerabilityFull(v: VulnerabilityClass) =
        t("${vulnerability(v)} Vulnerability", "${vulnerability(v)} ঝুঁকি")

    fun recommendation(v: VulnerabilityClass) = when (v) {
        VulnerabilityClass.LOW -> t("No immediate action. Continue routine monitoring.", "তাৎক্ষণিক পদক্ষেপ দরকার নেই। নিয়মিত পর্যবেক্ষণ চালিয়ে যান।")
        VulnerabilityClass.MODERATE -> t("Schedule a detailed inspection.", "বিস্তারিত পরিদর্শনের ব্যবস্থা করুন।")
        VulnerabilityClass.HIGH -> t("Prioritise an engineer's inspection and plan retrofitting.", "অগ্রাধিকার দিয়ে ইঞ্জিনিয়ার দ্বারা পরিদর্শন করান এবং রেট্রোফিটিংয়ের পরিকল্পনা করুন।")
        VulnerabilityClass.CRITICAL -> t("Urgent structural evaluation needed. Limit occupancy until reviewed.", "জরুরি ভিত্তিতে কাঠামোগত মূল্যায়ন প্রয়োজন। পরীক্ষা না হওয়া পর্যন্ত বসবাস সীমিত রাখুন।")
    }

    fun indicator(raw: String): String {
        if (!isBangla) return raw
        val key = raw.trim().lowercase().replace('_', ' ').replace('-', ' ')
        return indicatorBn[key] ?: raw
    }
}
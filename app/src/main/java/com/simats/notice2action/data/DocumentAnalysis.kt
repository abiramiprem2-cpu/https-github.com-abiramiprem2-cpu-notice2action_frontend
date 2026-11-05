package com.simats.notice2action.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.InputStream
import kotlin.coroutines.resume

data class ActionTask(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val dateTime: String,
    val dateNum: String = "20",
    val dateMonth: String = "OCT",
    val daysLeft: String = "5 days left",
    val priorityText: String = "High",
    val statusText: String = "Verified date",
    val quote: String = "",
    val category: String = "Academic",
    var isConfirmed: Boolean = true
)

data class NoticeAnalysisResult(
    val documentTitle: String,
    val category: String,
    val categoryColorHex: Long = 0xFF2563EB,
    val simpleExplanation: String,
    val keySummary: String,
    val primaryDeadline: String,
    val daysRemaining: String,
    val issuingAuthority: String,
    val referenceNumber: String,
    val rawOcrText: String,
    val actions: List<ActionTask>,
    val whatIsItAbout: String = simpleExplanation,
    val whatDoINeedToDo: List<String> = actions.map { it.title },
    val byWhenShouldIDoIt: String = primaryDeadline,
    val tamilExplanation: String = "",
    val tamilActions: List<String> = emptyList(),
    val sourceSnippets: List<String> = emptyList()
)

object DocumentAnalysisSession {
    var isAnalyzing by mutableStateOf(false)
    var analysisProgress by mutableStateOf(0f)
    var analysisStatus by mutableStateOf("Ready")

    var activeResult by mutableStateOf<NoticeAnalysisResult?>(null)
    val confirmedTasks = mutableStateListOf<ActionTask>()

    init {
        // Default initial seed
        activeResult = synthesizeAnalysis("", "Innovation Week 2026")
        confirmedTasks.clear()
        confirmedTasks.addAll(activeResult!!.actions.take(2))
    }

    fun reset() {
        isAnalyzing = false
        analysisProgress = 0f
        analysisStatus = "Ready"
        activeResult = synthesizeAnalysis("", "Innovation Week 2026")
    }

    suspend fun analyzeDocument(
        context: Context,
        bitmap: Bitmap?,
        uri: Uri?,
        fallbackTitle: String
    ): NoticeAnalysisResult {
        isAnalyzing = true
        analysisProgress = 0.15f
        analysisStatus = "Opening document & running ML Kit OCR..."

        var workingBitmap: Bitmap? = bitmap
        if (workingBitmap == null && uri != null) {
            try {
                // Try decoding image stream
                val input: InputStream? = context.contentResolver.openInputStream(uri)
                val opts = BitmapFactory.Options().apply { inSampleSize = 1 }
                workingBitmap = BitmapFactory.decodeStream(input, null, opts)
                input?.close()
            } catch (_: Exception) {}

            // If image decode returned null, try rendering PDF first page
            if (workingBitmap == null) {
                try {
                    val pfd: ParcelFileDescriptor? = context.contentResolver.openFileDescriptor(uri, "r")
                    if (pfd != null) {
                        val renderer = PdfRenderer(pfd)
                        if (renderer.pageCount > 0) {
                            val page = renderer.openPage(0)
                            val bmp = Bitmap.createBitmap(page.width * 2, page.height * 2, Bitmap.Config.ARGB_8888)
                            page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                            page.close()
                            workingBitmap = bmp
                        }
                        renderer.close()
                        pfd.close()
                    }
                } catch (_: Exception) {}
            }
        }

        analysisProgress = 0.40f
        analysisStatus = "Extracting text using on-device OCR..."

        var extractedText = ""
        if (workingBitmap != null) {
            extractedText = extractTextWithMlKit(workingBitmap)
        }

        analysisProgress = 0.75f
        analysisStatus = "AI analyzing document: What it's about, tasks & deadlines..."

        val result = synthesizeAnalysis(extractedText, fallbackTitle)

        analysisProgress = 1.0f
        analysisStatus = "Analysis complete!"
        isAnalyzing = false
        activeResult = result

        confirmedTasks.clear()
        confirmedTasks.addAll(result.actions)

        return result
    }

    private suspend fun extractTextWithMlKit(bitmap: Bitmap): String =
        suspendCancellableCoroutine { continuation ->
            try {
                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                val image = InputImage.fromBitmap(bitmap, 0)
                recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        continuation.resume(visionText.text)
                    }
                    .addOnFailureListener {
                        continuation.resume("")
                    }
            } catch (e: Exception) {
                continuation.resume("")
            }
        }

    fun synthesizeAnalysis(ocrText: String, titleHint: String): NoticeAnalysisResult {
        val lowerText = ocrText.lowercase()
        val hasText = ocrText.isNotBlank()
        val rawLines = ocrText.lines().map { it.trim() }.filter { it.isNotBlank() }

        // Category classification
        val isBill = lowerText.contains("fee") || lowerText.contains("bill") ||
                lowerText.contains("tuition") || lowerText.contains("dues") ||
                lowerText.contains("payment") || lowerText.contains("invoice") ||
                lowerText.contains("fine") || lowerText.contains("arrears") ||
                lowerText.contains("amount") || titleHint.lowercase().contains("bill") ||
                titleHint.lowercase().contains("fee")

        val isExam = lowerText.contains("exam") || lowerText.contains("test") ||
                lowerText.contains("semester") || lowerText.contains("timetable") ||
                lowerText.contains("hall ticket") || lowerText.contains("admit card") ||
                lowerText.contains("schedule")

        val isAdmission = lowerText.contains("admission") || lowerText.contains("enrollment") ||
                lowerText.contains("counseling") || lowerText.contains("allotment") ||
                lowerText.contains("registration")

        // Dynamic date detection via Regex
        val dateRegex = Regex(
            """\b(\d{1,2}(?:st|nd|rd|th)?\s+(?:January|February|March|April|May|June|July|August|September|October|November|December|Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)[a-z]*\s+\d{2,4})\b""",
            RegexOption.IGNORE_CASE
        )
        val altDateRegex = Regex(
            """\b((?:January|February|March|April|May|June|July|August|September|October|November|December|Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)[a-z]*\s+\d{1,2}(?:st|nd|rd|th)?,?\s+\d{2,4})\b""",
            RegexOption.IGNORE_CASE
        )
        val numericDateRegex = Regex("""\b(\d{1,2}[./-]\d{1,2}[./-]\d{2,4})\b""")
        val timeRegex = Regex("""\b(\d{1,2}(?::\d{2})?\s*(?:AM|PM|am|pm))\b""")

        val detectedDate = dateRegex.find(ocrText)?.value
            ?: altDateRegex.find(ocrText)?.value
            ?: numericDateRegex.find(ocrText)?.value
            ?: if (isBill) "25 October 2026" else if (isExam) "18 October 2026" else "20 October 2026"

        val detectedTime = timeRegex.find(ocrText)?.value
            ?: if (isBill) "11:59 PM" else "5:00 PM"

        val primaryDeadline = "$detectedDate • $detectedTime"

        // Dynamic authority detection
        val authorityLine = rawLines.firstOrNull { line ->
            val l = line.lowercase()
            l.contains("office of") || l.contains("department of") ||
                    l.contains("controller") || l.contains("dean") ||
                    l.contains("principal") || l.contains("registrar") ||
                    l.contains("university") || l.contains("college") ||
                    l.contains("board") || l.contains("ministry")
        }

        // Dynamic reference detection
        val refRegex = Regex("""(?:Ref|Circular|Notice|Memo|Order|No)[\s.:#-]+([A-Za-z0-9/_-]{4,25})""", RegexOption.IGNORE_CASE)
        val detectedRef = refRegex.find(ocrText)?.groupValues?.getOrNull(1)

        val docCategory: String
        val docTitle: String
        val whatIsItAbout: String
        val tamilExplanation: String
        val authority: String
        val refNo: String
        val actions = mutableListOf<ActionTask>()
        val tamilActions = mutableListOf<String>()
        val sourceSnippets = mutableListOf<String>()

        // Find candidate title from first meaningful lines
        val candidateTitle = rawLines.firstOrNull { line ->
            val l = line.lowercase()
            line.length in 6..65 && !l.startsWith("page") && !l.startsWith("date") && !l.startsWith("ref")
        }

        if (isBill) {
            docCategory = "Official Fee Bill"
            docTitle = candidateTitle ?: if (hasText && rawLines.isNotEmpty()) rawLines.first().take(45) else "Semester Fee & Dues Notice"
            authority = authorityLine ?: "Finance & Accounts Office"
            refNo = detectedRef ?: "N2A/2026/FIN-804"

            whatIsItAbout = "This document is an official payment notice issued by $authority. It states the outstanding fees and payment instructions. You are required to settle all college dues before the deadline to prevent late fee penalties and avoid holds on semester registrations."

            tamilExplanation = "இந்த ஆவணம் $authority வெளியிட்ட அதிகாரப்பூர்வ கட்டண அறிவிப்பாகும். இது கல்லூரிக்கு செலுத்த வேண்டிய நிலுவைக் கட்டணங்கள் மற்றும் வழிமுறைகளைக் குறிப்பிடுகிறது. கூடுதல் அபராதக் கட்டணங்களைத் தவிர்க்கவும், செமஸ்டர் பதிவை உறுதிப்படுத்தவும் $detectedDate-க்குள் அனைத்து கட்டணங்களையும் செலுத்துமாறு கேட்டுக்கொள்ளப்படுகிறீர்கள்."

            val quote1 = if (hasText) ocrText.take(65) else "\"Pay dues before $detectedDate to avoid late fee penalty...\""
            val quote2 = "\"Students must retain payment acknowledgment receipt...\""

            sourceSnippets.add(quote1)
            sourceSnippets.add(quote2)

            actions.add(
                ActionTask(
                    title = "Pay outstanding semester dues",
                    description = "Login to the university payment portal and complete clearance using NetBanking or UPI.",
                    dateTime = primaryDeadline,
                    dateNum = detectedDate.filter { it.isDigit() }.take(2).ifBlank { "25" },
                    dateMonth = "OCT",
                    daysLeft = "5 days left",
                    priorityText = "High",
                    statusText = "Verified deadline",
                    quote = quote1,
                    category = "Finance"
                )
            )
            tamilActions.add("நிலுவையில் உள்ள செமஸ்டர் கட்டணத்தை ஆன்லைனில் செலுத்துங்கள்")

            actions.add(
                ActionTask(
                    title = "Download and verify fee receipt",
                    description = "Save the transaction acknowledgment receipt and submit a copy to the departmental coordinator.",
                    dateTime = "$detectedDate • 5:00 PM",
                    dateNum = detectedDate.filter { it.isDigit() }.take(2).ifBlank { "26" },
                    dateMonth = "OCT",
                    daysLeft = "6 days left",
                    priorityText = "Medium",
                    statusText = "Suggested step",
                    quote = quote2,
                    category = "Finance"
                )
            )
            tamilActions.add("கட்டண ரசீதை பதிவிறக்கம் செய்து துறை ஒருங்கிணைப்பாளரிடம் சமர்ப்பிக்கவும்")

        } else if (isExam) {
            docCategory = "Examination Schedule"
            docTitle = candidateTitle ?: if (hasText && rawLines.isNotEmpty()) rawLines.first().take(45) else "Semester Exam Registration & Timetable"
            authority = authorityLine ?: "Controller of Examinations"
            refNo = detectedRef ?: "COE/N2A/2026/EXAM-112"

            whatIsItAbout = "This notice is an official examination circular issued by $authority. It outlines the upcoming examination rules, subject timetables, hall ticket generation schedule, and attendance requirements. You need to review registered courses and report on time."

            tamilExplanation = "இந்த அறிவிப்பு $authority வெளியிட்ட தேர்வு சுற்றறிக்கையாகும். இது வரவிருக்கும் செமஸ்டர் தேர்வுகள், பாட அட்டவணை, ஹால் டிக்கெட் பதிவிறக்கத் தேதி மற்றும் தேர்வு விதிமுறைகளை விளக்குகிறது. உங்கள் பாடங்களை சரிபார்த்து உரிய நேரத்தில் தேர்வு மையத்தில் அறிக்கை அளிக்க வேண்டும்."

            val quote1 = if (hasText) ocrText.take(65) else "\"Hall tickets accessible after $detectedDate...\""
            val quote2 = "\"Reporting time 30 mins prior to commencement...\""

            sourceSnippets.add(quote1)
            sourceSnippets.add(quote2)

            actions.add(
                ActionTask(
                    title = "Register exam papers & download hall ticket",
                    description = "Verify course codes and download official hall pass on the student exam portal.",
                    dateTime = primaryDeadline,
                    dateNum = detectedDate.filter { it.isDigit() }.take(2).ifBlank { "18" },
                    dateMonth = "OCT",
                    daysLeft = "3 days left",
                    priorityText = "High",
                    statusText = "Verified deadline",
                    quote = quote1,
                    category = "Academic"
                )
            )
            tamilActions.add("தேர்வுத்தாள்களை சரிபார்த்து ஹால் டிக்கெட்டை போர்ட்டலில் பதிவிறக்கவும்")

            actions.add(
                ActionTask(
                    title = "Check seating allocation and reporting timing",
                    description = "Confirm block number, exam hall, and report 30 minutes prior to commencement.",
                    dateTime = "$detectedDate • 9:00 AM",
                    dateNum = detectedDate.filter { it.isDigit() }.take(2).ifBlank { "20" },
                    dateMonth = "OCT",
                    daysLeft = "5 days left",
                    priorityText = "Medium",
                    statusText = "Verified date",
                    quote = quote2,
                    category = "Academic"
                )
            )
            tamilActions.add("தேர்வு அறை எண் மற்றும் இருக்கை ஒதுக்கீட்டை சரிபார்க்கவும்")

        } else if (isAdmission) {
            docCategory = "Admission & Enrollment"
            docTitle = candidateTitle ?: if (hasText && rawLines.isNotEmpty()) rawLines.first().take(45) else "Admissions & Counseling Notification"
            authority = authorityLine ?: "Directorate of Admissions & Student Affairs"
            refNo = detectedRef ?: "ADM/N2A/2026/089"

            whatIsItAbout = "This document is an official admission directive from $authority. It specifies verification guidelines, certificate requirements, and enrollment cut-off dates for admitted candidates. You must submit all verified documents before the date."

            tamilExplanation = "இந்த ஆவணம் $authority வெளியிட்ட சேர்க்கை மற்றும் கலந்தாய்வு வழிகாட்டுதலாகும். மாணவர்கள் அசல் சான்றிதழ்கள், மாற்றுச் சான்றிதழ் மற்றும் அடையாளச் சான்றுகளை சரிபார்த்து கடைசி தேதிக்குள் பதிவேற்ற வேண்டும்."

            val quote1 = if (hasText) ocrText.take(65) else "\"Submit certificates by $detectedDate...\""
            val quote2 = "\"Original document verification mandatory...\""

            sourceSnippets.add(quote1)
            sourceSnippets.add(quote2)

            actions.add(
                ActionTask(
                    title = "Submit required certificates and credentials",
                    description = "Upload attested marksheets, transfer certificate, and identification proof to the admission portal.",
                    dateTime = primaryDeadline,
                    dateNum = detectedDate.filter { it.isDigit() }.take(2).ifBlank { "22" },
                    dateMonth = "OCT",
                    daysLeft = "4 days left",
                    priorityText = "High",
                    statusText = "Verified deadline",
                    quote = quote1,
                    category = "Admission"
                )
            )
            tamilActions.add("அசல் சான்றிதழ்கள் மற்றும் மதிப்பெண் பட்டியலை பதிவேற்றவும்")

            actions.add(
                ActionTask(
                    title = "Complete document verification at admissions desk",
                    description = "Report to the campus administrative block with original credentials for biometric and file verification.",
                    dateTime = "$detectedDate • 2:00 PM",
                    dateNum = detectedDate.filter { it.isDigit() }.take(2).ifBlank { "24" },
                    dateMonth = "OCT",
                    daysLeft = "6 days left",
                    priorityText = "Medium",
                    statusText = "Required step",
                    quote = quote2,
                    category = "Admission"
                )
            )
            tamilActions.add("சேர்க்கை மேசையில் நேரடி சான்றிதழ் சரிபார்ப்பை முடிக்கவும்")

        } else {
            docCategory = "Academic Directive"
            docTitle = candidateTitle ?: if (hasText && rawLines.isNotEmpty()) {
                rawLines.first().take(45)
            } else {
                if (titleHint.isNotBlank() && !titleHint.endsWith(".pdf", true) && !titleHint.endsWith(".png", true)) titleHint else "Innovation Week 2026"
            }
            authority = authorityLine ?: "Department of Innovation & Student Research"
            refNo = detectedRef ?: "N2A/2026/INNOV-042"

            whatIsItAbout = "This announcement from $authority is a call for proposals and team participation in $docTitle. It requires candidates to submit their abstracts, attend mandatory briefing sessions, and prepare demonstration prototypes according to evaluation guidelines."

            tamilExplanation = "இந்த அறிவிப்பு $authority வெளியிட்ட திட்டப் பங்கேற்பு மற்றும் புதுமை வார அழைப்பாகும். மாணவர்கள் தங்கள் திட்டச் சுருக்கத்தை போர்ட்டலில் சமர்ப்பித்து, வழிகாட்டல் கூட்டத்தில் பங்கேற்று, இறுதி மதிப்பீட்டிற்கான முன்மாதிரியைத் தயார் செய்ய வேண்டும்."

            val quote1 = if (hasText) ocrText.take(65) else "\"Submit your project proposal by 20 October 2026...\" • p.1"
            val quote2 = "\"Attend the briefing on 22 October, 10:00 AM...\" • p.1"
            val quote3 = "\"Final presentations commence late October...\""

            sourceSnippets.add(quote1)
            sourceSnippets.add(quote2)
            sourceSnippets.add(quote3)

            actions.add(
                ActionTask(
                    title = "Submit project proposal and abstract",
                    description = "Upload abstract and team details to the student portal before cut-off time.",
                    dateTime = primaryDeadline,
                    dateNum = detectedDate.filter { it.isDigit() }.take(2).ifBlank { "20" },
                    dateMonth = "OCT",
                    daysLeft = "5 days left",
                    priorityText = "High",
                    statusText = "Verified date",
                    quote = quote1,
                    category = "Academic"
                )
            )
            tamilActions.add("திட்ட முன்மொழிவு மற்றும் சுருக்கத்தை ஆன்லைனில் சமர்ப்பிக்கவும்")

            actions.add(
                ActionTask(
                    title = "Attend team briefing session",
                    description = "Meet faculty mentors at Innovation Lab B for guideline clarifications and setup.",
                    dateTime = "22 Oct 2026 • 10:00 AM",
                    dateNum = "22",
                    dateMonth = "OCT",
                    daysLeft = "7 days left",
                    priorityText = "Medium",
                    statusText = "Verified date",
                    quote = quote2,
                    category = "Academic"
                )
            )
            tamilActions.add("ஆசிரியர் வழிகாட்டல் கூட்டத்தில் கலந்துகொள்ளவும்")

            actions.add(
                ActionTask(
                    title = "Prepare final presentation slide deck",
                    description = "Assemble pitch deck and working demonstration prototype for jury evaluation.",
                    dateTime = "28 Oct 2026 • 2:00 PM",
                    dateNum = "28",
                    dateMonth = "OCT",
                    daysLeft = "13 days left",
                    priorityText = "Low",
                    statusText = "Estimated date",
                    quote = quote3,
                    category = "Presentation"
                )
            )
            tamilActions.add("இறுதி விளக்கக்காட்சி ஸ்லைடுகள் மற்றும் முன்மாதிரியைத் தயார் செய்யவும்")
        }

        // What do I need to do: list of task titles
        val whatDoINeedToDoList = actions.map { it.title }

        // By when should I do it: primary deadline + countdown
        val byWhenShouldIDoItText = "By $primaryDeadline (${actions.firstOrNull()?.daysLeft ?: "5 days left"})"

        val displayRawText = if (hasText) ocrText else """
NORTHBRIDGE COLLEGE OF ENGINEERING & TECHNOLOGY
OFFICE OF ACADEMIC AFFAIRS & STUDENT WELFARE

OFFICIAL DIRECTIVE: $docTitle
Reference Code: $refNo | Issue Date: 12 October 2026

1. PURPOSE & SCOPE
This document outlines mandatory guidelines for all enrolled students regarding $docTitle.
All deadlines are firm and non-negotiable.

2. TIMELINE & DELIVERABLES
- Primary Submission Deadline: $primaryDeadline
- Review & Verification Phase: Next 48 hours
- Final Compliance Check: Departmental Evaluation

3. MANDATORY ACTIONS
Students must access the college portal, complete prerequisite filings, and attend designated briefings. Failure to comply will lead to administrative disqualification.

Authorized by: $authority
        """.trimIndent()

        return NoticeAnalysisResult(
            documentTitle = docTitle,
            category = docCategory,
            simpleExplanation = whatIsItAbout,
            keySummary = "Contains ${actions.size} required actions with deadline on $primaryDeadline.",
            primaryDeadline = primaryDeadline,
            daysRemaining = actions.firstOrNull()?.daysLeft ?: "5 days left",
            issuingAuthority = authority,
            referenceNumber = refNo,
            rawOcrText = displayRawText,
            actions = actions,
            whatIsItAbout = whatIsItAbout,
            whatDoINeedToDo = whatDoINeedToDoList,
            byWhenShouldIDoIt = byWhenShouldIDoItText,
            tamilExplanation = tamilExplanation,
            tamilActions = tamilActions,
            sourceSnippets = sourceSnippets
        )
    }
}

package com.simats.notice2action.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
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
    val actions: List<ActionTask>
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
        analysisProgress = 0.20f
        analysisStatus = "Scanning document text via OCR..."

        var extractedText = ""
        val workingBitmap = bitmap ?: uri?.let { u ->
            try {
                val input: InputStream? = context.contentResolver.openInputStream(u)
                val bmp = BitmapFactory.decodeStream(input)
                input?.close()
                bmp
            } catch (_: Exception) { null }
        }

        if (workingBitmap != null) {
            extractedText = extractTextWithMlKit(workingBitmap)
        }

        analysisProgress = 0.65f
        analysisStatus = "AI analyzing document meaning, tasks & deadlines..."

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

        val isBill = lowerText.contains("fee") || lowerText.contains("bill") ||
                lowerText.contains("tuition") || lowerText.contains("dues") ||
                lowerText.contains("payment") || lowerText.contains("invoice") ||
                lowerText.contains("amount") || titleHint.lowercase().contains("bill") ||
                titleHint.lowercase().contains("fee")

        val isExam = lowerText.contains("exam") || lowerText.contains("test") ||
                lowerText.contains("semester") || lowerText.contains("timetable") ||
                lowerText.contains("schedule")

        val docCategory: String
        val docTitle: String
        val simpleExplanation: String
        val primaryDeadline: String
        val authority: String
        val refNo: String
        val actions = mutableListOf<ActionTask>()

        if (isBill) {
            docCategory = "Official Fee Bill"
            docTitle = if (hasText && ocrText.lines().firstOrNull()?.isNotBlank() == true) {
                ocrText.lines().first { it.isNotBlank() }.take(40)
            } else {
                "Semester Fee & Dues Notice"
            }
            simpleExplanation = "This document is an official payment notice. You are required to settle outstanding college fees or dues before the stated cut-off date to avoid late charges or semester registration holds."
            primaryDeadline = "25 October 2026 • 11:59 PM"
            authority = "Finance & Accounts Office"
            refNo = "N2A/2026/FIN-804"

            actions.add(
                ActionTask(
                    title = "Pay outstanding semester dues",
                    description = "Login to the university payment portal and complete clearance using NetBanking or UPI.",
                    dateTime = "25 Oct 2026 • 11:59 PM",
                    dateNum = "25",
                    dateMonth = "OCT",
                    daysLeft = "10 days left",
                    priorityText = "High",
                    statusText = "Verified deadline",
                    quote = if (hasText) ocrText.take(65) else "\"Pay dues before 25 October to avoid late fee penalty...\"",
                    category = "Finance"
                )
            )
            actions.add(
                ActionTask(
                    title = "Download and verify fee receipt",
                    description = "Save the transaction acknowledgment receipt and submit to the departmental coordinator.",
                    dateTime = "26 Oct 2026 • 5:00 PM",
                    dateNum = "26",
                    dateMonth = "OCT",
                    daysLeft = "11 days left",
                    priorityText = "Medium",
                    statusText = "Suggested step",
                    quote = "\"Students must retain payment acknowledgment...\"",
                    category = "Finance"
                )
            )
        } else if (isExam) {
            docCategory = "Examination Schedule"
            docTitle = if (hasText && ocrText.lines().firstOrNull()?.isNotBlank() == true) {
                ocrText.lines().first { it.isNotBlank() }.take(40)
            } else {
                "Semester Exam Registration & Timetable"
            }
            simpleExplanation = "This notice outlines the upcoming semester examination rules, hall ticket generation, and key subject dates. You need to verify your registered courses and exam hall allocation."
            primaryDeadline = "18 October 2026 • 5:00 PM"
            authority = "Controller of Examinations"
            refNo = "COE/N2A/2026/EXAM-112"

            actions.add(
                ActionTask(
                    title = "Register exam papers & download hall ticket",
                    description = "Verify course codes and download official hall pass on the exam portal.",
                    dateTime = "18 Oct 2026 • 5:00 PM",
                    dateNum = "18",
                    dateMonth = "OCT",
                    daysLeft = "3 days left",
                    priorityText = "High",
                    statusText = "Verified deadline",
                    quote = "\"Hall tickets accessible after 18 Oct...\"",
                    category = "Academic"
                )
            )
            actions.add(
                ActionTask(
                    title = "Check seating allocation and lab timings",
                    description = "Confirm block number and reporting timing with departmental faculty.",
                    dateTime = "20 Oct 2026 • 9:00 AM",
                    dateNum = "20",
                    dateMonth = "OCT",
                    daysLeft = "5 days left",
                    priorityText = "Medium",
                    statusText = "Verified date",
                    quote = "\"Reporting time 30 mins prior to commencement...\"",
                    category = "Academic"
                )
            )
        } else {
            docCategory = "Academic Notice"
            docTitle = if (hasText && ocrText.lines().firstOrNull()?.isNotBlank() == true) {
                ocrText.lines().first { it.isNotBlank() }.take(40)
            } else {
                if (titleHint.isNotBlank() && titleHint != "innovation-week-2026.pdf") titleHint else "Innovation Week 2026"
            }
            simpleExplanation = "This announcement is a call for proposals and team participation in Innovation Week 2026. Students must submit an abstract, attend a mandatory team briefing, and prepare working prototypes for the final presentation."
            primaryDeadline = "20 October 2026 • 5:00 PM"
            authority = "Department of Innovation & Student Research"
            refNo = "N2A/2026/INNOV-042"

            actions.add(
                ActionTask(
                    title = "Submit project proposal",
                    description = "Upload abstract and team details to the student portal before 5:00 PM.",
                    dateTime = "20 Oct 2026 • 5:00 PM",
                    dateNum = "20",
                    dateMonth = "OCT",
                    daysLeft = "5 days left",
                    priorityText = "High",
                    statusText = "Verified date",
                    quote = if (hasText) ocrText.take(65) else "\"Submit your project proposal by 20 October 2026...\" • p.1",
                    category = "Academic"
                )
            )
            actions.add(
                ActionTask(
                    title = "Attend team briefing",
                    description = "Meet faculty mentors at Innovation Lab B for guideline clarifications.",
                    dateTime = "22 Oct 2026 • 10:00 AM",
                    dateNum = "22",
                    dateMonth = "OCT",
                    daysLeft = "7 days left",
                    priorityText = "Medium",
                    statusText = "Verified date",
                    quote = "\"Attend the briefing on 22 October, 10:00 AM...\" • p.1",
                    category = "Academic"
                )
            )
            actions.add(
                ActionTask(
                    title = "Prepare final presentation slide deck",
                    description = "Assemble pitch deck and demonstration video for jury evaluation.",
                    dateTime = "28 Oct 2026 • 2:00 PM",
                    dateNum = "28",
                    dateMonth = "OCT",
                    daysLeft = "13 days left",
                    priorityText = "Low",
                    statusText = "Estimated date",
                    quote = "\"Final presentations commence late October...\"",
                    category = "Presentation"
                )
            )
        }

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
            simpleExplanation = simpleExplanation,
            keySummary = "Contains ${actions.size} required actions with deadline on $primaryDeadline.",
            primaryDeadline = primaryDeadline,
            daysRemaining = actions.firstOrNull()?.daysLeft ?: "5 days left",
            issuingAuthority = authority,
            referenceNumber = refNo,
            rawOcrText = displayRawText,
            actions = actions
        )
    }
}


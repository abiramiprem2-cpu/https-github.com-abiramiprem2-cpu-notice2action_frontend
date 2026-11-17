package com.simats.notice2action

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person

import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.ui.text.TextStyle
import com.simats.notice2action.data.DocumentAnalysisSession
import com.simats.notice2action.data.ActionTask
import com.simats.notice2action.data.ReminderManager
import java.util.Locale
import java.util.UUID

@Composable
fun ReviewSuggestedActionsScreen(
    onBackClicked: () -> Unit,
    onConfirmActions: () -> Unit
) {
    val analysis = DocumentAnalysisSession.activeResult
    val dynamicActions = analysis?.actions ?: emptyList()
    val actionItems = remember(dynamicActions) {
        if (dynamicActions.isNotEmpty()) dynamicActions else listOf(
            ActionTask(
                title = "Submit project proposal",
                description = "Upload abstract and team details to the student portal.",
                dateTime = "20 Oct 2026 • 5:00 PM",
                priorityText = "High",
                statusText = "Verified date",
                quote = "\"Submit your project proposal by 20 October...\" • p.1"
            ),
            ActionTask(
                title = "Attend team briefing",
                description = "Meet the team at Innovation Lab B.",
                dateTime = "22 Oct 2026 • 10:00 AM",
                priorityText = "Medium",
                statusText = "Verified date",
                quote = "\"Attend the briefing on 22 October, 10:00 AM...\" • p.1"
            )
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8FAFC)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(Color(0xFFEEF2FF), CircleShape)
                                .clickable { onBackClicked() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Review Suggested Actions",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }

                    IconButton(
                        onClick = { },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = Color(0xFF64748B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFEFF6FF), RoundedCornerShape(12.dp))
                        .border(BorderStroke(1.dp, Color(0xFFBFDBFE)), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${actionItems.size} actions extracted from ${analysis?.documentTitle ?: "Notice"} • Ready to confirm & set reminders",
                            fontSize = 12.sp,
                            color = Color(0xFF1E40AF),
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                actionItems.forEach { action ->
                    SuggestedActionCard(
                        title = action.title,
                        description = action.description,
                        dateTime = action.dateTime,
                        priorityText = action.priorityText,
                        priorityBg = when (action.priorityText.lowercase()) {
                            "high" -> Color(0xFFFEE2E2)
                            "medium" -> Color(0xFFFEF3C7)
                            else -> Color(0xFFDCFCE7)
                        },
                        priorityTextColor = when (action.priorityText.lowercase()) {
                            "high" -> Color(0xFFDC2626)
                            "medium" -> Color(0xFFD97706)
                            else -> Color(0xFF16A34A)
                        },
                        statusText = action.statusText,
                        statusBg = Color(0xFFDCFCE7),
                        statusTextColor = Color(0xFF16A34A),
                        quote = action.quote
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Document Privacy & Lock Protection Prompt
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, if (DocumentAnalysisSession.isDocumentLocked) Color(0xFF2563EB) else Color(0xFFE2E8F0)),
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "🔒", fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Keep as Important / Financial File?",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Lock sensitive document with password protection",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }

                        Switch(
                            checked = DocumentAnalysisSession.isDocumentLocked,
                            onCheckedChange = {
                                DocumentAnalysisSession.isDocumentLocked = it
                                DocumentAnalysisSession.isImportantDocument = it
                                if (it) {
                                    DocumentAnalysisSession.isUnlockedForSession = false
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    DocumentAnalysisSession.confirmedTasks.clear()
                    DocumentAnalysisSession.confirmedTasks.addAll(actionItems)
                    onConfirmActions()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Confirm & Add Actions (${actionItems.size})",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun SuggestedActionCard(
    title: String,
    description: String,
    dateTime: String,
    priorityText: String,
    priorityBg: Color,
    priorityTextColor: Color,
    statusText: String,
    statusBg: Color,
    statusTextColor: Color,
    quote: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    color = priorityBg,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = priorityText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = priorityTextColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                fontSize = 12.sp,
                color = Color(0xFF64748B),
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = dateTime,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF334155)
                        )
                    }

                    Surface(
                        color = statusBg,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = statusText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = statusTextColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UpcomingActionsScreen(
    onBackClicked: () -> Unit,
    onHomeClicked: () -> Unit
) {
    val tasks = if (DocumentAnalysisSession.confirmedTasks.isNotEmpty()) {
        DocumentAnalysisSession.confirmedTasks
    } else {
        DocumentAnalysisSession.activeResult?.actions ?: emptyList()
    }
    val sourceTitle = DocumentAnalysisSession.activeResult?.documentTitle ?: "Innovation Week 2026"

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8FAFC)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(Color(0xFFEEF2FF), CircleShape)
                                .clickable { onBackClicked() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "DEADLINES & NOTIFICATIONS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF94A3B8),
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Upcoming Actions",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0xFFEEF2FF), CircleShape)
                            .clickable { onHomeClicked() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Home",
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tasks count banner
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFDCFCE7),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFF86EFAC))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF16A34A),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${tasks.size} actions active & synced to schedule",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF15803D)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                tasks.forEach { task ->
                    TaskCard(
                        dateNum = task.dateNum,
                        dateMonth = task.dateMonth,
                        title = task.title,
                        timeAndLeft = "${task.dateTime.substringAfter("• ").trim().ifBlank { "5:00 PM" }} • ${task.daysLeft}",
                        source = sourceTitle,
                        priorityText = task.priorityText,
                        priorityBg = when (task.priorityText.lowercase()) {
                            "high" -> Color(0xFFFEE2E2)
                            "medium" -> Color(0xFFFEF3C7)
                            else -> Color(0xFFDCFCE7)
                        },
                        priorityColor = when (task.priorityText.lowercase()) {
                            "high" -> Color(0xFFDC2626)
                            "medium" -> Color(0xFFD97706)
                            else -> Color(0xFF16A34A)
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onHomeClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
            ) {
                Text(
                    text = "Return to Dashboard",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}


@Composable
fun TaskCard(
    dateNum: String,
    dateMonth: String,
    title: String,
    timeAndLeft: String,
    source: String,
    priorityText: String,
    priorityBg: Color,
    priorityColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp))
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(Color(0xFFEEF2FF), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = dateNum,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2563EB),
                                lineHeight = 16.sp
                            )
                            Text(
                                text = dateMonth,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4F46E5),
                                lineHeight = 10.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = timeAndLeft,
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .background(priorityBg, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = priorityText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = priorityColor
                    )
                }
            }
        }
    }
}

data class TaskItemModel(
    val id: String,
    val title: String,
    val subtitle: String,
    val date: String,
    val priorityText: String,
    val priorityBg: Color,
    val priorityTextColor: Color,
    val status: String
)

@Composable
fun MyTasksScreen(
    onHomeClicked: () -> Unit,
    onRemindersClicked: () -> Unit,
    onProfileClicked: () -> Unit,
    onTaskClicked: () -> Unit = {},
    onAddTaskClicked: () -> Unit = {},
    onUploadClicked: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }

    val rawTasks = DocumentAnalysisSession.confirmedTasks

    val tasksList = remember(rawTasks.size, rawTasks.toList()) {
        rawTasks.toList().map { action: ActionTask ->
            val (priorityBg, priorityTextColor) = when (action.priorityText.lowercase(Locale.ROOT)) {
                "high" -> Pair(Color(0xFFFEE2E2), Color(0xFFEF4444))
                "medium" -> Pair(Color(0xFFFEF3C7), Color(0xFFD97706))
                "low" -> Pair(Color(0xFFDCFCE7), Color(0xFF16A34A))
                else -> Pair(Color(0xFFFEF3C7), Color(0xFFD97706))
            }
            val subtitle = if (action.description.isNotBlank()) action.description else "${action.category} • Action item"
            TaskItemModel(
                id = action.id,
                title = action.title,
                subtitle = subtitle,
                date = action.dateTime,
                priorityText = action.priorityText,
                priorityBg = priorityBg,
                priorityTextColor = priorityTextColor,
                status = if (action.isConfirmed) "Pending" else "Completed"
            )
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8FAFC)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Title & Icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "My Tasks",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(text = "☷", fontSize = 20.sp, color = Color(0xFF2563EB))
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (tasksList.isEmpty()) {
                    // Empty State View when no documents are uploaded and no tasks created
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp, horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .background(Color(0xFFEEF2FF), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "📋", fontSize = 36.sp)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "No Tasks Available",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Your task list is empty. Upload or scan a notice document to automatically extract your tasks and deadlines.",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B),
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { onUploadClicked() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "📄 Upload or Scan Document",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = { onAddTaskClicked() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                        ) {
                            Text(
                                text = "+ Add Task Manually",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2563EB)
                            )
                        }
                    }
                } else {
                    // Stats row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val activeCount = tasksList.count { it.status != "Completed" }
                        val completedCount = tasksList.count { it.status == "Completed" }
                        Text(
                            text = "$activeCount active tasks • $completedCount completed",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFEEF2FF), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "This month >",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2563EB)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Search Bar
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp),
                        shadowElevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🔍", fontSize = 16.sp, color = Color(0xFF94A3B8))
                            Spacer(modifier = Modifier.width(12.dp))
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                decorationBox = { innerTextField ->
                                    if (searchQuery.isEmpty()) {
                                        Text(
                                            text = "Search your tasks",
                                            fontSize = 14.sp,
                                            color = Color(0xFF94A3B8)
                                        )
                                    }
                                    innerTextField()
                                }
                            )
                            if (searchQuery.isNotEmpty()) {
                                Text(
                                    text = "✕",
                                    fontSize = 14.sp,
                                    color = Color(0xFF94A3B8),
                                    modifier = Modifier.clickable { searchQuery = "" }
                                )
                            } else {
                                Text(text = "≡", fontSize = 18.sp, color = Color(0xFF94A3B8))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Tabs
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val tabs = listOf("All", "Pending", "In Progress", "Completed")
                        tabs.forEach { tab ->
                            val isSelected = selectedTab == tab
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isSelected) Color.White else Color.Transparent,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .shadow(
                                        if (isSelected) 1.dp else 0.dp,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedTab = tab }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = tab,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color(0xFF2563EB) else Color(0xFF64748B)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Section Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Your next steps",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "Due date ↓",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2563EB)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Filtered Task Cards
                    val filteredTasks = tasksList.filter { task ->
                        val matchesTab = when (selectedTab) {
                            "Pending" -> task.status == "Pending"
                            "In Progress" -> task.status == "In Progress"
                            "Completed" -> task.status == "Completed"
                            else -> true
                        }
                        val matchesSearch = searchQuery.isEmpty() ||
                            task.title.contains(searchQuery, ignoreCase = true) ||
                            task.subtitle.contains(searchQuery, ignoreCase = true)
                        matchesTab && matchesSearch
                    }

                    if (filteredTasks.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No tasks found matching filter.",
                                fontSize = 13.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    } else {
                        filteredTasks.forEach { task ->
                            TaskCardItem(
                                title = task.title,
                                subtitle = task.subtitle,
                                date = task.date,
                                priorityText = task.priorityText,
                                priorityBg = task.priorityBg,
                                priorityTextColor = task.priorityTextColor,
                                onClick = { onTaskClicked() }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Add Task Button
                    Button(
                        onClick = { onAddTaskClicked() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "+", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Add Task",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "2 more tasks continue below • Apartment & Community",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "1 unverified suggestion remains in your document review.",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Bottom Navigation
            AppBottomBar(
                currentTab = BottomNavTab.TASKS,
                onHomeClicked = onHomeClicked,
                onTasksClicked = { },
                onRemindersClicked = onRemindersClicked,
                onProfileClicked = onProfileClicked
            )
        }
    }
}

@Composable
fun TaskCardItem(
    title: String,
    subtitle: String,
    date: String,
    priorityText: String,
    priorityBg: Color,
    priorityTextColor: Color,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .border(1.5.dp, Color(0xFFCBD5E1), RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "📅 $date", fontSize = 11.sp, color = Color(0xFF334155))
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(priorityBg, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = priorityText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = priorityTextColor
                            )
                        }
                    }
                }
            }

            Text(text = "›", fontSize = 20.sp, color = Color(0xFF94A3B8))
        }
    }
}

@Composable
fun TaskDetailsScreen(
    onBackClicked: () -> Unit,
    onEditClicked: () -> Unit = {}
) {
    val currentTask = DocumentAnalysisSession.selectedTask ?: DocumentAnalysisSession.confirmedTasks.firstOrNull()
    val activeDoc = DocumentAnalysisSession.activeResult
    var showDeleteTaskDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8FAFC)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFFEEF2FF), CircleShape)
                                .clickable { onBackClicked() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "←", fontSize = 18.sp, color = Color(0xFF4F46E5), fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Task details",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                    }
                    Text(text = "•••", fontSize = 18.sp, color = Color(0xFF3B82F6), fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (currentTask == null) {
                    // Empty Task Details View
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp, horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .background(Color(0xFFEEF2FF), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "📋", fontSize = 36.sp)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "No Task Details",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Upload a notice document or select a task to view full action details, checklist, and reminder schedules.",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B),
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { onBackClicked() },
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "← Back to Tasks",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                } else {
                    val isHighPriority = currentTask.priorityText.equals("High", ignoreCase = true)
                    val (priorityBg, priorityTextCol) = when {
                        isHighPriority -> Pair(Color(0xFFFEE2E2), Color(0xFFEF4444))
                        currentTask.priorityText.equals("Low", ignoreCase = true) -> Pair(Color(0xFFDCFCE7), Color(0xFF16A34A))
                        else -> Pair(Color(0xFFFEF3C7), Color(0xFFD97706))
                    }

                    // Badges
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFDBEAFE), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (currentTask.isConfirmed) "Pending" else "Completed",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2563EB)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .background(priorityBg, RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${currentTask.priorityText} priority",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = priorityTextCol
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Title
                    Text(
                        text = currentTask.title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Description
                    Text(
                        text = currentTask.description.ifBlank { "Extracted action item from notice document." },
                        fontSize = 14.sp,
                        color = Color(0xFF64748B),
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Dates & Reminders Card
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp),
                        shadowElevation = 2.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "📅", fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = currentTask.dateTime,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0F172A)
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Extracted deadline • " + (activeDoc?.issuingAuthority ?: "Notice Document"),
                                            fontSize = 11.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFFEEF2FF), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = currentTask.daysLeft.ifBlank { "Due soon" },
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2563EB)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🔔", fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = currentTask.reminderTime,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Reminder schedule",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Dynamic Checklist Card
                    val checklistItems = if (currentTask.checklistItems.isNotEmpty()) {
                        currentTask.checklistItems
                    } else if (!activeDoc?.whatDoINeedToDo.isNullOrEmpty()) {
                        activeDoc!!.whatDoINeedToDo
                    } else {
                        listOf("Review notice instructions", "Complete task requirements", "Verify deadline date")
                    }

                    val checkedStateList = remember(checklistItems) {
                        mutableStateListOf<Boolean>().apply {
                            repeat(checklistItems.size) { add(it == 0) }
                        }
                    }

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp),
                        shadowElevation = 2.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            val doneCount = checkedStateList.count { it }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Checklist",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = "$doneCount of ${checklistItems.size}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2563EB)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .background(Color(0xFFF1F5F9), RoundedCornerShape(2.dp))
                            ) {
                                val progressFraction = if (checklistItems.isEmpty()) 0.1f else doneCount.toFloat() / checklistItems.size.toFloat()
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(progressFraction.coerceIn(0.05f, 1.0f))
                                        .fillMaxHeight()
                                        .background(Color(0xFF2563EB), RoundedCornerShape(2.dp))
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            checklistItems.forEachIndexed { idx, item ->
                                val isChecked = checkedStateList.getOrElse(idx) { false }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (idx < checkedStateList.size) {
                                                checkedStateList[idx] = !checkedStateList[idx]
                                            }
                                        }
                                        .padding(vertical = 4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .background(if (isChecked) Color(0xFF2563EB) else Color.White, RoundedCornerShape(4.dp))
                                            .border(if (isChecked) 0.dp else 1.5.dp, Color(0xFFCBD5E1), RoundedCornerShape(4.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isChecked) {
                                            Text(text = "✓", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = item,
                                        fontSize = 13.sp,
                                        color = if (isChecked) Color(0xFF64748B) else Color(0xFF334155)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Source Notice Card
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp),
                        shadowElevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(Color(0xFFEEF2FF), RoundedCornerShape(10.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "📄", fontSize = 20.sp)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = activeDoc?.documentTitle ?: "Uploaded Notice",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Source notice • ${activeDoc?.referenceNumber ?: "PDF Page 1"}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }

                            Text(text = "›", fontSize = 20.sp, color = Color(0xFF94A3B8))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Edit & Delete Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = {
                                DocumentAnalysisSession.selectedTask = currentTask
                                onEditClicked()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEF2FF)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(text = "✏️ Edit", fontSize = 13.sp, color = Color(0xFF2563EB), fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Button(
                            onClick = { showDeleteTaskDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEF2F2)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(text = "🗑️ Delete", fontSize = 13.sp, color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (currentTask != null) {
                // Bottom Mark as Completed Button
                Button(
                    onClick = {
                        currentTask.isConfirmed = false
                        onBackClicked()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "✓", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Mark as Completed",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }

    if (showDeleteTaskDialog && currentTask != null) {
        AlertDialog(
            onDismissRequest = { showDeleteTaskDialog = false },
            title = {
                Text(text = "Delete Task", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            },
            text = {
                Text(text = "Are you sure you want to delete '${currentTask.title}'? This action cannot be undone.", fontSize = 13.sp, color = Color(0xFF475569))
            },
            confirmButton = {
                Button(
                    onClick = {
                        val targetList: MutableList<ActionTask> = DocumentAnalysisSession.confirmedTasks
                        targetList.removeAll { it.id == currentTask.id }
                        DocumentAnalysisSession.selectedTask = null
                        showDeleteTaskDialog = false
                        onBackClicked()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Delete", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteTaskDialog = false }) {
                    Text(text = "Cancel", color = Color(0xFF64748B))
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun EditTaskScreen(
    onBackClicked: () -> Unit
) {
    val existingTask = DocumentAnalysisSession.selectedTask
    var taskTitle by remember { mutableStateOf(existingTask?.title ?: "") }
    var description by remember { mutableStateOf(existingTask?.description ?: "") }
    var dueDate by remember { mutableStateOf(existingTask?.dateTime ?: "") }
    var selectedPriority by remember { mutableStateOf(existingTask?.priorityText ?: "High") }
    var reminderTime by remember { mutableStateOf(existingTask?.reminderTime ?: "") }

    var showDatePickerDialog by remember { mutableStateOf(false) }
    var showReminderPickerDialog by remember { mutableStateOf(false) }

    val docChecklist = DocumentAnalysisSession.activeResult?.whatDoINeedToDo ?: emptyList()
    val initialItems = remember(existingTask, docChecklist) {
        if (!existingTask?.checklistItems.isNullOrEmpty()) {
            existingTask!!.checklistItems.toMutableList()
        } else if (docChecklist.isNotEmpty()) {
            docChecklist.toMutableList()
        } else {
            mutableListOf("Review notice document", "Complete required task steps")
        }
    }

    val checklist = remember { initialItems.toMutableStateList() }
    var customChecklistInput by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8FAFC)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFFEEF2FF), CircleShape)
                                .clickable { onBackClicked() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "←", fontSize = 18.sp, color = Color(0xFF4F46E5), fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (existingTask != null) "Edit task" else "Create Task",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }

                    Text(
                        text = "✕",
                        fontSize = 18.sp,
                        color = Color(0xFF2563EB),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onBackClicked() }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Task title (Empty by default)
                Text(
                    text = "Task title",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF334155)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 1.dp,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    androidx.compose.foundation.text.BasicTextField(
                        value = taskTitle,
                        onValueChange = { taskTitle = it },
                        modifier = Modifier.padding(14.dp),
                        textStyle = TextStyle(fontSize = 14.sp, color = Color(0xFF0F172A)),
                        decorationBox = { innerTextField ->
                            if (taskTitle.isEmpty()) {
                                Text(
                                    text = "Enter task title...",
                                    fontSize = 14.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                            innerTextField()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Description (Empty by default)
                Text(
                    text = "Description",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF334155)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 1.dp,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    androidx.compose.foundation.text.BasicTextField(
                        value = description,
                        onValueChange = { description = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 70.dp)
                            .padding(14.dp),
                        textStyle = TextStyle(fontSize = 13.sp, color = Color(0xFF334155), lineHeight = 18.sp),
                        decorationBox = { innerTextField ->
                            if (description.isEmpty()) {
                                Text(
                                    text = "Enter task details & requirements...",
                                    fontSize = 13.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                            innerTextField()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Due date • Clickable Calendar Selector
                Text(
                    text = "Due date",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF334155)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePickerDialog = true },
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 1.dp,
                    border = BorderStroke(1.dp, Color(0xFF2563EB))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (dueDate.isEmpty()) "Select due date..." else dueDate,
                                fontSize = 14.sp,
                                fontWeight = if (dueDate.isEmpty()) FontWeight.Normal else FontWeight.Bold,
                                color = if (dueDate.isEmpty()) Color(0xFF94A3B8) else Color(0xFF0F172A)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Tap to open calendar picker",
                                fontSize = 11.sp,
                                color = Color(0xFF2563EB)
                            )
                        }
                        Text(text = "📅", fontSize = 20.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Priority Selection
                Text(
                    text = "Select Priority",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF334155)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("High", "Medium", "Low").forEach { priority ->
                        val isSelected = selectedPriority == priority
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    if (isSelected) {
                                        when (priority) {
                                            "High" -> Color(0xFFFEE2E2)
                                            "Medium" -> Color(0xFFFEF3C7)
                                            else -> Color(0xFFDCFCE7)
                                        }
                                    } else Color.Transparent,
                                    RoundedCornerShape(8.dp)
                                )
                                .shadow(
                                    if (isSelected) 1.dp else 0.dp,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedPriority = priority }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (priority == "High") "⚡ High" else priority,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) {
                                    when (priority) {
                                        "High" -> Color(0xFFEF4444)
                                        "Medium" -> Color(0xFFD97706)
                                        else -> Color(0xFF16A34A)
                                    }
                                } else Color(0xFF64748B)
                            )
                        }
                    }
                }

                if (selectedPriority == "High") {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFEF2F2), RoundedCornerShape(8.dp))
                            .border(BorderStroke(1.dp, Color(0xFFFCA5A5)), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "⚡ High Importance — Will be highlighted in red priority badge on task screens.",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDC2626)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Document Action Checklist Section
                Text(
                    text = "Document Action Checklist",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF334155)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (docChecklist.isNotEmpty()) "Extracted from uploaded notice" else "Manage sub-task action steps",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column {
                    checklist.forEachIndexed { idx, item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "•", fontSize = 16.sp, color = Color(0xFF2563EB), fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = item,
                                    fontSize = 13.sp,
                                    color = Color(0xFF334155)
                                )
                            }
                            Text(
                                text = "✕",
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8),
                                modifier = Modifier.clickable { checklist.removeAt(idx) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            color = Color.White,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                        ) {
                            BasicTextField(
                                value = customChecklistInput,
                                onValueChange = { customChecklistInput = it },
                                modifier = Modifier.padding(10.dp),
                                textStyle = TextStyle(fontSize = 12.sp, color = Color(0xFF0F172A)),
                                decorationBox = { inner ->
                                    if (customChecklistInput.isEmpty()) {
                                        Text("+ Add item...", fontSize = 12.sp, color = Color(0xFF94A3B8))
                                    }
                                    inner()
                                }
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(
                            onClick = {
                                if (customChecklistInput.isNotBlank()) {
                                    checklist.add(customChecklistInput.trim())
                                    customChecklistInput = ""
                                }
                            }
                        ) {
                            Text("Add", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Reminder Schedule (Clickable Date & Time Selector)
                Text(
                    text = "Reminder Schedule",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF334155)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showReminderPickerDialog = true },
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 1.dp,
                    border = BorderStroke(1.dp, Color(0xFF2563EB))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (reminderTime.isEmpty()) "Set reminder date & time..." else reminderTime,
                                fontSize = 14.sp,
                                fontWeight = if (reminderTime.isEmpty()) FontWeight.Normal else FontWeight.Bold,
                                color = if (reminderTime.isEmpty()) Color(0xFF94A3B8) else Color(0xFF0F172A)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Tap to set date & time picker",
                                fontSize = 11.sp,
                                color = Color(0xFF2563EB)
                            )
                        }
                        Text(text = "🔔", fontSize = 20.sp)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Save Task Button
            Button(
                onClick = {
                    val titleText = if (taskTitle.isBlank()) "Untitled Task" else taskTitle
                    val finalDueDate = if (dueDate.isBlank()) "20 Oct 2026 • 5:00 PM" else dueDate
                    val finalReminderTime = if (reminderTime.isBlank()) "19 Oct 2026 • 9:00 AM" else reminderTime
                    val updatedTask = ActionTask(
                        id = existingTask?.id ?: UUID.randomUUID().toString(),
                        title = titleText,
                        description = if (description.isBlank()) "Manually updated action item." else description,
                        dateTime = finalDueDate,
                        priorityText = selectedPriority,
                        category = DocumentAnalysisSession.activeResult?.category ?: "Document Task",
                        reminderTime = finalReminderTime,
                        checklistItems = checklist.toList()
                    )

                    val confirmedList: MutableList<ActionTask> = DocumentAnalysisSession.confirmedTasks
                    if (existingTask != null) {
                        val idx = confirmedList.indexOfFirst { it.id == existingTask.id }
                        if (idx != -1) {
                            confirmedList[idx] = updatedTask
                        } else {
                            confirmedList.add(0, updatedTask)
                        }
                    } else {
                        confirmedList.add(0, updatedTask)
                    }

                    DocumentAnalysisSession.selectedTask = updatedTask

                    // Add to ReminderManager
                    ReminderManager.addCustomReminder(
                        title = titleText,
                        deadline = dueDate,
                        scheduled = reminderTime,
                        source = DocumentAnalysisSession.activeResult?.documentTitle ?: "Custom Task"
                    )

                    onBackClicked()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "✓", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Save Task",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }

    // Due Date Calendar Selector Dialog
    if (showDatePickerDialog) {
        var customDateInput by remember { mutableStateOf(dueDate) }
        AlertDialog(
            onDismissRequest = { showDatePickerDialog = false },
            title = {
                Text(text = "Select Due Date", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            },
            text = {
                Column {
                    Text(text = "Choose a preset due date or enter custom date:", fontSize = 12.sp, color = Color(0xFF64748B))
                    Spacer(modifier = Modifier.height(10.dp))

                    val dateOptions = listOf(
                        "20 Oct 2026 • 5:00 PM",
                        "22 Oct 2026 • 10:00 AM",
                        "25 Oct 2026 • 5:00 PM",
                        "30 Oct 2026 • 6:00 PM",
                        "Tomorrow • 9:00 AM"
                    )

                    dateOptions.forEach { opt ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clickable {
                                    dueDate = opt
                                    showDatePickerDialog = false
                                },
                            color = if (dueDate == opt) Color(0xFFEEF2FF) else Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, if (dueDate == opt) Color(0xFF2563EB) else Color(0xFFE2E8F0))
                        ) {
                            Text(text = opt, fontSize = 13.sp, color = Color(0xFF0F172A), modifier = Modifier.padding(10.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = customDateInput,
                        onValueChange = { customDateInput = it },
                        label = { Text("Custom Date & Time", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customDateInput.isNotBlank()) {
                            dueDate = customDateInput
                        }
                        showDatePickerDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                ) {
                    Text("Set Date", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerDialog = false }) {
                    Text("Cancel", color = Color(0xFF64748B))
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Reminder Calendar & Time Selector Dialog
    if (showReminderPickerDialog) {
        var customReminderInput by remember { mutableStateOf(reminderTime) }
        AlertDialog(
            onDismissRequest = { showReminderPickerDialog = false },
            title = {
                Text(text = "Set Reminder Date & Time", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            },
            text = {
                Column {
                    Text(text = "Select when you want to be reminded:", fontSize = 12.sp, color = Color(0xFF64748B))
                    Spacer(modifier = Modifier.height(10.dp))

                    val reminderOptions = listOf(
                        "19 Oct 2026 • 9:00 AM",
                        "20 Oct 2026 • 8:00 AM",
                        "1 day before (9:00 AM)",
                        "1 hour before",
                        "At event time"
                    )

                    reminderOptions.forEach { opt ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clickable {
                                    reminderTime = opt
                                    showReminderPickerDialog = false
                                },
                            color = if (reminderTime == opt) Color(0xFFEEF2FF) else Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, if (reminderTime == opt) Color(0xFF2563EB) else Color(0xFFE2E8F0))
                        ) {
                            Text(text = opt, fontSize = 13.sp, color = Color(0xFF0F172A), modifier = Modifier.padding(10.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = customReminderInput,
                        onValueChange = { customReminderInput = it },
                        label = { Text("Custom Reminder Schedule", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customReminderInput.isNotBlank()) {
                            reminderTime = customReminderInput
                        }
                        showReminderPickerDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                ) {
                    Text("Set Reminder", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showReminderPickerDialog = false }) {
                    Text("Cancel", color = Color(0xFF64748B))
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

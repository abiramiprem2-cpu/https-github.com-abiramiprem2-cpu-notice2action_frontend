package com.simats.notice2action

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.simats.notice2action.data.DocumentAnalysisSession
import com.simats.notice2action.data.ActionTask
import com.simats.notice2action.data.ReminderManager

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
                        Column {
                            Text(
                                text = "TASK CREATION & MANAGEMENT",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF94A3B8),
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Review Suggested Actions",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        }
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
    onAddTaskClicked: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var tasksList by remember {
        mutableStateOf(
            listOf(
                TaskItemModel("1", "Submit project proposal", "Innovation Week 2026 • In progress", "20 Oct • 5:00 PM", "High", Color(0xFFFEE2E2), Color(0xFFEF4444), "In Progress"),
                TaskItemModel("2", "Attend team briefing", "Innovation Week 2026", "22 Oct • 10:00 AM", "Medium", Color(0xFFFEF3C7), Color(0xFFD97706), "Pending"),
                TaskItemModel("3", "Submit expense report", "Office • October expenses", "23 Oct • 4:00 PM", "Medium", Color(0xFFFEF3C7), Color(0xFFD97706), "Pending"),
                TaskItemModel("4", "Project abstract drafted", "Innovation Week 2026", "18 Oct • 2:00 PM", "Low", Color(0xFFDCFCE7), Color(0xFF16A34A), "Completed")
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
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Category Header
                Text(
                    text = "D • TASK CREATION & MANAGEMENT",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B),
                    letterSpacing = 1.sp
                )

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

                // Stats row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "5 active tasks • 2 completed",
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
                        androidx.compose.foundation.text.BasicTextField(
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
                            text = "No tasks found.",
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
    var check1 by remember { mutableStateOf(true) }
    var check2 by remember { mutableStateOf(false) }
    var check3 by remember { mutableStateOf(false) }
    var showDeleteTaskDialog by remember { mutableStateOf(false) }
    val completedCount = (if (check1) 1 else 0) + (if (check2) 1 else 0) + (if (check3) 1 else 0)
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
                Text(
                    text = "D • TASK CREATION & MANAGEMENT",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B),
                    letterSpacing = 1.sp
                )
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

                // Badges
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFDBEAFE), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "In progress",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2563EB)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .background(Color(0xFFFEE2E2), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "High priority",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEF4444)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title
                Text(
                    text = "Submit project proposal",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Description
                Text(
                    text = "Upload your project abstract and team member details to the student portal for Innovation Week 2026.",
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
                                        text = "20 October 2026 • 5:00 PM",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Extracted deadline • Verified by " + (com.simats.notice2action.data.UserSession.currentUser?.name?.ifBlank { "Admin" } ?: "Admin"),
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
                                    text = "5 days",
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
                                    text = "19 October 2026 • 9:00 AM",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Reminder selected by you",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Checklist Card
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
                            Text(
                                text = "Checklist",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "$completedCount of 3",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2563EB)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Progress Bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .background(Color(0xFFF1F5F9), RoundedCornerShape(2.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(if (completedCount == 0) 0.05f else completedCount / 3f)
                                    .fillMaxHeight()
                                    .background(Color(0xFF2563EB), RoundedCornerShape(2.dp))
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Checklist items
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { check1 = !check1 }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .background(if (check1) Color(0xFF2563EB) else Color.White, RoundedCornerShape(4.dp))
                                    .border(if (check1) 0.dp else 1.5.dp, Color(0xFFCBD5E1), RoundedCornerShape(4.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (check1) {
                                    Text(text = "✓", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Draft project abstract",
                                fontSize = 13.sp,
                                color = if (check1) Color(0xFF64748B) else Color(0xFF334155)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { check2 = !check2 }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .background(if (check2) Color(0xFF2563EB) else Color.White, RoundedCornerShape(4.dp))
                                    .border(if (check2) 0.dp else 1.5.dp, Color(0xFFCBD5E1), RoundedCornerShape(4.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (check2) {
                                    Text(text = "✓", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Add team member details",
                                fontSize = 13.sp,
                                color = if (check2) Color(0xFF64748B) else Color(0xFF334155)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { check3 = !check3 }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .background(if (check3) Color(0xFF2563EB) else Color.White, RoundedCornerShape(4.dp))
                                    .border(if (check3) 0.dp else 1.5.dp, Color(0xFFCBD5E1), RoundedCornerShape(4.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (check3) {
                                    Text(text = "✓", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Upload proposal to student portal",
                                fontSize = 13.sp,
                                color = if (check3) Color(0xFF64748B) else Color(0xFF334155)
                            )
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
                                    text = "Innovation Week 2026",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Original notice • PDF • Page 1",
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
                        onClick = { onEditClicked() },
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

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Completion and delete confirmation concepts on the UI board.",
                    fontSize = 10.sp,
                    color = Color(0xFF94A3B8),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Bottom Mark as Completed Button
            Button(
                onClick = { onBackClicked() },
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

    if (showDeleteTaskDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteTaskDialog = false },
            title = {
                Text(text = "Delete Task", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            },
            text = {
                Text(text = "Are you sure you want to delete 'Submit project proposal'? This action cannot be undone.", fontSize = 13.sp, color = Color(0xFF475569))
            },
            confirmButton = {
                Button(
                    onClick = {
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
    var taskTitle by remember { mutableStateOf("Submit project proposal") }
    var description by remember { mutableStateOf("Upload the abstract and team member details to the student portal.") }
    var selectedPriority by remember { mutableStateOf("High") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8FAFC)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(20.dp)
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
                        Column {
                            Text(
                                text = "D • TASK CREATION & MANAGEMENT",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF94A3B8),
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Edit task",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        }
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

                // Task title
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
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, color = Color(0xFF0F172A))
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Description
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
                        modifier = Modifier.padding(14.dp),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp, color = Color(0xFF334155), lineHeight = 18.sp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Due date • extracted
                Text(
                    text = "Due date • extracted",
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "20 Oct 2026",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF0F172A)
                        )
                        Text(text = "📅", fontSize = 16.sp, color = Color(0xFF64748B))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Priority
                Text(
                    text = "Priority",
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
                                    if (isSelected) Color.White else Color.Transparent,
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
                                text = priority,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color(0xFF2563EB) else Color(0xFF64748B)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Category
                Text(
                    text = "Category",
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
                    Text(
                        text = "College",
                        fontSize = 14.sp,
                        color = Color(0xFF0F172A),
                        modifier = Modifier.padding(14.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Checklist
                var editCheck1 by remember { mutableStateOf(true) }
                var editCheck2 by remember { mutableStateOf(false) }
                var editCheck3 by remember { mutableStateOf(false) }
                Text(
                    text = "Checklist",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF334155)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { editCheck1 = !editCheck1 }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(if (editCheck1) Color(0xFF2563EB) else Color.White, RoundedCornerShape(4.dp))
                                .border(if (editCheck1) 0.dp else 1.5.dp, Color(0xFFCBD5E1), RoundedCornerShape(4.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (editCheck1) {
                                Text(text = "✓", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Draft project abstract",
                            fontSize = 13.sp,
                            color = if (editCheck1) Color(0xFF64748B) else Color(0xFF334155)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { editCheck2 = !editCheck2 }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(if (editCheck2) Color(0xFF2563EB) else Color.White, RoundedCornerShape(4.dp))
                                .border(if (editCheck2) 0.dp else 1.5.dp, Color(0xFFCBD5E1), RoundedCornerShape(4.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (editCheck2) {
                                Text(text = "✓", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Add team member details",
                            fontSize = 13.sp,
                            color = if (editCheck2) Color(0xFF64748B) else Color(0xFF334155)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { editCheck3 = !editCheck3 }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(if (editCheck3) Color(0xFF2563EB) else Color.White, RoundedCornerShape(4.dp))
                                .border(if (editCheck3) 0.dp else 1.5.dp, Color(0xFFCBD5E1), RoundedCornerShape(4.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (editCheck3) {
                                Text(text = "✓", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Upload proposal to student portal",
                            fontSize = 13.sp,
                            color = if (editCheck3) Color(0xFF64748B) else Color(0xFF334155)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Reminder • selected by you
                Text(
                    text = "Reminder • selected by you",
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "19 Oct 2026 • 9:00 AM",
                            fontSize = 14.sp,
                            color = Color(0xFF0F172A)
                        )
                        Text(text = "🔔", fontSize = 16.sp, color = Color(0xFF64748B))
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Save Task Button
            Button(
                onClick = { onBackClicked() },
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
}

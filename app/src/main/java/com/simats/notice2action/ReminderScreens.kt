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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.material.icons.filled.Add
import com.simats.notice2action.data.ReminderManager
import com.simats.notice2action.data.ReminderItemModel

@Composable
fun RemindersScreen(
    onHomeClicked: () -> Unit,
    onProfileClicked: () -> Unit,
    onSettingsClicked: () -> Unit = {},
    onTasksClicked: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf("Upcoming") }
    var reminderToDelete by remember { mutableStateOf<ReminderItemModel?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

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
                // Header
                Text(
                    text = "E â€¢ DEADLINES & NOTIFICATIONS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B),
                    letterSpacing = 1.sp
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Reminders",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Color(0xFF2563EB),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.clickable { showAddDialog = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Add", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "âš²",
                            fontSize = 20.sp,
                            color = Color(0xFF2563EB),
                            modifier = Modifier.clickable { onSettingsClicked() }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tabs: Upcoming / Past
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFEEF2FF), RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                if (selectedTab == "Upcoming") Color.White else Color.Transparent,
                                RoundedCornerShape(8.dp)
                            )
                            .shadow(
                                if (selectedTab == "Upcoming") 1.dp else 0.dp,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedTab = "Upcoming" }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Upcoming",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == "Upcoming") Color(0xFF2563EB) else Color(0xFF64748B)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                if (selectedTab == "Past") Color.White else Color.Transparent,
                                RoundedCornerShape(8.dp)
                            )
                            .shadow(
                                if (selectedTab == "Past") 1.dp else 0.dp,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedTab = "Past" }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Past",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (selectedTab == "Past") Color(0xFF2563EB) else Color(0xFF64748B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Subheader
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${ReminderManager.getScheduledCount()} scheduled â€¢ ${ReminderManager.getActiveCount()} active",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )

                    Box(
                        modifier = Modifier
                            .background(Color(0xFFEEF2FF), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "October 2026",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2563EB)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Cards list from ReminderManager
                val displayedReminders = if (selectedTab == "Upcoming") {
                    ReminderManager.getUpcomingReminders()
                } else {
                    ReminderManager.getPastReminders()
                }

                if (displayedReminders.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (selectedTab == "Upcoming") "No upcoming reminders." else "No past reminders.",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                } else {
                    displayedReminders.forEach { item ->
                        ReminderCard(
                            item = item,
                            onToggle = { ReminderManager.toggleReminder(item.id) },
                            onEdit = { onSettingsClicked() },
                            onDelete = { reminderToDelete = item }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Settings Button
                Button(
                    onClick = { onSettingsClicked() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEF2FF)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "âš²", fontSize = 16.sp, color = Color(0xFF2563EB), fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Reminder Settings",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2563EB)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Smart reminders are automatically linked with your scanned notices and deadlines.",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Bottom navigation
            AppBottomBar(
                currentTab = BottomNavTab.REMINDERS,
                onHomeClicked = onHomeClicked,
                onTasksClicked = onTasksClicked,
                onRemindersClicked = { },
                onProfileClicked = onProfileClicked
            )
        }
    }

    if (reminderToDelete != null) {
        AlertDialog(
            onDismissRequest = { reminderToDelete = null },
            title = {
                Text(
                    text = "Delete Reminder",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete the reminder for '${reminderToDelete?.title}'?",
                    fontSize = 13.sp,
                    color = Color(0xFF475569)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        reminderToDelete?.let { ReminderManager.removeReminder(it.id) }
                        reminderToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Delete", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { reminderToDelete = null }) {
                    Text(text = "Cancel", color = Color(0xFF64748B))
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (showAddDialog) {
        var newTitle by remember { mutableStateOf("") }
        var newDeadline by remember { mutableStateOf("25 Oct 2026 â€¢ 5:00 PM") }
        var selectedPreset by remember { mutableStateOf("1 day before (9:00 AM)") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text(
                    text = "Add Smart Deadline Reminder",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("Task / Notice Title") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newDeadline,
                        onValueChange = { newDeadline = it },
                        label = { Text("Deadline Date & Time") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Alert Preset",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF475569)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("1 day before (9:00 AM)", "2 hours before", "Day of deadline").forEach { preset ->
                            val isSelected = selectedPreset == preset
                            Surface(
                                color = if (isSelected) Color(0xFF2563EB) else Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.clickable { selectedPreset = preset }
                            ) {
                                Text(
                                    text = if (preset.startsWith("1 day")) "1 day before" else if (preset.startsWith("2 hours")) "2 hrs before" else "On deadline",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else Color(0xFF475569),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTitle.isNotBlank()) {
                            ReminderManager.addCustomReminder(
                                title = newTitle.trim(),
                                deadline = newDeadline.trim(),
                                scheduled = "$selectedPreset alert",
                                source = "Custom Reminder",
                                preset = selectedPreset
                            )
                        }
                        showAddDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Save Reminder", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text(text = "Cancel", color = Color(0xFF64748B))
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun ReminderCard(
    item: ReminderItemModel,
    onToggle: (Boolean) -> Unit,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(if (item.initialEnabled) Color(0xFFEEF2FF) else Color(0xFFF1F5F9), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "ðŸ””", fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = item.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                }

                Switch(
                    checked = item.initialEnabled,
                    onCheckedChange = { onToggle(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF2563EB),
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFFE2E8F0)
                    ),
                    modifier = Modifier.scale(0.8f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Timing preset badge & source document badge
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFFEEF2FF),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "ðŸ“„ ${item.sourceDocument}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2563EB),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Surface(
                    color = Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "â° ${item.timingPreset}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB45309),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Alert scheduled: ${item.scheduledTime}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF334155)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "Notice deadline: ${item.deadline}",
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (item.initialEnabled) Color(0xFFDCFCE7) else Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (item.initialEnabled) "Active" else "Paused",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (item.initialEnabled) Color(0xFF16A34A) else Color(0xFF64748B)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (item.sourceDocument != "Custom Reminder") "Synced from Notice" else "Custom reminder",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "âœï¸", fontSize = 14.sp, color = Color(0xFF2563EB), modifier = Modifier.clickable { onEdit() })
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "ðŸ—‘ï¸", fontSize = 14.sp, color = Color(0xFFEF4444), modifier = Modifier.clickable { onDelete() })
                }
            }
        }
    }
}
@Composable
fun ReminderSettingsScreen(
    onBackClicked: () -> Unit
) {
    var allowNotifications by remember { mutableStateOf(true) }
    var soundEnabled by remember { mutableStateOf(true) }
    var vibrationEnabled by remember { mutableStateOf(false) }

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
                    text = "E • DEADLINES & NOTIFICATIONS",
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
                            text = "Reminder settings",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }
                    Text(text = "•••", fontSize = 18.sp, color = Color(0xFF2563EB), fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Card 1: Allow Notifications
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
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🔔", fontSize = 20.sp, color = Color(0xFF2563EB))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Allow notifications",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Visual preference • Not device permission",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                        Switch(
                            checked = allowNotifications,
                            onCheckedChange = { allowNotifications = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF2563EB)
                            ),
                            modifier = Modifier.scale(0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Default reminder presets
                Text(
                    text = "Default reminder presets",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    shadowElevation = 2.dp
                ) {
                    var selectedPresetIndex by remember { mutableStateOf(1) }
                    val presetOptions = listOf("On the due day", "1 day before", "3 days before", "7 days before")
                    Column(modifier = Modifier.padding(16.dp)) {
                        presetOptions.forEachIndexed { index, text ->
                            val isChecked = selectedPresetIndex == index
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedPresetIndex = index }
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .background(
                                            color = if (isChecked) Color(0xFF2563EB) else Color.White,
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                        .border(
                                            width = 1.5.dp,
                                            color = if (isChecked) Color.Transparent else Color(0xFFCBD5E1),
                                            shape = RoundedCornerShape(4.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isChecked) {
                                        Text(text = "✓", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = text,
                                    fontSize = 13.sp,
                                    fontWeight = if (isChecked) FontWeight.SemiBold else FontWeight.Medium,
                                    color = Color(0xFF334155)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Custom Schedule
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Custom schedule",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "Proposal task",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2563EB)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Date",
                            fontSize = 11.sp,
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
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "19 Oct 2026", fontSize = 13.sp, color = Color(0xFF0F172A))
                                Text(text = "📅", fontSize = 14.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Time",
                            fontSize = 11.sp,
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
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "9:00 AM", fontSize = 13.sp, color = Color(0xFF0F172A))
                                Text(text = "🕒", fontSize = 14.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .background(Color(0xFFEEF2FF), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "USER-SELECTED REMINDER • LOCAL TIME",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2563EB)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sound and Vibration Card
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
                                Text(text = "🔊", fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Notification sound",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Soft chime",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                            Switch(
                                checked = soundEnabled,
                                onCheckedChange = { soundEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFF2563EB)
                                ),
                                modifier = Modifier.scale(0.8f)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "📳", fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Vibration",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                            }
                            Switch(
                                checked = vibrationEnabled,
                                onCheckedChange = { vibrationEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFF2563EB),
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = Color(0xFFE2E8F0)
                                ),
                                modifier = Modifier.scale(0.8f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Info Banner Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFEEF2FF),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "ℹ️", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Reminder: 19 Oct, 9:00 AM. Extracted deadline: 20 Oct, 5:00 PM. These are separate.",
                            fontSize = 12.sp,
                            color = Color(0xFF2563EB),
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Save Settings Button
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
                        text = "Save Settings",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

package com.simats.notice2action

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.notice2action.data.BadgeItem
import com.simats.notice2action.data.DocumentManager
import com.simats.notice2action.data.StreakAndAchievementManager
import com.simats.notice2action.data.TaskManager

@Composable
fun DailyAiBriefingCard(
    modifier: Modifier = Modifier,
    onViewTasksClicked: () -> Unit = {},
    onScanNoticeClicked: () -> Unit = {}
) {
    val context = LocalContext.current
    var showBadgesDialog by remember { mutableStateOf(false) }

    val streak = remember { StreakAndAchievementManager.getCurrentStreak(context) }
    val badges = remember { StreakAndAchievementManager.getAllBadges(context) }
    val unlockedCount = badges.count { it.isUnlocked }

    val totalDocs = DocumentManager.documents.size
    val pendingTasks = TaskManager.getPendingTasks()
    val highPriorityTask = pendingTasks.firstOrNull { it.priorityText.equals("High", ignoreCase = true) || it.priorityText.equals("Urgent", ignoreCase = true) }
        ?: pendingTasks.firstOrNull()

    // Synthesize conversational daily briefing text
    val briefingMessage = remember(totalDocs, pendingTasks.size, highPriorityTask) {
        when {
            pendingTasks.isEmpty() && totalDocs > 0 -> {
                "All tasks are up to date! You have resolved all items across your $totalDocs active notices. Great job maintaining your streak!"
            }
            highPriorityTask != null -> {
                "You have ${pendingTasks.size} pending action${if (pendingTasks.size > 1) "s" else ""}. Priority focus: '${highPriorityTask.title}' (Due ${highPriorityTask.dateTime}). Complete it today to protect your on-time score."
            }
            totalDocs > 0 -> {
                "You have $totalDocs indexed notices with ${pendingTasks.size} pending steps. Everything is within safe deadlines."
            }
            else -> {
                "Welcome to Notice2Action! Scan your first bill, official circular, or exam notice to automatically extract deadlines and action items."
            }
        }
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color(0xFF0F172A),
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF1E1B4B),
                            Color(0xFF1E3A8A),
                            Color(0xFF0F172A)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Column {
                // Header Row with AI Pill and Streak Chip
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Color(0xFF2563EB),
                            shape = CircleShape,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Daily AI Briefing",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // Streak & Badges Chip (Clickable)
                    Surface(
                        color = Color.White.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.4f)),
                        modifier = Modifier.clickable { showBadgesDialog = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🔥 ${streak}d streak", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFDE047))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "•", fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "🏆 $unlockedCount badges", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Natural Language Briefing Text
                Text(
                    text = briefingMessage,
                    fontSize = 13.sp,
                    color = Color(0xFFE2E8F0),
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onViewTasksClicked() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "View Checklist", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    OutlinedButton(
                        onClick = { showBadgesDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(text = "Badges", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                }
            }
        }
    }

    if (showBadgesDialog) {
        AchievementsDialog(
            badges = badges,
            currentStreak = streak,
            onDismiss = { showBadgesDialog = false }
        )
    }
}

@Composable
fun AchievementsDialog(
    badges: List<BadgeItem>,
    currentStreak: Int,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = Color(0xFFFDE047).copy(alpha = 0.2f),
                    shape = CircleShape,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = "🏆", fontSize = 20.sp)
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = "Achievements & Streaks", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(text = "🔥 $currentStreak Day Active Streak", fontSize = 12.sp, color = Color(0xFFD97706), fontWeight = FontWeight.SemiBold)
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 380.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(badges) { badge ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = if (badge.isUnlocked) Color(0xFFEFF6FF) else Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, if (badge.isUnlocked) Color(0xFFBFDBFE) else Color(0xFFE2E8F0))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = badge.emoji, fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = badge.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (badge.isUnlocked) Color(0xFF1E3A8A) else Color(0xFF64748B)
                                )
                                Text(
                                    text = badge.description,
                                    fontSize = 11.sp,
                                    color = if (badge.isUnlocked) Color(0xFF3B82F6) else Color(0xFF94A3B8),
                                    lineHeight = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            if (badge.isUnlocked) {
                                Surface(
                                    color = Color(0xFFDCFCE7),
                                    shape = CircleShape,
                                    modifier = Modifier.size(22.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Check, contentDescription = "Unlocked", tint = Color(0xFF15803D), modifier = Modifier.size(14.dp))
                                    }
                                }
                            } else {
                                Icon(Icons.Default.Lock, contentDescription = "Locked", tint = Color(0xFFCBD5E1), modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "Done", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

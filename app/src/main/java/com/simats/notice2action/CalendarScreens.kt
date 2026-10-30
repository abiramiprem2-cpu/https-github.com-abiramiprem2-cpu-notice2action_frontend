package com.simats.notice2action

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CalendarScreen(
    onBackClicked: () -> Unit,
    onAddTaskClicked: () -> Unit = {}
) {
    var selectedMonthIndex by remember { mutableStateOf(9) }
    val months = listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
    var selectedDay by remember { mutableStateOf(20) }
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
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFEEF2FF), RoundedCornerShape(12.dp))
                        .clickable { onBackClicked() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "←", fontSize = 18.sp, color = Color(0xFF3B82F6), fontWeight = FontWeight.Bold)
                }
                Text(
                    text = "Calendar",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White, CircleShape)
                        .shadow(1.dp, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🔔", fontSize = 18.sp, color = Color(0xFF64748B))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Calendar Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shape = RoundedCornerShape(24.dp),
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0xFFF1F5F9), CircleShape)
                                .clickable { selectedMonthIndex = if (selectedMonthIndex > 0) selectedMonthIndex - 1 else 11 },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "‹", fontSize = 24.sp, color = Color(0xFF64748B))
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${months[selectedMonthIndex]} 2026",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                text = "3 upcoming deadlines",
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0xFFF1F5F9), CircleShape)
                                .clickable { selectedMonthIndex = if (selectedMonthIndex < 11) selectedMonthIndex + 1 else 0 },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "›", fontSize = 24.sp, color = Color(0xFF64748B))
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Days of week
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT").forEach { day ->
                            Text(
                                text = day,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF94A3B8),
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Calendar Grid
                    val days = (1..31).toList()
                    var dayIndex = 0
                    repeat(5) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            repeat(7) {
                                if (dayIndex < days.size) {
                                    val day = days[dayIndex]
                                    val isSelected = day == selectedDay
                                    val hasEvent = day == 22 || day == 24 || day == 25

                                    Column(
                                        modifier = Modifier.weight(1f).clickable { selectedDay = day },
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .background(
                                                    if (isSelected) Color(0xFF2563EB) else Color.Transparent,
                                                    RoundedCornerShape(12.dp)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = day.toString(),
                                                fontSize = 14.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) Color.White else Color(0xFF1E293B)
                                            )
                                        }
                                        if (hasEvent || isSelected) {
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Box(
                                                modifier = Modifier
                                                    .size(4.dp)
                                                    .background(
                                                        if (isSelected) Color.White else Color(0xFFF97316),
                                                        CircleShape
                                                    )
                                            )
                                        } else {
                                            Spacer(modifier = Modifier.height(6.dp))
                                        }
                                    }
                                    dayIndex++
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Selected Date Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "SELECTED DATE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Day $selectedDay • ${months[selectedMonthIndex]} 2026",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                }
                Box(
                    modifier = Modifier
                        .background(Color(0xFFEEF2FF), RoundedCornerShape(8.dp))
                        .clickable { onAddTaskClicked() }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "+", fontSize = 16.sp, color = Color(0xFF2563EB), fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Add Task", fontSize = 13.sp, color = Color(0xFF2563EB), fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Events List
            Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                EventCard(
                    time = "5:00\nPM",
                    title = "Submit project proposal",
                    subtitle = "Innovation Week 2026 • High Priority",
                    barColor = Color(0xFFDC2626)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                EventCard(
                    time = "9:00\nAM",
                    title = "Reminder: Submit proposal tomorrow",
                    subtitle = "Personal Reminder • Scheduled by you",
                    barColor = Color(0xFF2563EB)
                )
            }
        }
    }
}

@Composable
fun EventCard(time: String, title: String, subtitle: String, barColor: Color) {
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = time,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF94A3B8),
                textAlign = TextAlign.Center,
                modifier = Modifier.width(40.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(40.dp)
                    .background(barColor, RoundedCornerShape(1.5.dp))
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }
            
            Text(text = "›", fontSize = 20.sp, color = Color(0xFF94A3B8))
        }
    }
}

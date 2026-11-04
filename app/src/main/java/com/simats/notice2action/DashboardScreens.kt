package com.simats.notice2action

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Help
import com.simats.notice2action.data.AppStrings
import com.simats.notice2action.data.AuthRepository
import com.simats.notice2action.data.SessionManager
import androidx.compose.ui.platform.LocalContext
import com.simats.notice2action.data.UserSession

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DashboardScreen(
    user: com.simats.notice2action.data.User? = null,
    onScanDocumentClicked: () -> Unit,
    onUploadDocumentClicked: () -> Unit,
    onProfileClicked: () -> Unit,
    onMenuClicked: () -> Unit,
    onRemindersClicked: () -> Unit = {},
    onMyDocumentsClicked: () -> Unit = {},
    onTasksClicked: () -> Unit = {},
    onCalendarClicked: () -> Unit = {}
) {
    val context = LocalContext.current
    val savedUser = remember { SessionManager.getSavedUser(context) }
    var currentUser by remember { mutableStateOf(user ?: UserSession.currentUser ?: savedUser) }

    LaunchedEffect(user, UserSession.currentUser) {
        val resolved = user ?: UserSession.currentUser ?: SessionManager.getSavedUser(context)
        if (resolved != null) {
            currentUser = resolved
            if (UserSession.currentUser == null) {
                UserSession.currentUser = resolved
            }
        }
    }

    // Automatically load & refresh user profile from SessionManager / Backend
    LaunchedEffect(Unit) {
        val targetId = currentUser?.id ?: SessionManager.getSavedUser(context)?.id
        targetId?.let { id ->
            try {
                val res = AuthRepository.fetchProfile(id)
                res.onSuccess { freshUser ->
                    currentUser = freshUser
                    UserSession.currentUser = freshUser
                    val pass = SessionManager.getSavedPassword(context)
                    SessionManager.saveSession(context, freshUser, pass)
                }
            } catch (_: Exception) {}
        }
    }

    val activeUser = currentUser ?: UserSession.currentUser ?: savedUser
    val displayName = remember(activeUser, UserSession.currentUser) {
        val u = activeUser ?: UserSession.currentUser ?: SessionManager.getSavedUser(context)
        u?.name?.trim()?.ifBlank { null }
            ?: u?.email?.substringBefore("@")?.replaceFirstChar { it.uppercase() }
            ?: SessionManager.getSavedEmail(context).substringBefore("@").replaceFirstChar { it.uppercase() }.ifBlank { "User" }
    }
    val displayEmail = remember(activeUser, UserSession.currentUser) {
        val u = activeUser ?: UserSession.currentUser ?: SessionManager.getSavedUser(context)
        u?.email?.trim()?.ifBlank { null }
            ?: SessionManager.getSavedEmail(context).ifBlank { "Logged in" }
    }
    val displayImagePath = remember(activeUser, UserSession.currentUser) {
        val u = activeUser ?: UserSession.currentUser ?: SessionManager.getSavedUser(context)
        u?.imagePath
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = AppStrings.get("dash_category"),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8),
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${AppStrings.get("dash_good_morning")}, $displayName!",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }

                    IconButton(
                        onClick = onMenuClicked,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onProfileClicked() },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = displayName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = displayEmail,
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }

                    UserAvatar(
                        name = displayName,
                        imagePath = displayImagePath,
                        size = 42.dp,
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFEEF2FF), RoundedCornerShape(20.dp))
                        .padding(20.dp)
                ) {
                    Column {
                        Text(
                            text = AppStrings.get("dash_banner_title"),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B),
                            lineHeight = 24.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = AppStrings.get("dash_banner_sub"),
                            fontSize = 13.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(90.dp)
                            .background(Color(0xFF2563EB), RoundedCornerShape(16.dp))
                            .clickable { onScanDocumentClicked() }
                            .padding(16.dp),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        Column {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = AppStrings.get("dash_scan_document"),
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = AppStrings.get("dash_scan_document"),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(90.dp)
                            .shadow(2.dp, RoundedCornerShape(16.dp))
                            .background(Color.White, RoundedCornerShape(16.dp))
                            .clickable { onUploadDocumentClicked() }
                            .padding(16.dp),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        Column {
                            Icon(
                                imageVector = Icons.Default.UploadFile,
                                contentDescription = AppStrings.get("dash_upload_document"),
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = AppStrings.get("dash_upload_document"),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f).clickable { onMyDocumentsClicked() }) {
                        Text(
                            text = "12",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = AppStrings.get("dash_docs_processed"),
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                    Column(modifier = Modifier.weight(1f).clickable { onTasksClicked() }) {
                        Text(
                            text = "5",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = AppStrings.get("dash_upcoming_actions"),
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = AppStrings.get("dash_recent_docs"),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = AppStrings.get("dash_view_all"),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2563EB),
                        modifier = Modifier.clickable { onMyDocumentsClicked() }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(16.dp))
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .clickable { onMyDocumentsClicked() }
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
                                        .size(40.dp)
                                        .background(Color(0xFFEEF2FF), RoundedCornerShape(10.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Description,
                                        contentDescription = null,
                                        tint = Color(0xFF2563EB),
                                        modifier = Modifier.size(22.dp)
                                    )
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
                                        text = "PDF • 15 Oct 2026 • 2 pages",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.Default.MoreHoriz,
                                contentDescription = null,
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFFEF3C7), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = AppStrings.get("dash_verify_dates"),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFD97706)
                                )
                            }
                            Text(
                                text = "3 ${AppStrings.get("dash_actions_identified")}",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = AppStrings.get("dash_upcoming_deadlines"),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = AppStrings.get("dash_calendar"),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2563EB),
                        modifier = Modifier.clickable { onCalendarClicked() }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(16.dp))
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .clickable { onTasksClicked() }
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .background(Color(0xFFE2E8F0), RoundedCornerShape(4.dp))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Submit project proposal",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Innovation Week 2026",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Event,
                                        contentDescription = null,
                                        tint = Color(0xFF64748B),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "20 Oct • 5:00 PM",
                                        fontSize = 10.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFFEE2E2), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "High",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFDC2626)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            AppBottomBar(
                currentTab = BottomNavTab.HOME,
                onHomeClicked = { },
                onTasksClicked = onTasksClicked,
                onRemindersClicked = onRemindersClicked,
                onProfileClicked = onProfileClicked
            )
        }
    }
}

@Composable
fun NavigationDrawerScreen(
    user: com.simats.notice2action.data.User? = null,
    onClose: () -> Unit,
    onHome: () -> Unit,
    onLogout: () -> Unit,
    onRemindersClicked: () -> Unit = {},
    onCalendarClicked: () -> Unit = {},
    onMyDocumentsClicked: () -> Unit = {},
    onGroupsClicked: () -> Unit = {},
    onSettingsClicked: () -> Unit = {},
    onHelpClicked: () -> Unit = {},
    onTasksClicked: () -> Unit = {}
) {
    val context = LocalContext.current
    val savedUser = remember { SessionManager.getSavedUser(context) }
    val resolvedUser = user ?: UserSession.currentUser ?: savedUser

    val drawerName = remember(resolvedUser, UserSession.currentUser) {
        val u = resolvedUser ?: UserSession.currentUser ?: SessionManager.getSavedUser(context)
        u?.name?.trim()?.ifBlank { null }
            ?: u?.email?.substringBefore("@")?.replaceFirstChar { it.uppercase() }
            ?: SessionManager.getSavedEmail(context).substringBefore("@").replaceFirstChar { it.uppercase() }.ifBlank { "User" }
    }
    val drawerEmail = remember(resolvedUser, UserSession.currentUser) {
        val u = resolvedUser ?: UserSession.currentUser ?: SessionManager.getSavedUser(context)
        u?.email?.trim()?.ifBlank { null }
            ?: SessionManager.getSavedEmail(context).ifBlank { "" }
    }
    val drawerImagePath = remember(resolvedUser, UserSession.currentUser) {
        val u = resolvedUser ?: UserSession.currentUser ?: SessionManager.getSavedUser(context)
        u?.imagePath
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable { onClose() },
        color = Color.Transparent
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.82f)
                    .clickable(enabled = false) {},
                color = Color.White,
                shape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "NOTICE2ACTION",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2563EB),
                                letterSpacing = 1.sp
                            )
                            IconButton(
                                onClick = onClose,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color(0xFF64748B),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        UserAvatar(
                            name = drawerName,
                            imagePath = drawerImagePath,
                            size = 54.dp,
                            fontSize = 22.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = drawerName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = drawerEmail,
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        Spacer(modifier = Modifier.height(16.dp))

                        val menuItems = listOf(
                            Triple(Icons.Default.Home, "menu_home", onHome),
                            Triple(Icons.Default.Description, "menu_documents", onMyDocumentsClicked),
                            Triple(Icons.AutoMirrored.Filled.Assignment, "menu_tasks", onTasksClicked),
                            Triple(Icons.Default.Notifications, "menu_reminders", onRemindersClicked),
                            Triple(Icons.Default.CalendarToday, "menu_calendar", onCalendarClicked),
                            Triple(Icons.Default.Group, "menu_groups", onGroupsClicked),
                            Triple(Icons.Default.Settings, "menu_settings", onSettingsClicked),
                            Triple(Icons.AutoMirrored.Filled.Help, "menu_help", onHelpClicked)
                        )

                        menuItems.forEach { (iconVector, stringKey, clickAction) ->
                            val title = AppStrings.get(stringKey)
                            val isSelected = stringKey == "menu_home"
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) Color(0xFFEEF2FF) else Color.Transparent)
                                    .clickable {
                                        clickAction()
                                    }
                                    .padding(horizontal = 12.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = iconVector,
                                    contentDescription = title,
                                    tint = if (isSelected) Color(0xFF2563EB) else Color(0xFF64748B),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(14.dp))
                                Text(
                                    text = title,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color(0xFF2563EB) else Color(0xFF334155)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onLogout() }
                                .padding(horizontal = 12.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = AppStrings.get("menu_logout"),
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Text(
                                text = AppStrings.get("menu_logout"),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFDC2626)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Column {
                        Text(
                            text = "Notice2Action",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = AppStrings.get("dash_banner_sub"),
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = "Demo workspace • v1.0.0",
                            fontSize = 10.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }
        }
    }
}

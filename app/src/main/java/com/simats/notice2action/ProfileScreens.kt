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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.notice2action.data.AuthRepository
import com.simats.notice2action.data.UserSession
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    onHomeClicked: () -> Unit,
    onRemindersClicked: () -> Unit,
    onLogoutClicked: () -> Unit,
    onSettingsClicked: () -> Unit = {},
    onHelpClicked: () -> Unit = {},
    onTasksClicked: () -> Unit = {},
    onGroupsClicked: () -> Unit = {},
    onLanguageClicked: () -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val savedUser = remember { com.simats.notice2action.data.SessionManager.getSavedUser(context) }
    val initialUser = UserSession.currentUser ?: savedUser

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var userName by remember {
        mutableStateOf(
            initialUser?.name?.trim()?.ifBlank { null }
                ?: initialUser?.email?.substringBefore("@")?.replaceFirstChar { it.uppercase() }
                ?: "User"
        )
    }
    var userEmail by remember { mutableStateOf(initialUser?.email ?: "") }
    var userRole by remember { mutableStateOf(initialUser?.role ?: "Student - Northbridge College") }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val targetId = initialUser?.id ?: com.simats.notice2action.data.SessionManager.getSavedUser(context)?.id
        targetId?.let { id ->
            val res = AuthRepository.fetchProfile(id)
            res.onSuccess { fetched ->
                userName = fetched.name
                userEmail = fetched.email
                userRole = fetched.role
                UserSession.currentUser = fetched
                val pass = com.simats.notice2action.data.SessionManager.getSavedPassword(context)
                com.simats.notice2action.data.SessionManager.saveSession(context, fetched, pass)
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFF8FAFC),
        bottomBar = {
            AppBottomBar(
                currentTab = BottomNavTab.PROFILE,
                onHomeClicked = onHomeClicked,
                onTasksClicked = onTasksClicked,
                onRemindersClicked = onRemindersClicked,
                onProfileClicked = { }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Category Label
            Text(
                text = "H • PROFILE, SETTINGS & SUPPORT",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B),
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Header Title & Gear Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Profile",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Text(
                    text = "⚙️",
                    fontSize = 20.sp,
                    color = Color(0xFF2563EB),
                    modifier = Modifier.clickable { onSettingsClicked() }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Profile Card (Individual user only)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFEEF2FF),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(Color(0xFF2563EB), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            val initial = userName.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "U"
                            Text(
                                text = initial,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = userName,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = userEmail,
                                fontSize = 12.sp,
                                color = Color(0xFF64748B),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = userRole,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF2563EB),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = { showEditProfileDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "✏️", fontSize = 14.sp, color = Color(0xFF2563EB))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Edit Profile",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2563EB)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Settings Section 1 (Individual Settings - Group Items Removed)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 2.dp
            ) {
                Column {
                    ProfileSettingsRow("🔔", "Notifications", "2 reminders enabled", onClick = { onRemindersClicked() })
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    ProfileSettingsRow("🌐", "Language", "English", onClick = { onLanguageClicked() })
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Settings Section 2
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 2.dp
            ) {
                Column {
                    ProfileSettingsRow("👤", "Account settings", "Personal details & security", onClick = { onSettingsClicked() })
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    ProfileSettingsRow("🛡️", "Privacy & data", "Your documents, your control", onClick = { onSettingsClicked() })
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    ProfileSettingsRow("❓", "Help and Support", "Guides and contact options", onClick = { onHelpClicked() })
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Logout Button
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        UserSession.clear()
                        onLogoutClicked()
                    },
                color = Color(0xFFFEF2F2),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🚪", fontSize = 16.sp, color = Color(0xFFEF4444))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Logout",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEF4444)
                        )
                    }
                }
            }
        }
    }

    // Edit Profile Dialog
    if (showEditProfileDialog) {
        var tempName by remember { mutableStateOf(userName) }
        var tempEmail by remember { mutableStateOf(userEmail) }
        var tempRole by remember { mutableStateOf(userRole) }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = {
                Text(text = "Edit Profile", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column {
                        Text(text = "Full Name", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = tempName,
                            onValueChange = { tempName = it },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Column {
                        Text(text = "Email Address", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = tempEmail,
                            onValueChange = { tempEmail = it },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Column {
                        Text(text = "Role & Institution", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = tempRole,
                            onValueChange = { tempRole = it },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val userId = UserSession.currentUser?.id ?: 1
                        userName = tempName
                        userEmail = tempEmail
                        userRole = tempRole
                        showEditProfileDialog = false
                        coroutineScope.launch {
                            AuthRepository.updateProfile(userId, tempName, tempEmail, tempRole)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Save", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text(text = "Cancel", color = Color(0xFF64748B))
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun ProfileSettingsRow(icon: String, title: String, subtitle: String, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, fontSize = 18.sp, color = Color(0xFF2563EB))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f, fill = false)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "›", fontSize = 20.sp, color = Color(0xFF94A3B8))
    }
}

@Composable
fun SettingsAndPrivacyScreen(
    onBackClicked: () -> Unit,
    onLanguageClicked: () -> Unit = {},
    onDeleteAccountClicked: () -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
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
                    text = "H • PROFILE, SETTINGS & SUPPORT",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "←",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF334155),
                            modifier = Modifier
                                .clickable { onBackClicked() }
                                .padding(end = 12.dp)
                        )
                        Text(
                            text = "Settings & privacy",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                    }
                    Text(text = "🛡️", fontSize = 20.sp, color = Color(0xFF2563EB))
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Appearance
                Text(
                    text = "Appearance",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color.White, RoundedCornerShape(6.dp))
                            .shadow(1.dp, RoundedCornerShape(6.dp))
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Light", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Dark", fontSize = 12.sp, color = Color(0xFF64748B))
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "System", fontSize = 12.sp, color = Color(0xFF64748B))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Settings Group 1
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 2.dp
                ) {
                    Column {
                        ProfileSettingsRow("🌐", "Language", "English", onClick = { onLanguageClicked() })
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        ProfileSettingsRow("🔔", "Default reminders", "1 day before - 9:00 AM")
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        ProfileSettingsRow("🔓", "Document privacy", "Private unless you explicitly share")
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        ProfileSettingsRow("🗄️", "Data export", "Documents and tasks - Export ZIP")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Info Banner
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFEEF2FF), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🛡️", fontSize = 16.sp, color = Color(0xFF2563EB))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Sharing requires recipient authorization. No data is stored or exported in this demo.",
                        fontSize = 12.sp,
                        color = Color(0xFF2563EB),
                        lineHeight = 16.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Account control
                Text(
                    text = "Account control",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Delete Account",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Permanently remove your profile, private documents, tasks and reminders.",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B),
                            lineHeight = 16.sp
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    UserSession.currentUser?.id?.let { id ->
                                        AuthRepository.deleteAccount(id)
                                    }
                                    onDeleteAccountClicked()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEF2F2)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🗑️", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Delete Account", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LanguageSettingsScreen(
    onBackClicked: () -> Unit
) {
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
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "←",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF334155),
                            modifier = Modifier
                                .clickable { onBackClicked() }
                                .padding(end = 12.dp)
                        )
                        Text(
                            text = "Language",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                    }
                    Text(text = "🌐", fontSize = 20.sp, color = Color(0xFF2563EB))
                }

                Spacer(modifier = Modifier.height(24.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 2.dp
                ) {
                    Column {
                        LanguageRow("English", true)
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        LanguageRow("Spanish (Español)", false)
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        LanguageRow("French (Français)", false)
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        LanguageRow("German (Deutsch)", false)
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        LanguageRow("Chinese (中文)", false)
                    }
                }
            }
        }
    }
}

@Composable
fun LanguageRow(languageName: String, isSelected: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = languageName,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color(0xFF2563EB) else Color(0xFF1E293B)
        )
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(Color(0xFF2563EB), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "✓", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        } else {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .border(1.dp, Color(0xFFCBD5E1), CircleShape)
            )
        }
    }
}

@Composable
fun HelpSupportScreen(
    onBackClicked: () -> Unit
) {
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
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "←",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF334155),
                            modifier = Modifier
                                .clickable { onBackClicked() }
                                .padding(end = 12.dp)
                        )
                        Text(
                            text = "Help & Support",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                    }
                    Text(text = "❓", fontSize = 20.sp, color = Color(0xFF2563EB))
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Frequently asked questions",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        FaqItem(
                            question = "How do I capture a notice?",
                            answer = "Choose Scan Document or Upload Document. Keep the full page visible and use a clear PDF, JPG or PNG.",
                            isExpanded = true
                        )
                        FaqItem("How do I review extracted actions?", null, false)
                        FaqItem("Can I edit a task or its deadline?", null, false)
                    }
                }
            }
        }
    }
}

@Composable
fun FaqItem(question: String, answer: String?, isExpanded: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = question,
                fontSize = 12.sp,
                fontWeight = if (isExpanded) FontWeight.Bold else FontWeight.Medium,
                color = Color(0xFF334155)
            )
            Text(
                text = if (isExpanded) "︿" else "﹀",
                fontSize = 14.sp,
                color = Color(0xFF64748B),
                fontWeight = FontWeight.Bold
            )
        }
        
        if (isExpanded && answer != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = answer,
                fontSize = 12.sp,
                color = Color(0xFF64748B),
                lineHeight = 16.sp
            )
        }
    }
}

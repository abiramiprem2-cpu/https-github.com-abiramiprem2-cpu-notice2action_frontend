package com.simats.notice2action

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.notice2action.data.AppStrings
import com.simats.notice2action.data.AuthRepository
import com.simats.notice2action.data.LanguageManager
import com.simats.notice2action.data.SessionManager
import com.simats.notice2action.data.User
import com.simats.notice2action.data.UserSession
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

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
    val context = LocalContext.current
    val savedUser = remember { SessionManager.getSavedUser(context) }
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
    var userImagePath by remember { mutableStateOf(initialUser?.imagePath) }

    LaunchedEffect(Unit) {
        val targetId = initialUser?.id ?: SessionManager.getSavedUser(context)?.id
        targetId?.let { id ->
            val res = AuthRepository.fetchProfile(id)
            res.onSuccess { fetched ->
                userName = fetched.name
                userEmail = fetched.email
                userImagePath = fetched.imagePath
                UserSession.currentUser = fetched
                val pass = SessionManager.getSavedPassword(context)
                SessionManager.saveSession(context, fetched, pass)
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
                text = AppStrings.get("prof_category"),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B),
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Header Title & Settings Gear Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = AppStrings.get("prof_title"),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                IconButton(
                    onClick = { onSettingsClicked() },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = AppStrings.get("menu_settings"),
                        tint = Color(0xFF2563EB),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Profile Card (Individual user profile with avatar picture)
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
                        UserAvatar(
                            name = userName,
                            imagePath = userImagePath,
                            size = 58.dp,
                            fontSize = 24.sp
                        )
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
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = AppStrings.get("prof_edit_profile"),
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = AppStrings.get("prof_edit_profile"),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2563EB)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Settings Section 1
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 2.dp
            ) {
                Column {
                    ProfileSettingsRow(
                        icon = Icons.Default.Notifications,
                        title = AppStrings.get("prof_notifications"),
                        subtitle = AppStrings.get("prof_reminders_enabled"),
                        onClick = { onRemindersClicked() }
                    )
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    ProfileSettingsRow(
                        icon = Icons.Default.Language,
                        title = AppStrings.get("prof_language"),
                        subtitle = LanguageManager.getDisplayName(),
                        onClick = { onLanguageClicked() }
                    )
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
                    ProfileSettingsRow(
                        icon = Icons.Default.Person,
                        title = AppStrings.get("prof_account_settings"),
                        subtitle = AppStrings.get("prof_account_settings_sub"),
                        onClick = { onSettingsClicked() }
                    )
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    ProfileSettingsRow(
                        icon = Icons.Default.Security,
                        title = AppStrings.get("prof_privacy_data"),
                        subtitle = AppStrings.get("prof_privacy_data_sub"),
                        onClick = { onSettingsClicked() }
                    )
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    ProfileSettingsRow(
                        icon = Icons.AutoMirrored.Filled.Help,
                        title = AppStrings.get("prof_help_support"),
                        subtitle = AppStrings.get("prof_help_support_sub"),
                        onClick = { onHelpClicked() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Logout Button
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        UserSession.clear()
                        SessionManager.logout(context)
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
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = AppStrings.get("prof_logout"),
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = AppStrings.get("prof_logout"),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEF4444)
                        )
                    }
                }
            }
        }
    }

    // Comprehensive Edit Profile Dialog
    if (showEditProfileDialog) {
        EditProfileDialog(
            currentName = userName,
            currentEmail = userEmail,
            currentImagePath = userImagePath,
            onDismiss = { showEditProfileDialog = false },
            onProfileUpdated = { updatedUser ->
                userName = updatedUser.name
                userEmail = updatedUser.email
                userImagePath = updatedUser.imagePath
                showEditProfileDialog = false
            }
        )
    }
}

@Composable
fun EditProfileDialog(
    currentName: String,
    currentEmail: String,
    currentImagePath: String?,
    onDismiss: () -> Unit,
    onProfileUpdated: (User) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var tempName by remember { mutableStateOf(currentName) }
    var tempEmail by remember { mutableStateOf(currentEmail) }
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var selectedBase64 by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Launcher for picking an image from Gallery
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val originalBitmap = BitmapFactory.decodeStream(stream)
                    if (originalBitmap != null) {
                        // Downscale bitmap if dimensions exceed 800px for efficient storage & transmission
                        val maxDim = 800
                        val width = originalBitmap.width
                        val height = originalBitmap.height
                        val scaledBitmap = if (width > maxDim || height > maxDim) {
                            val ratio = width.toFloat() / height.toFloat()
                            val (w, h) = if (width > height) {
                                maxDim to (maxDim / ratio).toInt()
                            } else {
                                (maxDim * ratio).toInt() to maxDim
                            }
                            Bitmap.createScaledBitmap(originalBitmap, w, h, true)
                        } else {
                            originalBitmap
                        }

                        val baos = ByteArrayOutputStream()
                        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos)
                        val bytes = baos.toByteArray()
                        selectedBase64 = "data:image/jpeg;base64," + Base64.encodeToString(bytes, Base64.NO_WRAP)
                        selectedBitmap = scaledBitmap
                    }
                }
            } catch (e: Exception) {
                errorMessage = "Failed to load image: ${e.localizedMessage}"
            }
        }
    }

    AlertDialog(
        onDismissRequest = { if (!isSaving) onDismiss() },
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = AppStrings.get("edit_prof_title"),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                IconButton(
                    onClick = onDismiss,
                    enabled = !isSaving,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = AppStrings.get("edit_prof_cancel"),
                        tint = Color(0xFF64748B)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Profile Picture with Camera Action Badge
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clickable(enabled = !isSaving) {
                            galleryLauncher.launch("image/*")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    UserAvatar(
                        name = tempName.ifBlank { "User" },
                        imagePath = currentImagePath,
                        previewBitmap = selectedBitmap,
                        size = 96.dp,
                        fontSize = 36.sp
                    )

                    // Camera Icon Overlay Badge at bottom-right
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(30.dp)
                            .background(Color(0xFF2563EB), CircleShape)
                            .border(2.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = AppStrings.get("edit_prof_choose_photo"),
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Change Photo Button
                OutlinedButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    enabled = !isSaving,
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, Color(0xFF2563EB)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF2563EB)),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = AppStrings.get("edit_prof_choose_photo"),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (errorMessage != null) {
                    Surface(
                        color = Color(0xFFFEF2F2),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            color = Color(0xFFEF4444),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                // Full Name Input
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = AppStrings.get("edit_prof_full_name"),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334155)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = {
                            tempName = it
                            errorMessage = null
                        },
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF2563EB))
                        },
                        placeholder = { Text(AppStrings.get("edit_prof_enter_name"), color = Color(0xFF94A3B8)) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isSaving
                    )
                }

                // Email Input
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = AppStrings.get("edit_prof_email"),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334155)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = tempEmail,
                        onValueChange = {
                            tempEmail = it
                            errorMessage = null
                        },
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF2563EB))
                        },
                        placeholder = { Text(AppStrings.get("edit_prof_enter_email"), color = Color(0xFF94A3B8)) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isSaving
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val trimmedName = tempName.trim()
                    val trimmedEmail = tempEmail.trim()

                    if (trimmedName.isBlank()) {
                        errorMessage = "Full Name cannot be empty"
                        return@Button
                    }
                    if (trimmedEmail.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
                        errorMessage = "Please enter a valid email address"
                        return@Button
                    }

                    isSaving = true
                    errorMessage = null
                    val userId = UserSession.currentUser?.id ?: SessionManager.getSavedUser(context)?.id ?: 1

                    coroutineScope.launch {
                        val result = AuthRepository.updateProfile(
                            userId = userId,
                            name = trimmedName,
                            email = trimmedEmail,
                            imageBase64 = selectedBase64
                        )
                        result.onSuccess { updatedUser ->
                            // Cache newly picked bitmap locally to profile_avatar_${userId}.jpg
                            selectedBitmap?.let { bmp ->
                                try {
                                    val cachedFile = File(context.filesDir, "profile_avatar_${userId}.jpg")
                                    FileOutputStream(cachedFile).use { out ->
                                        bmp.compress(Bitmap.CompressFormat.JPEG, 90, out)
                                    }
                                } catch (_: Exception) {}
                            }
                            UserSession.currentUser = updatedUser
                            val pass = SessionManager.getSavedPassword(context)
                            SessionManager.saveSession(context, updatedUser, pass)
                            Toast.makeText(context, AppStrings.get("edit_prof_success"), Toast.LENGTH_SHORT).show()
                            onProfileUpdated(updatedUser)
                        }.onFailure { err ->
                            errorMessage = err.localizedMessage ?: "Failed to update profile. Please try again."
                            isSaving = false
                        }
                    }
                },
                enabled = !isSaving,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                shape = RoundedCornerShape(10.dp)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(AppStrings.get("edit_prof_saving"), color = Color.White)
                } else {
                    Text(AppStrings.get("edit_prof_save"), color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isSaving
            ) {
                Text(text = AppStrings.get("edit_prof_cancel"), color = Color(0xFF64748B))
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun ProfileSettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconTint: Color = Color(0xFF2563EB),
    onClick: () -> Unit = {}
) {
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
                modifier = Modifier
                    .size(36.dp)
                    .background(iconTint.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
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
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color(0xFF94A3B8),
            modifier = Modifier.size(20.dp)
        )
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
                    text = AppStrings.get("prof_category"),
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
                        IconButton(
                            onClick = onBackClicked,
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = AppStrings.get("action_back"),
                                tint = Color(0xFF334155)
                            )
                        }
                        Text(
                            text = AppStrings.get("settings_title"),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = Color(0xFF2563EB),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Appearance
                Text(
                    text = AppStrings.get("settings_appearance"),
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
                        Text(text = AppStrings.get("settings_light"), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = AppStrings.get("settings_dark"), fontSize = 12.sp, color = Color(0xFF64748B))
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = AppStrings.get("settings_system"), fontSize = 12.sp, color = Color(0xFF64748B))
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
                        ProfileSettingsRow(
                            icon = Icons.Default.Language,
                            title = AppStrings.get("prof_language"),
                            subtitle = LanguageManager.getDisplayName(),
                            onClick = { onLanguageClicked() }
                        )
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        ProfileSettingsRow(
                            icon = Icons.Default.Notifications,
                            title = AppStrings.get("settings_default_reminders"),
                            subtitle = AppStrings.get("settings_default_reminders_sub")
                        )
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        ProfileSettingsRow(
                            icon = Icons.Default.LockOpen,
                            title = AppStrings.get("settings_doc_privacy"),
                            subtitle = AppStrings.get("settings_doc_privacy_sub")
                        )
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        ProfileSettingsRow(
                            icon = Icons.Default.Archive,
                            title = AppStrings.get("settings_data_export"),
                            subtitle = AppStrings.get("settings_data_export_sub")
                        )
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
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = Color(0xFF2563EB),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = AppStrings.get("settings_info_banner"),
                        fontSize = 12.sp,
                        color = Color(0xFF2563EB),
                        lineHeight = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Account control
                Text(
                    text = AppStrings.get("settings_account_control"),
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
                            text = AppStrings.get("settings_delete_account"),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = AppStrings.get("settings_delete_account_desc"),
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
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = AppStrings.get("settings_delete_account"),
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = AppStrings.get("settings_delete_account"), color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
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
    val context = LocalContext.current
    val currentLang = LanguageManager.currentLanguage

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
                        IconButton(
                            onClick = onBackClicked,
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = AppStrings.get("action_back"),
                                tint = Color(0xFF334155)
                            )
                        }
                        Text(
                            text = AppStrings.get("lang_title"),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = null,
                        tint = Color(0xFF2563EB),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = AppStrings.get("lang_subtitle"),
                    fontSize = 13.sp,
                    color = Color(0xFF64748B)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    shadowElevation = 2.dp
                ) {
                    Column {
                        LanguageManager.supportedLanguages.forEachIndexed { index, option ->
                            if (index > 0) {
                                HorizontalDivider(color = Color(0xFFF1F5F9))
                            }
                            LanguageRow(
                                englishName = option.nameInEnglish,
                                nativeName = option.nativeName,
                                isSelected = currentLang == option.code,
                                onClick = {
                                    LanguageManager.setLanguage(context, option.code)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LanguageRow(
    englishName: String,
    nativeName: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = englishName,
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color(0xFF2563EB) else Color(0xFF1E293B)
            )
            if (englishName != nativeName) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = nativeName,
                    fontSize = 13.sp,
                    color = if (isSelected) Color(0xFF2563EB) else Color(0xFF64748B)
                )
            }
        }
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(Color(0xFF2563EB), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
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
                        IconButton(
                            onClick = onBackClicked,
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = AppStrings.get("action_back"),
                                tint = Color(0xFF334155)
                            )
                        }
                        Text(
                            text = AppStrings.get("prof_help_support"),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Help,
                        contentDescription = null,
                        tint = Color(0xFF2563EB),
                        modifier = Modifier.size(24.dp)
                    )
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
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = Color(0xFF64748B),
                modifier = Modifier.size(18.dp)
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

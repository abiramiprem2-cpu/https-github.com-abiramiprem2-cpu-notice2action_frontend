package com.simats.notice2action

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.simats.notice2action.data.AuthRepository
import com.simats.notice2action.data.LanguageManager
import com.simats.notice2action.data.NotificationHelper
import com.simats.notice2action.data.SessionManager
import com.simats.notice2action.data.UserSession
import com.simats.notice2action.ui.theme.Notice2actionTheme
import kotlinx.coroutines.delay
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.notice2action.voice.VoiceAssistantDialog
import com.simats.notice2action.voice.VoiceAssistantFloatingButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LanguageManager.init(this)
        NotificationHelper.createNotificationChannel(this)
        enableEdgeToEdge()
        setContent {
            Notice2actionTheme {
                val context = LocalContext.current
                val activity = context as? Activity
                var currentScreen by remember { mutableStateOf(AppScreen.SPLASH) }
                var showVoiceAssistant by remember { mutableStateOf(false) }
                var showExitConfirmDialog by remember { mutableStateOf(false) }

                val notificationPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { }

                LaunchedEffect(Unit) {
                    NotificationHelper.createNotificationChannel(context)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                    try {
                        com.google.firebase.messaging.FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val token = task.result
                                val prefs = context.getSharedPreferences("notice2action_firebase", Context.MODE_PRIVATE)
                                prefs.edit().putString("fcm_token", token).apply()
                                android.util.Log.d("Notice2Action_FCM", "FCM Device Token: $token")
                            }
                        }
                    } catch (_: Exception) {}
                }

                BackHandler {
                    when (currentScreen) {
                        AppScreen.MAIN -> {
                            showExitConfirmDialog = true
                        }
                        AppScreen.SPLASH, AppScreen.ONBOARDING_1, AppScreen.ONBOARDING_2, AppScreen.LOGIN -> {
                            activity?.finish()
                        }
                        AppScreen.TASK_DETAILS, AppScreen.EDIT_TASK -> {
                            currentScreen = AppScreen.MY_TASKS
                        }
                        AppScreen.NOTICE_DETAILS, AppScreen.REVIEW_ACTIONS, AppScreen.UPCOMING_ACTIONS -> {
                            currentScreen = AppScreen.MAIN
                        }
                        AppScreen.DOCUMENT_DETAILS, AppScreen.SHARE_NOTICE -> {
                            currentScreen = AppScreen.MY_DOCUMENTS
                        }
                        AppScreen.LANGUAGE_SETTINGS -> {
                            currentScreen = AppScreen.SETTINGS_PRIVACY
                        }
                        else -> {
                            currentScreen = AppScreen.MAIN
                        }
                    }
                }

                LaunchedEffect(Unit) {
                    LanguageManager.init(context)
                    if (currentScreen == AppScreen.SPLASH) {
                        val savedUser = SessionManager.getSavedUser(context)
                        val savedEmail = SessionManager.getSavedEmail(context)
                        val savedPassword = SessionManager.getSavedPassword(context)
                        val isLoggedIn = SessionManager.isLoggedIn(context)

                        if (isLoggedIn && savedUser != null) {
                            // Instant restore
                            UserSession.currentUser = savedUser
                            // Background refresh session
                            if (savedEmail.isNotBlank() && savedPassword.isNotBlank()) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        val loginRes = AuthRepository.login(savedEmail, savedPassword)
                                        loginRes.onSuccess { freshUser ->
                                            SessionManager.saveSession(context, freshUser, savedPassword)
                                        }
                                    } catch (_: Exception) {}
                                }
                            }
                            delay(1000L)
                            currentScreen = AppScreen.MAIN
                        } else if (savedEmail.isNotBlank() && savedPassword.isNotBlank()) {
                            // Auto-login with saved credentials
                            try {
                                val loginRes = withContext(Dispatchers.IO) {
                                    AuthRepository.login(savedEmail, savedPassword)
                                }
                                if (loginRes.isSuccess) {
                                    val user = loginRes.getOrNull()!!
                                    withContext(Dispatchers.Main) {
                                        UserSession.currentUser = user
                                    }
                                    SessionManager.saveSession(context, user, savedPassword)
                                    delay(800L)
                                    currentScreen = AppScreen.MAIN
                                } else {
                                    delay(1200L)
                                    currentScreen = AppScreen.LOGIN
                                }
                            } catch (_: Exception) {
                                delay(1200L)
                                currentScreen = AppScreen.LOGIN
                            }
                        } else if (savedEmail.isNotBlank()) {
                            // User previously registered/logged in; go straight to login without re-onboarding
                            delay(1200L)
                            currentScreen = AppScreen.LOGIN
                        } else {
                            // First-time app open with zero accounts
                            delay(1600L)
                            currentScreen = AppScreen.ONBOARDING_1
                        }
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    when (currentScreen) {
                        AppScreen.SPLASH -> {
                            SplashScreen()
                        }
                        AppScreen.ONBOARDING_1 -> {
                            OnboardingStep1Screen(
                                onNextClicked = { currentScreen = AppScreen.ONBOARDING_2 },
                                onSkipClicked = { currentScreen = AppScreen.LOGIN }
                            )
                        }
                        AppScreen.ONBOARDING_2 -> {
                            OnboardingStep2Screen(
                                onBackClicked = { currentScreen = AppScreen.ONBOARDING_1 },
                                onNextClicked = { currentScreen = AppScreen.LOGIN },
                                onSkipClicked = { currentScreen = AppScreen.LOGIN }
                            )
                        }
                        AppScreen.LOGIN -> {
                            LoginScreen(
                                onLoginSuccess = { currentScreen = AppScreen.MAIN },
                                onCreateAccountClicked = { currentScreen = AppScreen.CREATE_ACCOUNT },
                                onForgotPasswordClicked = { currentScreen = AppScreen.FORGOT_PASSWORD },
                                onBackClicked = { currentScreen = AppScreen.ONBOARDING_2 }
                            )
                        }
                        AppScreen.CREATE_ACCOUNT -> {
                            CreateAccountScreen(
                                onCreateSuccess = { currentScreen = AppScreen.MAIN },
                                onBackToLogin = { currentScreen = AppScreen.LOGIN }
                            )
                        }
                        AppScreen.FORGOT_PASSWORD -> {
                            ForgotPasswordScreen(
                                onBackToLogin = { currentScreen = AppScreen.LOGIN }
                            )
                        }
                        AppScreen.MAIN -> {
                            val activeUser = UserSession.currentUser ?: SessionManager.getSavedUser(context)
                            DashboardScreen(
                                user = activeUser,
                                onScanDocumentClicked = { currentScreen = AppScreen.SCAN_DOCUMENT },
                                onUploadDocumentClicked = { currentScreen = AppScreen.UPLOAD_DOCUMENT },
                                onProfileClicked = { currentScreen = AppScreen.PROFILE },
                                onMenuClicked = { currentScreen = AppScreen.NAVIGATION_DRAWER },
                                onRemindersClicked = { currentScreen = AppScreen.REMINDERS },
                                onMyDocumentsClicked = { currentScreen = AppScreen.MY_DOCUMENTS },
                                onTasksClicked = { currentScreen = AppScreen.MY_TASKS },
                                onCalendarClicked = { currentScreen = AppScreen.CALENDAR },
                                onVoiceAssistantClicked = { showVoiceAssistant = true }
                            )
                        }
                        AppScreen.SCAN_DOCUMENT -> {
                            ScanDocumentScreen(
                                onBackClicked = { currentScreen = AppScreen.MAIN },
                                onUseDocument = { currentScreen = AppScreen.DOCUMENT_PREVIEW }
                            )
                        }
                        AppScreen.UPLOAD_DOCUMENT -> {
                            UploadDocumentScreen(
                                onBackClicked = { currentScreen = AppScreen.MAIN },
                                onContinue = { currentScreen = AppScreen.DOCUMENT_PREVIEW },
                                onScanClicked = { currentScreen = AppScreen.SCAN_DOCUMENT }
                            )
                        }
                        AppScreen.DOCUMENT_PREVIEW -> {
                            DocumentPreviewScreen(
                                onBackClicked = { currentScreen = AppScreen.UPLOAD_DOCUMENT },
                                onContinue = { currentScreen = AppScreen.NOTICE_DETAILS }
                            )
                        }
                        AppScreen.NOTICE_DETAILS -> {
                            NoticeDetailsScreen(
                                onBackClicked = { currentScreen = AppScreen.DOCUMENT_PREVIEW },
                                onGenerateActions = { currentScreen = AppScreen.REVIEW_ACTIONS }
                            )
                        }
                        AppScreen.REVIEW_ACTIONS -> {
                            ReviewSuggestedActionsScreen(
                                onBackClicked = { currentScreen = AppScreen.NOTICE_DETAILS },
                                onConfirmActions = { currentScreen = AppScreen.UPCOMING_ACTIONS }
                            )
                        }
                        AppScreen.UPCOMING_ACTIONS -> {
                            UpcomingActionsScreen(
                                onBackClicked = { currentScreen = AppScreen.REVIEW_ACTIONS },
                                onHomeClicked = { currentScreen = AppScreen.MAIN }
                            )
                        }
                        AppScreen.NAVIGATION_DRAWER -> {
                            Box(modifier = Modifier.fillMaxSize()) {
                                val activeUser = UserSession.currentUser ?: SessionManager.getSavedUser(context)
                                DashboardScreen(
                                    user = activeUser,
                                    onScanDocumentClicked = { currentScreen = AppScreen.SCAN_DOCUMENT },
                                    onUploadDocumentClicked = { currentScreen = AppScreen.UPLOAD_DOCUMENT },
                                    onProfileClicked = { },
                                    onMenuClicked = { },
                                    onRemindersClicked = { currentScreen = AppScreen.REMINDERS },
                                    onMyDocumentsClicked = { currentScreen = AppScreen.MY_DOCUMENTS },
                                    onTasksClicked = { currentScreen = AppScreen.MY_TASKS },
                                    onCalendarClicked = { currentScreen = AppScreen.CALENDAR }
                                )
                                NavigationDrawerScreen(
                                    user = activeUser,
                                    onClose = { currentScreen = AppScreen.MAIN },
                                    onHome = { currentScreen = AppScreen.MAIN },
                                    onLogout = {
                                        SessionManager.logout(context)
                                        currentScreen = AppScreen.LOGIN
                                    },
                                    onRemindersClicked = { currentScreen = AppScreen.REMINDERS },
                                    onCalendarClicked = { currentScreen = AppScreen.CALENDAR },
                                    onMyDocumentsClicked = { currentScreen = AppScreen.MY_DOCUMENTS },
                                    onGroupsClicked = { currentScreen = AppScreen.YOUR_GROUPS },
                                    onSettingsClicked = { currentScreen = AppScreen.SETTINGS_PRIVACY },
                                    onHelpClicked = { currentScreen = AppScreen.HELP_SUPPORT },
                                    onTasksClicked = { currentScreen = AppScreen.MY_TASKS }
                                )
                            }
                        }
                        AppScreen.REMINDERS -> {
                            RemindersScreen(
                                onHomeClicked = { currentScreen = AppScreen.MAIN },
                                onProfileClicked = { currentScreen = AppScreen.PROFILE },
                                onSettingsClicked = { currentScreen = AppScreen.REMINDER_SETTINGS },
                                onTasksClicked = { currentScreen = AppScreen.MY_TASKS },
                                onUploadClicked = { currentScreen = AppScreen.UPLOAD_DOCUMENT }
                            )
                        }
                        AppScreen.REMINDER_SETTINGS -> {
                            ReminderSettingsScreen(
                                onBackClicked = { currentScreen = AppScreen.REMINDERS }
                            )
                        }
                        AppScreen.PROFILE -> {
                            ProfileScreen(
                                onHomeClicked = { currentScreen = AppScreen.MAIN },
                                onRemindersClicked = { currentScreen = AppScreen.REMINDERS },
                                onLogoutClicked = {
                                    SessionManager.logout(context)
                                    currentScreen = AppScreen.LOGIN
                                },
                                onSettingsClicked = { currentScreen = AppScreen.SETTINGS_PRIVACY },
                                onHelpClicked = { currentScreen = AppScreen.HELP_SUPPORT },
                                onTasksClicked = { currentScreen = AppScreen.MY_TASKS },
                                onGroupsClicked = { currentScreen = AppScreen.YOUR_GROUPS },
                                onLanguageClicked = { currentScreen = AppScreen.LANGUAGE_SETTINGS }
                            )
                        }
                        AppScreen.SETTINGS_PRIVACY -> {
                            SettingsAndPrivacyScreen(
                                onBackClicked = { currentScreen = AppScreen.PROFILE },
                                onLanguageClicked = { currentScreen = AppScreen.LANGUAGE_SETTINGS },
                                onDeleteAccountClicked = { currentScreen = AppScreen.LOGIN }
                            )
                        }
                        AppScreen.LANGUAGE_SETTINGS -> {
                            LanguageSettingsScreen(
                                onBackClicked = { currentScreen = AppScreen.SETTINGS_PRIVACY }
                            )
                        }
                        AppScreen.HELP_SUPPORT -> {
                            HelpSupportScreen(
                                onBackClicked = { currentScreen = AppScreen.PROFILE }
                            )
                        }
                        AppScreen.CALENDAR -> {
                            CalendarScreen(
                                onBackClicked = { currentScreen = AppScreen.MAIN },
                                onAddTaskClicked = { currentScreen = AppScreen.EDIT_TASK }
                            )
                        }
                        AppScreen.MY_DOCUMENTS -> {
                            MyDocumentsScreen(
                                onBackClicked = { currentScreen = AppScreen.MAIN },
                                onUploadDocumentClicked = { currentScreen = AppScreen.UPLOAD_DOCUMENT },
                                onDocumentClicked = { currentScreen = AppScreen.DOCUMENT_DETAILS }
                            )
                        }
                        AppScreen.DOCUMENT_DETAILS -> {
                            DocumentDetailsScreen(
                                onBackClicked = { currentScreen = AppScreen.MY_DOCUMENTS },
                                onShareClicked = { currentScreen = AppScreen.SHARE_NOTICE }
                            )
                        }
                        AppScreen.SHARE_NOTICE -> {
                            ShareNoticeScreen(
                                onBackClicked = { currentScreen = AppScreen.DOCUMENT_DETAILS }
                            )
                        }
                        AppScreen.MY_TASKS -> {
                            MyTasksScreen(
                                onHomeClicked = { currentScreen = AppScreen.MAIN },
                                onRemindersClicked = { currentScreen = AppScreen.REMINDERS },
                                onProfileClicked = { currentScreen = AppScreen.PROFILE },
                                onTaskClicked = { currentScreen = AppScreen.TASK_DETAILS },
                                onAddTaskClicked = { currentScreen = AppScreen.EDIT_TASK },
                                onUploadClicked = { currentScreen = AppScreen.UPLOAD_DOCUMENT }
                            )
                        }
                        AppScreen.TASK_DETAILS -> {
                            TaskDetailsScreen(
                                onBackClicked = { currentScreen = AppScreen.MY_TASKS },
                                onEditClicked = { currentScreen = AppScreen.EDIT_TASK }
                            )
                        }
                        AppScreen.EDIT_TASK -> {
                            EditTaskScreen(
                                onBackClicked = { currentScreen = AppScreen.TASK_DETAILS }
                            )
                        }
                        AppScreen.YOUR_GROUPS -> {
                            YourGroupsScreen(
                                onBackClicked = { currentScreen = AppScreen.MAIN },
                                onGroupClicked = { currentScreen = AppScreen.GROUP_DETAILS }
                            )
                        }
                        AppScreen.GROUP_DETAILS -> {
                            GroupDetailsScreen(
                                onBackClicked = { currentScreen = AppScreen.YOUR_GROUPS },
                                onPublishNoticeClicked = { currentScreen = AppScreen.PUBLISH_NOTICE }
                            )
                        }
                        AppScreen.PUBLISH_NOTICE -> {
                            PublishNoticeScreen(
                                onBackClicked = { currentScreen = AppScreen.GROUP_DETAILS }
                            )
                        }
                    }

                    if (currentScreen != AppScreen.SPLASH &&
                        currentScreen != AppScreen.ONBOARDING_1 &&
                        currentScreen != AppScreen.ONBOARDING_2 &&
                        currentScreen != AppScreen.LOGIN &&
                        currentScreen != AppScreen.CREATE_ACCOUNT &&
                        currentScreen != AppScreen.FORGOT_PASSWORD
                    ) {
                        VoiceAssistantFloatingButton(
                            onClick = { showVoiceAssistant = true },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(end = 20.dp, bottom = 80.dp)
                        )
                    }

                    if (showVoiceAssistant) {
                        VoiceAssistantDialog(
                            onDismiss = { showVoiceAssistant = false },
                            onNavigate = { screen ->
                                currentScreen = screen
                                showVoiceAssistant = false
                            }
                        )
                    }

                    if (showExitConfirmDialog) {
                        AlertDialog(
                            onDismissRequest = { showExitConfirmDialog = false },
                            title = {
                                Text(
                                    text = "Exit Notice2Action?",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                            },
                            text = {
                                Text(
                                    text = "Are you sure you want to exit the application?",
                                    fontSize = 13.sp,
                                    color = Color(0xFF475569)
                                )
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        showExitConfirmDialog = false
                                        activity?.finish()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(text = "Exit", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showExitConfirmDialog = false }) {
                                    Text(text = "Cancel", color = Color(0xFF64748B))
                                }
                            },
                            containerColor = Color.White,
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }
            }
        }
    }
}

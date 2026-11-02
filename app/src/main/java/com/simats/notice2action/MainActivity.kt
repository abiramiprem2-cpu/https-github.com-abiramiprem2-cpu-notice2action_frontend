package com.simats.notice2action

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.simats.notice2action.data.AuthRepository
import com.simats.notice2action.data.SessionManager
import com.simats.notice2action.data.UserSession
import com.simats.notice2action.ui.theme.Notice2actionTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Notice2actionTheme {
                val context = LocalContext.current
                var currentScreen by remember { mutableStateOf(AppScreen.SPLASH) }

                LaunchedEffect(Unit) {
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
                            DashboardScreen(
                                onScanDocumentClicked = { currentScreen = AppScreen.SCAN_DOCUMENT },
                                onUploadDocumentClicked = { currentScreen = AppScreen.UPLOAD_DOCUMENT },
                                onProfileClicked = { currentScreen = AppScreen.PROFILE },
                                onMenuClicked = { currentScreen = AppScreen.NAVIGATION_DRAWER },
                                onRemindersClicked = { currentScreen = AppScreen.REMINDERS },
                                onMyDocumentsClicked = { currentScreen = AppScreen.MY_DOCUMENTS },
                                onTasksClicked = { currentScreen = AppScreen.MY_TASKS },
                                onCalendarClicked = { currentScreen = AppScreen.CALENDAR }
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
                                onContinue = { currentScreen = AppScreen.DOCUMENT_PREVIEW }
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
                                DashboardScreen(
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
                                onTasksClicked = { currentScreen = AppScreen.MY_TASKS }
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
                                onAddTaskClicked = { currentScreen = AppScreen.EDIT_TASK }
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
                }
            }
        }
    }
}

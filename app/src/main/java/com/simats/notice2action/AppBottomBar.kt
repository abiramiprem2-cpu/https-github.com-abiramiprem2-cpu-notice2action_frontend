package com.simats.notice2action

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.notice2action.data.AppStrings

enum class BottomNavTab {
    HOME,
    TASKS,
    REMINDERS,
    PROFILE
}

@Composable
fun AppBottomBar(
    currentTab: BottomNavTab,
    onHomeClicked: () -> Unit = {},
    onTasksClicked: () -> Unit = {},
    onRemindersClicked: () -> Unit = {},
    onProfileClicked: () -> Unit = {}
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        NavigationBarItem(
            selected = currentTab == BottomNavTab.HOME,
            onClick = onHomeClicked,
            icon = {
                Icon(
                    imageVector = if (currentTab == BottomNavTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = AppStrings.get("nav_home"),
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = AppStrings.get("nav_home"),
                    fontSize = 11.sp,
                    fontWeight = if (currentTab == BottomNavTab.HOME) FontWeight.Bold else FontWeight.Medium
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF2563EB),
                selectedTextColor = Color(0xFF2563EB),
                indicatorColor = Color(0xFFDBEAFE),
                unselectedIconColor = Color(0xFF64748B),
                unselectedTextColor = Color(0xFF64748B)
            )
        )

        NavigationBarItem(
            selected = currentTab == BottomNavTab.TASKS,
            onClick = onTasksClicked,
            icon = {
                Icon(
                    imageVector = if (currentTab == BottomNavTab.TASKS) Icons.AutoMirrored.Filled.Assignment else Icons.AutoMirrored.Outlined.Assignment,
                    contentDescription = AppStrings.get("nav_tasks"),
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = AppStrings.get("nav_tasks"),
                    fontSize = 11.sp,
                    fontWeight = if (currentTab == BottomNavTab.TASKS) FontWeight.Bold else FontWeight.Medium
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF2563EB),
                selectedTextColor = Color(0xFF2563EB),
                indicatorColor = Color(0xFFDBEAFE),
                unselectedIconColor = Color(0xFF64748B),
                unselectedTextColor = Color(0xFF64748B)
            )
        )

        NavigationBarItem(
            selected = currentTab == BottomNavTab.REMINDERS,
            onClick = onRemindersClicked,
            icon = {
                Icon(
                    imageVector = if (currentTab == BottomNavTab.REMINDERS) Icons.Filled.Notifications else Icons.Outlined.Notifications,
                    contentDescription = AppStrings.get("nav_reminders"),
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = AppStrings.get("nav_reminders"),
                    fontSize = 11.sp,
                    fontWeight = if (currentTab == BottomNavTab.REMINDERS) FontWeight.Bold else FontWeight.Medium
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF2563EB),
                selectedTextColor = Color(0xFF2563EB),
                indicatorColor = Color(0xFFDBEAFE),
                unselectedIconColor = Color(0xFF64748B),
                unselectedTextColor = Color(0xFF64748B)
            )
        )

        NavigationBarItem(
            selected = currentTab == BottomNavTab.PROFILE,
            onClick = onProfileClicked,
            icon = {
                Icon(
                    imageVector = if (currentTab == BottomNavTab.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                    contentDescription = AppStrings.get("nav_profile"),
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = AppStrings.get("nav_profile"),
                    fontSize = 11.sp,
                    fontWeight = if (currentTab == BottomNavTab.PROFILE) FontWeight.Bold else FontWeight.Medium
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF2563EB),
                selectedTextColor = Color(0xFF2563EB),
                indicatorColor = Color(0xFFDBEAFE),
                unselectedIconColor = Color(0xFF64748B),
                unselectedTextColor = Color(0xFF64748B)
            )
        )
    }
}

package com.simats.notice2action

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.notice2action.data.ApiConfig
import com.simats.notice2action.data.UserSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL

@Composable
fun UserAvatar(
    name: String,
    imagePath: String? = null,
    previewBitmap: Bitmap? = null,
    size: Dp = 56.dp,
    fontSize: TextUnit = 24.sp,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFF2563EB),
    textColor: Color = Color.White,
    onClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var loadedBitmap by remember(previewBitmap, imagePath) { mutableStateOf<Bitmap?>(previewBitmap) }
    val userId = UserSession.currentUser?.id ?: 0

    LaunchedEffect(imagePath, previewBitmap, userId) {
        if (previewBitmap != null) {
            loadedBitmap = previewBitmap
            return@LaunchedEffect
        }

        val cachedFile = File(context.filesDir, "profile_avatar_${userId}.jpg")
        if (cachedFile.exists() && cachedFile.length() > 0) {
            try {
                val cachedBmp = BitmapFactory.decodeFile(cachedFile.absolutePath)
                if (cachedBmp != null) {
                    loadedBitmap = cachedBmp
                }
            } catch (_: Exception) {}
        }

        if (!imagePath.isNullOrBlank()) {
            val fullUrl = ApiConfig.getFullImageUrl(imagePath)
            if (fullUrl != null) {
                withContext(Dispatchers.IO) {
                    try {
                        val connection = URL(fullUrl).openConnection()
                        connection.connectTimeout = 6000
                        connection.readTimeout = 6000
                        connection.getInputStream().use { stream ->
                            val netBmp = BitmapFactory.decodeStream(stream)
                            if (netBmp != null) {
                                try {
                                    FileOutputStream(cachedFile).use { out ->
                                        netBmp.compress(Bitmap.CompressFormat.JPEG, 90, out)
                                    }
                                } catch (_: Exception) {}
                                withContext(Dispatchers.Main) {
                                    loadedBitmap = netBmp
                                }
                            }
                        }
                    } catch (_: Exception) {}
                }
            }
        }
    }

    val clickableMod = if (onClick != null) {
        modifier.clickable { onClick() }
    } else {
        modifier
    }

    val displayBitmap = previewBitmap ?: loadedBitmap

    if (displayBitmap != null) {
        Image(
            bitmap = displayBitmap.asImageBitmap(),
            contentDescription = "Profile Picture",
            contentScale = ContentScale.Crop,
            modifier = clickableMod
                .size(size)
                .clip(CircleShape)
        )
    } else {
        val initial = name.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "U"
        Box(
            modifier = clickableMod
                .size(size)
                .background(backgroundColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initial,
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}


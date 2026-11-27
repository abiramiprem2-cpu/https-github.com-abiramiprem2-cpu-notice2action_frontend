package com.simats.notice2action

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Notice2ActionLogo(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    isRound: Boolean = false,
    showGlow: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    val shape = if (isRound) CircleShape else RoundedCornerShape(size * 0.22f)

    Box(
        modifier = modifier
            .size(size)
            .then(
                if (showGlow) {
                    Modifier.shadow(
                        elevation = (size * 0.12f).coerceIn(4.dp, 16.dp),
                        shape = shape,
                        ambientColor = Color(0xFF2563EB),
                        spotColor = Color(0xFF1E3A8A)
                    )
                } else Modifier
            )
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF1E1B4B),
                        Color(0xFF1E3A8A),
                        Color(0xFF2563EB)
                    )
                )
            )
            .border(
                width = (size * 0.02f).coerceAtLeast(1.dp),
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.35f),
                        Color(0xFF38BDF8).copy(alpha = 0.2f),
                        Color.Transparent
                    )
                ),
                shape = shape
            )
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = if (isRound) R.drawable.app_logo_round else R.drawable.app_logo_squircle),
            contentDescription = "Notice2Action Logo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun Notice2ActionBrandingHeader(
    modifier: Modifier = Modifier,
    logoSize: Dp = 44.dp,
    titleSize: Int = 20,
    subtitle: String = "Turn Documents Into Actions",
    isLightMode: Boolean = true
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Notice2ActionLogo(
            size = logoSize,
            isRound = false,
            showGlow = true
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Notice",
                    fontSize = titleSize.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isLightMode) Color(0xFF0F172A) else Color.White
                )
                Text(
                    text = "2",
                    fontSize = titleSize.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF2563EB)
                )
                Text(
                    text = "Action",
                    fontSize = titleSize.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isLightMode) Color(0xFF0F172A) else Color.White
                )
            }
            if (subtitle.isNotBlank()) {
                Text(
                    text = subtitle,
                    fontSize = (titleSize * 0.55f).toInt().coerceAtLeast(11).sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isLightMode) Color(0xFF64748B) else Color.White.copy(alpha = 0.75f)
                )
            }
        }
    }
}

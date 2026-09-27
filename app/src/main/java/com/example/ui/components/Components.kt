package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.AppleFontFamily
import com.example.ui.theme.AppleGlassShadowLight
import com.example.ui.theme.SkylineBlue
import com.example.ui.theme.SkylineCyan
import com.example.ui.theme.SkylineError
import com.example.ui.theme.SkylineOffline
import com.example.ui.theme.SkylineOnline
import com.example.ui.theme.SkylineSuccess
import com.example.ui.theme.isAppInDarkTheme
import com.example.ui.util.HapticFeedbackHelper

/**
 * iPhone 17 Pro Max Titanium Ambient Backdrop container with fluid optical refraction.
 */
@Composable
fun AppleAmbientBackground(
    modifier: Modifier = Modifier,
    isDark: Boolean = isAppInDarkTheme(),
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                if (isDark) {
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF030712),
                            Color(0xFF071120),
                            Color(0xFF050B16),
                            Color(0xFF020409)
                        )
                    )
                } else {
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFFF6F8FD),
                            Color(0xFFFFFFFF),
                            Color(0xFFEFF5FC),
                            Color(0xFFF8FAFD)
                        )
                    )
                }
            )
    ) {
        // iPhone 17 Pro Max Titanium Ambient Glow Orbs for Fluid Glass Refraction
        if (!isDark) {
            // Natural Liquid Blue Aura Top-Right
            Box(
                modifier = Modifier
                    .size(320.dp)
                    .offset(x = 110.dp, y = (-50).dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                SkylineBlue.copy(alpha = 0.16f),
                                SkylineCyan.copy(alpha = 0.08f),
                                Color.Transparent
                            )
                        )
                    )
            )
            // Vibrant Liquid Cyan Bottom-Left Aura
            Box(
                modifier = Modifier
                    .size(340.dp)
                    .align(Alignment.BottomStart)
                    .offset(x = (-90).dp, y = 80.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                SkylineCyan.copy(alpha = 0.14f),
                                Color(0xFFBAE6FD).copy(alpha = 0.08f),
                                Color.Transparent
                            )
                        )
                    )
            )
            // Delicate Center Ambient Sheen
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .align(Alignment.CenterEnd)
                    .offset(x = 60.dp, y = 40.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                Color(0xFF5856D6).copy(alpha = 0.05f),
                                Color.Transparent
                            )
                        )
                    )
            )
        } else {
            // Deep Titanium Sapphire / OLED Orbs
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .offset(x = 100.dp, y = (-60).dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                SkylineBlue.copy(alpha = 0.22f),
                                Color.Transparent
                            )
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .size(320.dp)
                    .align(Alignment.BottomStart)
                    .offset(x = (-80).dp, y = 80.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                SkylineCyan.copy(alpha = 0.14f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        content()
    }
}

/**
 * iPhone 17 Pro Max Interactive Dynamic Island Widget
 * Floats at the top with live status, pulsating live indicator, and smooth spring expansion.
 */
@Composable
fun IPhone17DynamicIsland(
    isOnline: Boolean,
    remainingDays: Int,
    profileName: String,
    ipAddress: String,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    // Pulsing green live activity dot animation
    val infiniteTransition = rememberInfiniteTransition(label = "island_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        // Dynamic Island Body (OLED Ceramic Surface with Titanium Bevel)
        Box(
            modifier = Modifier
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                )
                .shadow(
                    elevation = if (isExpanded) 14.dp else 8.dp,
                    shape = RoundedCornerShape(if (isExpanded) 30.dp else 50.dp),
                    ambientColor = Color(0x66000000),
                    spotColor = if (isOnline) SkylineBlue.copy(alpha = 0.4f) else Color(0x33000000)
                )
                .clip(RoundedCornerShape(if (isExpanded) 30.dp else 50.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF000000),
                            Color(0xFF0A0F1A),
                            Color(0xFF050810)
                        )
                    )
                )
                .border(
                    width = 1.2.dp,
                    brush = Brush.linearGradient(
                        listOf(
                            Color(0x884A6FA5),
                            Color(0x331E293B),
                            Color(0x5538BDF8)
                        )
                    ),
                    shape = RoundedCornerShape(if (isExpanded) 30.dp else 50.dp)
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    HapticFeedbackHelper.performClick(context, haptic)
                    isExpanded = !isExpanded
                }
                .padding(horizontal = if (isExpanded) 18.dp else 14.dp, vertical = if (isExpanded) 14.dp else 8.dp)
                .testTag("iphone_dynamic_island")
        ) {
            if (!isExpanded) {
                // Collapsed Dynamic Island Capsule
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.width(260.dp)
                ) {
                    // Left Live Status Indicator
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(
                                    (if (isOnline) SkylineOnline else SkylineOffline).copy(alpha = pulseAlpha)
                                )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isOnline) "SKYLINE LIVE" else "OFFLINE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isOnline) SkylineCyan else Color(0xFF94A3B8),
                            letterSpacing = 1.2.sp
                        )
                    }

                    // Right Remaining Days Capsule
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "$remainingDays يوم",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (remainingDays <= 3) SkylineError else Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = if (isOnline) Icons.Default.Wifi else Icons.Default.WifiOff,
                            contentDescription = null,
                            tint = if (isOnline) SkylineOnline else SkylineOffline,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            } else {
                // Expanded iPhone 17 Pro Max Live Activity Pane
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Island Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(
                                        (if (isOnline) SkylineOnline else SkylineOffline).copy(alpha = pulseAlpha)
                                    )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SKYLINE LIVE ACTIVITY",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = SkylineCyan,
                                letterSpacing = 1.5.sp
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = "تصغير الجزيرة",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Metrics Grid (Titanium Glass Capsules)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Days Left Capsule
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(18.dp))
                                .background(Color(0xFF0F1A2E))
                                .border(1.dp, Color(0x4038BDF8), RoundedCornerShape(18.dp))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$remainingDays",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (remainingDays <= 3) SkylineError else Color.White
                                )
                                Text(
                                    text = "الأيام المتبقية",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Plan & Status Capsule
                        Box(
                            modifier = Modifier
                                .weight(1.3f)
                                .clip(RoundedCornerShape(18.dp))
                                .background(Color(0xFF0F1A2E))
                                .border(1.dp, Color(0x4038BDF8), RoundedCornerShape(18.dp))
                                .padding(12.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.Start) {
                                Text(
                                    text = profileName.ifBlank { "2-Nitro" },
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (isOnline) "الخدمة نشطة" else "غير متصل بالشبكة",
                                    fontSize = 10.5.sp,
                                    color = if (isOnline) SkylineOnline else Color(0xFF94A3B8),
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Island Bottom Action Pill
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF162540))
                            .clickable {
                                HapticFeedbackHelper.performClick(context, haptic)
                                onRefresh()
                            }
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = SkylineCyan,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "تحديث حالة الاتصال الحية",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

/**
 * iOS Liquid Glass Glossy Squircle Icon Component (matching the reference design).
 * Features radiant multi-stop gradients, Apple continuous squircle curvature,
 * specular top glossy light refraction, and glowing colorful drop shadow.
 */
@Composable
fun LiquidGlassSquircleIcon(
    gradient: Brush,
    modifier: Modifier = Modifier,
    size: Dp = 46.dp,
    shapeRadius: Dp = 14.dp,
    glowColor: Color = Color.Transparent,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .size(size)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(shapeRadius),
                ambientColor = glowColor.copy(alpha = 0.25f),
                spotColor = glowColor.copy(alpha = 0.45f)
            )
            .clip(RoundedCornerShape(shapeRadius))
            .background(gradient)
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.60f),
                        Color.White.copy(alpha = 0.18f),
                        Color.Transparent
                    )
                ),
                shape = RoundedCornerShape(shapeRadius)
            ),
        contentAlignment = Alignment.Center
    ) {
        // Specular Top Gloss Highlight (Liquid Glass Reflection)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(size * 0.45f)
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.38f),
                            Color.White.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )
        )
        content()
    }
}

// Predefined Liquid Glass Icon Gradient Palettes matching the reference image
object LiquidGradients {
    val WhatsApp = Brush.verticalGradient(listOf(Color(0xFF2FE677), Color(0xFF1EBE5D), Color(0xFF075E54)))
    val Instagram = Brush.linearGradient(listOf(Color(0xFF833AB4), Color(0xFFE1306C), Color(0xFFFD1D1D), Color(0xFFFCB045)))
    val Telegram = Brush.verticalGradient(listOf(Color(0xFF38BDF8), Color(0xFF0284C7), Color(0xFF0369A1)))
    val Signal = Brush.verticalGradient(listOf(Color(0xFF4F46E5), Color(0xFF3B82F6), Color(0xFF1D4ED8)))
    val Threads = Brush.verticalGradient(listOf(Color(0xFF27272A), Color(0xFF18181B), Color(0xFF09090B)))
    val SkylineSpeed = Brush.linearGradient(listOf(Color(0xFF00C6FF), Color(0xFF0072FF)))
    val SkylineRenew = Brush.linearGradient(listOf(Color(0xFFA855F7), Color(0xFF6366F1)))
    val SkylineCall = Brush.linearGradient(listOf(Color(0xFFFB923C), Color(0xFFEA580C)))
    val SkylineSupport = Brush.linearGradient(listOf(Color(0xFF34D399), Color(0xFF059669)))
}

/**
 * iPhone 17 Pro Max Floating Frosted Glass Dock (Bottom Navigation Capsule)
 * Crafted after the reference Liquid Glass floating pill design with internal
 * sliding active pill capsule, unread notification bubble badge, and tactile haptic response.
 */
@Composable
fun IPhoneFloatingDock(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    unreadNotificationsCount: Int = 0
) {
    val isDark = isAppInDarkTheme()
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    val tabs = listOf(
        Triple("الرئيسية", Icons.Default.Home, 0),
        Triple("الاشتراك", Icons.Default.CardMembership, 1),
        Triple("الإشعارات", Icons.Default.Notifications, 2),
        Triple("المطور", Icons.Default.Code, 3)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        // Frosted Floating Glass Pill Container
        Box(
            modifier = Modifier
                .shadow(
                    elevation = 18.dp,
                    shape = RoundedCornerShape(40.dp),
                    ambientColor = if (isDark) Color(0x99000000) else Color(0x1A0E2147),
                    spotColor = SkylineBlue.copy(alpha = if (isDark) 0.35f else 0.18f)
                )
                .clip(RoundedCornerShape(40.dp))
                .background(
                    if (isDark) {
                        Brush.verticalGradient(
                            listOf(
                                Color(0xF50D172A),
                                Color(0xFA070E1A)
                            )
                        )
                    } else {
                        Brush.verticalGradient(
                            listOf(
                                Color(0xF2FFFFFF),
                                Color(0xEAFFFFFF),
                                Color(0xE0EFF6FF)
                            )
                        )
                    }
                )
                .border(
                    width = 1.4.dp,
                    brush = if (isDark) {
                        Brush.verticalGradient(
                            listOf(
                                Color(0x6638BDF8),
                                Color(0x221E3A5F),
                                Color(0x100F172A)
                            )
                        )
                    } else {
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFFFFFFFF),
                                Color(0xCCFFFFFF),
                                Color(0x5094A3B8)
                            )
                        )
                    },
                    shape = RoundedCornerShape(40.dp)
                )
                .padding(horizontal = 8.dp, vertical = 7.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                tabs.forEach { (title, icon, index) ->
                    val isSelected = selectedTab == index

                    // Active Tab Capsule Pill (Liquid Glass Highlight)
                    val activeBg = if (isSelected) {
                        if (isDark) {
                            Brush.verticalGradient(
                                listOf(
                                    Color(0x3338BDF8),
                                    Color(0x1A0284C7)
                                )
                            )
                        } else {
                            Brush.verticalGradient(
                                listOf(
                                    Color(0x280284C7),
                                    Color(0x1538BDF8)
                                )
                            )
                        }
                    } else {
                        Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent))
                    }

                    val iconTint = if (isSelected) SkylineBlue else if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(30.dp))
                            .background(activeBg)
                            .then(
                                if (isSelected) {
                                    Modifier.border(
                                        width = 1.dp,
                                        brush = Brush.verticalGradient(
                                            listOf(
                                                (if (isDark) SkylineCyan else SkylineBlue).copy(alpha = 0.5f),
                                                Color.Transparent
                                            )
                                        ),
                                        shape = RoundedCornerShape(30.dp)
                                    )
                                } else Modifier
                            )
                            .clickable {
                                HapticFeedbackHelper.performClick(context, haptic)
                                onTabSelected(index)
                            }
                            .padding(horizontal = 14.dp, vertical = 9.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = title,
                                    tint = iconTint,
                                    modifier = Modifier.size(22.dp)
                                )

                                // Notification Bubble Badge (like WhatsApp / Telegram in reference mockup)
                                if (index == 2 && unreadNotificationsCount > 0) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .offset(x = 6.dp, y = (-4).dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF25D366))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = if (unreadNotificationsCount > 99) "99+" else "$unreadNotificationsCount",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.White
                                        )
                                    }
                                }
                            }

                            if (isSelected) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = title,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SkylineBlue
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * iPhone 17 Pro Max Control Center Action Grid (Quick Management Tiles)
 * Powered with Liquid Glass Glossy Squircle Icons
 */
@Composable
fun IPhoneControlCenterGrid(
    onNavigateToSubscription: () -> Unit,
    onOpenRenewDialog: () -> Unit,
    onOpenSupport: () -> Unit,
    onCallAgent: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isAppInDarkTheme()
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "مركز التحكم السريع (Control Center)",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Liquid Glass",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = SkylineCyan,
                letterSpacing = 0.8.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Tile 1: Quick Renew
            LiquidGlassCard(
                modifier = Modifier
                    .weight(1f)
                    .height(104.dp),
                shape = RoundedCornerShape(22.dp),
                isDark = isDark,
                elevation = 5.dp,
                onClick = {
                    HapticFeedbackHelper.performClick(context, haptic)
                    onOpenRenewDialog()
                },
                testTag = "control_tile_renew"
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    LiquidGlassSquircleIcon(
                        gradient = LiquidGradients.SkylineRenew,
                        size = 36.dp,
                        shapeRadius = 11.dp,
                        glowColor = Color(0xFFA855F7)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CreditCard,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                    Text(
                        text = "تجديد وتفعيل",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Tile 2: Subscription Details
            LiquidGlassCard(
                modifier = Modifier
                    .weight(1f)
                    .height(104.dp),
                shape = RoundedCornerShape(22.dp),
                isDark = isDark,
                elevation = 5.dp,
                onClick = {
                    HapticFeedbackHelper.performClick(context, haptic)
                    onNavigateToSubscription()
                },
                testTag = "control_tile_details"
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    LiquidGlassSquircleIcon(
                        gradient = LiquidGradients.SkylineSpeed,
                        size = 36.dp,
                        shapeRadius = 11.dp,
                        glowColor = Color(0xFF00C6FF)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CardMembership,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                    Text(
                        text = "تفاصيل الباقة",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Tile 3: WhatsApp Support (Emerald Liquid Squircle)
            LiquidGlassCard(
                modifier = Modifier
                    .weight(1f)
                    .height(104.dp),
                shape = RoundedCornerShape(22.dp),
                isDark = isDark,
                elevation = 5.dp,
                onClick = {
                    HapticFeedbackHelper.performClick(context, haptic)
                    onOpenSupport()
                },
                testTag = "control_tile_whatsapp"
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    LiquidGlassSquircleIcon(
                        gradient = LiquidGradients.WhatsApp,
                        size = 36.dp,
                        shapeRadius = 11.dp,
                        glowColor = Color(0xFF25D366)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_whatsapp),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = "واتساب الدعم",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Tile 4: Agent Call (Amber Liquid Squircle)
            LiquidGlassCard(
                modifier = Modifier
                    .weight(1f)
                    .height(104.dp),
                shape = RoundedCornerShape(22.dp),
                isDark = isDark,
                elevation = 5.dp,
                onClick = {
                    HapticFeedbackHelper.performClick(context, haptic)
                    onCallAgent()
                },
                testTag = "control_tile_call"
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    LiquidGlassSquircleIcon(
                        gradient = LiquidGradients.SkylineCall,
                        size = 36.dp,
                        shapeRadius = 11.dp,
                        glowColor = Color(0xFFFB923C)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                    Text(
                        text = "اتصال الوكيل",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

/**
 * Apple Liquid Glass Widget / Card Container
 */
@Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(26.dp),
    isDark: Boolean = isAppInDarkTheme(),
    elevation: Dp = 6.dp,
    onClick: (() -> Unit)? = null,
    testTag: String = "liquid_glass_card",
    content: @Composable () -> Unit
) {
    val glassBg = if (isDark) {
        Brush.verticalGradient(
            listOf(
                Color(0xEE111C30),
                Color(0xDD0D1728)
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(
                Color(0xEEFFFFFF),
                Color(0xDCFFFFFF),
                Color(0xD0F2F7FD)
            )
        )
    }

    val glassBorder = if (isDark) {
        Brush.verticalGradient(
            listOf(
                Color(0x664A90E2),
                Color(0x221E3A5F)
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(
                Color(0xFFFFFFFF),
                Color(0xCCFFFFFF),
                Color(0x50CBD5E1)
            )
        )
    }

    Box(
        modifier = modifier
            .testTag(testTag)
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = if (isDark) Color(0x66000000) else Color(0x120E2147),
                spotColor = if (isDark) SkylineBlue.copy(alpha = 0.25f) else SkylineBlue.copy(alpha = 0.12f)
            )
            .clip(shape)
            .background(glassBg)
            .border(1.2.dp, glassBorder, shape)
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else Modifier
            )
    ) {
        // Specular Top Glaze for Liquid Glass Refraction in Day Mode
        if (!isDark) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.5.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color.Transparent,
                                Color(0xFFFFFFFF),
                                Color(0xAAFFFFFF),
                                Color.Transparent
                            )
                        )
                    )
            )
        }
        content()
    }
}

@Composable
fun SkylineBrandHeader(
    modifier: Modifier = Modifier,
    isDark: Boolean = isAppInDarkTheme()
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LiquidGlassCard(
            modifier = Modifier.size(92.dp),
            shape = RoundedCornerShape(28.dp),
            isDark = isDark,
            elevation = 8.dp
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = R.drawable.ic_skyline_logo),
                    contentDescription = stringResource(R.string.app_name),
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(22.dp))
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "SKYLINE",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (isDark) Color.White else Color(0xFF0F172A),
            letterSpacing = 1.2.sp
        )

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = "INTERNET SERVICES",
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Bold,
            color = SkylineBlue,
            letterSpacing = 2.5.sp
        )
    }
}

@Composable
fun OnlineStatusChip(
    isOnline: Boolean,
    modifier: Modifier = Modifier
) {
    val isDark = isAppInDarkTheme()
    val bgColor = if (isOnline) {
        if (isDark) Color(0x3334C759) else Color(0xE6E8F9EE)
    } else {
        if (isDark) Color(0x338E8E93) else Color(0xE6F1F5F9)
    }
    val contentColor = if (isOnline) SkylineOnline else SkylineOffline
    val text = if (isOnline) "متصل بشبكة Skyline" else "غير متصل بالشبكة"

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50.dp))
            .background(bgColor)
            .border(1.dp, contentColor.copy(alpha = 0.35f), RoundedCornerShape(50.dp))
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Circle,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(9.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = contentColor
        )
    }
}

/**
 * Apple Dynamic Liquid Glass Status Capsule
 */
@Composable
fun CardOnlineStatusIndicator(
    isOnline: Boolean,
    isRefreshing: Boolean = false,
    lastUpdatedFormatted: String = "",
    onRefresh: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isDark = isAppInDarkTheme()
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    val bgColor = if (isOnline) {
        if (isDark) Color(0x3310B981) else Color(0xEEECFDF5)
    } else {
        if (isDark) Color(0x33F59E0B) else Color(0xEEFFFBEB)
    }

    val borderColor = if (isOnline) {
        if (isDark) Color(0x5534D399) else Color(0x80A7F3D0)
    } else {
        if (isDark) Color(0x55FBBF24) else Color(0x80FDE68A)
    }

    val contentColor = if (isOnline) {
        if (isDark) Color(0xFF34D399) else Color(0xFF047857)
    } else {
        if (isDark) Color(0xFFFBBF24) else Color(0xFFB45309)
    }

    val statusDotColor = if (isOnline) SkylineOnline else Color(0xFFF59E0B)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 9.dp)
            .testTag("card_online_status_indicator"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(contentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isOnline) Icons.Default.Wifi else Icons.Default.WifiOff,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(statusDotColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isOnline) "متصل (Online)" else "غير متصل (Offline)",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = if (isOnline) "• البيانات محدثة" else "• بيانات محفوظة محلياً",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = contentColor.copy(alpha = 0.85f)
                    )
                }

                if (!isOnline && lastUpdatedFormatted.isNotBlank()) {
                    Text(
                        text = "آخر تحديث متوفر: $lastUpdatedFormatted",
                        fontSize = 10.sp,
                        color = contentColor.copy(alpha = 0.8f)
                    )
                }
            }
        }

        if (onRefresh != null) {
            IconButton(
                onClick = {
                    HapticFeedbackHelper.performClick(context, haptic)
                    onRefresh()
                },
                modifier = Modifier
                    .size(28.dp)
                    .testTag("btn_refresh_card_status")
            ) {
                if (isRefreshing) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        color = contentColor,
                        modifier = Modifier.size(15.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "تحديث البيانات الآن",
                        tint = contentColor,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }
        }
    }
}

/**
 * Apple Watch / Fitness styled Liquid Glass Gauge with luminous gradient arc and frosted center
 */
@Composable
fun CircularDaysGauge(
    days: Int,
    percentage: Float = 0.82f,
    size: Dp = 90.dp,
    modifier: Modifier = Modifier
) {
    val isDark = isAppInDarkTheme()
    val animatedProgress by animateFloatAsState(
        targetValue = percentage.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 1000),
        label = "gauge_anim"
    )

    val isCompact = size <= 80.dp
    val strokeWidthDp = if (isCompact) 7.dp else 9.dp
    val numberFontSize = if (isCompact) 18.sp else 22.sp
    val labelFontSize = if (isCompact) 8.5.sp else 10.sp
    val percentFontSize = if (isCompact) 8.5.sp else 10.sp

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokePx = strokeWidthDp.toPx()
            val trackColor = if (isDark) Color(0x331E3A5F) else Color(0x33007AFF)
            
            // Track background with subtle glass opacity
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
            // Liquid Neon Progress arc
            drawArc(
                brush = Brush.sweepGradient(
                    listOf(
                        SkylineBlue,
                        SkylineCyan,
                        SkylineSuccess,
                        SkylineCyan,
                        SkylineBlue
                    )
                ),
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "$days",
                fontSize = numberFontSize,
                fontWeight = FontWeight.ExtraBold,
                color = if (isDark) Color.White else Color(0xFF0F172A),
                lineHeight = numberFontSize
            )
            Text(
                text = stringResource(R.string.days_remaining),
                fontSize = labelFontSize,
                fontWeight = FontWeight.SemiBold,
                color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                lineHeight = labelFontSize
            )
            val pctInt = (percentage * 100).toInt().coerceIn(0, 100)
            Text(
                text = "$pctInt%",
                fontSize = percentFontSize,
                fontWeight = FontWeight.Bold,
                color = SkylineSuccess,
                lineHeight = percentFontSize
            )
        }
    }
}

@Composable
fun StatusBadge(
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val isDark = isAppInDarkTheme()
    val bgColor = if (isActive) {
        if (isDark) Color(0x3334C759) else Color(0xEEECFDF5)
    } else {
        if (isDark) Color(0x33FF3B30) else Color(0xEEFEF2F2)
    }
    val borderColor = if (isActive) {
        if (isDark) Color(0x5534C759) else Color(0x80A7F3D0)
    } else {
        if (isDark) Color(0x55FF3B30) else Color(0x80FECACA)
    }
    val textColor = if (isActive) SkylineSuccess else SkylineError
    val text = if (isActive) stringResource(R.string.status_active) else stringResource(R.string.status_expired)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(textColor)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = text,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

@Composable
fun InfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    val isDark = isAppInDarkTheme()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = label,
                fontSize = 14.sp,
                color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
            )
        }
        Text(
            text = value.ifBlank { "—" },
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = valueColor
        )
    }
}

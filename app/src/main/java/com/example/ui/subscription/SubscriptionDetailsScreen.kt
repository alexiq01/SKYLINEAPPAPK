package com.example.ui.subscription

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.api.DashboardData
import com.example.data.api.ServiceData
import com.example.data.api.UserAccountData
import com.example.ui.components.AGENT_RESELLER_PHONE_NUMBER
import com.example.ui.components.AGENT_RESELLER_WHATSAPP_NUMBER
import com.example.ui.components.InfoRow
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.SkylineBlue
import com.example.ui.theme.SkylineCyan
import com.example.ui.theme.SkylineError
import com.example.ui.theme.SkylineOnline
import com.example.ui.theme.SkylineSuccess
import com.example.ui.theme.isAppInDarkTheme
import com.example.ui.util.HapticFeedbackHelper
import com.example.worker.NotificationScheduler
import kotlinx.coroutines.delay
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Subscription Details Screen
 * Displays complete subscription details including:
 * 1. Live Countdown Timer for the expiration date (Days, Hours, Minutes, Seconds)
 * 2. Progress Indicator for the remaining balance against plan price
 * 3. Detailed subscription & package breakdown
 * 4. Direct renewal and contact agent actions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionDetailsScreen(
    user: UserAccountData?,
    service: ServiceData?,
    dashboard: DashboardData?,
    remainingDays: Int,
    isServiceOnline: Boolean,
    isSubscriptionActive: Boolean,
    onBack: (() -> Unit)? = null,
    onRenew: () -> Unit = {},
    onRefresh: () -> Unit = {}
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val isDark = isAppInDarkTheme()

    // Real-time ticking clock for expiration countdown
    var currentTimeMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            currentTimeMillis = System.currentTimeMillis()
        }
    }

    val expirationDateStr = service?.expiration ?: ""
    val expirationMillis = remember(expirationDateStr) {
        NotificationScheduler.parseDateToMillis(expirationDateStr)
    }

    val remainingMillis = expirationMillis?.let { it - currentTimeMillis } ?: 0L
    val isExpired = expirationMillis != null && remainingMillis <= 0L

    // Countdown components
    val totalSeconds = if (remainingMillis > 0) remainingMillis / 1000L else 0L
    val countdownDays = (totalSeconds / 86400L).toInt()
    val countdownHours = ((totalSeconds % 86400L) / 3600L).toInt()
    val countdownMinutes = ((totalSeconds % 3600L) / 60L).toInt()
    val countdownSeconds = (totalSeconds % 60L).toInt()

    // Balance and Progress calculation
    val currentBalance = user?.balance ?: dashboard?.balance ?: 0.0
    val planPrice = service?.price ?: 45000.0
    val balanceProgress = if (planPrice > 0) {
        (currentBalance / planPrice).toFloat().coerceIn(0f, 1f)
    } else 0f

    val isExpiringSoon = countdownDays in 1..3

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("screen_subscription_details")
            .padding(bottom = 24.dp)
    ) {
        // 1. Hero Countdown Liquid Glass Card
        LiquidGlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("card_expiration_countdown"),
            shape = RoundedCornerShape(26.dp),
            isDark = isDark,
            elevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header badge row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isExpired) Color(0xFFEF4444).copy(alpha = 0.15f)
                                    else SkylineBlue.copy(alpha = 0.12f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = if (isExpired) Color(0xFFDC2626) else SkylineBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "العد التنازلي لانتهاء الاشتراك",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (expirationDateStr.isNotBlank()) "الموعد: $expirationDateStr" else "جاري المزامنة...",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    StatusBadge(isActive = !isExpired && isSubscriptionActive)
                }

                Spacer(modifier = Modifier.height(18.dp))

                if (isExpired) {
                    // Expired Alert State
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFFEF2F2),
                        border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "انتهى اشتراك هذه الباقة",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF991B1B)
                            )
                            Text(
                                text = "توقفت الخدمة مؤقتاً، يرجى تفعيل الاشتراك فوراً أو التواصل مع الوكيل",
                                fontSize = 12.sp,
                                color = Color(0xFFB91C1C),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    // Live Countdown Digital Blocks
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CountdownUnitBox(
                            value = countdownDays,
                            label = "يوم",
                            modifier = Modifier.weight(1f),
                            highlightColor = if (isExpiringSoon) Color(0xFFEF4444) else SkylineBlue
                        )
                        CountdownUnitBox(
                            value = countdownHours,
                            label = "ساعة",
                            modifier = Modifier.weight(1f),
                            highlightColor = if (isExpiringSoon) Color(0xFFEF4444) else SkylineBlue
                        )
                        CountdownUnitBox(
                            value = countdownMinutes,
                            label = "دقيقة",
                            modifier = Modifier.weight(1f),
                            highlightColor = SkylineCyan
                        )
                        CountdownUnitBox(
                            value = countdownSeconds,
                            label = "ثانية",
                            modifier = Modifier.weight(1f),
                            highlightColor = SkylineSuccess
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Expiration percentage bar
                    val totalCycleDays = 30f
                    val cycleProgress = (countdownDays / totalCycleDays).coerceIn(0f, 1f)
                    val animatedCycleProgress by animateFloatAsState(
                        targetValue = cycleProgress,
                        animationSpec = tween(800),
                        label = "cycle_progress"
                    )

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "المدة المتبقية من دورة الاشتراك",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${(animatedCycleProgress * 100).toInt()}% متبقي",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isExpiringSoon) Color(0xFFEF4444) else SkylineBlue
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { animatedCycleProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (isExpiringSoon) Color(0xFFEF4444) else SkylineBlue,
                            trackColor = if (isDark) Color(0xFF1E3A5F) else Color(0xFFE2E8F0)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Remaining Balance & Progress Indicator Liquid Glass Card
        LiquidGlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("card_balance_progress"),
            shape = RoundedCornerShape(26.dp),
            isDark = isDark,
            elevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(SkylineSuccess.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = SkylineSuccess,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "مؤشر الرصيد المتبقي",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "الرصيد المتاح مقارنة بقيمة الباقة الشهرية",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Percentage Badge
                    val balancePercent = (balanceProgress * 100).toInt()
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (balanceProgress >= 1f) SkylineSuccess.copy(alpha = 0.15f)
                        else SkylineCyan.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "$balancePercent%",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (balanceProgress >= 1f) SkylineSuccess else SkylineBlue,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Progress Bar with Animated Value
                val animatedBalanceProgress by animateFloatAsState(
                    targetValue = balanceProgress,
                    animationSpec = tween(1000),
                    label = "balance_anim"
                )

                LinearProgressIndicator(
                    progress = { animatedBalanceProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .testTag("progress_remaining_balance"),
                    color = if (balanceProgress >= 1f) SkylineSuccess else SkylineCyan,
                    trackColor = if (isDark) Color(0xFF1E3A5F) else Color(0xFFE2E8F0)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Balance Comparison Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "الرصيد المتوفر حالياً",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "IQD ${String.format(Locale.US, "%,.2f", currentBalance)}",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (currentBalance > 0) SkylineSuccess else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "سعر الباقة الشهرية",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "IQD ${String.format(Locale.US, "%,.0f", planPrice)}",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Sufficiency status chip
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = if (currentBalance >= planPrice) Color(0xFFECFDF5) else Color(0xFFF1F5F9)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (currentBalance >= planPrice) Icons.Default.CheckCircle else Icons.Default.Info,
                            contentDescription = null,
                            tint = if (currentBalance >= planPrice) Color(0xFF059669) else Color(0xFF64748B),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (currentBalance >= planPrice) {
                                "رصيدك كافٍ لتجديد الباقة تلقائياً عند موعد الانتهاء"
                            } else {
                                val diff = planPrice - currentBalance
                                "المتبقي لإتمام التجديد: IQD ${String.format(Locale.US, "%,.0f", diff)}"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (currentBalance >= planPrice) Color(0xFF065F46) else Color(0xFF475569)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Full Subscription & Package Details Liquid Glass Card
        LiquidGlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("card_full_subscription_details"),
            shape = RoundedCornerShape(26.dp),
            isDark = isDark,
            elevation = 6.dp
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "تفاصيل الاشتراك والباقة",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isServiceOnline) SkylineOnline.copy(alpha = 0.12f) else Color.Gray.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = if (isServiceOnline) "متصل • ONLINE" else "غير متصل • OFFLINE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isServiceOnline) SkylineOnline else Color.Gray,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                InfoRow(label = "اسم الباقة الحالية", value = service?.profileName ?: "2-Nitro", valueColor = SkylineBlue)
                InfoRow(label = stringResource(R.string.client), value = user?.name ?: user?.username ?: "ياسين طه")
                InfoRow(label = stringResource(R.string.username), value = user?.username ?: "ha@haq3")
                InfoRow(
                    label = stringResource(R.string.expiration_date),
                    value = NotificationScheduler.formatExpirationWithRemainingTime(expirationDateStr),
                    valueColor = if (isExpired) Color(0xFFDC2626) else SkylineBlue
                )
                InfoRow(
                    label = "سعر الاشتراك",
                    value = "IQD ${String.format(Locale.US, "%,.0f", planPrice)}"
                )
                InfoRow(
                    label = "التجديد التلقائي",
                    value = if (user?.autoRenew == 1) "مفعل تلقائياً" else "معطل"
                )
                InfoRow(label = "رقم العقد", value = user?.contractId ?: "300285")
                InfoRow(label = "الشركة المزودة", value = user?.company ?: "Skyline Internet")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Action Buttons (Contact Agent & Card Renewal)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = {
                    HapticFeedbackHelper.performClick(context, haptic)
                    try {
                        val msg = "مرحباً، أود الاستفسار عن اشتراكي وتجديده عبر الوكيل.\nاسم المستخدم: ${user?.username ?: ""}\nالباقة: ${service?.profileName ?: "2-Nitro"}"
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("https://wa.me/${AGENT_RESELLER_WHATSAPP_NUMBER.replace("+", "")}?text=${Uri.encode(msg)}")
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(intent)
                    } catch (_: Exception) {
                    }
                },
                modifier = Modifier
                    .weight(1.2f)
                    .height(50.dp)
                    .testTag("btn_contact_agent_details"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_whatsapp),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("تواصل مع الوكيل", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            Button(
                onClick = {
                    HapticFeedbackHelper.performClick(context, haptic)
                    onRenew()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("btn_renew_details"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SkylineBlue)
            ) {
                Icon(Icons.Default.Autorenew, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("تجديد الباقة", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/**
 * Single countdown digital block for days, hours, minutes, seconds
 */
@Composable
private fun CountdownUnitBox(
    value: Int,
    label: String,
    highlightColor: Color,
    modifier: Modifier = Modifier
) {
    val isDark = isAppInDarkTheme()
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = if (isDark) Color(0xFF13233D) else Color(0xFFF1F5F9),
        border = BorderStroke(1.dp, highlightColor.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = String.format(Locale.US, "%02d", value),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = highlightColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

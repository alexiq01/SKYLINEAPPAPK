package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.SkylineBlue
import com.example.ui.theme.SkylineCyan
import com.example.ui.theme.SkylineSuccess
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import com.example.ui.theme.ThemeController
import com.example.ui.theme.isAppInDarkTheme
import com.example.ui.util.HapticFeedbackHelper

const val DEVELOPER_INSTAGRAM_URL = "https://www.instagram.com/y_as.01"
const val DEVELOPER_WHATSAPP_NUMBER = "+9647830528876"
const val DEVELOPER_WHATSAPP_URL = "https://wa.me/9647830528876"
const val DEVELOPER_SUPER_QI_NUMBER = "4659615480"

/**
 * Developer and App Support Card
 * Includes Instagram, WhatsApp, and Copyable Super Qi number for donations/support.
 */
@Composable
fun DeveloperSupportCard(
    onCopied: (message: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    var isNumberCopied by remember { mutableStateOf(false) }
    val isDark = isAppInDarkTheme()

    fun copySuperQiNumber() {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clip = ClipData.newPlainText("SUPER QI", DEVELOPER_SUPER_QI_NUMBER)
        clipboard?.setPrimaryClip(clip)

        HapticFeedbackHelper.performCopySuccess(context, haptic)
        isNumberCopied = true
        onCopied("تم نسخ رقم سوبر كي (4659615480) بنجاح")

        scope.launch {
            delay(3000)
            isNumberCopied = false
        }
    }

    fun openUrl(url: String) {
        HapticFeedbackHelper.performClick(context, haptic)
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            onCopied("تعذر فتح الرابط")
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("developer_support_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(SkylineBlue, SkylineCyan)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "قسم المطور ودعم التطبيق",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "المطور: ياسين طه Yassin Taha (Software Engineer)",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(14.dp))

            // Social & Contact Links (Instagram & WhatsApp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Instagram Button
                OutlinedButton(
                    onClick = { openUrl(DEVELOPER_INSTAGRAM_URL) },
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("instagram_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (isDark) Color(0xFFFF7FB2) else Color(0xFFC13584)
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.linearGradient(listOf(Color(0xFFE1306C), Color(0xFF833AB4)))
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_instagram),
                        contentDescription = "إنستغرام",
                        modifier = Modifier.size(18.dp),
                        tint = if (isDark) Color(0xFFFF7FB2) else Color(0xFFC13584)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "إنستغرام",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // WhatsApp Button
                OutlinedButton(
                    onClick = { openUrl(DEVELOPER_WHATSAPP_URL) },
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("whatsapp_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (isDark) Color(0xFF4ADE80) else Color(0xFF128C7E)
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.linearGradient(listOf(Color(0xFF25D366), Color(0xFF128C7E)))
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_whatsapp),
                        contentDescription = "واتساب",
                        modifier = Modifier.size(18.dp),
                        tint = if (isDark) Color(0xFF4ADE80) else Color(0xFF128C7E)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "واتساب",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Super Qi Support Box
            val qiCardBg = if (isDark) listOf(Color(0xFF261906), Color(0xFF1F1404)) else listOf(Color(0xFFFFFBEB), Color(0xFFFEF3C7))
            val qiBorder = if (isDark) Color(0xFF78350F) else Color(0xFFFDE68A)
            val qiTitle = if (isDark) Color(0xFFFDE68A) else Color(0xFF92400E)
            val qiSub = if (isDark) Color(0xFFFBBF24) else Color(0xFFB45309)
            val qiSurface = if (isDark) Color(0xFF0F1A2E) else Color.White
            val qiNumText = if (isDark) Color(0xFFF8FAFC) else Color(0xFF1E293B)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.verticalGradient(colors = qiCardBg))
                    .border(1.dp, qiBorder, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF59E0B).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CreditCard,
                                contentDescription = null,
                                tint = if (isDark) Color(0xFFFBBF24) else Color(0xFFB45309),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = "دعم عبر سوبر كي (SUPER QI)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = qiTitle
                            )
                            Text(
                                text = "رقم المحفظة للدعم المالي:",
                                fontSize = 11.sp,
                                color = qiSub
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Copyable Number Container
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { copySuperQiNumber() }
                            .testTag("super_qi_copy_container"),
                        color = qiSurface,
                        tonalElevation = 1.dp,
                        border = androidx.compose.foundation.BorderStroke(1.dp, qiBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "SUPER QI",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = qiSub
                                )
                                Text(
                                    text = DEVELOPER_SUPER_QI_NUMBER,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = qiNumText
                                )
                            }

                            Button(
                                onClick = { copySuperQiNumber() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isNumberCopied) SkylineSuccess else Color(0xFFD97706)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.height(38.dp)
                            ) {
                                Icon(
                                    imageVector = if (isNumberCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isNumberCopied) "تم النسخ" else "نسخ الرقم",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "اضغط على الرقم أو زر النسخ للنسخ الفوري. دعمكم يساهم في الاستمرار في تطوير وتحديث التطبيق.",
                        fontSize = 11.sp,
                        color = qiSub,
                        textAlign = TextAlign.Start,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

/**
 * Full-screen Developer Section (المطور)
 * Dedicated screen accessible from the bottom navigation bar and drawer.
 */
@Composable
fun DeveloperScreen(
    onCopied: (String) -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val isDark = isAppInDarkTheme()

    fun copyText(label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard?.setPrimaryClip(clip)
        HapticFeedbackHelper.performCopySuccess(context, haptic)
        onCopied("تم نسخ $label بنجاح")
    }

    fun openUrl(url: String) {
        HapticFeedbackHelper.performClick(context, haptic)
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            onCopied("تعذر فتح الرابط")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("developer_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Developer Profile Hero Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(SkylineBlue, SkylineCyan)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(38.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "ياسين طه Yassin Taha (Software Engineer)",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SkylineBlue.copy(alpha = 0.12f),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "Software Engineer • مهندس برمجيات",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SkylineBlue,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "تطوير وهندسة برمجيات تطبيق Skyline Internet بحلول ذكية وسريعة.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }

        // 2. Global Theme Toggle Card (Light / Dark Mode)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                if (isDark) Color(0xFF312E81).copy(alpha = 0.5f) else Color(0xFFFEF3C7)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isDark) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = null,
                            tint = if (isDark) Color(0xFFA5B4FC) else Color(0xFFD97706),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "الوضع الليلي (Dark Mode)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isDark) "مفعل - عرض ليلي مريح للاشتراك" else "معطل - العرض الفاتح الافتراضي",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Switch(
                    checked = isDark,
                    onCheckedChange = {
                        ThemeController.toggle(context, isDark)
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onCopied(if (!isDark) "تم تفعيل الوضع الليلي 🌙" else "تم تفعيل الوضع الفاتح ☀️")
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = SkylineBlue
                    )
                )
            }
        }

        // 3. Detailed Contact Channels (Instagram & WhatsApp) with Clear Data and Icons
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "قنوات التواصل المباشر مع المطور",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(14.dp))

                // Instagram Row
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFF833AB4), Color(0xFFFD1D1D), Color(0xFFFCB045))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_instagram),
                                    contentDescription = "إنستغرام",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "إنستغرام (Instagram)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "@y_as.01",
                                    fontSize = 13.sp,
                                    color = SkylineBlue,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            IconButton(
                                onClick = { copyText("رابط الإنستغرام", DEVELOPER_INSTAGRAM_URL) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "نسخ الرابط",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Button(
                                onClick = { openUrl(DEVELOPER_INSTAGRAM_URL) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE1306C)),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.OpenInNew,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("فتح", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // WhatsApp Row
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF25D366)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_whatsapp),
                                    contentDescription = "واتساب",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "واتساب (WhatsApp)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = DEVELOPER_WHATSAPP_NUMBER,
                                    fontSize = 13.sp,
                                    color = Color(0xFF10B981),
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            IconButton(
                                onClick = { copyText("رقم الواتساب", DEVELOPER_WHATSAPP_NUMBER) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "نسخ الرقم",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Button(
                                onClick = { openUrl(DEVELOPER_WHATSAPP_URL) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF128C7E)),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_whatsapp),
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("مراسلة", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // 4. Dedicated Super Qi Support Card
        DeveloperSupportCard(onCopied = onCopied)

        // 5. System & Security Info Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = SkylineBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "أمان وتشفير البيانات",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "يتم تشفير كافة البيانات والاتصالات محلياً وسحابياً عبر تقنية OpenSSL AES-256-CBC المتوافقة مع سيرفرات Skyline Internet مع حماية خصوصية العميل التامة.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
    }
}

/**
 * Developer Info Dialog to be shown from Drawer or any action
 */
@Composable
fun DeveloperInfoDialog(
    onDismiss: () -> Unit,
    onCopied: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("إغلاق")
            }
        },
        text = {
            DeveloperSupportCard(
                onCopied = onCopied,
                modifier = Modifier.fillMaxWidth()
            )
        }
    )
}


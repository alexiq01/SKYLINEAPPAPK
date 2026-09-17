package com.example.ui.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.SkylineBrandHeader
import com.example.ui.theme.SkylineBlue
import com.example.ui.theme.SkylineCyan
import com.example.ui.theme.SkylineError
import com.example.ui.theme.isAppInDarkTheme
import com.example.ui.util.HapticFeedbackHelper

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val isDark = isAppInDarkTheme()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is LoginEvent.LoginSuccess -> onLoginSuccess()
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    if (isDark) {
                        Brush.verticalGradient(
                            0.0f to Color(0xFF040A18),
                            0.35f to Color(0xFF0A1835),
                            0.75f to Color(0xFF061126),
                            1.0f to Color(0xFF030814)
                        )
                    } else {
                        Brush.verticalGradient(
                            0.0f to Color(0xFFE0EDFF),
                            0.25f to Color(0xFFF0F5FD),
                            0.70f to Color(0xFFF7FAFF),
                            1.0f to Color(0xFFE8F1FC)
                        )
                    }
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 22.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(18.dp))

                // Skyline Logo and Brand Crest
                SkylineBrandHeader()

                Spacer(modifier = Modifier.height(16.dp))

                // Server Status Pill Badge
                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = if (isDark) Color(0xFF0F244A) else Color.White,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isDark) SkylineCyan.copy(alpha = 0.35f) else SkylineBlue.copy(alpha = 0.2f)
                    ),
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981))
                        )
                        Spacer(modifier = Modifier.width(7.dp))
                        Icon(
                            imageVector = Icons.Default.Dns,
                            contentDescription = null,
                            tint = SkylineCyan,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "بوابة المشتركين • admin.skylineiq.com",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDark) Color(0xFF93C5FD) else Color(0xFF1E40AF)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Modern Login Form Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_card"),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDark) Color(0xFF0D1E3A) else Color.White
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = if (isDark) 4.dp else 6.dp
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isDark) SkylineBlue.copy(alpha = 0.25f) else Color(0xFFE2E8F0)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.login_title),
                            fontSize = 21.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "أدخل بيانات اشتراكك للمتابعة وإدارة الباقة والسرعة",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Error Banner with Smooth Animation
                        AnimatedVisibility(
                            visible = !state.errorMessage.isNullOrBlank(),
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(SkylineError.copy(alpha = 0.12f))
                                    .border(1.dp, SkylineError.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = SkylineError,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = state.errorMessage.orEmpty(),
                                    fontSize = 12.sp,
                                    color = SkylineError,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Username Field
                        Text(
                            text = stringResource(R.string.username),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp)
                        )
                        OutlinedTextField(
                            value = state.username,
                            onValueChange = viewModel::onUsernameChange,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("username_input"),
                            placeholder = { Text("مثال: bbn.17@haq2", fontSize = 13.sp) },
                            leadingIcon = {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(SkylineBlue.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = stringResource(R.string.username),
                                        tint = SkylineBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SkylineBlue,
                                unfocusedBorderColor = if (isDark) Color(0xFF1E3A5F) else Color(0xFFCBD5E1),
                                focusedContainerColor = if (isDark) Color(0xFF091428) else Color(0xFFF8FAFC),
                                unfocusedContainerColor = if (isDark) Color(0xFF091428) else Color(0xFFF8FAFC)
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Password Field
                        Text(
                            text = stringResource(R.string.password),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp)
                        )
                        OutlinedTextField(
                            value = state.password,
                            onValueChange = viewModel::onPasswordChange,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("password_input"),
                            placeholder = { Text("••••••••", fontSize = 14.sp) },
                            leadingIcon = {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(SkylineBlue.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = stringResource(R.string.password),
                                        tint = SkylineBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            },
                            trailingIcon = {
                                IconButton(onClick = {
                                    HapticFeedbackHelper.performClick(context, haptic)
                                    viewModel.togglePasswordVisibility()
                                }) {
                                    Icon(
                                        imageVector = if (state.isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = if (state.isPasswordVisible) "إخفاء كلمة المرور" else "إظهار كلمة المرور",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    HapticFeedbackHelper.performClick(context, haptic)
                                    viewModel.login()
                                }
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SkylineBlue,
                                unfocusedBorderColor = if (isDark) Color(0xFF1E3A5F) else Color(0xFFCBD5E1),
                                focusedContainerColor = if (isDark) Color(0xFF091428) else Color(0xFFF8FAFC),
                                unfocusedContainerColor = if (isDark) Color(0xFF091428) else Color(0xFFF8FAFC)
                            )
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Modern Gradient Action Button (Login)
                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                HapticFeedbackHelper.performClick(context, haptic)
                                viewModel.login()
                            },
                            enabled = !state.isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("login_button"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SkylineBlue,
                                contentColor = Color.White
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 4.dp,
                                pressedElevation = 1.dp
                            )
                        ) {
                            if (state.isLoading) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(22.dp),
                                        color = Color.White,
                                        strokeWidth = 2.5.dp
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "جاري التحقق من الحساب...",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Wifi,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = stringResource(R.string.login_button),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // "أو" Divider with refined styling
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HorizontalDivider(
                                modifier = Modifier.weight(1f),
                                color = if (isDark) Color(0xFF1E3A5F) else Color(0xFFE2E8F0)
                            )
                            Surface(
                                shape = RoundedCornerShape(50.dp),
                                color = if (isDark) Color(0xFF091428) else Color(0xFFF1F5F9),
                                modifier = Modifier.padding(horizontal = 10.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.or),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            HorizontalDivider(
                                modifier = Modifier.weight(1f),
                                color = if (isDark) Color(0xFF1E3A5F) else Color(0xFFE2E8F0)
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Auto-login / Saved Session button with elevated chip look
                        OutlinedButton(
                            onClick = {
                                HapticFeedbackHelper.performClick(context, haptic)
                                viewModel.autoLogin()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("auto_login_button"),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (isDark) SkylineCyan.copy(alpha = 0.5f) else SkylineBlue.copy(alpha = 0.4f)
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (isDark) SkylineCyan else SkylineBlue,
                                containerColor = if (isDark) Color(0xFF091428).copy(alpha = 0.5f) else Color(0xFFF0F7FF)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.auto_login),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}


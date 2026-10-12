package com.example.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SystemUpdate
import com.example.update.UpdateManager
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import com.example.ui.components.AppleAmbientBackground
import com.example.ui.components.DeveloperScreen
import com.example.ui.components.IPhone17DynamicIsland
import com.example.ui.components.IPhoneControlCenterGrid
import com.example.ui.components.IPhoneFloatingDock
import com.example.ui.components.LiquidGlassCard
import com.example.ui.theme.ThemeController
import com.example.ui.theme.isAppInDarkTheme
import com.example.ui.util.HapticFeedbackHelper
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.api.ServiceData
import com.example.data.api.UserAccountData
import com.example.ui.components.CardOnlineStatusIndicator
import com.example.ui.components.CircularDaysGauge
import com.example.ui.components.DeveloperInfoDialog
import com.example.ui.components.DeveloperScreen
import com.example.ui.components.DeveloperSupportCard
import android.content.Intent
import android.net.Uri
import com.example.ui.components.AGENT_RESELLER_PHONE_NUMBER
import com.example.ui.components.AGENT_RESELLER_WHATSAPP_NUMBER
import com.example.ui.components.AGENT_RESELLER_WHATSAPP_URL
import com.example.ui.components.DEVELOPER_WHATSAPP_NUMBER
import com.example.ui.components.DEVELOPER_WHATSAPP_URL
import com.example.ui.components.InfoRow
import com.example.ui.components.OnlineStatusChip
import com.example.ui.components.StatusBadge
import com.example.ui.subscription.SubscriptionDetailsScreen
import com.example.ui.theme.SkylineBlue
import com.example.ui.theme.SkylineCyan
import com.example.ui.theme.SkylineOnline
import com.example.ui.theme.SkylineSuccess
import com.example.worker.ExpirationNotificationWorker
import com.example.worker.NotificationScheduler
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onSessionExpired: () -> Unit,
    onLogout: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val isDark = isAppInDarkTheme()
    val snackbarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // 0 = الرئيسية (Home), 1 = تفاصيل الاشتراك (Subscription Details), 2 = الإشعارات (Notifications), 3 = المطور (Developer)
    var selectedTab by remember { mutableIntStateOf(0) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showRenewDialog by remember { mutableStateOf(false) }
    var showDeveloperDialog by remember { mutableStateOf(false) }
    var redeemPinInput by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is HomeEvent.SessionExpired -> onSessionExpired()
                is HomeEvent.LoggedOut -> onLogout()
            }
        }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(state.successMessage) {
        state.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    val user = state.subscription?.user
    val service = state.subscription?.service
    val dashboard = state.subscription?.dashboard

    val remainingDays = dashboard?.remainingDays ?: NotificationScheduler.calculateRemainingDays(service?.expiration)
    val isServiceOnline = service?.isOnline ?: true
    val isSubscriptionActive = service?.subscriptionStatus?.isActive ?: NotificationScheduler.isSubscriptionActive(service?.expiration)
    val isDataOnline = !state.isOfflineMode && isServiceOnline

    val expirationStr = service?.expiration
    LaunchedEffect(expirationStr) {
        if (!expirationStr.isNullOrBlank()) {
            val expMillis = NotificationScheduler.parseDateToMillis(expirationStr)
            if (expMillis != null && expMillis <= System.currentTimeMillis()) {
                NotificationScheduler.checkAndTriggerExpiredAlertIfNeeded(context, expirationStr)
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(300.dp),
                drawerContainerColor = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SkylineBlue.copy(alpha = 0.08f))
                        .padding(20.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_skyline_logo),
                        contentDescription = null,
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = user?.name ?: user?.username ?: "ياسين طه",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = user?.username ?: "ha@haq3",
                        fontSize = 13.sp,
                        color = SkylineBlue
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (remainingDays <= 0) Color(0xFFFEE2E2) else SkylineBlue.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (remainingDays <= 0) Color(0xFFEF4444).copy(alpha = 0.4f) else SkylineBlue.copy(alpha = 0.25f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = if (remainingDays <= 0) Color(0xFFDC2626) else SkylineBlue,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = NotificationScheduler.formatExpirationWithRemainingTime(service?.expiration),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (remainingDays <= 0) Color(0xFFDC2626) else SkylineBlue
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Spacer(modifier = Modifier.height(12.dp))

                NavigationDrawerItem(
                    label = { Text("الصفحة الرئيسية") },
                    selected = selectedTab == 0,
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    onClick = {
                        selectedTab = 0
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("تفاصيل الاشتراك") },
                    selected = selectedTab == 1,
                    icon = { Icon(Icons.Default.CardMembership, contentDescription = null, tint = SkylineBlue) },
                    onClick = {
                        selectedTab = 1
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("الإشعارات والتنبيهات") },
                    selected = selectedTab == 2,
                    icon = { Icon(Icons.Default.Notifications, contentDescription = null) },
                    onClick = {
                        selectedTab = 2
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("قسم المطور") },
                    selected = selectedTab == 3,
                    icon = { Icon(Icons.Default.Code, contentDescription = null, tint = SkylineBlue) },
                    onClick = {
                        selectedTab = 3
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                // Dark mode toggle in drawer
                androidx.compose.material3.Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(NavigationDrawerItemDefaults.ItemPadding)
                        .clip(RoundedCornerShape(100)),
                    color = if (isDark) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                ThemeController.toggle(context, isDark)
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = null,
                                tint = if (isDark) Color(0xFFFFD54F) else SkylineBlue,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "الوضع الليلي",
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Switch(
                            checked = isDark,
                            onCheckedChange = {
                                ThemeController.toggle(context, isDark)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = SkylineBlue
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(modifier = Modifier.height(8.dp))

                NavigationDrawerItem(
                    label = { Text("التحقق من التحديثات") },
                    selected = false,
                    icon = { Icon(Icons.Default.SystemUpdate, contentDescription = null, tint = SkylineBlue) },
                    onClick = {
                        scope.launch { drawerState.close() }
                        UpdateManager.checkForUpdates(
                            context = context,
                            forceImmediateFetch = true,
                            onNoUpdate = {
                                scope.launch {
                                    snackbarHostState.showSnackbar("أنت تستخدم أحدث إصدار من التطبيق (v${com.example.BuildConfig.VERSION_NAME})")
                                }
                            },
                            onError = { err ->
                                scope.launch {
                                    snackbarHostState.showSnackbar("تعذر التحقق من التحديثات: $err")
                                }
                            }
                        )
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("تسجيل الخروج", color = Color.Red) },
                    selected = false,
                    icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Color.Red) },
                    onClick = {
                        scope.launch { drawerState.close() }
                        showLogoutDialog = true
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        if (selectedTab == 0) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_skyline_logo),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = stringResource(R.string.app_name),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "SKYLINE INTERNET",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = SkylineCyan,
                                        letterSpacing = 2.sp
                                    )
                                }
                            }
                        } else {
                            val screenTitle = when (selectedTab) {
                                1 -> "تفاصيل الاشتراك"
                                2 -> "الإشعارات والتنبيهات"
                                3 -> "عن المطور"
                                else -> stringResource(R.string.app_name)
                            }
                            Text(
                                text = screenTitle,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    navigationIcon = {
                        if (selectedTab == 0) {
                            IconButton(
                                onClick = { scope.launch { drawerState.open() } },
                                modifier = Modifier.testTag("menu_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "القائمة",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        } else {
                            IconButton(
                                onClick = { selectedTab = 0 },
                                modifier = Modifier.testTag("back_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "الرجوع للرئيسية",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    },
                    actions = {
                        // Global Theme Toggle
                        IconButton(
                            onClick = { ThemeController.toggle(context, isDark) },
                            modifier = Modifier.testTag("theme_toggle_button")
                        ) {
                            Icon(
                                imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = if (isDark) "التبديل للوضع الفاتح" else "التبديل للوضع الليلي",
                                tint = if (isDark) Color(0xFFFFD54F) else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        if (selectedTab == 0) {
                            IconButton(
                                onClick = { viewModel.loadData(isRefresh = true) },
                                modifier = Modifier.testTag("refresh_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = stringResource(R.string.refresh),
                                    tint = SkylineBlue
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = if (isDark) Color(0xF2071120) else Color(0xEBFFFFFF)
                    )
                )
            },
            bottomBar = {
                IPhoneFloatingDock(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    unreadNotificationsCount = 2
                )
            }
        ) { innerPadding ->
            AppleAmbientBackground(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                isDark = isDark
            ) {
                if (state.isLoading && state.subscription == null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = SkylineBlue,
                                modifier = Modifier.size(42.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "جاري تحميل بيانات الاشتراك ...",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        // Offline notice
                        AnimatedVisibility(visible = state.isOfflineMode) {
                            LiquidGlassCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                                    .testTag("offline_banner"),
                                shape = RoundedCornerShape(16.dp),
                                isDark = isDark
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = Color(0xFFD97706),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = stringResource(R.string.offline_cached_notice),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF92400E)
                                    )
                                }
                            }
                        }

                        AnimatedContent(
                            targetState = selectedTab,
                            transitionSpec = {
                                if (targetState > initialState) {
                                    (slideInHorizontally(
                                        initialOffsetX = { fullWidth -> fullWidth },
                                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = 0.85f)
                                    ) + fadeIn(animationSpec = tween(280))).togetherWith(
                                        slideOutHorizontally(
                                            targetOffsetX = { fullWidth -> -fullWidth / 3 },
                                            animationSpec = tween(260)
                                        ) + fadeOut(animationSpec = tween(200))
                                    )
                                } else {
                                    (slideInHorizontally(
                                        initialOffsetX = { fullWidth -> -fullWidth },
                                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = 0.85f)
                                    ) + fadeIn(animationSpec = tween(280))).togetherWith(
                                        slideOutHorizontally(
                                            targetOffsetX = { fullWidth -> fullWidth / 3 },
                                            animationSpec = tween(260)
                                        ) + fadeOut(animationSpec = tween(200))
                                    )
                                }
                            },
                            label = "tab_page_transition"
                        ) { currentTab ->
                            when (currentTab) {
                                0 -> {
                                    HomeScreenDashboard(
                                        user = user,
                                        service = service,
                                        dashboard = dashboard,
                                        remainingDays = remainingDays,
                                        isServiceOnline = isServiceOnline,
                                        isSubscriptionActive = isSubscriptionActive,
                                        isOnline = isDataOnline,
                                        isRefreshing = state.isRefreshing,
                                        lastUpdatedFormatted = state.lastUpdatedFormatted,
                                        onRefresh = { viewModel.loadData(isRefresh = true) },
                                        onRenew = { showRenewDialog = true },
                                        onNavigateToSubscription = { selectedTab = 1 },
                                        onShowMessage = { msg ->
                                            scope.launch { snackbarHostState.showSnackbar(msg) }
                                        }
                                    )
                                }
                                1 -> {
                                    SubscriptionDetailsScreen(
                                        user = user,
                                        service = service,
                                        dashboard = dashboard,
                                        remainingDays = remainingDays,
                                        isServiceOnline = isServiceOnline,
                                        isSubscriptionActive = isSubscriptionActive,
                                        onBack = { selectedTab = 0 },
                                        onRenew = { showRenewDialog = true },
                                        onRefresh = { viewModel.loadData(isRefresh = true) }
                                    )
                                }
                                2 -> {
                                    NotificationsTabScreen(
                                        user = user,
                                        dashboard = dashboard,
                                        service = service,
                                        remainingDays = remainingDays,
                                        onShowMessage = { msg ->
                                            scope.launch { snackbarHostState.showSnackbar(msg) }
                                        }
                                    )
                                }
                                3 -> {
                                    DeveloperScreen(
                                        onCopied = { msg ->
                                            scope.launch {
                                                snackbarHostState.showSnackbar(msg)
                                            }
                                        }
                                    )
                                }
                                else -> {
                                    HomeScreenDashboard(
                                        user = user,
                                        service = service,
                                        dashboard = dashboard,
                                        remainingDays = remainingDays,
                                        isServiceOnline = isServiceOnline,
                                        isSubscriptionActive = isSubscriptionActive,
                                        isOnline = isDataOnline,
                                        isRefreshing = state.isRefreshing,
                                        lastUpdatedFormatted = state.lastUpdatedFormatted,
                                        onRefresh = { viewModel.loadData(isRefresh = true) },
                                        onRenew = { showRenewDialog = true },
                                        onNavigateToSubscription = { selectedTab = 1 },
                                        onShowMessage = { msg ->
                                            scope.launch { snackbarHostState.showSnackbar(msg) }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Renew dialog & Agent Contact
    if (showRenewDialog) {
        AlertDialog(
            onDismissRequest = { showRenewDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Autorenew,
                        contentDescription = null,
                        tint = SkylineBlue,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "تجديد وتفعيل الخدمة", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text(
                        text = "يمكنك تجديد اشتراك باقة (${service?.profileName ?: "2-Nitro"}) بإدخال رمز كارت التعبئة (PIN)، أو التواصل المباشر مع الوكيل لتفعيل الاشتراك.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // Contact Agent Button directly in Renew Dialog (WhatsApp & Call)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                HapticFeedbackHelper.performClick(context, haptic)
                                try {
                                    val msg = "مرحباً، أود تفعيل وتجديد اشتراك خدمة الإنترنت Skyline الخاصة بي.\nاسم المستخدم: ${user?.username ?: ""}"
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        data = Uri.parse("https://wa.me/${AGENT_RESELLER_WHATSAPP_NUMBER.replace("+", "")}?text=${Uri.encode(msg)}")
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    }
                                    context.startActivity(intent)
                                } catch (_: Exception) {
                                    scope.launch { snackbarHostState.showSnackbar("تعذر فتح تطبيق واتساب") }
                                }
                            },
                            modifier = Modifier.weight(1.2f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_whatsapp),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("واتساب الوكيل", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                HapticFeedbackHelper.performClick(context, haptic)
                                try {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$AGENT_RESELLER_PHONE_NUMBER")).apply {
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    }
                                    context.startActivity(intent)
                                } catch (_: Exception) {
                                    scope.launch { snackbarHostState.showSnackbar("تعذر إجراء المكالمة") }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SkylineBlue)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                tint = SkylineBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("اتصال ($AGENT_RESELLER_PHONE_NUMBER)", fontSize = 11.sp, color = SkylineBlue, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "أو أدخل كارت التعبئة مباشرة:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = redeemPinInput,
                        onValueChange = { redeemPinInput = it },
                        label = { Text("أدخل رمز كارت التعبئة (PIN)") },
                        placeholder = { Text("مثال: 1234567890") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        HapticFeedbackHelper.performClick(context, haptic)
                        if (redeemPinInput.isNotBlank()) {
                            viewModel.redeemPin(redeemPinInput)
                            redeemPinInput = ""
                        }
                        showRenewDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SkylineBlue)
                ) {
                    Text("تأكيد التجديد بالكارت")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    HapticFeedbackHelper.performClick(context, haptic)
                    showRenewDialog = false
                }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(text = stringResource(R.string.logout), fontWeight = FontWeight.Bold)
            },
            text = {
                Text(text = stringResource(R.string.confirm_logout))
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.logout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text(stringResource(R.string.yes))
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    // Developer & Support Dialog (SUPER QI, Instagram, WhatsApp)
    if (showDeveloperDialog) {
        DeveloperInfoDialog(
            onDismiss = { showDeveloperDialog = false },
            onCopied = { msg ->
                scope.launch {
                    snackbarHostState.showSnackbar(msg)
                }
            }
        )
    }
}

/**
 * Screen 3 in image: الصفحة الرئيسية
 */
@Composable
private fun HomeScreenDashboard(
    user: UserAccountData?,
    service: ServiceData?,
    dashboard: com.example.data.api.DashboardData?,
    remainingDays: Int,
    isServiceOnline: Boolean,
    isSubscriptionActive: Boolean,
    isOnline: Boolean = true,
    isRefreshing: Boolean = false,
    lastUpdatedFormatted: String = "",
    onRefresh: () -> Unit = {},
    onRenew: () -> Unit,
    onNavigateToSubscription: (() -> Unit)? = null,
    onShowMessage: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val isDark = isAppInDarkTheme()

    // 0. iPhone 17 Pro Max Interactive Dynamic Island
    IPhone17DynamicIsland(
        isOnline = isOnline,
        remainingDays = remainingDays,
        profileName = service?.profileName ?: "2-Nitro",
        ipAddress = user?.staticIp ?: user?.address ?: "",
        onRefresh = onRefresh
    )

    Spacer(modifier = Modifier.height(10.dp))

    // 1. Top Plan Header Card with client name, expiration date, price, circular gauge & real-time sync status
    MiniPlanHeaderCard(
        planName = service?.profileName ?: "2-Nitro",
        clientName = user?.name ?: user?.username ?: "ياسين طه",
        username = user?.username ?: "ha@haq3",
        expirationDate = service?.expiration,
        price = service?.price ?: 45000.0,
        isActive = isSubscriptionActive,
        remainingDays = remainingDays,
        isOnline = isOnline,
        isRefreshing = isRefreshing,
        lastUpdatedFormatted = lastUpdatedFormatted,
        onRefresh = onRefresh,
        onClick = onNavigateToSubscription
    )

    Spacer(modifier = Modifier.height(14.dp))

    // iPhone 17 Pro Max Control Center Action Grid
    IPhoneControlCenterGrid(
        onNavigateToSubscription = { onNavigateToSubscription?.invoke() },
        onOpenRenewDialog = onRenew,
        onOpenSupport = {
            try {
                val msg = "مرحباً الدعم الفني لخدمة Skyline Internet.\nاسم المستخدم: ${user?.username ?: ""}"
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://wa.me/${DEVELOPER_WHATSAPP_NUMBER.replace("+", "")}?text=${Uri.encode(msg)}")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            } catch (_: Exception) {}
        },
        onCallAgent = {
            try {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$AGENT_RESELLER_PHONE_NUMBER")).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            } catch (_: Exception) {}
        }
    )

    // Alert Banner if Subscription is Expired or Expiring in <= 3 days
    if (!isSubscriptionActive || remainingDays <= 0) {
        Spacer(modifier = Modifier.height(14.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("card_expired_warning_banner"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEF4444).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "انتهى اشتراكك الحالي!",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF991B1B)
                        )
                        Text(
                            text = "يرجى تجديد الاشتراك أو التواصل مع الوكيل لتفعيل الخدمة فوراً",
                            fontSize = 12.sp,
                            color = Color(0xFFB91C1C)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            HapticFeedbackHelper.performClick(context, haptic)
                            try {
                                val msg = "مرحباً، لقد انتهى اشتراكي وأود تفعيله عبر الوكيل.\nاسم المستخدم: ${user?.username ?: ""}"
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    data = Uri.parse("https://wa.me/${AGENT_RESELLER_WHATSAPP_NUMBER.replace("+", "")}?text=${Uri.encode(msg)}")
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                                context.startActivity(intent)
                            } catch (_: Exception) {
                            }
                        },
                        modifier = Modifier
                            .weight(1.3f)
                            .height(44.dp)
                            .testTag("btn_expired_contact_agent"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_whatsapp),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("تواصل مع الوكيل للتفعيل", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            HapticFeedbackHelper.performClick(context, haptic)
                            onRenew()
                        },
                        modifier = Modifier
                            .weight(0.9f)
                            .height(44.dp)
                            .testTag("btn_expired_renew"),
                        colors = ButtonDefaults.buttonColors(containerColor = SkylineBlue),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("تجديد بالكارت", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    } else if (remainingDays in 1..3) {
        Spacer(modifier = Modifier.height(14.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFFFFFBEB),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = Color(0xFFD97706),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "اقترب موعد انتهاء الاشتراك ($remainingDays أيام متبقية)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF92400E)
                        )
                        Text(
                            text = "يمكنك التواصل مسبقاً مع الوكيل لتجهيز التجديد",
                            fontSize = 11.sp,
                            color = Color(0xFFB45309)
                        )
                    }
                }
                TextButton(
                    onClick = {
                        try {
                            val msg = "مرحباً، اشتراكي سينتهي خلال $remainingDays أيام وأود تجديده.\nاسم المستخدم: ${user?.username ?: ""}"
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse("https://wa.me/${DEVELOPER_WHATSAPP_NUMBER.replace("+", "")}?text=${Uri.encode(msg)}")
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            context.startActivity(intent)
                        } catch (_: Exception) {}
                    }
                ) {
                    Text("مراسلة الوكيل", color = Color(0xFF25D366), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // 2. Comprehensive Account & Balance Overview Liquid Glass Widget
    LiquidGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("account_subscription_card"),
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(SkylineBlue.copy(alpha = 0.14f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            tint = SkylineBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "تفاصيل المشترك والحساب",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isServiceOnline) SkylineOnline.copy(alpha = 0.14f) else Color.Gray.copy(alpha = 0.14f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(if (isServiceOnline) SkylineOnline else Color.Gray)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isServiceOnline) "متصل • ONLINE" else "غير متصل",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isServiceOnline) SkylineOnline else Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2x2 Modern KPI Stats Grid
            val currentBalance = user?.balance ?: dashboard?.balance ?: 0.0
            val planPrice = service?.price ?: 45000.0

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ModernKpiCard(
                    title = "الباقة الحالية",
                    value = service?.profileName ?: "2-Nitro",
                    icon = Icons.Default.CardMembership,
                    iconTint = SkylineBlue,
                    valueColor = SkylineBlue,
                    modifier = Modifier.weight(1f)
                )
                ModernKpiCard(
                    title = "الأيام المتبقية",
                    value = if (remainingDays <= 0) "منتهي" else "$remainingDays يوم",
                    icon = Icons.Default.AccessTime,
                    iconTint = if (remainingDays <= 0) Color(0xFFDC2626) else SkylineOnline,
                    valueColor = if (remainingDays <= 0) Color(0xFFDC2626) else SkylineOnline,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ModernKpiCard(
                    title = "سعر الاشتراك",
                    value = "IQD ${String.format(Locale.US, "%,.0f", planPrice)}",
                    icon = Icons.Default.CreditCard,
                    iconTint = SkylineSuccess,
                    valueColor = SkylineSuccess,
                    modifier = Modifier.weight(1f)
                )
                ModernKpiCard(
                    title = "الرصيد المتوفر",
                    value = "IQD ${String.format(Locale.US, "%,.2f", currentBalance)}",
                    icon = Icons.Default.Badge,
                    iconTint = SkylineCyan,
                    valueColor = if (currentBalance > 0) SkylineSuccess else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(10.dp))

            InfoRow(
                label = stringResource(R.string.client),
                value = user?.name ?: user?.username ?: "ياسين طه",
                icon = Icons.Default.Person
            )
            InfoRow(
                label = stringResource(R.string.username),
                value = user?.username ?: "ha@haq3",
                icon = Icons.Default.Badge
            )
            InfoRow(
                label = stringResource(R.string.expiration_date),
                value = NotificationScheduler.formatExpirationWithRemainingTime(service?.expiration),
                icon = Icons.Default.AccessTime,
                valueColor = if (remainingDays <= 0) Color(0xFFDC2626) else SkylineBlue
            )

            if (onNavigateToSubscription != null) {
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onNavigateToSubscription() }
                        .padding(vertical = 8.dp, horizontal = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(SkylineBlue.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CardMembership,
                                contentDescription = null,
                                tint = SkylineBlue,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "عرض العداد التنازلي وتفاصيل الشبكة الكاملة",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SkylineBlue
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        tint = SkylineBlue,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // 3. Network & Connection Specs Card (بيانات الاتصال والشبكة والخادم)
    LiquidGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("network_specs_card"),
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(SkylineCyan.copy(alpha = 0.14f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Dns,
                            contentDescription = null,
                            tint = SkylineCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "بيانات الشبكة والخادم",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isServiceOnline) SkylineOnline.copy(alpha = 0.14f) else Color.Gray.copy(alpha = 0.14f)
                ) {
                    Text(
                        text = if (isServiceOnline) "نشط ومتاح" else "غير متصل",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isServiceOnline) SkylineOnline else Color.Gray,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            InfoRow(
                label = "حالة الاتصال بالخدمة",
                value = if (isServiceOnline) "متصل بالخدمة • ONLINE" else "غير متصل بالشبكة",
                icon = Icons.Default.CheckCircle,
                valueColor = if (isServiceOnline) SkylineSuccess else Color.Gray
            )
            if (!user?.phone.isNullOrBlank()) {
                InfoRow(
                    label = stringResource(R.string.phone),
                    value = user?.phone ?: "",
                    icon = Icons.Default.Phone
                )
            }
            if (!user?.address.isNullOrBlank()) {
                InfoRow(
                    label = stringResource(R.string.address),
                    value = user?.address ?: "",
                    icon = Icons.Default.Home
                )
            }
            val isAutoRenewActive = user?.autoRenew == 1 || user?.autoRenew == null
            InfoRow(
                label = stringResource(R.string.auto_renew),
                value = if (isAutoRenewActive) "مفعل تلقائياً" else "غير مفعل",
                icon = Icons.Default.Autorenew,
                valueColor = if (isAutoRenewActive) SkylineSuccess else Color.Gray
            )
        }
    }

    Spacer(modifier = Modifier.height(28.dp))
}

/**
 * Modern KPI metric card for fast glanceable dashboard insights
 */
@Composable
private fun ModernKpiCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier
) {
    val isDark = isAppInDarkTheme()
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = if (isDark) Color(0x2E1E293B) else Color(0x66F1F5F9),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDark) Color(0x3338BDF8) else Color(0x40CBD5E1)
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(iconTint.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = valueColor,
                maxLines = 1
            )
        }
    }
}

/**
 * Mini header card present at the top of Screen 4 and Screen 5 (Apple Liquid Glass Hero Widget)
 */
@Composable
private fun MiniPlanHeaderCard(
    planName: String,
    clientName: String? = null,
    username: String? = null,
    expirationDate: String? = null,
    price: Double? = null,
    isActive: Boolean,
    remainingDays: Int,
    isOnline: Boolean = true,
    isRefreshing: Boolean = false,
    lastUpdatedFormatted: String = "",
    onRefresh: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val isDark = isAppInDarkTheme()
    LiquidGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("mini_plan_card"),
        shape = RoundedCornerShape(26.dp),
        isDark = isDark,
        elevation = 7.dp,
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            CardOnlineStatusIndicator(
                isOnline = isOnline,
                isRefreshing = isRefreshing,
                lastUpdatedFormatted = lastUpdatedFormatted,
                onRefresh = onRefresh
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Subscriber Name & Monogram Avatar
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(SkylineBlue, SkylineCyan)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (clientName?.take(1) ?: "ي"),
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = clientName ?: "ياسين طه",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "المشترك • @${username ?: "ha@haq3"}",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Plan Pill Tag & Status Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = SkylineBlue.copy(alpha = 0.12f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SkylineBlue.copy(alpha = 0.25f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(SkylineBlue)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "باقة $planName",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SkylineBlue
                                )
                            }
                        }
                        StatusBadge(isActive = isActive)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Expiration Capsule
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (remainingDays <= 0) Color(0x22EF4444) else (if (isDark) Color(0x331E293B) else Color(0x80F1F5F9))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = if (remainingDays <= 0) Color(0xFFDC2626) else SkylineBlue,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = NotificationScheduler.formatExpirationWithRemainingTime(expirationDate),
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (remainingDays <= 0) Color(0xFFDC2626) else SkylineBlue
                            )
                        }
                    }

                    if (price != null && price > 0) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = SkylineSuccess.copy(alpha = 0.12f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CreditCard,
                                    contentDescription = null,
                                    tint = SkylineSuccess,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "سعر الاشتراك: IQD ${String.format(Locale.US, "%,.0f", price)}",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SkylineSuccess
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                CircularDaysGauge(
                    days = remainingDays,
                    percentage = if (remainingDays <= 0) 0f else (remainingDays / 30f).coerceIn(0.05f, 1f),
                    size = 84.dp
                )
            }
        }
    }
}

/**
 * Dedicated clean Notifications feed for user alerts (subscription expiration, warnings, updates)
 * Free of technical jargon (WorkManager, PeriodicWorkRequest, etc.)
 */
@Composable
private fun NotificationsTabScreen(
    user: UserAccountData?,
    dashboard: com.example.data.api.DashboardData?,
    service: ServiceData?,
    remainingDays: Int,
    onShowMessage: (String) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val haptic = LocalHapticFeedback.current
    val isDark = isAppInDarkTheme()
    val expirationDateStr = service?.expiration ?: ""
    val isExpired = remainingDays <= 0
    val isExpiringSoon = remainingDays in 1..3

    val inboxItems by com.example.notifications.NotificationInboxManager.notificationsFlow.collectAsState()

    // Automatically generate system subscription alert in inbox if not already there and expiring soon / expired
    LaunchedEffect(remainingDays, expirationDateStr) {
        if (expirationDateStr.isNotBlank() && inboxItems.isEmpty()) {
            if (isExpired) {
                com.example.notifications.NotificationInboxManager.addNotification(
                    context = context,
                    title = "انتهى اشتراك الإنترنت",
                    message = "انتهت صلاحية اشتراكك الحالي ($expirationDateStr). يرجى التجديد لتجنب انقطاع الخدمة.",
                    type = "expired"
                )
            } else if (isExpiringSoon) {
                com.example.notifications.NotificationInboxManager.addNotification(
                    context = context,
                    title = "تنبيه: اقتراب انتهاء الاشتراك",
                    message = "متبقي $remainingDays أيام على انتهاء اشتراكك الحالي ($expirationDateStr).",
                    type = "expiring_soon"
                )
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Notifications Header & Quick Actions Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDark) Color(0xFF0D1B33) else Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isDark) SkylineBlue.copy(alpha = 0.2f) else Color(0xFFE2E8F0)
            )
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(SkylineBlue, SkylineCyan)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "الإشعارات",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "مركز الإشعارات",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (inboxItems.isEmpty()) "لا توجد إشعارات جديدة"
                                else "${inboxItems.size} إشعار مسجل",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (inboxItems.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                HapticFeedbackHelper.performClick(context, haptic)
                                com.example.notifications.NotificationInboxManager.clearAll(context)
                                onShowMessage("تم مسح كافة الإشعارات")
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "مسح الكل",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // If expiring soon or expired, show direct alert box
                if (isExpired || isExpiringSoon) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = if (isExpired) Color(0xFFEF4444).copy(alpha = 0.10f)
                               else Color(0xFFF59E0B).copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isExpired) Color(0xFFEF4444).copy(alpha = 0.3f)
                            else Color(0xFFF59E0B).copy(alpha = 0.3f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = if (isExpired) Color(0xFFEF4444) else Color(0xFFD97706),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isExpired) "انتهى اشتراك الإنترنت" else "تنبيه قرب انتهاء الاشتراك",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (isExpired) Color(0xFFDC2626) else Color(0xFFB45309)
                                )
                                Text(
                                    text = if (isExpired) "تاريخ الانتهاء: $expirationDateStr"
                                           else "متبقي $remainingDays يوم - تاريخ الانتهاء: $expirationDateStr",
                                    fontSize = 11.sp,
                                    color = if (isExpired) Color(0xFFDC2626) else Color(0xFFB45309)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 3-Day Notification System Status Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = SkylineBlue.copy(alpha = 0.08f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SkylineBlue.copy(alpha = 0.25f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = null,
                                    tint = SkylineBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "نظام التنبيه التلقائي (قبل 3 أيام)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SkylineBlue
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = SkylineSuccess.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "مفعل ومجدول",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SkylineSuccess,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "يقوم النظام المحلي بجدولة إشعار تذكيري يظهر على الهاتف قبل انتهاء الاشتراك بـ 3 أيام، مع فحص دوري بالخلفية لضمان استمرارية الخدمة.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedButton(
                            onClick = {
                                HapticFeedbackHelper.performClick(context, haptic)
                                NotificationScheduler.triggerTestNotification(
                                    context = context,
                                    alertType = com.example.worker.ExpirationNotificationWorker.ALERT_3D,
                                    expirationDateStr = expirationDateStr
                                )
                                onShowMessage("تم إرسال إشعار (متبقي 3 أيام) إلى شريط الإشعارات!")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                                .testTag("btn_test_3d_notification"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = SkylineBlue),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SkylineBlue)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("تجربة إشعار متبقي 3 أيام الآن", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = {
                                HapticFeedbackHelper.performClick(context, haptic)
                                NotificationScheduler.sendSubscriptionRenewedNotification(
                                    context = context,
                                    planName = service?.profileName,
                                    newExpirationDate = expirationDateStr
                                )
                                onShowMessage("تم إرسال إشعار (تم التفعيل بنجاح! استمتع بجودة الإنترنت)")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                                .testTag("btn_test_renewal_notification"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = SkylineSuccess),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SkylineSuccess)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = SkylineSuccess,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("تجربة إشعار التفعيل والتجديد", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SkylineSuccess)
                        }
                    }
                }
            }
        }

        // Dedicated Notification Feed List
        if (inboxItems.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDark) Color(0xFF0D1B33).copy(alpha = 0.6f) else Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp, horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(SkylineBlue.copy(alpha = 0.08f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsNone,
                            contentDescription = null,
                            tint = SkylineBlue.copy(alpha = 0.6f),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "صندوق الإشعارات فارغ",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "ستظهر هنا كافة إشعارات وتنبيهات انتهاء الاشتراك فور وصولها",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                inboxItems.forEach { item ->
                    NotificationFeedCard(item = item)
                }
            }
        }
    }
}

@Composable
private fun NotificationFeedCard(item: com.example.notifications.AppNotificationItem) {
    val isDark = isAppInDarkTheme()
    val isExpired = item.type == "expired"
    val isExpiringSoon = item.type == "expiring_soon" || item.type == "3d" || item.type == "24h" || item.type == "1h"

    val iconColor = when {
        isExpired -> Color(0xFFEF4444)
        isExpiringSoon -> Color(0xFFF59E0B)
        else -> SkylineBlue
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("notification_item_${item.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color(0xFF0D1B33) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDark) iconColor.copy(alpha = 0.25f) else Color(0xFFE2E8F0)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (item.formattedTime.isNotBlank()) {
                        Text(
                            text = item.formattedTime,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = item.message,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 17.sp
                )
            }
        }
    }
}

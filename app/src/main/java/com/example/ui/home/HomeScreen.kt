package com.example.ui.home

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.ui.platform.LocalContext
import com.example.ui.components.DeveloperScreen
import com.example.ui.theme.ThemeController
import com.example.ui.theme.isAppInDarkTheme
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
import com.example.ui.components.CircularDaysGauge
import com.example.ui.components.DeveloperInfoDialog
import com.example.ui.components.DeveloperSupportCard
import com.example.ui.components.InfoRow
import com.example.ui.components.OnlineStatusChip
import com.example.ui.components.StatusBadge
import com.example.ui.theme.SkylineBlue
import com.example.ui.theme.SkylineCyan
import com.example.ui.theme.SkylineOnline
import com.example.ui.theme.SkylineSuccess
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
    val isDark = isAppInDarkTheme()
    val snackbarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // 0 = الرئيسية (Home), 1 = الخدمات (Subscription Details), 2 = الإشعارات, 3 = المزيد (Customer Details), 4 = المطور
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
                        text = user?.name ?: user?.username ?: "ياسين حامد",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = user?.username ?: "bbn.17@haq2",
                        fontSize = 13.sp,
                        color = SkylineBlue
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Dns,
                            contentDescription = null,
                            tint = SkylineCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "admin.skylineiq.com/user",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
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
                    icon = { Icon(Icons.Default.Speed, contentDescription = null) },
                    onClick = {
                        selectedTab = 1
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("معلومات العميل") },
                    selected = selectedTab == 3,
                    icon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
                    onClick = {
                        selectedTab = 3
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
                    label = { Text("تحديث البيانات") },
                    selected = false,
                    icon = { Icon(Icons.Default.Refresh, contentDescription = null) },
                    onClick = {
                        viewModel.loadData(isRefresh = true)
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("قسم المطور والدعم (SUPER QI)") },
                    selected = selectedTab == 4,
                    icon = { Icon(Icons.Default.Code, contentDescription = null, tint = SkylineBlue) },
                    onClick = {
                        scope.launch { drawerState.close() }
                        selectedTab = 4
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
                                1 -> stringResource(R.string.subscription_details)
                                2 -> stringResource(R.string.nav_notifications)
                                3 -> stringResource(R.string.customer_info)
                                4 -> "قسم المطور والدعم"
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
                                onClick = { selectedTab = 2 },
                                modifier = Modifier.testTag("notifications_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsNone,
                                    contentDescription = "الإشعارات",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        } else {
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
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = { Icon(Icons.Default.Home, contentDescription = stringResource(R.string.nav_home)) },
                        label = { Text(stringResource(R.string.nav_home), fontSize = 10.sp) },
                        modifier = Modifier.testTag("nav_home")
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = { Icon(Icons.Default.Speed, contentDescription = stringResource(R.string.nav_services)) },
                        label = { Text(stringResource(R.string.nav_services), fontSize = 10.sp) },
                        modifier = Modifier.testTag("nav_services")
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = { Icon(Icons.Default.Notifications, contentDescription = stringResource(R.string.nav_notifications)) },
                        label = { Text(stringResource(R.string.nav_notifications), fontSize = 10.sp) },
                        modifier = Modifier.testTag("nav_notifications")
                    )
                    NavigationBarItem(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        icon = { Icon(Icons.Default.AccountCircle, contentDescription = stringResource(R.string.nav_more)) },
                        label = { Text(stringResource(R.string.nav_more), fontSize = 10.sp) },
                        modifier = Modifier.testTag("nav_customer")
                    )
                    NavigationBarItem(
                        selected = selectedTab == 4,
                        onClick = { selectedTab = 4 },
                        icon = { Icon(Icons.Default.Code, contentDescription = "المطور") },
                        label = {
                            Text(
                                text = "المطور",
                                fontSize = 10.sp,
                                fontWeight = if (selectedTab == 4) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.testTag("nav_developer")
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(
                        if (isDark) {
                            Brush.verticalGradient(
                                0.0f to Color(0xFF061126),
                                0.35f to Color(0xFF0B1932),
                                1.0f to Color(0xFF061126)
                            )
                        } else {
                            Brush.verticalGradient(
                                0.0f to Color(0xFFEBF2FD),
                                0.30f to Color(0xFFF4F7FC),
                                1.0f to Color(0xFFEDF2FA)
                            )
                        }
                    )
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
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                                    .testTag("offline_banner"),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
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

                        when (selectedTab) {
                            0 -> {
                                // Screen 3: الصفحة الرئيسية
                                HomeScreenDashboard(
                                    user = user,
                                    service = service,
                                    dashboard = dashboard,
                                    remainingDays = remainingDays,
                                    isServiceOnline = isServiceOnline,
                                    isSubscriptionActive = isSubscriptionActive,
                                    onOpenSubscriptionDetails = { selectedTab = 1 }
                                )
                            }
                            1 -> {
                                // Screen 5: تفاصيل الاشتراك
                                SubscriptionDetailsScreen(
                                    user = user,
                                    service = service,
                                    remainingDays = remainingDays,
                                    isServiceOnline = isServiceOnline,
                                    isSubscriptionActive = isSubscriptionActive,
                                    onRenew = { showRenewDialog = true }
                                )
                            }
                            2 -> {
                                // Notifications view
                                NotificationsTabScreen(
                                    service = service,
                                    remainingDays = remainingDays
                                )
                            }
                            3 -> {
                                // Screen 4: معلومات العميل
                                CustomerDetailsScreen(
                                    user = user,
                                    service = service,
                                    remainingDays = remainingDays,
                                    isServiceOnline = isServiceOnline,
                                    isSubscriptionActive = isSubscriptionActive,
                                    onCopied = { msg ->
                                        scope.launch {
                                            snackbarHostState.showSnackbar(msg)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Renew dialog
    if (showRenewDialog) {
        AlertDialog(
            onDismissRequest = { showRenewDialog = false },
            title = {
                Text(text = "تجديد الخدمة", fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        text = "يمكنك تجديد اشتراك باقة (${service?.profileName ?: "2-Nitro"}) باستخدام كارت شحن Skyline أو عبر الرصيد المتوفر.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(14.dp))
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
                        if (redeemPinInput.isNotBlank()) {
                            viewModel.redeemPin(redeemPinInput)
                            redeemPinInput = ""
                        }
                        showRenewDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SkylineBlue)
                ) {
                    Text("تأكيد التجديد")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenewDialog = false }) {
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
    onOpenSubscriptionDetails: () -> Unit
) {
    // Online badge chip
    OnlineStatusChip(isOnline = isServiceOnline)

    Spacer(modifier = Modifier.height(14.dp))

    // Greeting Row
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "مرحبًا، ${user?.name ?: user?.username ?: "ياسين"} 👋",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = stringResource(R.string.welcome_subtitle),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(SkylineBlue.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                tint = SkylineBlue,
                modifier = Modifier.size(36.dp)
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Card 1: الباقة الحالية
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("current_plan_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.current_plan),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = service?.profileName ?: "2-Nitro",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    StatusBadge(isActive = isSubscriptionActive)
                }

                CircularDaysGauge(
                    days = remainingDays,
                    percentage = if (remainingDays <= 0) 0f else (remainingDays / 30f).coerceIn(0.05f, 1f),
                    size = 96.dp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(14.dp))

            // Dedicated highlighted expiration & remaining time banner inside the card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SkylineBlue.copy(alpha = 0.08f))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = SkylineBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = NotificationScheduler.formatExpirationWithRemainingTime(service?.expiration),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = SkylineBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Start & Expiration Dates
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.start_date),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = NotificationScheduler.formatArabicDate(user?.registeredOn),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = stringResource(R.string.expiration_date),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = NotificationScheduler.formatArabicDate(service?.expiration),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SkylineBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Balance & Unpaid Invoices
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.unpaid_invoices),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "IQD ${String.format(Locale.US, "%.2f", dashboard?.unpaidInvoices ?: 0.0)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = stringResource(R.string.account_balance),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "IQD ${String.format(Locale.US, "%.2f", user?.balance ?: dashboard?.balance ?: 0.0)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Card 2: معلومات الخدمة >
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenSubscriptionDetails() }
            .testTag("service_summary_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.service_info),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // الحالة: ONLINE
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.status),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Circle,
                        contentDescription = null,
                        tint = if (isServiceOnline) SkylineOnline else Color.Gray,
                        modifier = Modifier.size(8.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isServiceOnline) "ONLINE" else "OFFLINE",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isServiceOnline) SkylineOnline else Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // السعر: IQD 45,000
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.price),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "IQD ${String.format(Locale.US, "%,.0f", service?.price ?: 45000.0)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // التجديد التلقائي
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.auto_renew),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Switch(
                    checked = user?.autoRenew == 1,
                    onCheckedChange = null,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = SkylineBlue
                    )
                )
            }
        }
    }
}

/**
 * Screen 4 in image: معلومات العميل
 */
@Composable
private fun CustomerDetailsScreen(
    user: UserAccountData?,
    service: ServiceData?,
    remainingDays: Int,
    isServiceOnline: Boolean,
    isSubscriptionActive: Boolean,
    onCopied: (String) -> Unit
) {
    // Top Mini Card
    MiniPlanHeaderCard(
        planName = service?.profileName ?: "2-Nitro",
        isActive = isSubscriptionActive,
        remainingDays = remainingDays
    )

    Spacer(modifier = Modifier.height(14.dp))

    // Card 1: معلومات الاشتراك
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("customer_subscription_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "معلومات الاشتراك",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(14.dp))

            InfoRow(label = stringResource(R.string.client), value = user?.name ?: "ياسين حامد")
            InfoRow(label = stringResource(R.string.start_date), value = NotificationScheduler.formatArabicDate(user?.registeredOn))
            InfoRow(label = stringResource(R.string.expiration_date), value = NotificationScheduler.formatExpirationWithRemainingTime(service?.expiration), valueColor = SkylineBlue)
            InfoRow(
                label = stringResource(R.string.status),
                value = if (isServiceOnline) "ONLINE" else "OFFLINE",
                valueColor = if (isServiceOnline) SkylineOnline else Color.Gray
            )
            InfoRow(label = stringResource(R.string.price), value = "IQD ${String.format(Locale.US, "%,.0f", service?.price ?: 45000.0)}")
            InfoRow(label = stringResource(R.string.auto_renew), value = if (user?.autoRenew == 1) "مفعل" else "معطل")
        }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Card 2: معلومات الخدمة
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("customer_service_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "معلومات الخدمة",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(14.dp))

            InfoRow(label = stringResource(R.string.service_speed), value = service?.downloadLimit ?: "--")
            InfoRow(label = stringResource(R.string.static_ip), value = user?.staticIp ?: "--")
            InfoRow(label = stringResource(R.string.username), value = user?.username ?: "bbn.17@haq2")
            InfoRow(label = stringResource(R.string.password), value = "••••••••••")
            InfoRow(label = stringResource(R.string.company), value = user?.company ?: "--")
            InfoRow(label = stringResource(R.string.contract_id), value = user?.contractId ?: "300285")
        }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Card 3: قسم المطور ودعم التطبيق (Instagram, WhatsApp, SUPER QI)
    DeveloperSupportCard(onCopied = onCopied)
    Spacer(modifier = Modifier.height(14.dp))
}

/**
 * Screen 5 in image: تفاصيل الاشتراك
 */
@Composable
private fun SubscriptionDetailsScreen(
    user: UserAccountData?,
    service: ServiceData?,
    remainingDays: Int,
    isServiceOnline: Boolean,
    isSubscriptionActive: Boolean,
    onRenew: () -> Unit
) {
    // Top Mini Card
    MiniPlanHeaderCard(
        planName = service?.profileName ?: "2-Nitro",
        isActive = isSubscriptionActive,
        remainingDays = remainingDays
    )

    Spacer(modifier = Modifier.height(14.dp))

    // Card 1: معلومات الاشتراك
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("subscription_info_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "معلومات الاشتراك",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(14.dp))

            InfoRow(label = stringResource(R.string.current_plan), value = service?.profileName ?: "2-Nitro")
            InfoRow(label = stringResource(R.string.service_description), value = service?.description ?: user?.name ?: "ياسين حامد")
            InfoRow(label = stringResource(R.string.start_date), value = NotificationScheduler.formatArabicDate(user?.registeredOn))
            InfoRow(label = stringResource(R.string.expiration_date), value = NotificationScheduler.formatExpirationWithRemainingTime(service?.expiration), valueColor = SkylineBlue)
            InfoRow(
                label = stringResource(R.string.status),
                value = if (isServiceOnline) "ONLINE" else "OFFLINE",
                valueColor = if (isServiceOnline) SkylineOnline else Color.Gray
            )
            InfoRow(label = stringResource(R.string.price), value = "IQD ${String.format(Locale.US, "%,.0f", service?.price ?: 45000.0)}")
            InfoRow(label = stringResource(R.string.auto_renew), value = if (user?.autoRenew == 1) "مفعل" else "معطل")
        }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Card 2: Contact & Contract details
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("subscription_contact_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            InfoRow(label = stringResource(R.string.phone), value = user?.phone ?: "--")
            InfoRow(label = stringResource(R.string.address), value = user?.address ?: "--")
            InfoRow(label = stringResource(R.string.company), value = user?.company ?: "--")
            InfoRow(label = stringResource(R.string.contract_id), value = user?.contractId ?: "300285")
        }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // Bottom Action Button: تجديد الخدمة
    Button(
        onClick = onRenew,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("renew_button"),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = SkylineBlue)
    ) {
        Icon(
            imageVector = Icons.Default.Autorenew,
            contentDescription = null,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(R.string.renew_service),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }

    Spacer(modifier = Modifier.height(12.dp))
}

/**
 * Mini header card present at the top of Screen 4 and Screen 5
 */
@Composable
private fun MiniPlanHeaderCard(
    planName: String,
    isActive: Boolean,
    remainingDays: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("mini_plan_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = planName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                StatusBadge(isActive = isActive)
            }

            CircularDaysGauge(
                days = remainingDays,
                percentage = if (remainingDays <= 0) 0f else (remainingDays / 30f).coerceIn(0.05f, 1f),
                size = 76.dp
            )
        }
    }
}

/**
 * Notifications view
 */
@Composable
private fun NotificationsTabScreen(
    service: ServiceData?,
    remainingDays: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SkylineBlue.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = SkylineBlue
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "تنبيه انتهاء الاشتراك",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "ينتهي اشتراك باقة ${service?.profileName ?: "2-Nitro"} في ${service?.expiration?.take(10) ?: "2026-10-13"}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(14.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SkylineSuccess.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Router,
                        contentDescription = null,
                        tint = SkylineSuccess
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "حالة خادم Skyline",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "الاتصال مباشر ومستقر عبر admin.skylineiq.com/user",
                        fontSize = 12.sp,
                        color = SkylineSuccess
                    )
                }
            }
        }
    }
}

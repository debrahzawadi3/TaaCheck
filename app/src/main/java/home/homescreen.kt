package screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Help
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.happybirthday.taacheck.R
import com.happybirthday.taacheck.ui.theme.ButterYellow
import viewmodels.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val userName = uiState.userName
    val hasNewNotification = uiState.hasNewNotification
    val isLoading = uiState.isLoading

    Scaffold(
        containerColor = ButterYellow,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Hi, $userName",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { Log.d("HomeScreen", "Logo clicked") }) {
                        Image(
                            painter = painterResource(id = R.drawable.taacheck_logo),
                            contentDescription = "TaaCheck Logo",
                            modifier = Modifier.size(80.dp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate("notifications") {
                            popUpTo("home") { inclusive = false }
                            launchSingleTop = true
                        }
                    }) {
                        Icon(
                            painter = painterResource(
                                id = if (hasNewNotification)
                                    R.drawable.ic_notification_red
                                else
                                    R.drawable.ic_notification
                            ),
                            contentDescription = "notifications"
                        )
                    }

                    IconButton(onClick = {
                        navController.navigate("help") {
                            popUpTo("home") { inclusive = false }
                            launchSingleTop = true
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Help,
                            contentDescription = "Help",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ButterYellow)
            )
        },
        bottomBar = {
            BottomNavBar(
                navController = navController,
                onHomeClick = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onServiceClick = {
                    navController.navigate(Routes.CREATE_SERVICE) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onReportClick = {
                    navController.navigate(Routes.CREATE_REPORT) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onProfileClick = {
                    navController.navigate(Routes.PROFILE) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = BluePrimary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(
                        PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = innerPadding.calculateTopPadding() + 24.dp,
                            bottom = innerPadding.calculateBottomPadding()
                        )
                    ),
                verticalArrangement = Arrangement.spacedBy(30.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(60.dp)
                ) {
                    CardItem(
                        title = "Post Feed",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        onClick = { navController.navigate("postFeed") },
                        imageRes = R.drawable.post_background
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(30.dp)
                    ) {
                        CardItem(
                            title = "Service Requests",
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f),
                            onClick = { navController.navigate("serviceRequests") },
                            imageRes = R.drawable.service_card
                        )

                        CardItem(
                            title = "Service Providers",
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f),
                            onClick = { navController.navigate("serviceProviders") },
                            imageRes = R.drawable.provider_card
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CardItem(
    title: String,
    modifier: Modifier,
    onClick: () -> Unit,
    imageRes: Int
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .border(width = 2.dp, color = Color.Black, shape = RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                        )
                    )
                    .padding(8.dp)
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
    }
}






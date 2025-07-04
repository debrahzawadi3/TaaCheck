package screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Help
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.happybirthday.taacheck.ui.theme.BluePrimary
import com.happybirthday.taacheck.ui.theme.ButterYellow
import viewmodels.NormalUserProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NormalUserProfileScreen(
    navController: NavController,
    viewModel: NormalUserProfileViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = ButterYellow,
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    IconButton(onClick = { navController.navigate("help") }) {
                        Icon(Icons.Default.Help, contentDescription = "Help", tint = Color.Black)
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
        if (state.isLoading) {
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
                    .verticalScroll(scrollState)
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!state.profileImageUrl.isNullOrEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(state.profileImageUrl),
                        contentDescription = "Profile image",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(BluePrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.fullName.take(1).uppercase(),
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White
                        )
                    }
                }

                Text(state.fullName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(state.email, color = Color.DarkGray)
                Text(state.phone, color = Color.DarkGray)
                Text(state.county, color = Color.DarkGray)

                Divider(thickness = 1.dp)
                Button(
                    onClick = {
                        navController.navigate(Routes.YOUR_POSTS) {
                            popUpTo(Routes.PROFILE) { inclusive = false }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("View Your Posts", color = Color.White)
                }
                Button(
                    onClick = {
                        navController.navigate(Routes.YOUR_SERVICE_REQUESTS) {
                            popUpTo(Routes.PROFILE) { inclusive = false }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("View Your Service Requests", color = Color.White)
                }

                Button(
                    onClick = { navController.navigate(Routes.SIGNUP_PROVIDER) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Change To Service Provider", color = Color.Black)
                }
            }
        }
    }
}
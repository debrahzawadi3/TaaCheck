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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.happybirthday.taacheck.ui.theme.BluePrimary
import com.happybirthday.taacheck.ui.theme.ButterYellow
import viewmodels.ServiceProviderProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceProviderProfileScreen(
    navController: NavController,
    viewModel: ServiceProviderProfileViewModel = viewModel()
) {
    val userData by viewModel.userData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

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
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                        launchSingleTop = true
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
            userData?.let { data ->
                val name = data["fullName"] as? String ?: "N/A"
                val profession = data["profession"] as? String ?: "N/A"
                val bio = data["bio"] as? String ?: "N/A"
                val county = data["county"] as? String ?: "N/A"
                val gender = data["gender"] as? String ?: "N/A"
                val experience = data["experience"] as? String ?: "N/A"
                val idNumber = data["idNumber"] as? String ?: "N/A"
                val rating = (data["rating"] as? Number)?.toDouble() ?: 0.0
                val completedJobs = (data["completedJobs"] as? Number)?.toInt() ?: 0
                val imageUrl = data["profileImageUrl"] as? String

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(innerPadding)
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (!imageUrl.isNullOrEmpty()) {
                        Image(
                            painter = rememberAsyncImagePainter(imageUrl),
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
                                text = name.take(1),
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White
                            )
                        }
                    }

                    Text(name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(profession, color = BluePrimary)
                    Spacer(modifier = Modifier.height(4.dp)) // just for breathing space

                    Text(
                        text = "TaaCheck ID: ${data["serviceCode"] ?: "Unavailable"}",
                        color = Color.DarkGray,
                        fontSize = 14.sp
                    )

                    Divider(thickness = 1.dp)

                    InfoText("Bio", bio)
                    InfoText("County", county)
                    InfoText("Gender", gender)
                    InfoText("Experience", "$experience years")
                    InfoText("ID Number", idNumber)
                    InfoText("Rating", "%.1f ★".format(rating))
                    InfoText("Jobs Completed", "$completedJobs")

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
                        onClick = { navController.navigate(Routes.SIGNUP_USER) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Yellow,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Change To Normal User", color = Color.Black)
                    }
                }
            } ?: Text("Failed to load profile", modifier = Modifier.padding(16.dp), color = Color.Red)
        }
    }
}

@Composable
fun InfoText(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("$label:", fontWeight = FontWeight.SemiBold)
        Text(value)
    }
}
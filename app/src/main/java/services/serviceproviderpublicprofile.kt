package services

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.firestore.FirebaseFirestore
import com.happybirthday.taacheck.ui.theme.BluePrimary
import com.happybirthday.taacheck.ui.theme.ButterYellow
import kotlinx.coroutines.tasks.await
import screens.BottomNavBar
import screens.Routes

@Composable
fun ServiceProviderProfileDetailScreen(uid: String, navController: NavController = rememberNavController()) {
    val context = LocalContext.current

    var fullName by remember { mutableStateOf("") }
    var profession by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(0.0) }
    var completedJobs by remember { mutableStateOf(0) }
    var profileImageUrl by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(uid) {
        try {
            val doc = FirebaseFirestore.getInstance().collection("users").document(uid).get().await()
            fullName = doc.getString("fullName") ?: ""
            profession = doc.getString("profession") ?: ""
            bio = doc.getString("bio") ?: ""
            rating = doc.getDouble("rating") ?: 0.0
            completedJobs = (doc.getLong("completedJobs") ?: 0).toInt()
            profileImageUrl = doc.getString("profileImageUrl") ?: ""
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to load profile: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        containerColor = ButterYellow,
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
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = "Profile",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 16.dp)
                )

                if (profileImageUrl.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(model = profileImageUrl),
                        contentDescription = "Profile Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
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
                            text = fullName.take(1),
                            color = Color.White,
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = profession,
                    color = BluePrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Divider(color = Color.Black, thickness = 1.dp)

                Text("Bio: $bio", color = Color.Black, fontSize = 16.sp)
                Text("Rating: %.1f ★".format(rating), color = Color.Black, fontSize = 16.sp)
                Text("Completed Jobs: $completedJobs", color = Color.Black, fontSize = 16.sp)

                Button(
                    onClick = { navController.navigate("requestService/$uid") },
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Request For Service Provider", color = Color.White)
                }
            }
        }
    }
}
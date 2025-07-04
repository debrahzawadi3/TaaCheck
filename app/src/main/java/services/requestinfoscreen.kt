package services

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.happybirthday.taacheck.ui.theme.BluePrimary
import com.happybirthday.taacheck.ui.theme.ButterYellow
import kotlinx.coroutines.tasks.await
import screens.BottomNavBar
import services.models.ServiceRequest

@Composable
fun RequestInfoScreen(requestId: String, navController: NavController = rememberNavController()) {
    val db = FirebaseFirestore.getInstance()
    var request by remember { mutableStateOf<ServiceRequest?>(null) }

    LaunchedEffect(requestId) {
        try {
            val snapshot = db.collection("service_requests").document(requestId).get().await()
            request = snapshot.toObject(ServiceRequest::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Scaffold(
        containerColor = ButterYellow,
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            Column {
                Text(
                    text = "Request Info",
                    fontSize = 26.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Divider(color = Color.Black, thickness = 1.dp)
            }

            if (request != null) {
                Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {

                    Column {
                        Text("Title", fontSize = 18.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = Color.Black)
                        Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                        Text(request!!.title, fontSize = 16.sp, color = Color.DarkGray)
                    }

                    Column {
                        Text("Description", fontSize = 18.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = Color.Black)
                        Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                        Text(request!!.description, fontSize = 16.sp, color = Color.DarkGray)
                    }

                    Column {
                        Text("Location", fontSize = 18.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = Color.Black)
                        Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                        Text(request!!.locationTag, fontSize = 16.sp, color = Color.DarkGray)
                    }

                    Button(
                        onClick = {
                            navController.navigate("acceptService/${FirebaseAuth.getInstance().currentUser?.uid}")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("ACCEPT SERVICE REQUEST", color = Color.White)
                    }
                }
            } else {
                Text("Loading request data...", color = Color.Black)
            }
        }
    }
}
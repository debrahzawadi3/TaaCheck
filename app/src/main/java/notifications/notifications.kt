package notifications

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.happybirthday.taacheck.R
import com.happybirthday.taacheck.ui.theme.ButterYellow
import kotlinx.coroutines.tasks.await
import screens.BottomNavBar

@Composable
fun NotificationsScreen(navController: NavController = rememberNavController()) {
    val scrollState = rememberScrollState()
    val firestore = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    var acceptanceNotifications by remember { mutableStateOf(listOf<DocumentSnapshot>()) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        try {
            userId?.let {
                firestore.collection("users").document(it).update("hasNewNotification", false)
                val snapshot = firestore.collection("acceptances")
                    .whereEqualTo("receiverId", it)
                    .get()
                    .await()
                acceptanceNotifications = snapshot.documents
            }
        } catch (e: Exception) {
            Log.e("NotificationsScreen", "Error fetching notifications: ", e)
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
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "notifications",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Divider(color = Color.Black, thickness = 1.dp)

            if (acceptanceNotifications.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(top = 64.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_nothing),
                        contentDescription = "No notifications",
                        modifier = Modifier.size(120.dp).padding(bottom = 16.dp)
                    )
                    Text("No notifications yet", fontSize = 16.sp, color = Color.Gray)
                }
            }

            acceptanceNotifications.forEach { doc ->
                val name = doc.getString("name") ?: ""
                val taaCheckId = doc.getString("taaCheckId") ?: ""
                val phone = doc.getString("businessPhone") ?: ""
                val email = doc.getString("email") ?: ""
                val serviceProviderId = doc.getString("senderId") ?: ""
                val acceptanceId = doc.id

                NotificationInfoCard(name, taaCheckId, phone, email)

                ActionDecisionCard(
                    onAccept = {
                        val providerRef = firestore.collection("users").document(serviceProviderId)
                        providerRef.get().addOnSuccessListener {
                            val completed = it.getLong("completedTasks") ?: 0
                            providerRef.update("completedTasks", completed + 1)
                        }
                        firestore.collection("service_requests")
                            .whereEqualTo("receiverId", userId)
                            .get()
                            .addOnSuccessListener { snapshot ->
                                snapshot.documents.forEach { it.reference.delete() }
                            }
                        firestore.collection("acceptances").document(acceptanceId).delete()
                        firestore.collection("users").document(serviceProviderId)
                            .collection("messages")
                            .add(mapOf("text" to "Your request has been accepted", "timestamp" to System.currentTimeMillis()))
                        Toast.makeText(context, "Thank you for using Taa Check", Toast.LENGTH_LONG).show()
                    },
                    onDecline = {
                        firestore.collection("acceptances").document(acceptanceId).delete()
                        firestore.collection("users").document(userId!!).collection("messages")
                            .add(mapOf("text" to "You have declined", "timestamp" to System.currentTimeMillis()))
                        firestore.collection("users").document(serviceProviderId)
                            .collection("messages")
                            .add(mapOf("text" to "Your service offer was declined", "timestamp" to System.currentTimeMillis()))
                        Toast.makeText(context, "You have declined the request", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

@Composable
fun NotificationInfoCard(name: String, taaCheckId: String, phone: String, email: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Service Request Accepted", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Divider(color = Color.LightGray)
            Text("Name: $name", fontSize = 14.sp)
            Text("TaaCheck ID: $taaCheckId", fontSize = 14.sp)
            Text("Phone: $phone", fontSize = 14.sp)
            Text("Email: $email", fontSize = 14.sp)
            Text("The contact info above has been provided so you can be able to communicate with the service provider. Thank You", fontSize = 13.sp, color = Color.DarkGray)
        }
    }
}

@Composable
fun ActionDecisionCard(onAccept: () -> Unit, onDecline: () -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirm Decline") },
            text = { Text("Are you sure you want to decline?") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    onDecline()
                }) {
                    Text("Decline")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                "If you accept the services of the above service provider click accept so we can have your request confirmed and deleted from the feed, if not click decline.",
                fontSize = 14.sp,
                color = Color.Black
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onAccept,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
                ) {
                    Text("Accept", color = Color.White)
                }

                Button(
                    onClick = { showDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text("Decline", color = Color.White)
                }
            }
        }
    }
}



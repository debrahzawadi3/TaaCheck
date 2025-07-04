package home

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.happybirthday.taacheck.R
import com.happybirthday.taacheck.ui.theme.BluePrimary
import com.happybirthday.taacheck.ui.theme.ButterYellow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import screens.BottomNavBar
import java.text.SimpleDateFormat
import java.util.*

data class ServiceRequest(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val description: String = "",
    val locationTag: String = "",
    val timestamp: Long = 0L
)

fun formatTimestamp(time: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    return sdf.format(Date(time))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YourServiceRequestsScreen(navController: NavController) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    val scope = rememberCoroutineScope()

    var allRequests by remember { mutableStateOf(emptyList<ServiceRequest>()) }
    var filteredRequests by remember { mutableStateOf(emptyList<ServiceRequest>()) }
    var searchQuery by remember { mutableStateOf("") }
    var searchTriggered by remember { mutableStateOf(false) }

    suspend fun loadServices() {
        try {
            val result = db.collection("service_requests")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            allRequests = result.documents.mapNotNull { doc ->
                val data = doc.data
                if (data != null) {
                    ServiceRequest(
                        id = doc.id,
                        userId = data["userId"] as? String ?: "",
                        title = data["title"] as? String ?: "",
                        description = data["description"] as? String ?: "",
                        locationTag = data["locationTag"] as? String ?: "",
                        timestamp = data["timestamp"] as? Long ?: 0L
                    )
                } else null
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to load service requests", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        scope.launch { loadServices() }
    }

    BackHandler(enabled = searchTriggered) {
        searchQuery = ""
        searchTriggered = false
    }

    Scaffold(
        containerColor = ButterYellow,
        bottomBar = {
            BottomNavBar(navController = navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Your Requested Services",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Divider(color = Color.Black, thickness = 1.dp)

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search by Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = {
                        searchTriggered = true
                        filteredRequests = allRequests.filter {
                            it.title.contains(searchQuery.trim(), ignoreCase = true)
                        }
                    }) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )

            val services = if (searchTriggered) filteredRequests else allRequests

            if (services.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 64.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_nothing),
                        contentDescription = "No Services",
                        modifier = Modifier
                            .size(120.dp)
                            .padding(bottom = 16.dp)
                    )
                    Text(
                        text = if (searchTriggered) "No results found" else "You haven’t requested any services yet.",
                        color = Color.Gray,
                        fontSize = 16.sp,
                        fontStyle = FontStyle.Italic
                    )
                }
            } else {
                services.forEach { service ->
                    ServiceRequestCard(service, navController, onDeleted = {
                        scope.launch {
                            loadServices()
                            if (searchTriggered) {
                                filteredRequests = allRequests.filter {
                                    it.title.contains(searchQuery.trim(), ignoreCase = true)
                                }
                            }
                        }
                    })
                }
            }
        }
    }
}

@Composable
fun ServiceRequestCard(
    service: ServiceRequest,
    navController: NavController,
    onDeleted: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val scope = rememberCoroutineScope()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(12.dp)
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(16.dp)
        ) {
            Text(service.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Text(service.description, fontSize = 16.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("County: ${service.locationTag}", color = Color.DarkGray, fontSize = 12.sp)
                Text("Posted: ${formatTimestamp(service.timestamp)}", fontSize = 12.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        navController.navigate("create_service?editId=${service.id}") {
                            popUpTo("yourServiceRequests") { inclusive = false }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                ) {
                    Text("Edit", color = Color.White)
                }

                Button(
                    onClick = {
                        onDeleted()
                        scope.launch {
                            db.collection("service_requests").document(service.id).delete()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Delete", color = Color.White)
                }
            }
        }
    }
}
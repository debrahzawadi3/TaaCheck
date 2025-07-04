package screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch

val ButterYellow = Color(0xFFFFEDA8)
val BluePrimary = Color(0xFF0D47A1)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateServiceRequestScreen(
    navController: NavController,
    editId: String? = null
) {
    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val scope = rememberCoroutineScope()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var locationTag by remember { mutableStateOf("") }
    var isEditMode by remember { mutableStateOf(false) }
    var countyExpanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) } // Loading state

    val counties = listOf(
        "Mombasa", "Kwale", "Kilifi", "Tana River", "Lamu", "Taita Taveta",
        "Garissa", "Wajir", "Mandera", "Marsabit", "Isiolo", "Meru", "Tharaka-Nithi",
        "Embu", "Kitui", "Machakos", "Makueni", "Nyandarua", "Nyeri", "Kirinyaga",
        "Murang'a", "Kiambu", "Turkana", "West Pokot", "Samburu", "Trans Nzoia",
        "Uasin Gishu", "Elgeyo Marakwet", "Nandi", "Baringo", "Laikipia", "Nakuru",
        "Narok", "Kajiado", "Kericho", "Bomet", "Kakamega", "Vihiga", "Bungoma",
        "Busia", "Siaya", "Kisumu", "Homa Bay", "Migori", "Kisii", "Nyamira", "Nairobi"
    )

    LaunchedEffect(editId) {
        if (!editId.isNullOrBlank()) {
            val doc = db.collection("service_requests").document(editId).get().await()
            if (doc.exists()) {
                title = doc.getString("title") ?: ""
                description = doc.getString("description") ?: ""
                locationTag = doc.getString("locationTag") ?: ""
                isEditMode = true
            }
        }
    }

    Scaffold(
        containerColor = ButterYellow,
        topBar = {
            TopAppBar(
                title = {},
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = if (isEditMode) "Edit Service Request" else "Create Service Request",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            Divider(color = Color.Black, thickness = 1.dp)

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Request Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Request Description") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 5,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = countyExpanded,
                onExpandedChange = { countyExpanded = !countyExpanded }
            ) {
                OutlinedTextField(
                    value = locationTag,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select County") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = countyExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    enabled = !isLoading
                )

                ExposedDropdownMenu(
                    expanded = countyExpanded,
                    onDismissRequest = { countyExpanded = false }
                ) {
                    counties.forEach { county ->
                        DropdownMenuItem(
                            text = { Text(county) },
                            onClick = {
                                locationTag = county
                                countyExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (!title.isBlank() && !description.isBlank() && !locationTag.isBlank() && userId != null) {
                        isLoading = true
                        scope.launch {
                            try {
                                val request = mapOf(
                                    "title" to title,
                                    "description" to description,
                                    "locationTag" to locationTag,
                                    "timestamp" to System.currentTimeMillis(),
                                    "userId" to userId
                                )

                                if (isEditMode && !editId.isNullOrBlank()) {
                                    db.collection("service_requests")
                                        .document(editId)
                                        .update(request)
                                } else {
                                    db.collection("service_requests")
                                        .add(request)
                                }

                                navController.navigate(Routes.YOUR_SERVICE_REQUESTS) {
                                    popUpTo(Routes.HOME)
                                }
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        if (isEditMode) "Update Request" else "Post Request",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
package posts

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import screens.BottomNavBar
import screens.Routes

val ButterYellow = Color(0xFFFFEDA8)
val BluePrimary = Color(0xFF0D47A1)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateReportPostScreen(navController: NavController, postId: String? = null) {
    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var isUploading by remember { mutableStateOf(false) }

    val locationExpanded = remember { mutableStateOf(false) }

    val counties = listOf(
        "Mombasa", "Kwale", "Kilifi", "Tana River", "Lamu", "Taita Taveta", "Garissa", "Wajir", "Mandera", "Marsabit",
        "Isiolo", "Meru", "Tharaka-Nithi", "Embu", "Kitui", "Machakos", "Makueni", "Nyandarua", "Nyeri", "Kirinyaga",
        "Murang'a", "Kiambu", "Turkana", "West Pokot", "Samburu", "Trans Nzoia", "Uasin Gishu", "Elgeyo Marakwet",
        "Nandi", "Baringo", "Laikipia", "Nakuru", "Narok", "Kajiado", "Kericho", "Bomet", "Kakamega", "Vihiga",
        "Bungoma", "Busia", "Siaya", "Kisumu", "Homa Bay", "Migori", "Kisii", "Nyamira", "Nairobi"
    )

    LaunchedEffect(postId) {
        if (!postId.isNullOrBlank()) {
            try {
                val doc = firestore.collection("posts").document(postId).get().await()
                title = doc.getString("title") ?: ""
                description = doc.getString("description") ?: ""
                location = doc.getString("location") ?: ""
            } catch (_: Exception) {
                Toast.makeText(context, "Failed to load post", Toast.LENGTH_SHORT).show()
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
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (postId != null) "Edit Post" else "Create Report Post",
                fontSize = 20.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )

            Divider(color = Color.Black, thickness = 1.dp)

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = locationExpanded.value,
                onExpandedChange = { locationExpanded.value = !locationExpanded.value }
            ) {
                OutlinedTextField(
                    value = location,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select County") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = locationExpanded.value)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = locationExpanded.value,
                    onDismissRequest = { locationExpanded.value = false }
                ) {
                    counties.forEach { county ->
                        DropdownMenuItem(
                            text = { Text(county) },
                            onClick = {
                                location = county
                                locationExpanded.value = false
                            }
                        )
                    }
                }
            }

            Button(
                onClick = {
                    if (userId != null && title.isNotBlank() && description.isNotBlank() && location.isNotBlank()) {
                        isUploading = true

                        Toast.makeText(
                            context,
                            if (postId != null) "Post updated" else "Post saved successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        val newPost = mapOf(
                            "username" to userId,
                            "title" to title,
                            "location" to location,
                            "timestamp" to System.currentTimeMillis(),
                            "description" to description,
                            "userId" to userId
                        )

                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("new_post", newPost)

                        navController.navigate(Routes.YOUR_POSTS) {
                            popUpTo(Routes.YOUR_POSTS) { inclusive = true }
                        }

                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                if (!postId.isNullOrBlank()) {
                                    firestore.collection("posts").document(postId).set(newPost).await()
                                } else {
                                    firestore.collection("posts").add(newPost).await()
                                }
                            } catch (_: Exception) {

                            }
                        }
                    } else {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isUploading
            ) {
                if (isUploading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        if (postId != null) "Update Post" else "Post",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
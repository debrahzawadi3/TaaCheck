package services

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.happybirthday.taacheck.R
import com.happybirthday.taacheck.ui.theme.ButterYellow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import screens.BottomNavBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceProvidersScreen(navController: NavController) {
    val firestore = FirebaseFirestore.getInstance()
    var allProviders by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var filteredProviders by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }

    var searchTriggered by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val professions = listOf(
        "Electrician",
        "Electrical Engineer",
        "Solar Technician",
        "Appliance Repair Technician",
        "Lighting Specialist",
        "Power Line Installer",
        "Cable Technician"
    )
    var selectedProfession by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        firestore.collection("users")
            .whereEqualTo("role", "serviceProvider")
            .get()
            .addOnSuccessListener { result ->
                allProviders = result.documents.mapNotNull { it.data }
            }
    }

    BackHandler(enabled = searchTriggered) {
        searchTriggered = false
        selectedProfession = ""
        focusManager.clearFocus()
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
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Service Providers Feed",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Divider(color = Color.Black, thickness = 1.dp)

            if (!searchTriggered) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedProfession,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Profession") },
                        trailingIcon = {
                            Row {
                                Icon(
                                    imageVector = Icons.Filled.ArrowDropDown,
                                    contentDescription = "Dropdown"
                                )
                                IconButton(onClick = {
                                    if (selectedProfession.isNotBlank()) {
                                        isLoading = true
                                        searchTriggered = true
                                        scope.launch {
                                            delay(800)
                                            filteredProviders = allProviders.filter {
                                                val profession = it["profession"] as? String ?: ""
                                                profession.equals(selectedProfession, ignoreCase = true)
                                            }
                                            isLoading = false
                                        }
                                    }
                                }) {
                                    Icon(Icons.Default.Search, contentDescription = "Search")
                                }
                            }
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        singleLine = true
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        professions.forEach { profession ->
                            DropdownMenuItem(
                                text = { Text(profession) },
                                onClick = {
                                    selectedProfession = profession
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
                return@Column
            }

            val toDisplay = if (searchTriggered) filteredProviders else allProviders

            if (toDisplay.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 64.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_nothing),
                        contentDescription = "No providers",
                        modifier = Modifier
                            .size(120.dp)
                            .padding(bottom = 16.dp)
                    )
                    Text(
                        text = if (searchTriggered) "No results found" else "No service providers yet",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                return@Column
            }

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                toDisplay.forEach { provider ->
                    val name = provider["fullName"] as? String ?: "Unknown"
                    val rating = provider["rating"] as? Double ?: 0.0
                    val completedJobs = provider["completedJobs"] as? Long ?: 0L
                    val uid = provider["uid"] as? String ?: ""

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate("publicProfile/$uid")
                            },
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = name, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Rating: ${"%.1f".format(rating)} ⭐")
                            Text(text = "Completed Jobs: $completedJobs")
                        }
                    }
                }
            }
        }
    }
}
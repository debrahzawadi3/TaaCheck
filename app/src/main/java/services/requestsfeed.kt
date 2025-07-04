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
import com.happybirthday.taacheck.ui.theme.BluePrimary
import com.happybirthday.taacheck.ui.theme.ButterYellow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import screens.BottomNavBar
import screens.Routes
import services.models.ServiceRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceRequestFeedScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    var serviceRequests by remember { mutableStateOf<List<ServiceRequest>>(emptyList()) }
    var filteredRequests by remember { mutableStateOf<List<ServiceRequest>>(emptyList()) }
    var selectedCounty by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var searchTriggered by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val counties = listOf(
        "Baringo", "Bomet", "Bungoma", "Busia", "Elgeyo-Marakwet", "Embu", "Garissa", "Homa Bay",
        "Isiolo", "Kajiado", "Kakamega", "Kericho", "Kiambu", "Kilifi", "Kirinyaga", "Kisii", "Kisumu",
        "Kitui", "Kwale", "Laikipia", "Lamu", "Machakos", "Makueni", "Mandera", "Marsabit", "Meru",
        "Migori", "Mombasa", "Murang'a", "Nairobi", "Nakuru", "Nandi", "Narok", "Nyamira", "Nyandarua",
        "Nyeri", "Samburu", "Siaya", "Taita-Taveta", "Tana River", "Tharaka-Nithi", "Trans Nzoia",
        "Turkana", "Uasin Gishu", "Vihiga", "Wajir", "West Pokot"
    )

    fun refreshRequests() {
        scope.launch {
            val snapshot = db.collection("service_requests").get().await()
            val requests = snapshot.documents.mapNotNull { doc ->
                doc.toObject(ServiceRequest::class.java)?.copy(id = doc.id)
            }
            serviceRequests = requests.sortedByDescending { it.timestamp }
        }
    }

    LaunchedEffect(Unit) {
        refreshRequests()
    }

    BackHandler(enabled = searchTriggered) {
        searchTriggered = false
        selectedCounty = ""
        focusManager.clearFocus()
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Service Request Feed",
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
                        value = selectedCounty,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select County") },
                        trailingIcon = {
                            Row {
                                Icon(
                                    imageVector = Icons.Filled.ArrowDropDown,
                                    contentDescription = "Dropdown"
                                )
                                IconButton(onClick = {
                                    focusManager.clearFocus()
                                    if (selectedCounty.isNotBlank()) {
                                        isLoading = true
                                        searchTriggered = true
                                        scope.launch {
                                            filteredRequests = serviceRequests.filter {
                                                it.locationTag.equals(selectedCounty, ignoreCase = true)
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
                        counties.forEach { county ->
                            DropdownMenuItem(
                                text = { Text(county) },
                                onClick = {
                                    selectedCounty = county
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

            val toDisplay = if (searchTriggered) filteredRequests else serviceRequests

            if (toDisplay.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(top = 64.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_nothing),
                        contentDescription = null,
                        modifier = Modifier.size(120.dp).padding(bottom = 16.dp)
                    )
                    Text(
                        text = if (searchTriggered) "No results found" else "No service requests yet",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                return@Column
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                toDisplay.forEach { request ->
                    ServiceRequestCard(request) {
                        navController.navigate("requestInfo/${request.id}")
                    }
                }
            }
        }
    }
}

@Composable
fun ServiceRequestCard(request: ServiceRequest, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = BluePrimary),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = request.title,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 18.sp
            )
            Text(text = request.locationTag, color = Color.White)
        }
    }
}
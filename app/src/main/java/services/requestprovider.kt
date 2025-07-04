package services

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.happybirthday.taacheck.ui.theme.BluePrimary
import com.happybirthday.taacheck.ui.theme.ButterYellow
import screens.BottomNavBar

@Composable
fun RequestProviderScreen(
    serviceProviderUid: String,
    navController: NavController = rememberNavController()
) {
    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()

    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    LaunchedEffect(serviceProviderUid) {
        firestore.collection("users")
            .document(serviceProviderUid)
            .update("hasNewNotification", true)
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
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "REQUEST FOR PROVIDER",
                fontSize = 24.sp,
                color = Color.Black,
                modifier = Modifier.padding(top = 16.dp)
            )

            Divider(color = Color.Black, thickness = 1.dp)

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Your Full Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Your Location") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = contact,
                onValueChange = { contact = it },
                label = { Text("Email or Phone Number") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Service Description") },
                modifier = Modifier.fillMaxWidth()
            )

            Divider(color = Color.Black, thickness = 1.dp)

            Button(
                onClick = {
                    if (name.isNotBlank() && location.isNotBlank() && contact.isNotBlank() && description.isNotBlank()) {
                        val requestData = hashMapOf(
                            "name" to name,
                            "location" to location,
                            "contact" to contact,
                            "description" to description,
                            "timestamp" to System.currentTimeMillis(),
                            "receiverId" to serviceProviderUid
                        )

                        firestore.collection("requests")
                            .add(requestData)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Request sent!", Toast.LENGTH_SHORT).show()
                                name = ""
                                location = ""
                                contact = ""
                                description = ""
                                navController.popBackStack()
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Failed to send request.", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("SUBMIT", color = Color.White)
            }
        }
    }
}



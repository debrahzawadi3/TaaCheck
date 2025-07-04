package services

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.happybirthday.taacheck.ui.theme.BluePrimary
import com.happybirthday.taacheck.ui.theme.ButterYellow
import screens.BottomNavBar
import screens.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceAcceptanceFormScreen(
    navController: NavController = rememberNavController(),
    serviceProviderUid: String
) {
    var name by remember { mutableStateOf("") }
    var taaCheckId by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var businessPhone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    var isSubmitting by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    val roles = listOf(
        "Electrician",
        "Electrical Engineer",
        "Solar Technician",
        "Appliance Repair Technician",
        "Lighting Specialist",
        "Power Line Installer",
        "Cable Technician"
    )

    val scope = rememberCoroutineScope()
    val firestore = FirebaseFirestore.getInstance()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    val allFieldsFilled = name.isNotBlank() && taaCheckId.isNotBlank() &&
            role.isNotBlank() && businessPhone.isNotBlank() && email.isNotBlank()

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
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Acceptance Form",
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
                value = taaCheckId,
                onValueChange = { taaCheckId = it },
                label = { Text("TaaCheck ID") },
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = role,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Your Role") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    roles.forEach {
                        DropdownMenuItem(
                            text = { Text(it) },
                            onClick = {
                                role = it
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = businessPhone,
                onValueChange = { businessPhone = it },
                label = { Text("Business Phone Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    isSubmitting = true
                    focusManager.clearFocus()

                    firestore.collection("users")
                        .whereEqualTo("serviceCode", taaCheckId.trim())
                        .whereEqualTo("role", "serviceProvider")
                        .get()
                        .addOnSuccessListener { result ->
                            if (!result.isEmpty) {
                                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                                val formData = hashMapOf(
                                    "name" to name,
                                    "taaCheckId" to taaCheckId,
                                    "role" to role,
                                    "businessPhone" to businessPhone,
                                    "email" to email,
                                    "receiverId" to serviceProviderUid,
                                    "senderId" to userId,
                                    "description" to "Service request accepted by $name",
                                    "timestamp" to System.currentTimeMillis()
                                )

                                firestore.collection("acceptances").add(formData)

                                firestore.collection("users")
                                    .document(serviceProviderUid)
                                    .update("hasNewNotification", true)

                                isSubmitting = false
                                navController.popBackStack()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Invalid TaaCheck ID or not a registered service provider.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                isSubmitting = false
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                context,
                                "Error verifying TaaCheck ID: ${it.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                            isSubmitting = false
                        }
                },
                enabled = allFieldsFilled && !isSubmitting,
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp))
                } else {
                    Text("ACCEPT", color = Color.White)
                }
            }
        }
    }
}





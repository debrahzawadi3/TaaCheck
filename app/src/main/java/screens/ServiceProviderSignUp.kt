package screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.happybirthday.taacheck.ui.theme.BluePrimary
import com.happybirthday.taacheck.ui.theme.ButterYellow
import com.happybirthday.taacheck.ui.theme.TextBlack
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceProviderSignUpScreen(navController: NavController) {
    val isInPreview = LocalInspectionMode.current
    val context = LocalContext.current

    val auth = if (!isInPreview) FirebaseAuth.getInstance() else null
    val firestore = if (!isInPreview) FirebaseFirestore.getInstance() else null

    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var selectedCounty by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Not specified") }
    var profession by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }
    var idNumber by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var countyExpanded by remember { mutableStateOf(false) }
    var professionExpanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val counties = listOf(
        "Mombasa", "Kwale", "Kilifi", "Tana River", "Lamu", "Taita Taveta",
        "Garissa", "Wajir", "Mandera", "Marsabit", "Isiolo", "Meru", "Tharaka-Nithi",
        "Embu", "Kitui", "Machakos", "Makueni", "Nyandarua", "Nyeri", "Kirinyaga",
        "Murang'a", "Kiambu", "Turkana", "West Pokot", "Samburu", "Trans Nzoia",
        "Uasin Gishu", "Elgeyo Marakwet", "Nandi", "Baringo", "Laikipia", "Nakuru",
        "Narok", "Kajiado", "Kericho", "Bomet", "Kakamega", "Vihiga", "Bungoma",
        "Busia", "Siaya", "Kisumu", "Homa Bay", "Migori", "Kisii", "Nyamira", "Nairobi"
    )

    val professions = listOf(
        "Electrician",
        "Electrical Engineer",
        "Solar Technician",
        "Appliance Repair Technician",
        "Lighting Specialist",
        "Power Line Installer",
        "Cable Technician"
    )

    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(ButterYellow)
            .border(2.dp, Color.Black)
            .padding(5.dp),
        color = ButterYellow
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Service Provider Sign Up", fontSize = 24.sp, color = TextBlack)

            OutlinedTextField(value = fullName, onValueChange = { fullName = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = phoneNumber, onValueChange = { phoneNumber = it }, label = { Text("Phone Number") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email Address") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), modifier = Modifier.fillMaxWidth())

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = icon, contentDescription = null)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(imageVector = icon, contentDescription = null)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            ExposedDropdownMenuBox(
                expanded = countyExpanded,
                onExpandedChange = { countyExpanded = !countyExpanded }
            ) {
                OutlinedTextField(
                    value = selectedCounty,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select County") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = countyExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = countyExpanded,
                    onDismissRequest = { countyExpanded = false }
                ) {
                    counties.forEach { county ->
                        DropdownMenuItem(
                            text = { Text(county) },
                            onClick = {
                                selectedCounty = county
                                countyExpanded = false
                            }
                        )
                    }
                }
            }

            Text("Gender")
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                RadioButton(selected = gender == "Male", onClick = { gender = "Male" }); Text("Male")
                RadioButton(selected = gender == "Female", onClick = { gender = "Female" }); Text("Female")
                RadioButton(selected = gender == "Not specified", onClick = { gender = "Not specified" }); Text("Rather not say")
            }

            ExposedDropdownMenuBox(
                expanded = professionExpanded,
                onExpandedChange = { professionExpanded = !professionExpanded }
            ) {
                OutlinedTextField(
                    value = profession,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Profession") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = professionExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = professionExpanded,
                    onDismissRequest = { professionExpanded = false }
                ) {
                    professions.forEach { job ->
                        DropdownMenuItem(
                            text = { Text(job) },
                            onClick = {
                                profession = job
                                professionExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(value = experience, onValueChange = { experience = it }, label = { Text("Years of Experience") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = idNumber, onValueChange = { idNumber = it }, label = { Text("ID/License Number") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = bio, onValueChange = { bio = it }, label = { Text("Short Bio") }, modifier = Modifier.fillMaxWidth())

            Button(
                onClick = {
                    if (isInPreview || isLoading) return@Button
                    isLoading = true

                    if (fullName.isBlank() || phoneNumber.isBlank() || email.isBlank() || password.isBlank()
                        || confirmPassword.isBlank() || selectedCounty.isBlank() || profession.isBlank()
                        || experience.isBlank() || idNumber.isBlank() || bio.isBlank()
                    ) {
                        Toast.makeText(context, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
                        isLoading = false
                        return@Button
                    }

                    if (password != confirmPassword) {
                        Toast.makeText(context, "Passwords do not match.", Toast.LENGTH_SHORT).show()
                        isLoading = false
                        return@Button
                    }

                    auth?.createUserWithEmailAndPassword(email, password)
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val userId = auth.currentUser?.uid ?: ""
                                val serviceCode = UUID.randomUUID().toString().take(8)

                                val userData = hashMapOf(
                                    "uid" to userId,
                                    "fullName" to fullName,
                                    "phoneNumber" to phoneNumber,
                                    "email" to email,
                                    "county" to selectedCounty,
                                    "gender" to gender,
                                    "profession" to profession,
                                    "experience" to experience,
                                    "idNumber" to idNumber,
                                    "bio" to bio,
                                    "role" to "serviceProvider",
                                    "rating" to 0.0,
                                    "completedJobs" to 0,
                                    "isFeatured" to false,
                                    "profileImageUrl" to "",
                                    "serviceCode" to serviceCode
                                )

                                firestore?.collection("users")?.document(userId)
                                    ?.set(userData)
                                    ?.addOnSuccessListener {
                                        navController.navigate(Routes.HOME) {
                                            popUpTo(Routes.SIGNUP_PROVIDER) { inclusive = true }
                                            launchSingleTop = true
                                        }
                                    }
                                    ?.addOnFailureListener {
                                        Toast.makeText(context, "Failed to save user: ${it.message}", Toast.LENGTH_SHORT).show()
                                    }
                                    ?.addOnCompleteListener {
                                        isLoading = false
                                    }
                            } else {
                                Toast.makeText(context, "Auth failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                isLoading = false
                            }
                        }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary, contentColor = Color.White)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Signing Up...")
                } else {
                    Text("Sign Up")
                }
            }
        }
    }
}
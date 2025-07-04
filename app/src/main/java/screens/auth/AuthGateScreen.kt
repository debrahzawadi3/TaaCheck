package screens.auth

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AuthGateScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val currentUser = auth.currentUser

        if (currentUser == null) {
            navController.navigate("login") {
                popUpTo("authGate") { inclusive = true }
            }
        } else {
            db.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        navController.navigate("home") {
                            popUpTo("authGate") { inclusive = true }
                        }
                    } else {
                        auth.signOut()
                        Toast.makeText(context, "Account was deleted", Toast.LENGTH_SHORT).show()
                        navController.navigate("login") {
                            popUpTo("authGate") { inclusive = true }
                        }
                    }
                }
                .addOnFailureListener {
                    auth.signOut()
                    navController.navigate("login") {
                        popUpTo("authGate") { inclusive = true }
                    }
                }
        }
    }

    // Optional: Show progress while checking
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

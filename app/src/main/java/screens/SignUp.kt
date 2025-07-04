package screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController


@Composable
fun RoleSelectionScreen(navController: NavController) {
    val isInPreview = LocalInspectionMode.current
    if (!isInPreview) {
        val systemUiController = rememberSystemUiController()
        val primaryColor = MaterialTheme.colorScheme.primary
        SideEffect {
            systemUiController.setStatusBarColor(
                color = primaryColor,
                darkIcons = false
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
            .padding(5.dp, 25.dp)
            .border(2.dp, MaterialTheme.colorScheme.onSecondary, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RoleButton("Continue as User") {
                navController.navigate("signup_user")
            }

            Spacer(modifier = Modifier.height(24.dp))

            RoleButton("Continue as Service Provider") {
                navController.navigate("signup_provider")
            }

            Spacer(modifier = Modifier.height(32.dp))

            TextButton(
                onClick = { navController.navigate("login") }
            ) {
                Text(
                    text = "Already have an account? Log in",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun RoleButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp)
    ) {
        Text(text = text, fontSize = 16.sp)
    }
}

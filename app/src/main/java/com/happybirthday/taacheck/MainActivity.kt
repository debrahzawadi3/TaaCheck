package com.happybirthday.taacheck

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.happybirthday.taacheck.ui.theme.TaaCheckTheme
import kotlinx.coroutines.tasks.await
import screens.AppNavGraph
import screens.OnboardingUtils
import screens.Routes

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val splashScreen = installSplashScreen()

        setContent {
            TaaCheckTheme {
                val context = LocalContext.current
                val onboardingUtils = remember { OnboardingUtils(context) }
                val navController = rememberNavController()

                var isAppReady by remember { mutableStateOf(false) }
                var startDestination by remember { mutableStateOf(Routes.HOME) }

                splashScreen.setKeepOnScreenCondition {
                    !isAppReady
                }

                LaunchedEffect(Unit) {
                    val user = FirebaseAuth.getInstance().currentUser
                    val firestore = FirebaseFirestore.getInstance()

                    if (!onboardingUtils.isOnboardingCompleted()) {
                        startDestination = Routes.ONBOARDING
                    } else if (user == null) {
                        startDestination = Routes.SIGNUP
                    } else {
                        val doc = firestore.collection("users").document(user.uid).get().await()
                        startDestination = if (doc.exists()) {
                            Routes.HOME
                        } else {
                            FirebaseAuth.getInstance().signOut()
                            Routes.SIGNUP
                        }
                    }

                    isAppReady = true
                }

                if (isAppReady) {
                    AppNavGraph(navController = navController, startDestination = startDestination)
                }
            }
        }
    }
}






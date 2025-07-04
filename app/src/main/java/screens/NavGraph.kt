package screens

import notifications.NotificationsScreen
import posts.CreateReportPostScreen
import posts.YourPostsScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.happybirthday.taacheck.screens.OnboardingScreen
import home.HelpScreen
import home.YourServiceRequestsScreen
import kotlinx.coroutines.launch
import posts.PostFeedScreen
import screens.auth.LoginScreen
import services.RequestInfoScreen
import services.RequestProviderScreen
import services.ServiceAcceptanceFormScreen
import services.ServiceProviderProfileDetailScreen
import services.ServiceProvidersScreen
import services.ServiceRequestFeedScreen

@Composable
fun AppNavGraph(navController: NavHostController, startDestination: String) {
    NavHost(navController = navController, startDestination = startDestination) {

        composable(Routes.ONBOARDING) {
            val context = LocalContext.current
            val onboardingUtils = remember { OnboardingUtils(context) }
            val scope = rememberCoroutineScope()

            OnboardingScreen(
                onFinished = {
                    onboardingUtils.setOnboardingCompleted()
                    scope.launch {
                        navController.navigate(Routes.SIGNUP) {
                            popUpTo(Routes.ONBOARDING) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Routes.SIGNUP) {
            RoleSelectionScreen(navController)
        }

        composable(Routes.LOGIN) {
            LoginScreen(navController)
        }

        composable(Routes.SIGNUP_USER) {
            NormalUserSignUpScreen(navController)
        }

        composable(Routes.SIGNUP_PROVIDER) {
            ServiceProviderSignUpScreen(navController)
        }

        composable(Routes.HOME) {
            HomeScreen(navController)
        }

        composable(Routes.POST_FEED) {
            PostFeedScreen(navController)
        }

        composable(Routes.SERVICE_REQUESTS) {
            ServiceRequestFeedScreen(navController)
        }

        composable(Routes.SERVICE_PROVIDERS) {
            ServiceProvidersScreen(navController)
        }

        composable(Routes.NOTIFICATIONS) {
            NotificationsScreen(navController)
        }

        composable("publicProfile/{uid}") { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid") ?: ""
            ServiceProviderProfileDetailScreen(uid = uid, navController = navController)
        }

        composable("requestService/{uid}") { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid") ?: ""
            RequestProviderScreen(serviceProviderUid = uid, navController = navController)
        }

        composable("requestInfo/{requestId}") { backStackEntry ->
            val requestId = backStackEntry.arguments?.getString("requestId") ?: ""
            RequestInfoScreen(requestId = requestId, navController = navController)
        }

        composable("acceptService/{uid}") { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid") ?: ""
            ServiceAcceptanceFormScreen(navController = navController, serviceProviderUid = uid)
        }

        composable(
            "create_service?editId={editId}",
            arguments = listOf(navArgument("editId") { nullable = true })
        ) { backStackEntry ->
            val editId = backStackEntry.arguments?.getString("editId")
            CreateServiceRequestScreen(navController, editId)
        }

        composable(
            route = "create_report?postId={postId}",
            arguments = listOf(navArgument("postId") { nullable = true })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId")
            CreateReportPostScreen(navController, postId)
        }

        composable(Routes.PROFILE) {
            var userType by remember { mutableStateOf<String?>(null) }
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            val firestore = FirebaseFirestore.getInstance()

            LaunchedEffect(userId) {
                firestore.collection("users").document(userId).get()
                    .addOnSuccessListener { doc ->
                        userType = doc.getString("role")
                    }
                    .addOnFailureListener {
                        userType = "user"
                    }
            }

            when (userType) {
                null -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                "serviceProvider" -> ServiceProviderProfileScreen(navController)
                else -> NormalUserProfileScreen(navController)
            }
        }

        composable(Routes.YOUR_POSTS) {
            YourPostsScreen(navController)
        }

        composable(Routes.YOUR_SERVICE_REQUESTS) {
            YourServiceRequestsScreen(navController)
        }
        composable("help") {
            HelpScreen(navController)
        }
    }
}

object Routes {
    const val ONBOARDING = "onboarding"
    const val SIGNUP = "signup"
    const val LOGIN = "login"
    const val SIGNUP_USER = "signup_user"
    const val SIGNUP_PROVIDER = "signup_provider"
    const val HOME = "home"
    const val POST_FEED = "postFeed"
    const val SERVICE_REQUESTS = "serviceRequests"
    const val SERVICE_PROVIDERS = "serviceProviders"
    const val NOTIFICATIONS = "notifications"
    const val PROFILE = "profile"
    const val REQUEST_INFO = "requestInfo/{requestId}"
    const val ACCEPT_SERVICE = "acceptService/{uid}"
    const val YOUR_POSTS = "yourPosts"
    const val YOUR_SERVICE_REQUESTS = "yourServiceRequests"

    fun acceptService(uid: String) = "acceptService/$uid"
    fun requestInfo(requestId: String) = "requestInfo/$requestId"
    fun createReport(postId: String? = null): String {
        return if (postId != null) "create_report?postId=$postId" else "create_report"
    }
    fun createService(editId: String? = null): String {
        return if (editId != null) "create_service?editId=$editId" else "create_service"
    }
}






package posts

import android.widget.Toast
import screens.ButterYellow
import com.happybirthday.taacheck.R
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import screens.BottomNavBar
import screens.Routes
import java.text.SimpleDateFormat
import java.util.*
import services.models.Post

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YourPostsScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    val navBackStackEntry = navController.currentBackStackEntry
    val savedStateHandle = navBackStackEntry?.savedStateHandle

    var allPosts by remember { mutableStateOf(listOf<Post>()) }
    var searchQuery by remember { mutableStateOf("") }
    var searchActive by remember { mutableStateOf(false) }

    val filteredPosts = allPosts.filter {
        it.title.contains(searchQuery, ignoreCase = true)
    }

    LaunchedEffect(Unit) {
        savedStateHandle?.get<Map<String, Any>>("new_post")?.let { localPostMap ->
            val localPost = Post(
                id = UUID.randomUUID().toString(), // temporary local ID
                username = localPostMap["username"] as? String ?: "",
                title = localPostMap["title"] as? String ?: "",
                location = localPostMap["location"] as? String ?: "",
                timestamp = localPostMap["timestamp"] as? Long ?: System.currentTimeMillis(),
                description = localPostMap["description"] as? String ?: "",

            )
            allPosts = listOf(localPost) + allPosts
            savedStateHandle.remove<Map<String, Any>>("new_post")
        }

        scope.launch {
            val fetched = fetchUserPosts(userId)
            allPosts = fetched
        }
    }

    BackHandler(enabled = searchActive) {
        searchQuery = ""
        searchActive = false
        focusManager.clearFocus()
    }

    Scaffold(
        containerColor = ButterYellow,
        bottomBar = { BottomNavBar(
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
        ) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Your Posts", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Divider(thickness = 1.dp, color = Color.Black)

            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    searchActive = it.isNotEmpty()
                },
                label = { Text("Search by title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            val toDisplay = if (searchActive) filteredPosts else allPosts

            if (toDisplay.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 64.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_nothing),
                        contentDescription = "No posts",
                        modifier = Modifier.size(120.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (searchActive) "No matching posts found" else "You haven’t posted anything yet",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    toDisplay.forEach { post ->
                        UserPostCard(
                            post = post,
                            navController = navController
                        ) {
                            allPosts = allPosts.filterNot { it.id == post.id }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserPostCard(
    post: Post,
    navController: NavController,
    onPostDeleted: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }

    val formattedTime = remember(post.timestamp) {
        try {
            val date = Date(post.timestamp)
            SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(date)
        } catch (e: Exception) {
            "Unknown Time"
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    isDeleting = true
                    onPostDeleted()
                    scope.launch {
                        delay(800)
                        isDeleting = false
                    }
                    scope.launch {
                        deletePost(post.id)
                        Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            },
            title = { Text("Delete Post") },
            text = { Text("Are you sure you want to delete this post?") }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(12.dp)
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(16.dp)
        ) {
            Text(post.title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            Text(post.description, fontSize = 16.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(formattedTime, fontSize = 12.sp, color = Color.Gray)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = {
                        navController.navigate(Routes.createReport(post.id)) {
                            popUpTo(Routes.YOUR_POSTS) { inclusive = false }
                        }
                    }) {
                        Text("Edit")
                    }

                    if (isDeleting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = Color.Red
                        )
                    } else {
                        TextButton(onClick = { showDeleteDialog = true }) {
                            Text("Delete", color = Color.Red)
                        }
                    }
                }
            }
        }
    }
}

suspend fun fetchUserPosts(userId: String): List<Post> {
    return try {
        val snapshot = FirebaseFirestore.getInstance()
            .collection("posts")
            .whereEqualTo("userId", userId)
            .get()
            .await()

        snapshot.documents.mapNotNull { doc ->
            doc.toObject(Post::class.java)?.copy(id = doc.id)
        }
    } catch (e: Exception) {
        emptyList()
    }
}

suspend fun deletePost(postId: String) {
    FirebaseFirestore.getInstance()
        .collection("posts")
        .document(postId)
        .delete()
        .await()
}



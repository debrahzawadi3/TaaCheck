package posts

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.happybirthday.taacheck.R
import com.happybirthday.taacheck.ui.theme.ButterYellow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import screens.BottomNavBar
import java.text.SimpleDateFormat
import java.util.*
import services.models.Post

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostFeedScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val context = LocalContext.current

    var allPosts by remember { mutableStateOf<List<Post>>(emptyList()) }
    var filteredPosts by remember { mutableStateOf<List<Post>>(emptyList()) }

    var selectedCounty by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var searchTriggered by remember { mutableStateOf(false) }

    // Load posts
    LaunchedEffect(Unit) {
        allPosts = fetchPostsFromFirestore().sortedByDescending { it.likes }
    }

    // Trigger filtering
    LaunchedEffect(allPosts, selectedCounty, searchTriggered) {
        if (searchTriggered && selectedCounty.isNotBlank()) {
            isLoading = true
            filteredPosts = allPosts.filter {
                it.location.equals(selectedCounty, ignoreCase = true)
            }
            isLoading = false
        }
    }

    // Reset on back
    BackHandler(enabled = searchTriggered) {
        searchTriggered = false
        selectedCounty = ""
        focusManager.clearFocus()
    }

    Scaffold(
        containerColor = ButterYellow,
        bottomBar = {
            BottomNavBar(navController = navController, onHomeClick = {
                navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                    launchSingleTop = true
                }
            })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column {
                Text("Post Feed", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
                Divider(color = Color.Black, thickness = 1.dp)
            }

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
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                IconButton(onClick = {
                                    focusManager.clearFocus()
                                    if (selectedCounty.isNotBlank()) {
                                        searchTriggered = true
                                    }
                                }) {
                                    Icon(Icons.Default.Search, contentDescription = "Search")
                                }
                            }
                        },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
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
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                return@Column
            }

            val toDisplay = if (searchTriggered) filteredPosts else allPosts

            if (toDisplay.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 64.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_nothing),
                        contentDescription = null,
                        modifier = Modifier.size(120.dp).padding(bottom = 16.dp)
                    )
                    Text(
                        text = if (searchTriggered) "No results found" else "No posts yet",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
            }

            toDisplay.forEach { post ->
                FirebasePostItem(post = post, userId = userId, onLikeChanged = {
                    scope.launch {
                        allPosts = fetchPostsFromFirestore().sortedByDescending { it.likes }
                    }
                })
            }
        }
    }
}

@Composable
fun FirebasePostItem(
    post: Post,
    userId: String,
    onLikeChanged: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    var liked by remember { mutableStateOf(false) }
    var likeCount by remember { mutableStateOf(post.likes) }

    LaunchedEffect(post.id) {
        val doc = db.collection("posts").document(post.id)
            .collection("likes").document(userId)
            .get().await()
        liked = doc.exists()
    }

    val formattedTime = remember(post.timestamp) {
        try {
            val date = Date(post.timestamp)
            SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(date)
        } catch (_: Exception) {
            "Unknown Time"
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(12.dp)
    ) {
        Column(modifier = Modifier
            .background(Color.White)
            .padding(16.dp)) {
            Text(post.title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            Text(post.location, fontSize = 14.sp, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(8.dp))
            Text(post.description, fontSize = 14.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(
                            id = if (liked) R.drawable.ic_upvote_filled else R.drawable.ic_upvote
                        ),
                        contentDescription = "Like",
                        tint = if (liked) Color.Red else Color.Black,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                val likesRef = db.collection("posts").document(post.id).collection("likes")
                                val postRef = db.collection("posts").document(post.id)

                                if (!liked) {
                                    liked = true
                                    likeCount++
                                    likesRef.document(userId).set(mapOf("liked" to true))
                                    postRef.update("likes", FieldValue.increment(1))
                                } else {
                                    liked = false
                                    likeCount--
                                    likesRef.document(userId).delete()
                                    postRef.update("likes", FieldValue.increment(-1))
                                }

                                onLikeChanged()
                            }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("$likeCount", fontSize = 14.sp)
                }
                Text(formattedTime, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

suspend fun fetchPostsFromFirestore(): List<Post> {
    return try {
        val snapshot = FirebaseFirestore.getInstance()
            .collection("posts")
            .get()
            .await()

        snapshot.documents.mapNotNull { doc ->
            doc.toObject(Post::class.java)?.copy(id = doc.id)
        }
    } catch (_: Exception) {
        emptyList()
    }
}

val counties = listOf(
    "Baringo", "Bomet", "Bungoma", "Busia", "Elgeyo-Marakwet", "Embu", "Garissa", "Homa Bay",
    "Isiolo", "Kajiado", "Kakamega", "Kericho", "Kiambu", "Kilifi", "Kirinyaga", "Kisii", "Kisumu",
    "Kitui", "Kwale", "Laikipia", "Lamu", "Machakos", "Makueni", "Mandera", "Marsabit", "Meru",
    "Migori", "Mombasa", "Murang'a", "Nairobi", "Nakuru", "Nandi", "Narok", "Nyamira", "Nyandarua",
    "Nyeri", "Samburu", "Siaya", "Taita-Taveta", "Tana River", "Tharaka-Nithi", "Trans Nzoia",
    "Turkana", "Uasin Gishu", "Vihiga", "Wajir", "West Pokot"
)






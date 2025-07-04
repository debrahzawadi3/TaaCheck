package services.models

data class Post(
    val id: String = "",
    val username: String = "",
    val title: String = "",
    val location: String = "",
    val timestamp: Long = 0L,
    val description: String = "",
    val likes: Int = 0
)
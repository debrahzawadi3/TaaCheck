package services.models

import kotlinx.serialization.Serializable

@Serializable
data class ServiceRequest(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val locationTag: String = "",
    val timestamp: Long = 0L
)
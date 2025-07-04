package viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class NormalUserProfileUiState(
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val county: String = "",
    val profileImageUrl: String? = null,
    val isLoading: Boolean = true
)

class NormalUserProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(NormalUserProfileUiState())
    val uiState: StateFlow<NormalUserProfileUiState> = _uiState

    init {
        fetchUserData()
    }

    private fun fetchUserData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        viewModelScope.launch {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        _uiState.value = NormalUserProfileUiState(
                            fullName = doc.getString("fullName") ?: "",
                            email = doc.getString("email") ?: "",
                            phone = doc.getString("phoneNumber") ?: "",
                            county = doc.getString("county") ?: "",
                            profileImageUrl = doc.getString("profileImageUrl"),
                            isLoading = false
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(isLoading = false)
                    }
                }
                .addOnFailureListener {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
        }
    }
}

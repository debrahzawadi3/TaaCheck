package screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem(Routes.HOME, "Home", Icons.Default.Home)
    object Service : BottomNavItem(Routes.CREATE_SERVICE, "Service", Icons.Default.Build)
    object Report : BottomNavItem(Routes.CREATE_REPORT, "Report", Icons.Default.Info)
    object Profile : BottomNavItem(Routes.PROFILE, "Profile", Icons.Default.Person)
}
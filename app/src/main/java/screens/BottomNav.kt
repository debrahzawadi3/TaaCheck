package screens

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.happybirthday.taacheck.ui.theme.BluePrimary
import com.happybirthday.taacheck.ui.theme.ButterYellow
import androidx.navigation.NavDestination.Companion.hierarchy

@Composable
fun BottomNavBar(
    navController: NavController,
    onHomeClick: (() -> Unit)? = null,
    onServiceClick: (() -> Unit)? = null,
    onReportClick: (() -> Unit)? = null,
    onProfileClick: (() -> Unit)? = null
) {
    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentDestination = navBackStackEntry?.destination

    val navItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Service,
        BottomNavItem.Report,
        BottomNavItem.Profile
    )

    NavigationBar(containerColor = ButterYellow, tonalElevation = 4.dp) {
        navItems.forEach { item ->
            val isSelected = currentDestination?.hierarchy?.any {
                it.route?.startsWith(item.route) == true
            } == true

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    when (item) {
                        is BottomNavItem.Home -> {
                            onHomeClick?.invoke() ?: run {
                                if (!isSelected) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        }

                        is BottomNavItem.Service -> {
                            onServiceClick?.invoke() ?: run {
                                if (!isSelected) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        }

                        is BottomNavItem.Report -> {
                            onReportClick?.invoke() ?: run {
                                if (!isSelected) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        }

                        is BottomNavItem.Profile -> {
                            onProfileClick?.invoke() ?: run {
                                if (!isSelected) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        }
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = BluePrimary,
                    unselectedIconColor = Color.Black,
                    indicatorColor = ButterYellow
                )
            )
        }
    }
}
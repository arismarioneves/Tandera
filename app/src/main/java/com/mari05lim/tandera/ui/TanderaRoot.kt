package com.mari05lim.tandera.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.TaskAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mari05lim.tandera.R
import com.mari05lim.tandera.data.TitleSummary
import com.mari05lim.tandera.ui.catalog.CatalogScreen
import com.mari05lim.tandera.ui.details.DetailsScreen
import com.mari05lim.tandera.ui.home.HomeScreen
import com.mari05lim.tandera.ui.search.SearchScreen
import com.mari05lim.tandera.ui.settings.SettingsScreen
import com.mari05lim.tandera.ui.watched.WatchedScreen
import kotlin.reflect.KClass

private enum class Tab(val route: Any, val routeClass: KClass<*>, @StringRes val label: Int, val icon: ImageVector) {
    Home(HomeRoute, HomeRoute::class, R.string.tab_home, Icons.Rounded.Home),
    Search(SearchRoute, SearchRoute::class, R.string.tab_search, Icons.Rounded.Search),
    Watched(WatchedRoute, WatchedRoute::class, R.string.tab_watched, Icons.Rounded.TaskAlt),
    Settings(SettingsRoute, SettingsRoute::class, R.string.tab_settings, Icons.Rounded.Settings),
}

@Composable
fun TanderaRoot() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val destination = backStackEntry?.destination
    val currentTab = Tab.entries.firstOrNull { tab -> destination?.hasRoute(tab.routeClass) == true }

    val openTitle: (TitleSummary) -> Unit = { navController.navigate(DetailsRoute(it.type, it.id)) }

    Scaffold(
        // Cada tela cuida da barra de status; aqui só a barra de navegação inferior.
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            if (currentTab != null) {
                NavigationBar {
                    Tab.entries.forEach { tab ->
                        NavigationBarItem(
                            selected = tab == currentTab,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, null) },
                            label = { Text(stringResource(tab.label)) },
                        )
                    }
                }
            }
        },
    ) { padding ->
        NavHost(
            navController,
            startDestination = HomeRoute,
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding),
        ) {
            composable<HomeRoute> {
                HomeScreen(onTitleClick = openTitle, onSeeAll = { catalog, type -> navController.navigate(CatalogRoute(catalog, type)) })
            }
            composable<SearchRoute> { SearchScreen(onTitleClick = openTitle) }
            composable<WatchedRoute> { WatchedScreen(onTitleClick = openTitle) }
            composable<SettingsRoute> { SettingsScreen() }
            composable<CatalogRoute> { CatalogScreen(onBack = navController::popBackStack, onTitleClick = openTitle) }
            composable<DetailsRoute> { DetailsScreen(onBack = navController::popBackStack) }
        }
    }
}

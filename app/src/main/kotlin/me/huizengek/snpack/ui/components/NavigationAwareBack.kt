package me.huizengek.snpack.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.ramcosta.composedestinations.utils.currentDestinationAsState
import me.huizengek.snpack.LocalNavigator
import me.huizengek.snpack.NavGraph
import me.huizengek.snpack.NavGraphs

@Composable
fun NavigationAwareBack(
    modifier: Modifier = Modifier,
    navigator: NavController = LocalNavigator.current,
    navGraph: NavGraph = NavGraphs.root,
    onClick: (next: () -> Unit) -> Unit = { it() }
) {
    val destination by navigator.currentDestinationAsState()
    val show = remember { destination?.route != navGraph.startRoute.route }

    if (show) IconButton(
        onClick = { onClick { navigator.popBackStack() } },
        modifier = modifier
    ) {
        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
    }
}

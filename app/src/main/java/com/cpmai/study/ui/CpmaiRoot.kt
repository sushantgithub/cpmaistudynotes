package com.cpmai.study.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material.icons.outlined.Style
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cpmai.study.data.ContentRepository
import com.cpmai.study.data.ProgressStore
import com.cpmai.study.ui.screens.DisclaimerGate
import com.cpmai.study.ui.screens.ExamScreen
import com.cpmai.study.ui.screens.FlashcardScreen
import com.cpmai.study.ui.screens.GlossaryScreen
import com.cpmai.study.ui.screens.HomeScreen
import com.cpmai.study.ui.screens.LearnScreen
import com.cpmai.study.ui.screens.LegalScreen
import com.cpmai.study.ui.screens.PatternLabScreen
import com.cpmai.study.ui.screens.PhaseMapScreen
import com.cpmai.study.ui.screens.PracticeHubScreen
import com.cpmai.study.ui.screens.ProgressScreen
import com.cpmai.study.ui.screens.QuizScreen
import com.cpmai.study.ui.screens.SearchScreen
import com.cpmai.study.ui.screens.TopicDetailScreen
import com.cpmai.study.ui.screens.UnlockScreen

private data class Tab(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

@Composable
fun CpmaiRoot(repo: ContentRepository, store: ProgressStore) {
    val nav = rememberNavController()
    val progress by store.progress.collectAsState(initial = com.cpmai.study.data.UserProgress())
    if (!progress.disclaimerAccepted) {
        DisclaimerGate(onAccept = { store.acceptDisclaimer() })
        return
    }
    val tabs = listOf(
        Tab("home", "Home", Icons.Outlined.Home),
        Tab("learn", "Learn", Icons.Outlined.MenuBook),
        Tab("cards", "Cards", Icons.Outlined.Style),
        Tab("practice", "Practice", Icons.Outlined.Quiz),
        Tab("lab", "Patterns", Icons.Outlined.AutoAwesome)
    )
    val back by nav.currentBackStackEntryAsState()
    val route = back?.destination?.route ?: "home"
    val hideBar = route.startsWith("quiz") || route.startsWith("exam") || route.startsWith("topic/") ||
        route.startsWith("notes") || route == "search" || route == "glossary" || route == "phases" ||
        route == "progress" || route == "legal" || route == "unlock"

    Scaffold(
        bottomBar = {
            if (!hideBar) {
                NavigationBar {
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = route == tab.route || (tab.route == "learn" && route.startsWith("topic")),
                            onClick = {
                                nav.navigate(tab.route) {
                                    popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(nav, startDestination = "home", modifier = Modifier.padding(padding)) {
            composable("home") {
                HomeScreen(repo, progress, nav)
            }
            composable("learn") {
                LearnScreen(
                    repo, progress,
                    onOpen = { nav.navigate("topic/$it") },
                    onUnlock = { nav.navigate("unlock") }
                )
            }
            composable("cards") {
                FlashcardScreen(repo, store, progress, topicId = null, onBack = null)
            }
            composable(
                "cards/{topicId}",
                arguments = listOf(navArgument("topicId") { type = NavType.StringType })
            ) { entry ->
                FlashcardScreen(
                    repo, store, progress,
                    topicId = entry.arguments?.getString("topicId"),
                    onBack = { nav.popBackStack() }
                )
            }
            composable("practice") { PracticeHubScreen(repo, progress, nav) }
            composable("lab") { PatternLabScreen(repo, store, onUnlock = { nav.navigate("unlock") }) }
            composable("progress") { ProgressScreen(repo, progress, onBack = { nav.popBackStack() }) }
            composable("search") { SearchScreen(repo, onBack = { nav.popBackStack() }, onTopic = {
                if (com.cpmai.study.data.Entitlement.topicAllowed(it, progress.fullUnlocked)) nav.navigate("topic/$it")
                else nav.navigate("unlock")
            }) }
            composable("glossary") { GlossaryScreen(repo, onBack = { nav.popBackStack() }) }
            composable("phases") { PhaseMapScreen(repo, store, onBack = { nav.popBackStack() }, onUnlock = { nav.navigate("unlock") }) }
            composable("unlock") { UnlockScreen(store, progress, onBack = { nav.popBackStack() }) }
            composable("legal") { LegalScreen(onBack = { nav.popBackStack() }) }
            composable(
                "topic/{id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { entry ->
                val id = entry.arguments?.getString("id") ?: return@composable
                TopicDetailScreen(
                    repo, progress, id,
                    onBack = { nav.popBackStack() },
                    onQuiz = { nav.navigate("quiz/$it") },
                    onCards = { nav.navigate("cards/$it") }
                )
            }
            composable(
                "quiz/{topicId}",
                arguments = listOf(navArgument("topicId") { type = NavType.StringType })
            ) { entry ->
                QuizScreen(
                    repo, store,
                    topicId = entry.arguments?.getString("topicId"),
                    examMode = false,
                    onDone = { nav.popBackStack() }
                )
            }
            composable("exam") {
                ExamScreen(repo, store, onDone = { nav.popBackStack() })
            }
        }
    }
}

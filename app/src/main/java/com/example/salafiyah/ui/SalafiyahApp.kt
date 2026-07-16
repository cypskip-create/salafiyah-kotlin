package com.example.salafiyah.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.salafiyah.data.repository.AppRepository
import com.example.salafiyah.model.Book
import com.example.salafiyah.model.Surah
import com.example.salafiyah.ui.screens.CourseScreen
import com.example.salafiyah.ui.screens.DownloadsScreen
import com.example.salafiyah.ui.screens.HomeScreen
import com.example.salafiyah.ui.screens.LibraryScreen
import com.example.salafiyah.ui.screens.MajlisRoomScreen
import com.example.salafiyah.ui.screens.NoteEditorScreen
import com.example.salafiyah.ui.screens.NotesScreen
import com.example.salafiyah.ui.screens.PdfReaderScreen
import com.example.salafiyah.ui.screens.QuranScreen
import com.example.salafiyah.ui.screens.SplashScreen
import com.example.salafiyah.ui.screens.SurahReaderScreen
import com.example.salafiyah.ui.theme.AppColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

sealed interface Route {
    data object Splash : Route
    data object Shell : Route
    data object Notes : Route
    data object AddNote : Route
    data object Library : Route
    data object Downloads : Route
    data object Courses : Route
    data class SurahReader(val surah: Surah) : Route
    data class PdfReader(val book: Book) : Route
}

private data class TabItem(val label: String, val icon: ImageVector)

private val tabs = listOf(
    TabItem("Home", Icons.Default.Home),
    TabItem("Quran", Icons.Default.MenuBook),
    TabItem("Majlis", Icons.Default.VideoCall),
)

@Composable
fun SalafiyahApp() {
    val context = LocalContext.current
    val repository = remember { AppRepository(context) }
    val backStack = remember { mutableStateListOf<Route>(Route.Splash) }

    fun navigate(route: Route) { backStack.add(route) }
    fun replace(route: Route) { backStack.clear(); backStack.add(route) }
    fun back() { if (backStack.size > 1) backStack.removeAt(backStack.lastIndex) }

    when (val route = backStack.last()) {
        Route.Splash -> {
            LaunchedEffect(Unit) {
                delay(800)
                replace(Route.Shell)
            }
            SplashScreen()
        }
        Route.Shell -> HomeShell(repository, ::navigate)
        Route.Notes -> ScreenFrame("Notes", ::back) { NotesScreen(repository, ::navigate) }
        Route.AddNote -> ScreenFrame("Write Note", ::back) { NoteEditorScreen(repository, ::back) }
        Route.Library -> ScreenFrame("Library", ::back) { LibraryScreen(repository, ::navigate) }
        Route.Downloads -> ScreenFrame("Downloads", ::back) { DownloadsScreen(repository) }
        Route.Courses -> ScreenFrame("Courses", ::back) { CourseScreen() }
        is Route.SurahReader -> ScreenFrame(route.surah.englishName, ::back) { SurahReaderScreen(repository, route.surah) }
        is Route.PdfReader -> ScreenFrame(route.book.title, ::back) { PdfReaderScreen(route.book) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeShell(repository: AppRepository, onNavigate: (Route) -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(drawerContainerColor = AppColors.Cream) {
                DrawerHeader()
                DrawerItem("Notes", Icons.Default.EditNote) { onNavigate(Route.Notes) }
                DrawerItem("Library", Icons.Default.Book) { onNavigate(Route.Library) }
                DrawerItem("Downloads", Icons.Default.Download) { onNavigate(Route.Downloads) }
                DrawerItem("Courses", Icons.Default.School) { onNavigate(Route.Courses) }
            }
        },
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Salafiyah", color = AppColors.Gold, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = AppColors.GreenDark),
                )
            },
            bottomBar = {
                NavigationBar(containerColor = AppColors.GreenDark) {
                    tabs.forEachIndexed { index, tab ->
                        val selected = selectedTab == index
                        NavigationBarItem(
                            selected = selected,
                            onClick = { selectedTab = index },
                            icon = { Icon(tab.icon, tab.label, tint = if (selected) AppColors.Gold else Color.White.copy(alpha = 0.72f)) },
                            label = { Text(tab.label, color = if (selected) AppColors.Gold else Color.White.copy(alpha = 0.72f), fontSize = 11.sp) },
                        )
                    }
                }
            },
            containerColor = AppColors.Cream,
        ) { padding ->
            Box(Modifier.padding(padding)) {
                when (selectedTab) {
                    0 -> HomeScreen(onNavigate)
                    1 -> QuranScreen(repository, onNavigate)
                    2 -> MajlisRoomScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScreenFrame(title: String, onBack: () -> Unit, content: @Composable () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, color = AppColors.Gold, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppColors.GreenDark),
            )
        },
        containerColor = AppColors.Cream,
    ) { padding -> Box(Modifier.padding(padding)) { content() } }
}

@Composable
private fun DrawerHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColors.GreenDark)
            .padding(20.dp),
    ) {
        Text("Salafiyah", color = AppColors.Gold, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text("Quran, notes, library, and study tools", color = Color.White.copy(alpha = 0.75f))
    }
}

@Composable
private fun DrawerItem(title: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(AppColors.GreenMid.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = AppColors.GreenDark, modifier = Modifier.size(19.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(title, color = AppColors.TextDark, fontWeight = FontWeight.SemiBold)
    }
    Divider(color = Color.Black.copy(alpha = 0.05f))
}
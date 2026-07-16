package com.example.salafiyah.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.salafiyah.data.repository.AppRepository
import com.example.salafiyah.model.Book
import com.example.salafiyah.model.Surah
import com.example.salafiyah.ui.Route
import com.example.salafiyah.ui.theme.AppColors
import kotlinx.coroutines.launch

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(AppColors.GreenDeep, AppColors.GreenDark))),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("سلفية", color = AppColors.GoldLight, fontSize = 44.sp, fontWeight = FontWeight.Bold)
            Text("Salafiyah", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Quran and Islamic study", color = Color.White.copy(alpha = 0.72f))
        }
    }
}

@Composable
fun HomeScreen(onNavigate: (Route) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { WelcomeBanner() }
        item { AyahCard() }
        item { SectionHeader("Start here", "The main app stays simple: Quran, Majlis, notes, and your library.") }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ActionCard("Notes", Icons.Default.EditNote, Modifier.weight(1f)) { onNavigate(Route.Notes) }
                ActionCard("Library", Icons.Default.Book, Modifier.weight(1f)) { onNavigate(Route.Library) }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ActionCard("Downloads", Icons.Default.Download, Modifier.weight(1f)) { onNavigate(Route.Downloads) }
                ActionCard("Courses", Icons.Default.School, Modifier.weight(1f)) { onNavigate(Route.Courses) }
            }
        }
    }
}

@Composable
fun QuranScreen(repository: AppRepository, onNavigate: (Route) -> Unit) {
    var surahs by remember { mutableStateOf<List<Surah>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        runCatching { repository.loadSurahs() }
            .onSuccess { surahs = it; error = null }
            .onFailure { error = "Could not load Quran. Check internet and try again." }
        loading = false
    }

    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { SectionHeader("Quran", "Live Surah list loaded from api.alquran.cloud.") }
        if (loading) item { Text("Loading Quran...", color = AppColors.TextLight) }
        error?.let { item { Text(it, color = Color(0xFFB3261E)) } }
        items(surahs) { surah ->
            AppCard(onClick = { onNavigate(Route.SurahReader(surah)) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(surah.number.toString(), color = AppColors.Gold, fontWeight = FontWeight.Bold, modifier = Modifier.width(34.dp))
                    Column(Modifier.weight(1f)) {
                        Text(surah.englishName, color = AppColors.TextDark, fontWeight = FontWeight.Bold)
                        Text("${surah.englishNameTranslation} • ${surah.numberOfAyahs} ayat • ${surah.revelationType}", color = AppColors.TextLight, fontSize = 13.sp)
                    }
                    Text(surah.arabicName, color = AppColors.GreenDark, fontSize = 20.sp)
                }
            }
        }
    }
}

@Composable
fun SurahReaderScreen(repository: AppRepository, surah: Surah) {
    var ayahs by remember { mutableStateOf(emptyList<com.example.salafiyah.model.Ayah>()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(surah.number) {
        runCatching { repository.loadAyahs(surah.number) }
            .onSuccess { ayahs = it; error = null }
            .onFailure { error = "Could not load this Surah." }
        loading = false
    }

    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { SectionHeader(surah.englishName, "${surah.arabicName} • ${surah.numberOfAyahs} ayat") }
        if (loading) item { Text("Loading ayahs...", color = AppColors.TextLight) }
        error?.let { item { Text(it, color = Color(0xFFB3261E)) } }
        items(ayahs) { ayah ->
            AppCard {
                Text(ayah.arabic, modifier = Modifier.fillMaxWidth(), color = AppColors.GreenDark, fontSize = 27.sp, textAlign = TextAlign.End)
                Spacer(Modifier.height(10.dp))
                Text("${ayah.numberInSurah}. ${ayah.translation}", color = AppColors.TextMid)
            }
        }
    }
}

@Composable
fun NotesScreen(repository: AppRepository, onNavigate: (Route) -> Unit) {
    val notes by repository.observeNotes().collectAsState(initial = emptyList())
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SectionHeader("Notes", "Saved locally with Room.", Modifier.weight(1f))
                IconButton(onClick = { onNavigate(Route.AddNote) }) {
                    Icon(Icons.Default.Add, "Add note", tint = AppColors.GreenDark)
                }
            }
        }
        if (notes.isEmpty()) item { Text("No notes yet. Tap + to write one.", color = AppColors.TextLight) }
        items(notes) { note ->
            AppCard {
                Text(note.title, color = AppColors.TextDark, fontWeight = FontWeight.Bold)
                Text(note.subject, color = AppColors.GreenMid, fontSize = 13.sp)
                Spacer(Modifier.height(6.dp))
                Text(note.content, color = AppColors.TextLight, maxLines = 3, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
fun NoteEditorScreen(repository: AppRepository, onDone: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    FormScreen {
        OutlinedTextField(title, { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(subject, { subject = it }, label = { Text("Subject") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(content, { content = it }, label = { Text("Note") }, minLines = 7, modifier = Modifier.fillMaxWidth())
        Button(
            onClick = { scope.launch { repository.saveNote(title, subject, content); onDone() } },
            enabled = content.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        ) { Text("Save note") }
    }
}

@Composable
fun LibraryScreen(repository: AppRepository, onNavigate: (Route) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val books by repository.observeBooks().collectAsState(initial = emptyList())

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val title = uri.lastPathSegment?.substringAfterLast('/') ?: "Uploaded book"
            scope.launch { repository.saveBook(title, uri) }
        }
    }

    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SectionHeader("Library", "Upload PDF books from phone storage.", Modifier.weight(1f))
                IconButton(onClick = { picker.launch(arrayOf("application/pdf")) }) {
                    Icon(Icons.Default.Add, "Upload book", tint = AppColors.GreenDark)
                }
            }
        }
        if (books.isEmpty()) item { Text("No books uploaded yet.", color = AppColors.TextLight) }
        items(books) { book ->
            AppCard(onClick = { onNavigate(Route.PdfReader(book)) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Book, null, tint = AppColors.GreenMid)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(book.title, color = AppColors.TextDark, fontWeight = FontWeight.Bold)
                        Text(book.category, color = AppColors.TextLight, fontSize = 13.sp)
                    }
                    Icon(Icons.Default.OpenInNew, null, tint = AppColors.Gold)
                }
            }
        }
    }
}

@Composable
fun PdfReaderScreen(book: Book) {
    val context = LocalContext.current
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader(book.title, "Stored URI: ${book.uri}")
        AppCard {
            Text("The book was saved from local storage.", color = AppColors.TextMid)
            Spacer(Modifier.height(10.dp))
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(Uri.parse(book.uri), "application/pdf")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(intent, "Open PDF"))
                },
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Open PDF") }
        }
    }
}

@Composable
fun DownloadsScreen(repository: AppRepository) {
    val downloads by repository.observeDownloads().collectAsState(initial = emptyList())
    var title by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { SectionHeader("Downloads", "Uses Android DownloadManager.") }
        item {
            AppCard {
                OutlinedTextField(title, { title = it }, label = { Text("Book title") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(url, { url = it }, label = { Text("PDF URL") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
                Button(
                    onClick = { scope.launch { repository.downloadBook(title, url); title = ""; url = "" } },
                    enabled = url.startsWith("http"),
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("Download") }
            }
        }
        items(downloads) { item ->
            AppCard {
                Text(item.title, color = AppColors.TextDark, fontWeight = FontWeight.Bold)
                Text(item.url, color = AppColors.TextLight, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("Download id: ${item.downloadId}", color = AppColors.GreenMid, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun MajlisRoomScreen() {
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader("Majlis Room", "Simple study room screen ready for live tools.")
        AppCard {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(AppColors.GreenDeep),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.VideoCall, null, tint = AppColors.Gold, modifier = Modifier.size(72.dp))
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Button(onClick = {}, modifier = Modifier.weight(1f)) { Text("Join") }
                OutlinedButton(onClick = {}, modifier = Modifier.weight(1f)) { Text("Prepare") }
            }
        }
    }
}

@Composable
fun CourseScreen() {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { SectionHeader("Courses", "Small, readable course cards.") }
        items(sampleCourses) { course ->
            AppCard {
                Text(course.title, color = AppColors.GreenDark, fontWeight = FontWeight.Bold)
                Text(course.instructor, color = AppColors.TextLight, fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))
                Text(course.description, color = AppColors.TextMid)
            }
        }
    }
}

private val sampleCourses = listOf(
    com.example.salafiyah.model.Course("aqeedah", "Foundations of Aqeedah", "Tawheed, worship, sincerity, and the foundations of belief.", "Salafiyah Learning"),
    com.example.salafiyah.model.Course("quran", "How to Study the Quran", "A clean method for recitation, tafsir, reflection, and review.", "Salafiyah Learning"),
    com.example.salafiyah.model.Course("hadith", "Nawawi Forty Essentials", "Selected hadith lessons with practical benefits.", "Salafiyah Learning"),
)

@Composable
private fun WelcomeBanner() {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.horizontalGradient(listOf(AppColors.GreenDark, AppColors.GreenMid)))
            .padding(20.dp),
    ) {
        Text("السلام عليكم", color = AppColors.GoldLight, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        Text("Welcome back to your study space.", color = Color.White.copy(alpha = 0.75f))
    }
}

@Composable
private fun AyahCard() {
    AppCard {
        Text("وَقُل رَّبِّ زِدْنِي عِلْمًا", color = AppColors.GreenDark, fontSize = 28.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        Text("\"My Lord, increase me in knowledge.\"", color = AppColors.TextMid, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun ActionCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.height(90.dp).clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = AppColors.GreenMid),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = Color.White)
            Spacer(Modifier.height(6.dp))
            Text(title, color = Color.White)
        }
    }
}

@Composable
private fun FormScreen(content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp), content = content)
}

@Composable
private fun SectionHeader(title: String, subtitle: String, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(title, color = AppColors.GreenDark, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text(subtitle, color = AppColors.TextLight)
    }
}

@Composable
private fun AppCard(onClick: (() -> Unit)? = null, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick == null) Modifier else Modifier.clickable(onClick = onClick)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Column(Modifier.padding(16.dp), content = content)
    }
}
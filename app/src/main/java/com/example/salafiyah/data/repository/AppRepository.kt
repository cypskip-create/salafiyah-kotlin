package com.example.salafiyah.data.repository

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import com.example.salafiyah.data.local.AppDatabase
import com.example.salafiyah.data.local.BookEntity
import com.example.salafiyah.data.local.DownloadEntity
import com.example.salafiyah.data.local.NoteEntity
import com.example.salafiyah.data.remote.QuranApiService
import com.example.salafiyah.model.Ayah
import com.example.salafiyah.model.Book
import com.example.salafiyah.model.DownloadItem
import com.example.salafiyah.model.Note
import com.example.salafiyah.model.Surah
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppRepository(context: Context) {
    private val appContext = context.applicationContext
    private val db = AppDatabase.get(appContext)

    private val quranApi = Retrofit.Builder()
        .baseUrl("https://api.alquran.cloud/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(QuranApiService::class.java)

    fun observeNotes(): Flow<List<Note>> {
        return db.noteDao().observeNotes().map { notes ->
            notes.map { Note(it.id, it.title, it.subject, it.content, it.createdAt) }
        }
    }

    suspend fun saveNote(title: String, subject: String, content: String) {
        db.noteDao().insert(
            NoteEntity(
                title = title.trim().ifBlank { "Untitled note" },
                subject = subject.trim().ifBlank { "General" },
                content = content.trim(),
                createdAt = System.currentTimeMillis(),
            )
        )
    }

    fun observeBooks(): Flow<List<Book>> {
        return db.bookDao().observeBooks().map { books ->
            books.map { Book(it.id, it.title, it.uri, it.category, it.addedAt) }
        }
    }

    suspend fun saveBook(title: String, uri: Uri, category: String = "Library") {
        db.bookDao().insert(
            BookEntity(
                title = title.ifBlank { "Uploaded book" },
                uri = uri.toString(),
                category = category,
                addedAt = System.currentTimeMillis(),
            )
        )
    }

    fun observeDownloads(): Flow<List<DownloadItem>> {
        return db.downloadDao().observeDownloads().map { downloads ->
            downloads.map { DownloadItem(it.id, it.title, it.url, it.downloadId, it.createdAt) }
        }
    }

    suspend fun downloadBook(title: String, url: String) {
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle(title.ifBlank { "Salafiyah download" })
            .setDescription("Downloading Islamic study material")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "${title.ifBlank { "salafiyah-book" }.replace(" ", "_")}.pdf",
            )

        val manager = appContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val id = manager.enqueue(request)

        db.downloadDao().insert(
            DownloadEntity(
                title = title.ifBlank { "Salafiyah download" },
                url = url,
                downloadId = id,
                createdAt = System.currentTimeMillis(),
            )
        )
    }

    suspend fun loadSurahs(): List<Surah> {
        return quranApi.getSurahs().data.map {
            Surah(
                number = it.number,
                englishName = it.englishName,
                englishNameTranslation = it.englishNameTranslation,
                arabicName = it.name,
                numberOfAyahs = it.numberOfAyahs,
                revelationType = it.revelationType,
            )
        }
    }

    suspend fun loadAyahs(surahNumber: Int): List<Ayah> {
        val arabic = quranApi.getArabicSurah(surahNumber).data.ayahs
        val english = quranApi.getEnglishSurah(surahNumber).data.ayahs
        return arabic.mapIndexed { index, ayah ->
            Ayah(
                numberInSurah = ayah.numberInSurah,
                arabic = ayah.text,
                translation = english.getOrNull(index)?.text.orEmpty(),
            )
        }
    }
}
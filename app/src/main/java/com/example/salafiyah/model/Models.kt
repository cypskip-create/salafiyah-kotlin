package com.example.salafiyah.model

data class Surah(
    val number: Int,
    val englishName: String,
    val englishNameTranslation: String,
    val arabicName: String,
    val numberOfAyahs: Int,
    val revelationType: String,
)

data class Ayah(
    val numberInSurah: Int,
    val arabic: String,
    val translation: String,
)

data class Note(
    val id: Long,
    val title: String,
    val subject: String,
    val content: String,
    val createdAt: Long,
)

data class Book(
    val id: Long,
    val title: String,
    val uri: String,
    val category: String,
    val addedAt: Long,
)

data class DownloadItem(
    val id: Long,
    val title: String,
    val url: String,
    val downloadId: Long,
    val createdAt: Long,
)

data class Course(
    val id: String,
    val title: String,
    val description: String,
    val instructor: String,
)
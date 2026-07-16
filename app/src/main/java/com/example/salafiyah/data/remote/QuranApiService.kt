package com.example.salafiyah.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

interface QuranApiService {
    @GET("surah")
    suspend fun getSurahs(): SurahListResponse

    @GET("surah/{number}/quran-uthmani")
    suspend fun getArabicSurah(@Path("number") number: Int): SurahAyahResponse

    @GET("surah/{number}/en.asad")
    suspend fun getEnglishSurah(@Path("number") number: Int): SurahAyahResponse
}

data class SurahListResponse(val data: List<SurahDto>)

data class SurahDto(
    val number: Int,
    val englishName: String,
    val englishNameTranslation: String,
    val name: String,
    val numberOfAyahs: Int,
    val revelationType: String,
)

data class SurahAyahResponse(val data: SurahDataDto)

data class SurahDataDto(val ayahs: List<AyahDto>)

data class AyahDto(
    val numberInSurah: Int,
    val text: String,
)
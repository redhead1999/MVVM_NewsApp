package com.androiddevs.mvvmnewsapp.repository.service

import com.androiddevs.mvvmnewsapp.model.NewsResponse
import com.androiddevs.mvvmnewsapp.utils.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {
    @GET("v2/top-headlines")
    suspend fun getBreakingNews(
        @Query("country") country: String = "ru",
        @Query("page") pageNumber: Int,
        @Query("apiKey") apiKey: String = Constants.API_KEY
    ): Response<NewsResponse>
    @GET("v2/everything")
    suspend fun getSearchNews(
        @Query("q") searchQuery: String,
        @Query("page") pageNumber: Int,
        @Query("apiKey") apiKey: String = Constants.API_KEY
    ): Response<NewsResponse>
}
package com.androiddevs.mvvmnewsapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import com.androiddevs.mvvmnewsapp.model.Article
import com.androiddevs.mvvmnewsapp.model.NewsResponse
import com.androiddevs.mvvmnewsapp.repository.NewsRepository
import com.androiddevs.mvvmnewsapp.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(
    val newsrepository: NewsRepository
) : ViewModel() {
    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var brekingPageNumebr = 1
    var breakingNewsResponse: NewsResponse? = null
    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchPageNumber = 1
    var searchNewsResponse: NewsResponse? = null
    lateinit var articles: LiveData<PagedList<Article>>
    init {
        getBreakingNews("ru")
    }
    private fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        breakingNews.postValue(Resource.Loading())
        val response = newsrepository.getBreakingNews(countryCode, brekingPageNumebr)
        breakingNews.postValue(handleBreakingNewsResponse(response))
    }
    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                brekingPageNumebr++
                if (breakingNewsResponse == null) {
                    breakingNewsResponse = resultResponse
                } else {
                    val oldArticles = breakingNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(breakingNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
    fun getSearchedNews(queryString: String) = viewModelScope.launch {
        searchNews.postValue(Resource.Loading())
        val searchNewsResponse = newsrepository.getSearchNews(queryString, searchPageNumber)
        searchNews.postValue(handleSearchNewsResponse(searchNewsResponse))
    }
    private fun handleSearchNewsResponse(respons: Response<NewsResponse>): Resource<NewsResponse>? {
        if (respons.isSuccessful) {
            respons.body()?.let { resultResponse ->
                searchPageNumber++
                if (searchNewsResponse == null){
                    searchNewsResponse = resultResponse
                }else{
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(respons.message())
    }
    fun insertArticle(article: Article) = viewModelScope.launch {
        newsrepository.upsert(article)
    }
    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsrepository.delete(article)
    }
    fun getSavedArticles() = newsrepository.getAllArticles()
    fun getBreakingNews() : LiveData<PagedList<Article>>{
        return articles
    }
}

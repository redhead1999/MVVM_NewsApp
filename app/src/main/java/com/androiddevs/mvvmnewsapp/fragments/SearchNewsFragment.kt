package com.androiddevs.mvvmnewsapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevs.mvvmnewsapp.MainActivity
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.adapters.ArticleAdapter
import com.androiddevs.mvvmnewsapp.utils.Constants
import com.androiddevs.mvvmnewsapp.utils.Resource
import com.androiddevs.mvvmnewsapp.utils.shareNews
import com.androiddevs.mvvmnewsapp.viewModel.NewsViewModel

import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_search_news.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class SearchNewsFragment : Fragment(R.layout.fragment_search_news) {
    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: ArticleAdapter
    val TAG = "SearchNewsFragment"
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
        setupRecyclerView()
        newsAdapter.setOnItemCLickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_breakingNewsFragment_to_articleFragment,
                bundle
            )
        }
        newsAdapter.onSaveClickListener {
            viewModel.insertArticle(it)
            Snackbar.make(requireView(),"Saved", Snackbar.LENGTH_SHORT).show()
        }
        newsAdapter.onDeleteClickListener {
            viewModel.deleteArticle(it)
            Snackbar.make(requireView(),"Removed", Snackbar.LENGTH_SHORT).show()
        }
        newsAdapter.onShareNewsClick {
            shareNews(context, it)
        }
        var searchJob : Job? = null
        etSearch.addTextChangedListener{ editable ->
            searchJob?.cancel()
            searchJob = MainScope().launch {
                delay(Constants.SEARCH_TIME_DELAY)
                editable?.let{
                    if (!editable.toString().trim().isEmpty()){
                        viewModel.getSearchedNews(editable.toString())
                    }
                }
            }
        }
        viewModel.searchNews.observe(viewLifecycleOwner, Observer { newsResponse ->
            when(newsResponse){
                is Resource.Success -> {
                    shimmerFrame3.stopShimmerAnimation()
                    shimmerFrame3.visibility = View.GONE
                    newsResponse.data?.let { news->
                        newsAdapter.differ.submitList(news.articles)
                    }
                }
                is Resource.Error -> {
                    shimmerFrame3.stopShimmerAnimation()
                    shimmerFrame3.visibility = View.GONE
                    newsResponse.message?.let { message ->
                        Log.e(TAG,"Error :: $message")
                    }
                }
                is Resource.Loading -> {
                    shimmerFrame3.startShimmerAnimation()
                    shimmerFrame3.visibility = View.VISIBLE
                }
            }
        })
    }
    private fun setupRecyclerView() {
        newsAdapter = ArticleAdapter()
        rvSearchNews.apply {
            adapter  = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}
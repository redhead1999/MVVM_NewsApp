package com.androiddevs.mvvmnewsapp.adapters

import android.view.LayoutInflater
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.databinding.ItemSavedNewsBinding
import com.androiddevs.mvvmnewsapp.model.Article
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView


class SavedNewsAdapter : RecyclerView.Adapter<SavedNewsAdapter.SavedNewsViewHolder>() {

    private val diffUtilCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }
        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.id == newItem.id
        }
    }
    inner class SavedNewsViewHolder(var view: ItemSavedNewsBinding) :
        RecyclerView.ViewHolder(view.root)
    val differ = AsyncListDiffer(this, diffUtilCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedNewsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = DataBindingUtil.inflate<ItemSavedNewsBinding>(
            inflater,
            R.layout.item_saved_news,
            parent,
            false
        )
        return SavedNewsViewHolder(view)
    }
    override fun getItemCount() = differ.currentList.size
    override fun onBindViewHolder(holder: SavedNewsViewHolder, position: Int) {
        val article = differ.currentList[position]
        holder.view.article = article
        // Item CLick Listener
        //Bind these click listeners later
        holder.itemView.setOnClickListener {
            onItemClickListener?.let {
                article.let { article ->
                    it(article)
                }
            }
        }
        holder.view.ivShare.setOnClickListener {
            onShareNewsClick?.let {
                article?.let { it1 -> it(it1) }
            }
        }
    }
    private var onItemClickListener: ((Article) -> Unit)? = null
    private var onShareNewsClick: ((Article) -> Unit)? = null
    fun setOnItemCLickListener(listener: ((Article) -> Unit)) {
        onItemClickListener = listener
    }
    fun onShareNewsClick(listener: (Article) -> Unit) {
        onShareNewsClick = listener
    }
}

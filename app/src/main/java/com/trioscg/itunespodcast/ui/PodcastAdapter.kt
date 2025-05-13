package com.trioscg.itunespodcast.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.trioscg.itunespodcast.R
import com.trioscg.itunespodcast.data.Podcast

class PodcastAdapter(
    private var items: List<Podcast>,
    private val onItemClick: (Podcast) -> Unit
) : RecyclerView.Adapter<PodcastAdapter.PodcastViewHolder>() {

    fun updateList(newItems: List<Podcast>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PodcastViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_podcast, parent, false)
        return PodcastViewHolder(view)
    }

    override fun onBindViewHolder(holder: PodcastViewHolder, position: Int) {
        holder.bind(items[position], onItemClick)
    }

    override fun getItemCount() = items.size

    class PodcastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageArtwork: ImageView = itemView.findViewById(R.id.imageViewArtwork)
        private val textTitle: TextView = itemView.findViewById(R.id.textViewTitle)
        private val textArtist: TextView = itemView.findViewById(R.id.textViewArtist)

        fun bind(podcast: Podcast, onItemClick: (Podcast) -> Unit) {
            textTitle.text = podcast.collectionName
            textArtist.text = podcast.artistName
            Glide.with(itemView.context)
                .load(podcast.artworkUrl100)
                .into(imageArtwork)

            itemView.setOnClickListener {
                onItemClick(podcast)
            }
        }
    }
}




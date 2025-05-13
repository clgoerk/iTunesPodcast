package com.trioscg.itunespodcast.ui


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.trioscg.itunespodcast.R
import com.trioscg.itunespodcast.data.Episode

class EpisodeAdapter(
    private val episodes: List<Episode>,
    private val onItemClick: (Episode) -> Unit
) : RecyclerView.Adapter<EpisodeAdapter.EpisodeViewHolder>() {


    inner class EpisodeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewTitle: TextView = itemView.findViewById(R.id.textViewEpisodeTitle)
        val textViewDate: TextView = itemView.findViewById(R.id.textViewEpisodeDate)
        val textViewDescription: TextView = itemView.findViewById(R.id.textViewEpisodeDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_episode, parent, false)
        return EpisodeViewHolder(view)
    }

    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        val episode = episodes[position]
        holder.textViewTitle.text = episode.title
        holder.textViewDate.text = episode.pubDate
        holder.textViewDescription.text = HtmlCompat.fromHtml(episode.description, HtmlCompat.FROM_HTML_MODE_LEGACY)

        holder.itemView.setOnClickListener {
            onItemClick(episode)
        }
    }


    override fun getItemCount(): Int = episodes.size
}
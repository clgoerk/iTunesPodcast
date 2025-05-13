package com.trioscg.itunespodcast.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.trioscg.itunespodcast.R
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.trioscg.itunespodcast.data.Episode
import com.trioscg.itunespodcast.data.Podcast
import com.trioscg.itunespodcast.data.SubscriptionManager
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream
import java.net.URL

class PodcastDetailActivity : AppCompatActivity() {

    private lateinit var subscribeButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_podcast_detail)

        subscribeButton = findViewById(R.id.buttonSubscribe)

        updateSubscribeButton()

        subscribeButton.setOnClickListener {
            val podcast = Podcast(
                collectionName = intent.getStringExtra("title") ?: "",
                trackName = intent.getStringExtra("title") ?: "", // Use same title if needed
                artistName = intent.getStringExtra("artist") ?: "",
                artworkUrl100 = intent.getStringExtra("artworkUrl") ?: "",
                feedUrl = intent.getStringExtra("feedUrl") ?: "",
                trackId = intent.getStringExtra("feedUrl")?.hashCode()?.toLong() ?: 0L
            )

            SubscriptionManager.addSubscription(this, podcast)
            Toast.makeText(this, "Subscribed!", Toast.LENGTH_SHORT).show()
            updateSubscribeButton()
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        val imageArtwork: ImageView = findViewById(R.id.imageViewDetailArtwork)
        val textTitle: TextView = findViewById(R.id.textViewDetailTitle)
        val textArtist: TextView = findViewById(R.id.textViewDetailArtist)
        val textFeedUrl: TextView = findViewById(R.id.textViewDetailFeedUrl)

        val title = intent.getStringExtra("title") ?: ""
        supportActionBar?.title = title
        val artist = intent.getStringExtra("artist") ?: ""
        val artworkUrl = intent.getStringExtra("artworkUrl") ?: ""
        val feedUrl = intent.getStringExtra("feedUrl") ?: ""

        textTitle.text = title
        textArtist.text = artist
        textFeedUrl.text = feedUrl

        Glide.with(this).load(artworkUrl).into(imageArtwork)

        val recyclerViewEpisodes: RecyclerView = findViewById(R.id.recyclerViewEpisodes)
        recyclerViewEpisodes.layoutManager = LinearLayoutManager(this)

        if (feedUrl.isNotEmpty()) {
            loadEpisodes(feedUrl) { episodes ->
                recyclerViewEpisodes.adapter = EpisodeAdapter(episodes) { episode ->
                    val intent = Intent(this, EpisodeDetailActivity::class.java)
                    intent.putExtra("title", episode.title)
                    intent.putExtra("pubDate", episode.pubDate)
                    intent.putExtra("description", episode.description)
                    intent.putExtra("audioUrl", episode.audioUrl)
                    startActivity(intent)
                }
            }
        }
    }

    private fun loadEpisodes(feedUrl: String, callback: (List<Episode>) -> Unit) {
        Thread {
            try {
                val url = URL(feedUrl)
                val connection = url.openConnection()
                val inputStream = connection.getInputStream()
                val episodes = parseEpisodes(inputStream)
                runOnUiThread { callback(episodes) }
                inputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread { callback(emptyList()) }
            }
        }.start()
    }

    private fun parseEpisodes(inputStream: InputStream): List<Episode> {
        val episodes = mutableListOf<Episode>()
        val factory = XmlPullParserFactory.newInstance()
        val parser = factory.newPullParser()
        parser.setInput(inputStream, null)

        var eventType = parser.eventType
        var insideItem = false
        var title = ""
        var pubDate = ""
        var audioUrl = ""
        var description = ""

        while (eventType != XmlPullParser.END_DOCUMENT) {
            val tagName = parser.name
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    if (tagName.equals("item", ignoreCase = true)) {
                        insideItem = true
                    } else if (insideItem) {
                        when {
                            tagName.equals("title", ignoreCase = true) -> title = parser.nextText()
                            tagName.equals("pubDate", ignoreCase = true) -> pubDate = parser.nextText()
                            tagName.equals("enclosure", ignoreCase = true) -> audioUrl = parser.getAttributeValue(null, "url") ?: ""
                            tagName.equals("description", ignoreCase = true) -> description = parser.nextText()
                        }
                    }
                }
                XmlPullParser.END_TAG -> {
                    if (tagName.equals("item", ignoreCase = true)) {
                        episodes.add(Episode(title, pubDate, audioUrl, description))
                        title = ""
                        pubDate = ""
                        audioUrl = ""
                        description = ""
                        insideItem = false
                    }
                }
            }
            eventType = parser.next()
        }
        return episodes
    }

    private fun updateSubscribeButton() {
        val isSubscribed = SubscriptionManager.isSubscribed(
            this,
            intent.getStringExtra("feedUrl")?.hashCode()?.toLong() ?: 0L
        )
        subscribeButton.text = if (isSubscribed) "Unsubscribe" else "Subscribe"
    }
}
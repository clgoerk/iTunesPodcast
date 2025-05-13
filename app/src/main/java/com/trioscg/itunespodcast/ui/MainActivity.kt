package com.trioscg.itunespodcast.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.trioscg.itunespodcast.R
import com.trioscg.itunespodcast.data.ITunesApi
import com.trioscg.itunespodcast.data.Podcast
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var editTextSearch: EditText
    private lateinit var buttonSearch: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PodcastAdapter
    private lateinit var api: ITunesApi

    // Initialize an empty list of podcasts
    private var podcastList: MutableList<Podcast> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val subscriptionsButton = findViewById<Button>(R.id.buttonSubscriptions)
        subscriptionsButton.setOnClickListener {
            val intent = Intent(this, SubscriptionsActivity::class.java)
            startActivity(intent)
        }

        // Init Views
        editTextSearch = findViewById(R.id.editTextSearch)
        buttonSearch = findViewById(R.id.buttonSearch)
        recyclerView = findViewById(R.id.recyclerView)

        // Setup RecyclerView
        adapter = PodcastAdapter(podcastList) { podcast ->
            val intent = Intent(this, PodcastDetailActivity::class.java).apply {
                putExtra("title", podcast.collectionName)
                putExtra("artist", podcast.artistName)
                putExtra("artworkUrl", podcast.artworkUrl100)
                putExtra("feedUrl", podcast.feedUrl)
            }
            startActivity(intent)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Init Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        api = retrofit.create(ITunesApi::class.java)

        // Handle Search Button Click
        buttonSearch.setOnClickListener {
            val query = editTextSearch.text.toString().trim()
            if (query.isNotEmpty()) {
                searchPodcasts(query)
            }
        }
    }

    private fun searchPodcasts(query: String) {
        lifecycleScope.launch {
            try {
                val response = api.searchPodcasts(query)
                podcastList.clear()
                podcastList.addAll(response.results)
                adapter.updateList(response.results)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

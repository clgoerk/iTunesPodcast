package com.trioscg.itunespodcast.ui

import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.material.appbar.MaterialToolbar
import com.trioscg.itunespodcast.R
import androidx.core.net.toUri

class EpisodeDetailActivity : AppCompatActivity() {

    private var exoPlayer: ExoPlayer? = null
    private lateinit var playerView: PlayerView
    private lateinit var playerContainer: FrameLayout
    private lateinit var infoScrollView: ScrollView
    private var isFullscreen = false
    private var playbackPosition: Long = 0L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_episode_detail)

        // Restore saved position
        playbackPosition = savedInstanceState?.getLong("playback_position") ?: 0L

        playerView = findViewById(R.id.playerView)
        playerContainer = findViewById(R.id.playerContainer)
        infoScrollView = findViewById(R.id.infoScrollView)

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val title = intent.getStringExtra("title") ?: ""
        val pubDate = intent.getStringExtra("pubDate") ?: ""
        val description = intent.getStringExtra("description") ?: ""
        val audioUrl = intent.getStringExtra("audioUrl") ?: ""

        findViewById<TextView>(R.id.textViewEpisodeTitle).text = title
        findViewById<TextView>(R.id.textViewEpisodeDate).text = pubDate
        findViewById<TextView>(R.id.textViewEpisodeDescription).text =
            HtmlCompat.fromHtml(description, HtmlCompat.FROM_HTML_MODE_LEGACY)

        initializePlayer(audioUrl)
        addFullscreenButton()
    }

    private fun initializePlayer(url: String) {
        exoPlayer = ExoPlayer.Builder(this).build().also { player ->
            playerView.player = player
            val mediaItem = MediaItem.fromUri(url.toUri())
            player.setMediaItem(mediaItem)
            player.prepare()
            player.seekTo(playbackPosition) // ⬅️ Seek to saved position
            player.play()
        }
    }

    private fun addFullscreenButton() {
        val fullscreenButton = ImageButton(this).apply {
            setImageResource(R.drawable.ic_fullscreen) // Ensure this drawable exists
            setBackgroundColor(android.graphics.Color.TRANSPARENT)
            setOnClickListener { toggleFullscreen() }
        }

        fullscreenButton.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.END or Gravity.BOTTOM
            marginEnd = 16
            bottomMargin = 36
        }

        val controlView = playerView.findViewById<View>(com.google.android.exoplayer2.ui.R.id.exo_controller)
        if (controlView is ViewGroup) {
            controlView.addView(fullscreenButton)
        }
    }

    private fun toggleFullscreen() {
        if (isFullscreen) {
            playerContainer.layoutParams.height = dpToPx(200)
            infoScrollView.visibility = View.VISIBLE
            findViewById<MaterialToolbar>(R.id.toolbar).visibility = View.VISIBLE
            isFullscreen = false
        } else {
            playerContainer.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            infoScrollView.visibility = View.GONE
            findViewById<MaterialToolbar>(R.id.toolbar).visibility = View.GONE
            isFullscreen = true
        }
        playerContainer.requestLayout()
    }

    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save current position
        playbackPosition = exoPlayer?.currentPosition ?: 0L
        outState.putLong("playback_position", playbackPosition)
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer?.release()
        exoPlayer = null
    }
}



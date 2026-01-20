package org.example.app.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import org.example.app.R

class PlayerActivity : FragmentActivity() {

    private lateinit var titleText: TextView
    private lateinit var playerView: PlayerView

    private var player: ExoPlayer? = null

    private var videoTitle: String = ""
    private var videoUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        titleText = findViewById(R.id.playerTitle)
        playerView = findViewById(R.id.playerView)

        videoTitle = intent.getStringExtra(EXTRA_TITLE).orEmpty()
        videoUrl = intent.getStringExtra(EXTRA_URL).orEmpty()

        titleText.text = videoTitle
    }

    override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        // Allow quick back navigation with remote.
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish()
            return true
        }
        return super.onKeyUp(keyCode, event)
    }

    private fun initializePlayer() {
        if (player != null) return
        val exoPlayer = ExoPlayer.Builder(this).build()
        player = exoPlayer
        playerView.player = exoPlayer

        val mediaItem = MediaItem.fromUri(Uri.parse(videoUrl))
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    private fun releasePlayer() {
        val p = player ?: return
        playerView.player = null
        p.release()
        player = null
    }

    companion object {
        private const val EXTRA_TITLE = "extra_title"
        private const val EXTRA_URL = "extra_url"

        // PUBLIC_INTERFACE
        fun createIntent(context: Context, title: String, url: String): Intent {
            val i = Intent(context, PlayerActivity::class.java)
            i.putExtra(EXTRA_TITLE, title)
            i.putExtra(EXTRA_URL, url)
            return i
        }
    }
}

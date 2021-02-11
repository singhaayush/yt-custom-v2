package com.example.ytcustomv2.ui.ytcustom

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.ytcustomv2.R
import com.example.ytcustomv2.player.listeners.AbstractYouTubePlayerListener
import com.example.ytcustomv2.player.utils.YouTubePlayer
import com.example.ytcustomv2.player.utils.loadOrCueVideo
import com.example.ytcustomv2.player.views.YouTubePlayerView

class YTPlayerActivity : AppCompatActivity() {
    private val TAG = "VideoPlayActivity"
    lateinit var customPlayerUi: View
    lateinit var ytPlayer: YouTubePlayer
    lateinit var youTubePlayerView:YouTubePlayerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_y_t_player)
        youTubePlayerView=findViewById(R.id.youtube_player_view)
        youTubePlayerView =
            findViewById<YouTubePlayerView>(R.id.youtube_player_view)
//        val iFramePlayerOptions =
//            IFramePlayerOptions.Builder()
//                .controls(0)
//                .rel(1)
//                .ivLoadPolicy(3)
//                .ccLoadPolicy(1)
//                .build()
        lifecycle.addObserver(youTubePlayerView)
        customPlayerUi =
            youTubePlayerView.inflateCustomPlayerUi(R.layout.custom_player_ui)
        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                ytPlayer = youTubePlayer
                val customPlayerUiController = YTPlayerUiController(
                    this@YTPlayerActivity,
                    customPlayerUi,
                    youTubePlayer,
                    youTubePlayerView
                )
                youTubePlayer.addListener(customPlayerUiController)
                youTubePlayerView.addFullScreenListener(customPlayerUiController)
                youTubePlayer.loadOrCueVideo(lifecycle, "To9aHYD5OVk", 0f)
            }
        })
    }

    override fun onRestart() {
        super.onRestart()
        if (ytPlayer != null)
            ytPlayer.play()
    }


    override fun onBackPressed() {

        if (youTubePlayerView.isFullScreen())
            youTubePlayerView.exitFullScreen()
        else
            super.onBackPressed()
    }
}
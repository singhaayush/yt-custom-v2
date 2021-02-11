package com.example.ytcustomv2.player.utils

import com.example.ytcustomv2.player.PlayerConstants
import com.example.ytcustomv2.player.listeners.AbstractYouTubePlayerListener

class PlaybackResumer : AbstractYouTubePlayerListener() {

    private var canLoad = false
    private var isPlaying = false
    private var error: PlayerConstants.PlayerError? = null

    private var currentVideoId: String? = null
    private var currentSecond: Float = 0f

    fun resume(youTubePlayer: YouTubePlayer) {
        currentVideoId?.let { videoId ->
            if (isPlaying && error == PlayerConstants.PlayerError.HTML_5_PLAYER)
                youTubePlayer.loadOrCueVideo(canLoad, videoId, currentSecond)
            else if (!isPlaying && error == PlayerConstants.PlayerError.HTML_5_PLAYER)
                youTubePlayer.cueVideo(videoId, currentSecond)
        }

        error = null
    }

    override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
        when (state) {
            PlayerConstants.PlayerState.ENDED -> {
                isPlaying = false
                return
            }
            PlayerConstants.PlayerState.PAUSED -> {
                isPlaying = false
                return
            }
            PlayerConstants.PlayerState.PLAYING -> {
                isPlaying = true
                return
            }
            else -> { }
        }
    }

    override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
        if (error == PlayerConstants.PlayerError.HTML_5_PLAYER)
            this.error = error
    }

    override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
        currentSecond = second
    }

    override fun onVideoId(youTubePlayer: YouTubePlayer, videoId: String) {
        currentVideoId = videoId
    }

    fun onLifecycleResume() {
        canLoad = true
    }

    fun onLifecycleStop() {
        canLoad = false
    }
}

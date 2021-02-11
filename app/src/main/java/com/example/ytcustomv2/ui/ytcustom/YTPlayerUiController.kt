package com.example.ytcustomv2.ui.ytcustom

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.ActivityInfo
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import com.example.ytcustomv2.R
import com.example.ytcustomv2.player.PlayerConstants
import com.example.ytcustomv2.player.listeners.AbstractYouTubePlayerListener
import com.example.ytcustomv2.player.listeners.YouTubePlayerFullScreenListener
import com.example.ytcustomv2.player.utils.YouTubePlayer
import com.example.ytcustomv2.player.utils.YouTubePlayerTracker
import com.example.ytcustomv2.player.views.YouTubePlayerView
import com.example.ytcustomv2.ui.utils.FadeViewHelper
import com.example.ytcustomv2.ui.views.YouTubePlayerSeekBar
import com.example.ytcustomv2.ui.views.YouTubePlayerSeekBarListener


internal class YTPlayerUiController(
    private val context: Context,
    private val playerUi: View,
    private val youTubePlayer: YouTubePlayer,
    private val youTubePlayerView: YouTubePlayerView,
) : AbstractYouTubePlayerListener(), YouTubePlayerFullScreenListener {

    private lateinit var playPauseButton: ImageView

    // panel is used to intercept clicks on the WebView, I don't want the user to be able to click the WebView directly.
    private var panel: View? = null
    private val playerTracker: YouTubePlayerTracker = YouTubePlayerTracker()
    private var fullscreen = false
    private lateinit var fullScreenIcon: ImageView
    private lateinit var youTubePlayerSeekBar: YouTubePlayerSeekBar
    private lateinit var fadeViewHelper: FadeViewHelper
    private lateinit var fullScreenText: TextView
    private lateinit var forwardButton: ImageView
    private lateinit var backwardButton: ImageView
    private lateinit var speedLayout: LinearLayout
    private var currentPlayBackRate = 0

    companion object {
        const val DEFAULT_ANIMATION_DURATION = 50L
    }

    private fun initViews(playerUi: View) {
        panel = playerUi.findViewById(R.id.panel)
        fullScreenIcon = playerUi.findViewById(R.id.enter_exit_fullscreen_button)
        fullScreenText = playerUi.findViewById(R.id.tv_fullscreen_state)
        fadeViewHelper = FadeViewHelper(playerUi.findViewById(R.id.controls_container))
        youTubePlayer.addListener(fadeViewHelper)
        youTubePlayerSeekBar = playerUi.findViewById(R.id.youtube_player_seekbar)
        forwardButton = playerUi.findViewById(R.id.exo_ffwd)
        backwardButton = playerUi.findViewById(R.id.exo_rew);
        speedLayout = playerUi.findViewById(R.id.ll_speed)

        attachListeners()

    }

    private fun attachListeners() {
        fadeViewHelper.animationDuration = DEFAULT_ANIMATION_DURATION
        fadeViewHelper.fadeOutDelay = FadeViewHelper.DEFAULT_FADE_OUT_DELAY

        playPauseButton = playerUi.findViewById(R.id.play_pause_button)
        val enterExitFullscreenButton =
            playerUi.findViewById<ImageView>(R.id.enter_exit_fullscreen_button)

        playPauseButton.setOnClickListener { view: View? ->
            if (playerTracker.state == PlayerConstants.PlayerState.PLAYING) {
                playPauseButton.background = ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_exo_play_48
                )
                youTubePlayer.pause()
            } else if (playerTracker.state == PlayerConstants.PlayerState.PAUSED) {
                youTubePlayer.play()
            }
        }
        enterExitFullscreenButton.setOnClickListener {
            if (fullscreen) youTubePlayerView.exitFullScreen() else youTubePlayerView.enterFullScreen()
            fullscreen = !fullscreen
        }
        fullScreenText.setOnClickListener {
            if (fullscreen) youTubePlayerView.exitFullScreen() else youTubePlayerView.enterFullScreen()
            fullscreen = !fullscreen
        }
        forwardButton.setOnClickListener {
            if (playerTracker.state == PlayerConstants.PlayerState.PLAYING) {
                youTubePlayer.seekTo((playerTracker.currentSecond + 10f).coerceAtMost(playerTracker.videoDuration - 1))
            }
        }

        backwardButton.setOnClickListener {
            if (playerTracker.state == PlayerConstants.PlayerState.PLAYING) {
                youTubePlayer.seekTo((playerTracker.currentSecond - 10f).coerceAtLeast(0f))
            }
        }

        panel?.setOnClickListener {
            if (playerTracker.state != PlayerConstants.PlayerState.PAUSED)
                fadeViewHelper.toggleVisibility()


        }

        val backBtn = playerUi.findViewById<ImageView>(R.id.iv_back);
        backBtn.setOnClickListener {
            if (youTubePlayerView.isFullScreen()) youTubePlayerView.exitFullScreen()
            else (context as YTPlayerActivity).onBackPressed()
        }

        youTubePlayer.addListener(youTubePlayerSeekBar)
        youTubePlayerSeekBar.youtubePlayerSeekBarListener = object : YouTubePlayerSeekBarListener {
            override fun seekTo(time: Float) {
                youTubePlayer.seekTo(time)
            }
        }
        speedLayout.setOnClickListener { showAlertDialog() }

    }

    override fun onReady(youTubePlayer: YouTubePlayer) {
        if (playerTracker.state == PlayerConstants.PlayerState.PAUSED) {
//            listner.onStateChange(true)
            youTubePlayer.play()
        }
    }

    override fun onStateChange(
        youTubePlayer: YouTubePlayer,
        state: PlayerConstants.PlayerState
    ) {
        if (state != PlayerConstants.PlayerState.BUFFERING) {
            showBufferingUI(false)
        }

        when (state) {
            PlayerConstants.PlayerState.PLAYING -> {
//                listner.onStateChange(true)
                playPauseButton.background = ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_exo_pause_48
                )
                panel!!.setBackgroundColor(
                    ContextCompat.getColor(context, android.R.color.transparent)
                )

            }
            PlayerConstants.PlayerState.PAUSED -> {
                //listner.onStateChange(false)
                playPauseButton.background = ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_exo_play_48
                )
                panel!!.requestFocus()

            }
            PlayerConstants.PlayerState.BUFFERING -> {
                // listner.onStateChange(false)
                showBufferingUI(true)
            }
            PlayerConstants.PlayerState.UNSTARTED -> {
                //listner.onStateChange(false)
                showBufferingUI(true)
            }
        }


    }

    private fun showBufferingUI(isBuffering: Boolean) {
        if (isBuffering) {
            val bufferingProgressBar = playerUi.findViewById<ProgressBar>(R.id.pb_buffer)
            bufferingProgressBar.visibility = View.VISIBLE
            forwardButton.visibility = View.INVISIBLE
            backwardButton.visibility = View.INVISIBLE
            playPauseButton.visibility = View.INVISIBLE
            youTubePlayerSeekBar.showBufferingProgress = true
        } else {
            val bufferingProgressBar = playerUi.findViewById<ProgressBar>(R.id.pb_buffer)
            bufferingProgressBar.visibility = View.INVISIBLE
            forwardButton.visibility = View.VISIBLE
            backwardButton.visibility = View.VISIBLE
            playPauseButton.visibility = View.VISIBLE
        }


    }

    @SuppressLint("SetTextI18n")
    override fun onCurrentSecond(
        youTubePlayer: YouTubePlayer,
        second: Float
    ) {
        if (playerTracker.videoDuration - second <= 1f) {
            (context as Activity).onBackPressed()
        }
    }

    override fun onYouTubePlayerEnterFullScreen() {
        fullScreenIcon.background =
            ContextCompat.getDrawable(context, R.drawable.ic_baseline_fullscreen_exit_48)
        playerUi.findViewById<TextView>(R.id.tv_fullscreen_state).text = "Exit Fullscreen"
        val viewParams = playerUi.layoutParams
        viewParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        viewParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        playerUi.layoutParams = viewParams
        var window = (context as YTPlayerActivity).window
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        (context).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

    }

    override fun onYouTubePlayerExitFullScreen() {
        fullScreenIcon.background =
            ContextCompat.getDrawable(context, R.drawable.ic_baseline_fullscreen_48)
        playerUi.findViewById<TextView>(R.id.tv_fullscreen_state).text = "Fullscreen"

        val viewParams = playerUi.layoutParams
        viewParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        viewParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        playerUi.layoutParams = viewParams

        val playerViewParam = youTubePlayerView.layoutParams
        playerViewParam.height = ViewGroup.LayoutParams.MATCH_PARENT
        playerViewParam.width = ViewGroup.LayoutParams.MATCH_PARENT
        youTubePlayerView.layoutParams = playerViewParam
        var window = (context as YTPlayerActivity).window
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN
        )
        (context).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

    }

    init {
        youTubePlayer.addListener(playerTracker)
        initViews(playerUi)
    }

    interface PlayerControllerListener {
        fun onStateChange(isPlaying: Boolean)
    }

    //    private fun showAlertDialog() {
//        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(context)
//        alertDialog.setTitle("AlertDialog")
//        val items = arrayOf("Normal", "1.5", "2.0", "0.5", "0.25")
//        val checkedItems = booleanArrayOf(false, false, false, false, false, false)
//        alertDialog.setSingleChoiceItems(items, checkedItems
//        ) { _, which, isChecked ->
//            when (which) {
//                0 -> youTubePlayer.setPlaybackRate(1.0f)
//                1 -> youTubePlayer.setPlaybackRate(1.5f)
//                2 -> youTubePlayer.setPlaybackRate(0.5f)
//                3 -> youTubePlayer.setPlaybackRate(0.25f)
//
//            }
//        }
//        val alert: AlertDialog = alertDialog.create()
//        alert.setCanceledOnTouchOutside(false)
//        alert.show()
//    }
    private fun showAlertDialog() {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle("AlertDialog")
        val items = arrayOf("Normal", "1.5", "2.0", "0.5", "0.25")
        val checkedItem = currentPlayBackRate
        alertDialog.setSingleChoiceItems(
            items, checkedItem
        ) { dialog, which ->
            when (which) {
                0 -> youTubePlayer.setPlaybackRate(1.0f)
                1 -> youTubePlayer.setPlaybackRate(1.5f)
                2 -> youTubePlayer.setPlaybackRate(2.0f)
                3 -> youTubePlayer.setPlaybackRate(0.5f)
                4 -> youTubePlayer.setPlaybackRate(0.25f)

            }
            currentPlayBackRate = which
            dialog.cancel()
        }
        val alert = alertDialog.create()
        alert.show()
    }
}
package com.example.sentra.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.sentra.model.CameraItem
import com.example.sentra.R

class CameraStreamActivity : AppCompatActivity() {

    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView
    private lateinit var progressBar: ProgressBar
    private var rtspUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_stream)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val tvBack = findViewById<TextView>(R.id.tvBack)
        val tvName = findViewById<TextView>(R.id.tvStreamCameraName)
        val tvLocation = findViewById<TextView>(R.id.tvStreamLocation)

        playerView = findViewById(R.id.playerView)
        progressBar = findViewById(R.id.progressBar)

        val cameraItem = intent.getParcelableExtra<CameraItem>("CAMERA_DATA")

        if (cameraItem != null) {
            tvName.text = cameraItem.name
            tvLocation.text = cameraItem.location
            rtspUrl = cameraItem.streamURL

            if (rtspUrl.isEmpty()) {
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Stream URL is missing", Toast.LENGTH_SHORT).show()
            }
        }

        btnBack.setOnClickListener { finish() }
        tvBack.setOnClickListener { finish() }
    }

    override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    @OptIn(UnstableApi::class)
    private fun initializePlayer() {
        if (rtspUrl.isEmpty()) return

        // إظهار التحميل فوراً
        progressBar.visibility = View.VISIBLE

        player = ExoPlayer.Builder(this).build().apply {

            // 🌟 استخدام RtspMediaSource بدلاً من MediaItem العادي
            val mediaSource = androidx.media3.exoplayer.rtsp.RtspMediaSource.Factory()
                .setForceUseRtpTcp(true) // السطر ده هو اللي بيحل 90% من مشاكل الشاشة السودة والتقطيع
                .createMediaSource(MediaItem.fromUri(rtspUrl))

            setMediaSource(mediaSource) // لاحظ إننا استخدمنا setMediaSource هنا

            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)

                    when (playbackState) {
                        Player.STATE_BUFFERING -> progressBar.visibility = View.VISIBLE
                        Player.STATE_READY -> progressBar.visibility = View.GONE
                        Player.STATE_ENDED -> progressBar.visibility = View.GONE
                        Player.STATE_IDLE -> {}
                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    super.onPlayerError(error)
                    progressBar.visibility = View.GONE
                    android.util.Log.e("SENTRA_RTSP", "Error: ${error.message}", error)
                    Toast.makeText(this@CameraStreamActivity, "Stream unavailable", Toast.LENGTH_LONG).show()
                }
            })

            prepare()
            playWhenReady = true
        }

        playerView.player = player
    }

    private fun releasePlayer() {
        player?.release()
        player = null
    }
}
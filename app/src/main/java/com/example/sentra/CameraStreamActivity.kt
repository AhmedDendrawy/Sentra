package com.example.sentra

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class CameraStreamActivity : AppCompatActivity() {
    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView
    private var rtspUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_stream)

        // استقبال البيانات
        val cameraItem = intent.getParcelableExtra<CameraItem>("CAMERA_DATA")
        // افترضنا إن رابط الكاميرا جاي مع الاوبجكت، لو مش معاك جربه برابط وهمي
        // rtspUrl = cameraItem?.rtspUrl ?: "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mp4"
        // ملحوظة: الرابط اللي فوق ده للتجربة (RTSP public)

        rtspUrl = "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4" // الرابط اللي في التصميم

        playerView = findViewById(R.id.playerView)

        // ... باقي تعريفات النصوص والأسماء ...
    }

    override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer() // لازم توقف الفيديو لما تخرج عشان البطارية والميموري
    }

    private fun initializePlayer() {
        if (rtspUrl.isEmpty()) return

        player = ExoPlayer.Builder(this).build().apply {
            val mediaItem = MediaItem.fromUri(rtspUrl)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true // تشغيل تلقائي
        }

        playerView.player = player
    }

    private fun releasePlayer() {
        player?.release()
        player = null
    }
}
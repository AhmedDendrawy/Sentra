package com.example.sentra.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
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

        // استقبال البيانات
        val cameraItem = intent.getParcelableExtra<CameraItem>("CAMERA_DATA")

        playerView = findViewById(R.id.playerView)
        progressBar = findViewById(R.id.progressBar)

        if (cameraItem != null) {
            tvName.text = cameraItem.name
            tvLocation.text = cameraItem.location

            // 🌟 السحر هنا: خلينا rtspUrl ياخد قيمته من الكاميرا اللي ضغطنا عليها 🌟
            rtspUrl = cameraItem.rtspUrl

            // لو الرابط فاضي (اليوزر ماكتبوش وهو بيضيف الكاميرا)
            if (rtspUrl.isEmpty()) {
                Toast.makeText(this, "Please enter a valid URL in Edit Camera", Toast.LENGTH_SHORT).show()
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

    private fun initializePlayer() {
        if (rtspUrl.isEmpty()) return

        player = ExoPlayer.Builder(this).build().apply {
            val mediaItem = MediaItem.fromUri(rtspUrl)
            setMediaItem(mediaItem)

            // إضافة المستمع (Listener) لمراقبة الحالة
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)

                    when (playbackState) {
                        Player.STATE_BUFFERING -> progressBar.visibility = View.VISIBLE
                        Player.STATE_READY -> progressBar.visibility = View.GONE
                        Player.STATE_ENDED -> progressBar.visibility = View.GONE
                        Player.STATE_IDLE -> progressBar.visibility = View.GONE
                    }
                }


                override fun onPlayerError(error: PlaybackException) {
                    super.onPlayerError(error)
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@CameraStreamActivity, "Error playing video, Check URL", Toast.LENGTH_SHORT).show()
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
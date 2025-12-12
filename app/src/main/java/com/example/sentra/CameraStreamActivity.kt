package com.example.sentra

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class CameraStreamActivity : AppCompatActivity() {

    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView
    private lateinit var progressBar: ProgressBar // 1. تعريف المتغير
    private var rtspUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_stream)
        val tvName = findViewById<TextView>(R.id.tvStreamCameraName)
        val tvLocation = findViewById<TextView>(R.id.tvStreamLocation)
        // استقبال البيانات
        val cameraItem = intent.getParcelableExtra<CameraItem>("CAMERA_DATA")

        // الرابط التجريبي (mp4) للتأكد
        rtspUrl = "rtsp://rtspstream:haqbbANLLxmMnv-FSpdxx@zephyr.rtsp.stream/people"

        playerView = findViewById(R.id.playerView)
        progressBar = findViewById(R.id.progressBar) // 2. ربط المتغير بالـ XML
        if (cameraItem != null) {
            tvName.text = cameraItem.name       // هنا السحر: بنغير الاسم لاسم الكاميرا المضغوطة
            tvLocation.text = cameraItem.location // وبنغير المكان كمان

            // (اختياري) تحديث حالة الأونلاين/أوفلاين
//            if (cameraItem.isOnline) {
//                tvStatus.text = "Online"
//                tvStatus.setBackgroundResource(R.drawable.bg_rounded_status) // تأكد إنه أخضر
//            } else {
//                tvStatus.text = "Offline"
//                // ممكن تعمل خلفية حمراء لو عندك drawable ليها
//            }

            // ملحوظة: حالياً احنا مثبتين الرابط عشان التجربة
            // rtspUrl = cameraItem.rtspUrl ?: "" <--- ده هنفعله لما يكون عندك روابط حقيقية لكل كاميرا
        }
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

            // 3. إضافة المستمع (Listener) لمراقبة الحالة
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)

                    when (playbackState) {
                        Player.STATE_BUFFERING -> {
                            // لو الفيديو لسه بيحمل -> اظهر التحميل
                            progressBar.visibility = View.VISIBLE
                        }
                        Player.STATE_READY -> {
                            // لو الفيديو اشتغل -> اخفي التحميل
                            progressBar.visibility = View.GONE
                        }
                        Player.STATE_ENDED -> {
                            // لو الفيديو خلص -> اخفي التحميل
                            progressBar.visibility = View.GONE
                        }
                        Player.STATE_IDLE -> {
                            // في وضع الخمول -> اخفي التحميل
                            progressBar.visibility = View.GONE
                        }
                    }
                }

                // (اختياري) التعامل مع الأخطاء عشان التحميل مايفضلش يلف لو الرابط بايظ
                override fun onPlayerError(error: PlaybackException) {
                    super.onPlayerError(error)
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@CameraStreamActivity, "Error playing video", Toast.LENGTH_SHORT).show()
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
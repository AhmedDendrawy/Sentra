package com.example.sentra.ui.camera

import android.content.ContentValues
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.TextureView
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.rtsp.RtspMediaSource
import com.example.sentra.data.model.CameraItem
import com.example.sentra.databinding.ActivityCameraStreamBinding
import java.io.OutputStream

class CameraStreamActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraStreamBinding
    private var player: ExoPlayer? = null
    private var rtspUrl: String = ""
    private var isFullscreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        binding = ActivityCameraStreamBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val cameraItem = intent.getParcelableExtra<CameraItem>("CAMERA_DATA")

        if (cameraItem != null) {
            binding.tvStreamCameraName.text = cameraItem.name
            binding.tvStreamLocation.text = cameraItem.location
            rtspUrl = cameraItem.streamURL

            if (rtspUrl.isEmpty()) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Stream URL is missing", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnBack.setOnClickListener { finish() }
        binding.tvBack.setOnClickListener { finish() }

        binding.btnRefresh.setOnClickListener {
            Toast.makeText(this, "Refreshing stream...", Toast.LENGTH_SHORT).show()
            player?.stop()
            player?.seekTo(0)
            player?.prepare()
            player?.play()
        }

        binding.btnFullscreen.setOnClickListener {
            toggleFullscreen()
        }

        binding.btnScreenshot.setOnClickListener {
            takeScreenshot()
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

    @OptIn(UnstableApi::class)
    private fun initializePlayer() {
        if (rtspUrl.isEmpty()) return

        binding.progressBar.visibility = View.VISIBLE

        player = ExoPlayer.Builder(this).build().apply {
            val mediaSource = RtspMediaSource.Factory()
                .setForceUseRtpTcp(true)
                .createMediaSource(MediaItem.fromUri(rtspUrl))

            setMediaSource(mediaSource)

            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)

                    when (playbackState) {
                        Player.STATE_BUFFERING -> binding.progressBar.visibility = View.VISIBLE
                        Player.STATE_READY -> binding.progressBar.visibility = View.GONE
                        Player.STATE_ENDED -> binding.progressBar.visibility = View.GONE
                        Player.STATE_IDLE -> {}
                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    super.onPlayerError(error)
                    binding.progressBar.visibility = View.GONE
                    Log.e("SENTRA_RTSP", "Error: ${error.message}", error)
                    Toast.makeText(this@CameraStreamActivity, "Stream unavailable", Toast.LENGTH_LONG).show()
                }
            })

            prepare()
            playWhenReady = true
        }

        binding.playerView.player = player
    }

    private fun releasePlayer() {
        player?.release()
        player = null
    }

    private fun toggleFullscreen() {
        if (isFullscreen) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            WindowInsetsControllerCompat(window, binding.playerView).show(WindowInsetsCompat.Type.systemBars())
            isFullscreen = false
        } else {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            WindowInsetsControllerCompat(window, binding.playerView).hide(WindowInsetsCompat.Type.systemBars())
            isFullscreen = true
        }
    }

    @OptIn(UnstableApi::class)
    private fun takeScreenshot() {
        val videoSurfaceView = binding.playerView.videoSurfaceView

        if (videoSurfaceView is TextureView) {
            val bitmap = videoSurfaceView.bitmap
            if (bitmap != null) {
                saveBitmapToGallery(bitmap)
            } else {
                Toast.makeText(this, "Please wait for the stream to load", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Make sure app:surface_type='texture_view' is in XML", Toast.LENGTH_LONG).show()
        }
    }

    private fun saveBitmapToGallery(bitmap: Bitmap) {
        val filename = "Sentra_Cam_${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        var imageUri: android.net.Uri? = null

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Sentra")
                }
                imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            } else {
                val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
                val image = java.io.File(imagesDir, filename)
                fos = java.io.FileOutputStream(image)
            }

            fos?.use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                Toast.makeText(this, "Screenshot saved to Gallery!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error saving screenshot", Toast.LENGTH_SHORT).show()
        }
    }
}
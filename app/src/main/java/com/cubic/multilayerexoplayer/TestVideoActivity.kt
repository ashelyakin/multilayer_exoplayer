package com.cubic.multilayerexoplayer

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.cubic.multilayerexoplayer.databinding.ActivityTestVideoBinding
import com.cubic.multilayerexoplayer.databinding.TestVideoLayoutBinding
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.StyledPlayerView

class TestVideoActivity : AppCompatActivity() {

    private lateinit var exoplayer: ExoPlayer
    private lateinit var playerView: StyledPlayerView

    private var binding: ActivityTestVideoBinding? = null
    private var videoLayoutBinding: TestVideoLayoutBinding? = null

    private val videoResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result?.data?.data?.let{
                    setMediaItemAndStartPlay(it)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestVideoBinding.inflate(layoutInflater)
        videoLayoutBinding = TestVideoLayoutBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        initPlayer()
        initWebView()

        binding?.btnSelectVideo?.setOnClickListener {
            val intent = Intent()
            intent.type = "video/*"
            intent.action = Intent.ACTION_GET_CONTENT
            videoResultLauncher.launch(intent)
        }
    }

    private fun initPlayer(){
        val renderersFactory = DefaultRenderersFactory(this)
            .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF)

        val trackSelector = DefaultTrackSelector(this)

        val builder = ExoPlayer.Builder(this, renderersFactory)
            .setTrackSelector(trackSelector)
            .setMediaSourceFactory(DefaultMediaSourceFactory(this, DefaultExtractorsFactory()))

        exoplayer = builder.build()

        exoplayer.repeatMode = Player.REPEAT_MODE_ALL

        with(videoLayoutBinding?.testExoPlayerView){
            if (this != null)
                playerView = this
        }
        playerView.player = exoplayer
        playerView.controllerAutoShow = false
        playerView.useController = false
        playerView.controllerHideOnTouch = false
        playerView.hideController()
    }

    private fun setMediaItemAndStartPlay(uri: Uri){
        val mediaItem = MediaItem.Builder()
            .setUri(uri)
            .setMimeType(contentResolver.getType(uri))
            .build()
        exoplayer.setMediaItems(listOf(mediaItem), true)

        exoplayer.prepare()
        exoplayer.playWhenReady = true

        videoLayoutBinding?.root?.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT)
        binding?.mainLayout?.addView(videoLayoutBinding?.root)
    }

    private fun initWebView(){
        videoLayoutBinding?.simpleWebView?.loadUrl("https://www.google.com/")
    }
}
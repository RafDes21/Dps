package com.rafdev.Dps

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.rafdev.Dps.adapter.OnCardClickListener
import com.rafdev.Dps.clients.RetrofitClient
import com.rafdev.Dps.models.*
import kotlinx.coroutines.*
import retrofit2.Response

import com.rafdev.dps.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), OnCardClickListener {
    private lateinit var binding: ActivityMainBinding
    private val dataItems = mutableListOf<VideoItemModel>()
    private lateinit var adapter: DataAdapter
    private val retrofit = RetrofitClient().getRetrofit()
    private lateinit var progressBar: ProgressBar

    //exoplayer
    private lateinit var player: SimpleExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressBar = binding.progressBar
        player = SimpleExoPlayer.Builder(this).build()
        binding.playerView.player = player

        initRecyclarView()
        initVideo()
        loadVideoData()
        
    }

    private fun initVideo() {
        val m3u8Url = "https://redirector.dps.live/hls/ecuavisa2/playlist.m3u8"
        val mediaItem =
            MediaItem.fromUri(Uri.parse(m3u8Url))
        player.setMediaItem(mediaItem)
        player.prepare()
        viewGonePogressBar()
        player.play()

    }

    private fun viewGonePogressBar() {
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)

                if (playbackState == Player.STATE_READY) {
                    // Oculta el ProgressBar una vez que el video está listo para reproducirse
                    progressBar.visibility = View.GONE
                }
            }
        })
    }

    private fun loadVideoData() {
        CoroutineScope(Dispatchers.IO).launch {
            val call: Response<ResponseModel> =
                retrofit.create(ApiService::class.java).getData()
            val response: ResponseModel? = call.body()
            runOnUiThread {
                if (call.isSuccessful) {
                    val data = response?.data ?: emptyList()
                    dataItems.clear()
                    dataItems.addAll(data)
                    adapter.notifyDataSetChanged()
                } else {
                    showError()
                }
            }
        }
    }

    private fun showError() {
        Toast.makeText(this, "An error occurred while getting the data", Toast.LENGTH_SHORT).show()
    }

    override fun onCardClick(m3u8Url: String) {
        progressBar.visibility = View.VISIBLE
        player.stop()
        val mediaItem = MediaItem.fromUri(Uri.parse(m3u8Url))
        player.clearMediaItems()
        player.setMediaItem(mediaItem)
        player.prepare()
        viewGonePogressBar()
        player.play()
    }

    private fun initRecyclarView() {
        adapter = DataAdapter(dataItems, this)
        binding.rvItems.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvItems.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        // Liberamos los recursos del reproductor cuando se destruye la actividad
        player.release()
    }
}
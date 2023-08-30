package com.rafdev.apptv

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.rafdev.apptv.databinding.ActivityMainBinding
import com.rafdev.apptv.models.*
import kotlinx.coroutines.*
import retrofit2.Response


class MainActivity : AppCompatActivity(), OnCardClickListener {
    private lateinit var binding: ActivityMainBinding
    private val dataItems = mutableListOf<DataItem>()
    private lateinit var adapter: DataAdapter
    private val retrofit = RetrofitClient().getRetrofit()

    //exoplayer
    private lateinit var player: SimpleExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //create el objet exoplayer
        player = SimpleExoPlayer.Builder(this).build()
        // Conectamos el reproductor al reproductor de la vista
        binding.playerView.player = player
        initRecyclarView()
        loadVideoData()
        // Creamos un MediaItem utilizando la URL de la fuente del video
        val mediaItem =
            MediaItem.fromUri(Uri.parse("https://unlimited2-cl-isp.dps.live/sportinghd/sportinghd.smil/playlist.m3u8"))
        // Añadimos el MediaItem al reproductor
        player.setMediaItem(mediaItem)
        // Preparamos y reproducimos el video
        player.prepare()
        player.play()


    }

    private fun loadVideoData() {
        CoroutineScope(Dispatchers.IO).launch {
            val call: Response<DataModel> =
                retrofit.create(ApiService::class.java).getData()
            val response: DataModel? = call.body()
            runOnUiThread {
                if (call.isSuccessful) {
                    val data = response?.data ?: emptyList()
                    dataItems.clear()
                    dataItems.addAll(data)
                    adapter.notifyDataSetChanged()
                } else {
                    // showError()
                }
            }
        }
    }

    override fun onCardClick(m3u8Url: String) {
        player.stop()
        // Mostrar la URL en un Toast
        //Toast.makeText(this, m3u8Url, Toast.LENGTH_SHORT).show()
        val mediaItem = MediaItem.fromUri(Uri.parse(m3u8Url))
        // Clear the current playlist and add the new MediaItem
        player.clearMediaItems()
        //player.addMediaItem(mediaItem)
        player.setMediaItem(mediaItem)
        // Prepare the new video
        player.prepare()
        // Play the new video immediately
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
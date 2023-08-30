package com.rafdev.apptv

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.rafdev.apptv.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import models.ApiService
import models.DataAdapter
import models.DataItem
import models.DataModel
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val dataItems = mutableListOf<DataItem>()
    private lateinit var adapter: DataAdapter
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
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val call: Response<DataModel> =
                    getRetrofit().create(ApiService::class.java).getData()
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

            // Creamos un MediaItem utilizando la URL de la fuente del video
            val mediaItem =
                MediaItem.fromUri(Uri.parse("https://redirector.dps.live/hls/ecuavisa2/playlist.m3u8"))
            // Añadimos el MediaItem al reproductor
            player.setMediaItem(mediaItem)
            // Preparamos y reproducimos el video
            player.prepare()
            player.play()


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initRecyclarView() {
        adapter = DataAdapter(dataItems)
        binding.rvItems.layoutManager = LinearLayoutManager(this)
        binding.rvItems.adapter = adapter
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://ecu.dpsgo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Liberamos los recursos del reproductor cuando se destruye la actividad
        player.release()
    }
}
package com.rafdev.apptv

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.rafdev.apptv.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

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

       try {
           // Creamos un MediaItem utilizando la URL de la fuente del video
           val mediaItem = MediaItem.fromUri(Uri.parse("https://redirector.dps.live/hls/ecuavisa2/playlist.m3u8"))

           // Añadimos el MediaItem al reproductor
           player.setMediaItem(mediaItem)

           // Preparamos y reproducimos el video
           player.prepare()
           player.play()
       }catch (e:Exception){
           e.printStackTrace()
       }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Liberamos los recursos del reproductor cuando se destruye la actividad
        player.release()
    }
}
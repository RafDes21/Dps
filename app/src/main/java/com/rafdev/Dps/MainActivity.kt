package com.rafdev.Dps

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.gms.ads.AdRequest
import com.rafdev.Dps.adapter.OnCardClickListener
import com.rafdev.Dps.clients.RetrofitClient
import com.rafdev.Dps.models.*
import com.rafdev.dps.R
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

    var isPlaying: Boolean = false


    companion object {
        const val MY_CHANNEL_ID = "myChannel"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        Intent(this, MediaPlayerService::class.java).also {
            startService(it)
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressBar = binding.progressBar
        player = SimpleExoPlayer.Builder(this).build()
        binding.playerView.player = player


        binding.pause.setOnClickListener {

            if (isPlaying) {
                val playIntent = Intent(this, MediaPlayerService::class.java)
                playIntent.action = "PLAY_ACTION"
                startService(playIntent)
                isPlaying = false

            } else {
                val pauseIntent = Intent(this, MediaPlayerService::class.java)
                pauseIntent.action = "PAUSE_ACTION"
                startService(pauseIntent)
                isPlaying = true
            }
        }


        val but = binding.btnNotification
        createChannel()
        but.setOnClickListener {
            Toast.makeText(this, "es una prueba", Toast.LENGTH_SHORT).show()
            notification()
        }

        initRecyclarView()
        initVideo()
        loadVideoData()
        pruebas()
        initLoadAds()

        val filter = IntentFilter("ACTION_BOOLEAN") // Asegúrate de usar la misma acción que en el servicio
        registerReceiver(receiver, filter)

    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val receivedBoolean = intent?.getBooleanExtra("miBooleano", false)
            receivedBoolean?.let {
                if (it) {
                    binding.progress.visibility = View.GONE
                } else {
                    binding.progress.visibility = View.INVISIBLE
                }
            }
        }
    }


    fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                MY_CHANNEL_ID,
                "MySuperChannel",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "SUSCRIBETE"
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }


    private fun notification() {

        val builder = NotificationCompat.Builder(this, MY_CHANNEL_ID)
            .setSmallIcon(R.drawable.isologo)
            .setContentTitle("My title")
            .setContentText("Esto es un ejemplo <3")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            notify(1, builder.build())
        }
    }

    private fun initLoadAds() {
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
    }

    private fun pruebas() {
        Toast.makeText(this, "es una prueba", Toast.LENGTH_SHORT).show()
    }

    private fun initVideo() {
        val m3u8Url = "https://redirector.dps.live/hls-video-vod/canal-13/cl2/1045348/smil:CSGwaM0ry00Y.smil/playlist.m3u8"
//        val m3u8Url = "https://redirector.dps.live/hls/ecuavisa2/playlist.m3u8"
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
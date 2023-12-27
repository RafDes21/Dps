package com.rafdev.Dps

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.rafdev.dps.R

class MediaPlayerService : Service() {

    companion object {
        const val MY_CHANNEL_ID = "myChannel"
    }

    private var player: SimpleExoPlayer? = null
    private lateinit var playerNotificationManager: PlayerNotificationManager

    private var  isPlayerPrepared = false

//    private val binder = LocalBinder()
//
//    inner class LocalBinder : Binder() {
//        fun getService(): MediaPlayerService = this@MediaPlayerService
//    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        initializePlayer()
        createChannel()

        setupNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        when (intent?.action) {
            "PAUSE_ACTION" -> pause()
            "PLAY_ACTION" -> play()
        }

        return START_STICKY
    }

    private fun initializePlayer() {
        player = SimpleExoPlayer.Builder(this).build()

        val mediaItem = MediaItem.fromUri("https://mdstrm.com/audio/639b7943e0a27908a55dbf9e/live.m3u8")
        player?.setMediaItem(mediaItem)
        player?.addListener(object : Player.Listener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playbackState == Player.STATE_READY && playWhenReady) {
                    // El reproductor está listo y reproduciendo
                    isPlayerPrepared = true
                    sendBroadcastState(isPlayerPrepared)
                }
            }
        })
        player?.prepare()
        player?.play()
    }

    private fun sendBroadcastState(isPrepared: Boolean) {
        val intent = Intent("ACTION_BOOLEAN")
        intent.putExtra("miBooleano", isPrepared)
        sendBroadcast(intent)
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


    private fun setupNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE)

        val notificationLayout = RemoteViews(packageName, R.layout.custom_notification)

//        val playIcon = if (isPlayerPrepared) R.drawable.ic_sinal else R.drawable.ic_pause
//
//        notificationLayout.setImageViewResource(R.id.btn_play, playIcon)


        val notification = NotificationCompat.Builder(this, MY_CHANNEL_ID)
            .setContentTitle("Reproduciendo")
            .setContentText("Contenido multimedia")
            .setSmallIcon(R.drawable.ic_sinal)
            .setCustomBigContentView(notificationLayout)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(1, notification)
        return notification
    }


    fun pause() {
        player?.pause()
    }

    fun play() {
        player?.play()
    }



    override fun onDestroy() {
        super.onDestroy()
        playerNotificationManager.setPlayer(null)
        player?.release()
        player = null
    }
}

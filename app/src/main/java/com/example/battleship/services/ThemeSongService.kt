package com.example.battleship.services

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.example.battleship.R

class ThemeSongService : Service(){
    private var themeSong : MediaPlayer? = null
    private var transitionSound : MediaPlayer? = null


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        Log.i("service","creation")
        themeSong = MediaPlayer.create(this, R.raw.call_of_dutty_theme)
        themeSong?.isLooping = true
        transitionSound = MediaPlayer.create(this, R.raw.sea_wave2)
        transitionSound?.setOnCompletionListener {
            stopSelf()
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.i("service","starting")
        var length = 0
        if (intent!!.getBooleanExtra("startService",false)) {
            if(themeSong?.isPlaying==false){
                themeSong?.start()
            }
        }
        if(intent!!.getBooleanExtra("transition",false)){
            themeSong?.stop()
            transitionSound?.start()
            return startId
        }
        length=themeSong!!.currentPosition
        if(intent!!.getStringExtra("musicState") == "pause") {
            themeSong?.pause()
        }
        else if (intent!!.getStringExtra("musicState") == "play") {
            themeSong?.seekTo(length)
            themeSong?.start()
        }
        else if(themeSong?.isPlaying==false) themeSong?.start()
        return startId
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("service","stopped")
        if(themeSong!!.isPlaying)
            themeSong?.stop()
        if(transitionSound!!.isPlaying)
            transitionSound?.stop()
        themeSong?.release()
        transitionSound?.release()
    }
}
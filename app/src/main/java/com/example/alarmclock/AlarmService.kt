package com.example.alarmclock

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder

class AlarmService : Service() {

       private lateinit var mediaPlayer: MediaPlayer
       override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
            if (intent?.action == "STOP_ALARM") {
                stopAlarm()
                stopSelf()
                return START_NOT_STICKY

            }
            mediaPlayer = MediaPlayer.create(this, R.raw.app_src_main_res_raw_alarm)
            mediaPlayer.isLooping = true
            mediaPlayer.start()
            return START_STICKY
        }

       private fun stopAlarm() {
            if (::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
                mediaPlayer.stop()
                mediaPlayer.release()

            }
        }

        override fun onDestroy() {
            stopAlarm()
            super.onDestroy()
        }

        override fun onBind(intent: Intent): IBinder? {
            return null

        }
    }

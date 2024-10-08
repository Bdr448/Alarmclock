package com.example.alarmclock
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.icu.text.DateFormat
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {
    private lateinit var alarmSetCard: View
    private lateinit var alarmSetText: TextView
    private lateinit var cancelAlarmButton: Button
    private lateinit var realTimeClock: TextView
    private lateinit var alarmTimeDisplay: TextView

    private val handler = Handler(Looper.getMainLooper())
    private val timeFormat = SimpleDateFormat("hh:mm:ss a MMM, dd yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }
        val createAlarmButton: Button = findViewById(R.id.create_alarm)
        alarmSetCard  = findViewById(R.id.alarmSetCard_2)
        alarmSetText = findViewById(R.id.alarmSetText)
        cancelAlarmButton = findViewById(R.id.cancel_button_alarm)
        realTimeClock = findViewById(R.id.current_time)
        alarmTimeDisplay = findViewById(R.id.alarmTimeDisplay)
        alarmSetCard.visibility = View.GONE

        updateClock()
        createAlarmButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
            val currentMinute = calendar.get(Calendar.MINUTE)

            TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                calendar.set(Calendar.MINUTE, selectedMinute)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)

                if (calendar.before(Calendar.getInstance())) {
                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                }
                setAlarm(calendar.timeInMillis)

                val alarmTimeText = SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(calendar.time)
                alarmSetText.text = "Alarm Set time"
                alarmTimeDisplay.text = alarmTimeText
                alarmSetCard.visibility = View.VISIBLE
            }, currentHour, currentMinute, false).show()
        }
        cancelAlarmButton.setOnClickListener {
            cancelAlarm()
            Toast.makeText(this, "Alarm Cancelled", Toast.LENGTH_SHORT).show()
            alarmSetCard.visibility = View.GONE
        }
    }
    private fun updateClock() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                realTimeClock.text = timeFormat.format(Date())
                handler.postDelayed(this, 1000)
            }
        }, 10)
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun setAlarm(timeInMillis: Long) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
    }

    private fun cancelAlarm() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        alarmManager.cancel(pendingIntent)
    }
}

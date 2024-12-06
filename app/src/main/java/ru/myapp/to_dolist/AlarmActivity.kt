package ru.myapp.to_dolist

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AlarmActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)

        val todoName = intent.getStringExtra("TODO_NAME") ?: "Дело"

        val messageView: TextView = findViewById(R.id.message)
        val stopButton: Button = findViewById(R.id.stopButton)

        messageView.text = "Пора сделать: $todoName"

        stopButton.setOnClickListener {
            AlarmReceiver.ringtone?.stop()
            finish() // Закрываем активность
        }
    }
}
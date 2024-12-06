package ru.myapp.to_dolist

import android.app.Dialog
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId", "WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "TODO_CHANNEL",
                "Напоминания",
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        val listView = findViewById<ListView>(R.id.listView)
        val user: EditText = findViewById(R.id.user_name)
        val button: Button = findViewById(R.id.button)

        val todos: MutableList<Pair<String, Calendar?>> = mutableListOf()
        val adapter = TodoAdapter(this, todos)
        listView.adapter = adapter

        listView.setOnItemLongClickListener { _, _, i, _ ->
            val text = todos[i].first
            todos.removeAt(i)
            updateAdapter(adapter, todos)

            Toast.makeText(applicationContext, "Удален $text", Toast.LENGTH_SHORT).show()
            true
        }

        button.setOnClickListener {
            val text = user.text.toString().trim()
            if (text != "") {
                todos.add(Pair(text, null))
                updateAdapter(adapter, todos)
            }
            user.text.clear()
        }

        listView.setOnItemClickListener { _, _, i, _ ->
            val todo = todos[i]

            showCustomTimePickerDialog(todo.first) { selectedHour, selectedMinute ->
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                calendar.set(Calendar.MINUTE, selectedMinute)

                todos[i] = Pair(todo.first, calendar)
                updateAdapter(adapter, todos)

                Toast.makeText(
                    this,
                    "Время установлено для \"${todo.first}\": $selectedHour:$selectedMinute",
                    Toast.LENGTH_SHORT
                ).show()

                setAlarm(todo.first, calendar)
            }
        }
    }

    private fun updateAdapter(
        adapter: TodoAdapter,
        todos: MutableList<Pair<String, Calendar?>>
    ) {
        adapter.notifyDataSetChanged()
    }

    fun showCustomTimePickerDialog(todoName: String, onTimeSet: (hour: Int, minute: Int) -> Unit) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.custom_time_picker_dialog)

        val timePicker: TimePicker = dialog.findViewById(R.id.timePicker)
        timePicker.setIs24HourView(true)
        timePicker.hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        timePicker.minute = Calendar.getInstance().get(Calendar.MINUTE)

        val confirmButton: Button = dialog.findViewById(R.id.confirmButton)
        confirmButton.setOnClickListener {
            val selectedHour = timePicker.hour
            val selectedMinute = timePicker.minute
            onTimeSet(selectedHour, selectedMinute)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun setAlarm(todo: String, calendar: Calendar) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java).apply {
            putExtra("TODO_NAME", todo)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            todo.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }
}
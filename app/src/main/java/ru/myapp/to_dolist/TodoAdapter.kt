package ru.myapp.to_dolist

import android.content.Context
import android.icu.util.Calendar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class TodoAdapter(context: Context, private val todos: MutableList<Pair<String, Calendar?>>) :
    ArrayAdapter<Pair<String, Calendar?>>(context, 0, todos) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)

        val todo = todos[position]

        val textView1: TextView = view.findViewById(android.R.id.text1)
        val textView2: TextView = view.findViewById(android.R.id.text2)

        textView1.text = todo.first

        textView2.text = if (todo.second != null) {
            val calendar = todo.second
            val hour = String.format("%02d", calendar?.get(Calendar.HOUR_OF_DAY) ?: 0)
            val minute = String.format("%02d", calendar?.get(Calendar.MINUTE) ?: 0)
            "$hour:$minute"
        } else {
            "Не установлено"
        }

        return view
    }
}

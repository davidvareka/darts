package ladislav.sevcuj.endlessdarts

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
fun Calendar.asDatetimeString(): String {
    return SimpleDateFormat("dd.MM.yyyy HH:mm").format(time)
}

@SuppressLint("SimpleDateFormat")
fun Calendar.asTimeString(): String {
    return SimpleDateFormat("HH:mm").format(time)
}

object DateInstance {
    fun now(): Calendar {
        return Calendar.getInstance(Locale.getDefault())
    }

    fun fromTimestamp(timestamp: Long): Calendar? {
        if (timestamp > 0) {
            val calendar = Calendar.getInstance(Locale.getDefault())
            calendar.timeInMillis = timestamp

            return calendar
        }

        return null
    }
}
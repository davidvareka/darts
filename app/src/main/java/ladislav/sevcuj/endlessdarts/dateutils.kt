package ladislav.sevcuj.endlessdarts

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
fun Calendar.asDateString(): String {
    return SimpleDateFormat("dd.MM.yyyy").format(time)
}

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

    fun fromString(string: String): Calendar {
        val cal = Calendar.getInstance()

        val format = if (string.length == 10) {
            "dd.MM.yyyy"
        } else {
            "dd.MM.yyyy HH:mm"
        }

        val sdf = SimpleDateFormat(format, Locale.getDefault())
        cal.time = sdf.parse(string)

        return cal
    }
}
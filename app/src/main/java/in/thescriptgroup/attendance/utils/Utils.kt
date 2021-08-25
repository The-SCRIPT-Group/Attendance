package `in`.thescriptgroup.attendance.utils

import `in`.thescriptgroup.attendance.R
import android.content.Context
import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.*

object Utils {

    fun updateTime(editor: SharedPreferences.Editor, context: Context) {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US)
        val time = Calendar.getInstance().time
        val timestamp = simpleDateFormat.format(time)
        editor.putString(context.getString(R.string.timestamp_key), timestamp).commit()
    }

    fun average(a: Int, b: Int): Double = (a.toDouble() / b.toDouble()) * 100

    fun bunkIt(present_: Int, total_: Int, desired: Int, context: Context, value: String): String {
        var count = 0
        val attendance = present_ * 100 / total_
        var present = present_
        var total: Int = total_

        when {
            attendance < desired -> {
                while (present * 100 / total < desired) {
                    present++
                    total++
                    count++
                }
                if (count != 0)
                    return context.getString(R.string.attend, count, value)
            }
            attendance > desired -> {
                total++
                while (present * 100 / total >= desired) {
                    total++
                    count++
                }
                if (count != 0)
                    return context.getString(R.string.bunk, count, value)
            }
        }
        return context.getString(R.string.living_on_the_edge, value)
    }
}
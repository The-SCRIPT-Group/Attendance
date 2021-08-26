package `in`.thescriptgroup.attendance.utils

import `in`.thescriptgroup.attendance.R
import `in`.thescriptgroup.attendance.models.Subject
import android.content.Context
import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.*

object Utils {

    fun updateTime(editor: SharedPreferences.Editor) {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US)
        val time = Calendar.getInstance().time
        val timestamp = simpleDateFormat.format(time)
        editor.putString(Constants.timestamp_key, timestamp).commit()
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

    fun totalCard(subjects: MutableList<Subject>?): MutableList<Subject>? {
        if (subjects != null) {
            var th_present = 0
            var th_total = 0
            var pr_present = 0
            var pr_total = 0
            var tu_present = 0
            var tu_total = 0
            var in_present = 0
            var in_total = 0
            for (subject in subjects) {
                th_present += subject.th_present
                th_total += subject.th_total
                pr_present += subject.pr_present
                pr_total += subject.pr_total
                tu_present += subject.tu_present
                tu_total += subject.tu_total
                in_present += subject.in_present
                in_total += subject.in_total
            }
            subjects.add(
                Subject(
                    "Total",
                    th_present,
                    th_total,
                    pr_present,
                    pr_total,
                    tu_present,
                    tu_total,
                    in_present,
                    in_total
                )
            )
        }
        return subjects
    }
}
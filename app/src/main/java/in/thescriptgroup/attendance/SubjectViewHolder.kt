package `in`.thescriptgroup.attendance

import `in`.thescriptgroup.attendance.models.Subject
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs
import java.util.*

class SubjectViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.list_item, parent, false)) {
    private var subjectView: TextView? = null
    private var theoryView: TextView? = null
    private var pracView: TextView? = null


    init {
        subjectView = itemView.findViewById(R.id.subject_name)
        theoryView = itemView.findViewById(R.id.theory_perc)
        pracView = itemView.findViewById(R.id.prac_perc)
    }

    @SuppressLint("SetTextI18n")
    fun bind(subject: Subject) {
        subjectView?.text = subject.name
        if (subject.th_total != 0)
            theoryView?.text = "Theory: ${String.format(
                "%.2f",
                (subject.th_present / subject.th_total.toDouble()) * 100
            )}%"
        var pracs = ""
        if (subject.pr_total != 0)
            pracs = "Practical: ${String.format(
                "%.2f",
                (subject.pr_present / subject.pr_total.toDouble()) * 100
            )}%\n"
        if (subject.tu_total != 0)
            pracs += "Tutorial: ${String.format(
                "%.2f",
                (subject.tu_present / subject.tu_total.toDouble()) * 100
            )}%"
        pracView?.text = pracs
    }
}

class ListAdapter(private val list: ArrayList<Subject>) :
    RecyclerView.Adapter<SubjectViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return SubjectViewHolder(inflater, parent)
    }

    private fun getSubjectDetails(view: View, subject: Subject): String {
        var message = ""

        if (subject.name == "Total") {
            val (total_present, total_total) = subject.getTotal()
            message += "Total Attendance:  $total_present / $total_total ( ${String.format(
                "%.2f",
                (total_present / total_total.toDouble()) * 100
            )}% )\n\n"
        }

        message += "Attended :-\n"

        if (subject.th_total != 0) {
            message += "\t\tTheory: ${subject.th_present} / ${subject.th_total} ( ${String.format(
                "%.2f",
                (subject.th_present / subject.th_total.toDouble()) * 100
            )}% )\n"
        }
        if (subject.pr_total != 0) {
            message += "\t\tPractical: ${subject.pr_present} / ${subject.pr_total} ( ${String.format(
                "%.2f",
                (subject.pr_present / subject.pr_total.toDouble()) * 100
            )}% )\n"
        }
        if (subject.tu_total != 0) {
            message += "\t\tTutorial: ${subject.tu_present} / ${subject.tu_total} ( ${String.format(
                "%.2f",
                (subject.tu_present / subject.tu_total.toDouble()) * 100
            )}% )\n"
        }
        message += "\n\n"

        val sharedPref: SharedPreferences = view.context.getSharedPreferences(
            view.context.getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )
        val desired =
            sharedPref.getInt(view.context.getString(R.string.desired_attendance_key), 75)
        val data = subject.calculateLectures(desired)
        for (key in data.keys) {
            message += "You " +
                    when {
                        data[key]!! < 0 -> "need to attend ${abs(data[key]!!)}"
                        data[key]!! > 0 -> "can bunk ${data[key]}"
                        else -> "cannot bunk any"
                    } + " $key \n"
        }
        return message
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        holder.bind(list[position])

        holder.itemView.setOnClickListener { view ->
            val subject = list[position]
            val customDialog = AlertDialog.Builder(view.context)
            val message = getSubjectDetails(view, subject)
            customDialog
                .setMessage(message)
                .setNeutralButton("Dismiss") { dialog, _ ->
                    dialog.cancel()
                }
            val dialog = customDialog.create()
            dialog.setTitle(subject.name)
            dialog.show()
        }
    }

    override fun getItemCount(): Int = list.size
}
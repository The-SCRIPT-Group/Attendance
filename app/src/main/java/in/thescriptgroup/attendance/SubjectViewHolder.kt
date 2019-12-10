package `in`.thescriptgroup.attendance

import `in`.thescriptgroup.attendance.models.Subject
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

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

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size
}
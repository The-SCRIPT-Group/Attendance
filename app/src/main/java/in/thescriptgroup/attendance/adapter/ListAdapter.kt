package `in`.thescriptgroup.attendance.adapter

import `in`.thescriptgroup.attendance.R
import `in`.thescriptgroup.attendance.databinding.ListItemBinding
import `in`.thescriptgroup.attendance.models.Subject
import `in`.thescriptgroup.attendance.utils.Utils
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import javax.inject.Inject

class ListAdapter @Inject constructor(private val preferences: SharedPreferences) :
    RecyclerView.Adapter<ListAdapter.SubjectViewHolder>() {

    private var list: List<Subject> = listOf()

    class SubjectViewHolder(private val binding: ListItemBinding, private val context: Context) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(subject: Subject, desiredAttendance: Int) {
            binding.subjectName.text = subject.name

            if (!subject.isExpanded) {
                hideExpandedItems()
            }

            itemView.setOnClickListener {
                if (subject.isExpanded) {
                    subject.isExpanded = false
                    hideExpandedItems()
                } else {
                    subject.isExpanded = true
                    binding.attended.visibility = View.VISIBLE
                    if (subject.th_total != 0) {
                        binding.attendedTheory.visibility = View.VISIBLE
                        binding.bunkTheory.visibility = View.VISIBLE
                    }
                    if (subject.pr_total != 0) {
                        binding.attendedPractical.visibility = View.VISIBLE
                        binding.bunkPractical.visibility = View.VISIBLE
                    }
                    if (subject.tu_total != 0) {
                        binding.attendedTutorial.visibility = View.VISIBLE
                        binding.bunkTutorial.visibility = View.VISIBLE
                    }
                    if (subject.in_total != 0) {
                        binding.attendedInternship.visibility = View.VISIBLE
                    }
                }
            }

            if (subject.in_total != 0) {
                binding.intPerc.text = context.getString(
                    R.string.internship, Utils.average(subject.in_present, subject.th_total)
                )
                binding.attendedInternship.text = context.getString(
                    R.string.attended_internship, subject.in_present, subject.in_total
                )
                binding.intPerc.visibility = View.VISIBLE
            } else {
                binding.intPerc.visibility = View.GONE
            }

            if (subject.th_total != 0) {
                binding.theoryPerc.text = context.getString(
                    R.string.theory, Utils.average(subject.th_present, subject.th_total)
                )
                binding.attendedTheory.text = context.getString(
                    R.string.attended_theory, subject.th_present, subject.th_total
                )
                binding.bunkTheory.text =
                    Utils.bunkIt(
                        subject.th_present,
                        subject.th_total,
                        desiredAttendance,
                        context,
                        context.getString(R.string.theory_name)
                    )
                binding.theoryPerc.visibility = View.VISIBLE
            } else {
                binding.theoryPerc.visibility = View.GONE
            }

            if (subject.pr_total != 0) {
                binding.pracPerc.text = context.getString(
                    R.string.practical, Utils.average(subject.pr_present, subject.pr_total)
                )
                binding.attendedPractical.text = context.getString(
                    R.string.attended_practical, subject.pr_present, subject.pr_total
                )
                binding.bunkPractical.text =
                    Utils.bunkIt(
                        subject.pr_present,
                        subject.pr_total,
                        desiredAttendance,
                        context,
                        context.getString(R.string.practical_name)
                    )
                binding.pracPerc.visibility = View.VISIBLE
            } else {
                binding.pracPerc.visibility = View.GONE
            }

            if (subject.tu_total != 0) {
                binding.tutPerc.text = context.getString(
                    R.string.tutorial, Utils.average(subject.tu_present, subject.tu_total)
                )
                binding.attendedTutorial.text = context.getString(
                    R.string.attended_tutorial, subject.tu_present, subject.tu_total
                )
                binding.bunkTutorial.text =
                    Utils.bunkIt(
                        subject.tu_present,
                        subject.tu_total,
                        desiredAttendance,
                        context,
                        context.getString(R.string.tutorial_name)
                    )
                binding.tutPerc.visibility = View.VISIBLE
            } else {
                binding.tutPerc.visibility = View.GONE
            }
        }

        private fun hideExpandedItems() {
            binding.attended.visibility = View.GONE
            binding.attendedTheory.visibility = View.GONE
            binding.attendedPractical.visibility = View.GONE
            binding.attendedTutorial.visibility = View.GONE
            binding.attendedInternship.visibility = View.GONE
            binding.bunkTheory.visibility = View.GONE
            binding.bunkPractical.visibility = View.GONE
            binding.bunkTutorial.visibility = View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemBinding.inflate(inflater, parent, false)
        return SubjectViewHolder(binding, parent.context)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val desiredAttendance =
            preferences.getInt(
                holder.itemView.context.getString(R.string.desired_attendance_key),
                75
            )
        holder.bind(list[position], desiredAttendance)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(attendance: List<Subject>) {
        list = attendance
        notifyDataSetChanged()
    }
}

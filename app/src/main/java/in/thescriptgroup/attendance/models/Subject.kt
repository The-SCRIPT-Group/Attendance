package `in`.thescriptgroup.attendance.models

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Subject(
    @SerializedName("subject")
    var name: String,
    @SerializedName("th_present")
    var th_present: Int,
    @SerializedName("th_total")
    var th_total: Int,

    var pr_present: Int,
    var pr_total: Int,
    var tu_present: Int,
    var tu_total: Int
) {
    constructor(name: String) : this(name, 0, 0, 0, 0, 0, 0)

    operator fun plus(subject: Subject) {
        this.th_present += subject.th_present
        this.th_total += subject.th_total
        this.pr_present += subject.pr_present
        this.pr_total += subject.pr_total
        this.tu_present += subject.tu_present
        this.tu_total += subject.tu_total
    }
}

class SubjectList : ArrayList<Subject>() {}
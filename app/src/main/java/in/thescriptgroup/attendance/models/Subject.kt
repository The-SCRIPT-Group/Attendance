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
    var tu_total: Int,

    val response: String
) {
    constructor(name: String) : this(name, 0, 0, 0, 0, 0, 0, "")

    operator fun plus(subject: Subject) {
        this.th_present += subject.th_present
        this.th_total += subject.th_total
        this.pr_present += subject.pr_present
        this.pr_total += subject.pr_total
        this.tu_present += subject.tu_present
        this.tu_total += subject.tu_total
    }

    fun getTotal(): Pair<Int, Int> {
        return Pair(
            this.th_present + this.pr_present + this.tu_present,
            this.th_total + this.pr_total + this.tu_total
        )
    }

    fun calculateLectures(desired: Int): HashMap<String, Int> {
        val data: HashMap<String, Int> = hashMapOf()

        if (this.th_total != 0) {
            data["Lectures"] =
                getLectureCount(this.th_present, this.th_total, desired)
        }
        if (this.pr_total != 0) {
            data["Practicals"] =
                getLectureCount(this.pr_present, this.pr_total, desired)
        }
        if (this.tu_total != 0) {
            data["Tutorials"] =
                getLectureCount(this.tu_present, this.tu_total, desired)
        }
        return data
    }

    private fun getLectureCount(
        present: Int,
        total: Int,
        desired: Int
    ): Int {
        var count: Int
        val lessAttendance: Boolean = present * 100 / total < desired
        var present: Int = present
        var total: Int = total

        if (lessAttendance) {
            count = 0
            while (present * 100 / total < desired) {
                present++
                total++
                count++
            }
        } else {
            count = -1
            while (present * 100 / total > desired) {
                total++
                count++
            }
        }
        return if (lessAttendance) -count else count
    }
}

class SubjectList : ArrayList<Subject>()
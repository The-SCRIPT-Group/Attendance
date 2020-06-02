package `in`.thescriptgroup.attendance.models


import com.squareup.moshi.Json
import kotlinx.serialization.Serializable

@Serializable
data class Subject(
    @Json(name = "subject")
    var name: String,

    var th_present: Int,
    var th_total: Int,

    var pr_present: Int,
    var pr_total: Int,

    var tu_present: Int,
    var tu_total: Int,

    var in_present: Int,
    var in_total: Int,

    val response: String
) {
    constructor(name: String) : this(name, 0, 0, 0, 0, 0, 0, 0, 0, "")

    operator fun plus(subject: Subject) {
        this.th_present += subject.th_present
        this.th_total += subject.th_total
        this.pr_present += subject.pr_present
        this.pr_total += subject.pr_total
        this.tu_present += subject.tu_present
        this.in_present += subject.in_present
        this.in_total += subject.in_total
        this.tu_total += subject.tu_total
    }

    fun getTotal(): Pair<Int, Int> {
        return Pair(
            this.th_present + this.pr_present + this.tu_present + this.in_present,
            this.th_total + this.pr_total + this.tu_total + this.in_total
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
        if (this.in_total != 0) {
            data["Internship"] =
                getLectureCount(this.in_present, this.in_total, desired)
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

@Serializable
data class SubjectList(var subjectList: List<Subject>) : ArrayList<Subject>() {

    fun listToArrayList(): SubjectList {
        val ret = arrayListOf<Subject>()
        subjectList.forEach {
            ret.add(it)
        }
        return SubjectList(ret)
    }
}
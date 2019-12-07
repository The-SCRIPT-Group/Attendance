package `in`.thescriptgroup.attendance.models

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Subject(
    @SerializedName("subject")
    val name: String,
    @SerializedName("th_present")
    val th_present: Int,
    @SerializedName("th_total")
    val th_total: Int,

    val pr_present: Int,
    val pr_total: Int,
    val tu_present: Int,
    val tu_total: Int
)

class SubjectList : ArrayList<Subject>() {}
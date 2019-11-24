package `in`.thescriptgroup.attendance.models

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Subject(
    @SerializedName("subject")
    val name: String,
    val type: String,
    val present: Int,
    val total: Int
) {
    val missed: Int = total - present
}
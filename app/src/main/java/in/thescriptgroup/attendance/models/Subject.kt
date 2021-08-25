package `in`.thescriptgroup.attendance.models

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Keep
data class Subject(
    @Json(name = "subject") var name: String = "",
    val th_present: Int = 0,
    val th_total: Int = 0,
    val pr_present: Int = 0,
    val pr_total: Int = 0,
    val tu_present: Int = 0,
    val tu_total: Int = 0,
    val in_present: Int = 0,
    val in_total: Int = 0,
    val response: String = "",
    @Transient var isExpanded: Boolean = false
)
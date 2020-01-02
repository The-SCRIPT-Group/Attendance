package `in`.thescriptgroup.attendance.models

import kotlinx.serialization.Serializable

@Serializable
data class ErrorMessage(
    var response: String
) {
    constructor(response: List<Subject>?) : this(response.toString())
}

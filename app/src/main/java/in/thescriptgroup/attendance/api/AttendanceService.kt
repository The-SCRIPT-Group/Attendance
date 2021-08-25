package `in`.thescriptgroup.attendance.api

import `in`.thescriptgroup.attendance.models.Subject
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AttendanceService {
    @FormUrlEncoded
    @POST("attendance")
    suspend fun getAttendance(
        @Field("username") username: String,
        @Field("password") password: String
    ): List<Subject>

    object ApiClient {
        const val BASE_URL: String = "https://tsg-poseidon.herokuapp.com/api/"
    }
}

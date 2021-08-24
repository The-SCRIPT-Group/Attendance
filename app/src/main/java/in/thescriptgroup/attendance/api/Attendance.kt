package `in`.thescriptgroup.attendance.api

import `in`.thescriptgroup.attendance.models.Subject
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

object ApiClient {
    const val BASE_URL: String = "https://tsg-poseidon.herokuapp.com/api/"
}

interface Attendance {
    @FormUrlEncoded
    @POST("attendance")
    fun getAttendance(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<List<Subject>>
}

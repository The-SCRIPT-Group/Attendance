package `in`.thescriptgroup.attendance

import `in`.thescriptgroup.attendance.models.Subject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

object ApiClient {
    private const val BASE_URL = "https://tsg-erp-api.herokuapp.com/api/"
    private var retrofit: Retrofit? = null

    val client: Retrofit
        get() {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            // If retrofit is null we build the object above
            // so non-null assertion is fine.
            return retrofit!!
        }
}

interface Attendance {
    @FormUrlEncoded
    @POST("attendance")
    fun getAttendance(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<List<Subject>>
}
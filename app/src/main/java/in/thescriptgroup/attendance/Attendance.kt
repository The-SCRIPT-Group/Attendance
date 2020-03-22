package `in`.thescriptgroup.attendance

import `in`.thescriptgroup.attendance.models.Subject
import android.util.Log
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

object ApiClient {
    private val BASE_URL = arrayOf(
        "h",
        "t",
        "t",
        "p",
        "s",
        ":",
        "/",
        "/",
        "p",
        "o",
        "s",
        "e",
        "i",
        "d",
        "o",
        "n",
        ".",
        "t",
        "h",
        "e",
        "s",
        "c",
        "r",
        "i",
        "p",
        "t",
        "g",
        "r",
        "o",
        "u",
        "p",
        ".",
        "i",
        "n"
    )
    private var retrofit: Retrofit? = null

    val client: Retrofit
        get() {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL.joinToString(""))
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).build())
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

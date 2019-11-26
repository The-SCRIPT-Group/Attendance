package `in`.thescriptgroup.attendance

import `in`.thescriptgroup.attendance.models.Subject
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_attendance.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class AttendanceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance)
        title = "Attendance"
        val username = intent.getStringExtra("username")!!
        val password = intent.getStringExtra("password")!!
        val call = ApiClient.client.create(Attendance::class.java).getAttendance(username, password)
        call.enqueue(object : Callback<List<Subject>> {
            override fun onResponse(call: Call<List<Subject>>, response: Response<List<Subject>>) {
                Objects.requireNonNull<List<Subject>>(response.body(), "Response body is null")
                val attendanceData: List<Subject> = response.body()!!
                attendanceData.forEach {
                    try {
                        attendanceView.append("${it.type} - ${it.name} \t ${String.format("%.2f", it.percent)}%\n")
                    } catch (e: ArithmeticException) {
                    }
                }
            }

            override fun onFailure(call: Call<List<Subject>>, t: Throwable) {
                Log.v("onFailure", t.message!!)
                Toast.makeText(this@AttendanceActivity, "Error occurred!", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }
}

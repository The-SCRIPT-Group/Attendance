package `in`.thescriptgroup.attendance

import `in`.thescriptgroup.attendance.models.Subject
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class LoginActivity : AppCompatActivity() {

    val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newlogin)

        val sharedPref = this.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )

        //val progressBar:ProgressBar = this.progressBar1

        submit.setOnClickListener {

            Toast.makeText(this, "Verifying credentials!", Toast.LENGTH_SHORT).show()
            val username = username_input.text.toString()
            val password = password_input.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter the details!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!(username[0] in "Ss" && username.length == 11)) {
                Toast.makeText(this, "Invalid ID!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

           // this@LoginActivity.runOnUiThread(java.lang.Runnable { progressBar1.visibility=View.VISIBLE })
            //this@LoginActivity.progressBar1.visibility=View.VISIBLE

            submit.isEnabled = false
            val call =
                ApiClient.client.create(Attendance::class.java)
                    .getAttendance(username, password)
            call.enqueue(object : Callback<List<Subject>> {
                override fun onResponse(
                    call: Call<List<Subject>>,
                    response: Response<List<Subject>>
                ) {
                    Objects.requireNonNull<List<Subject>>(
                        response.body(),
                        "Response body is null"
                    )
                    val attendanceData: List<Subject> = response.body()!!
                    val err: String? = attendanceData[0].response
                    if (err != null) {
                        Toast.makeText(
                            this@LoginActivity,
                            err,
                            Toast.LENGTH_SHORT
                        ).show()
                        password_input.text.clear()
                        submit.isEnabled = true
                        return
                    }
                    val attendanceStr = gson.toJson(attendanceData)
                    val timestamp = Calendar.getInstance().time.toString()
                    with(sharedPref.edit()) {
                        putString(getString(R.string.username_key), username)
                        putString(getString(R.string.password_key), password)
                        putString(getString(R.string.attendance_key), attendanceStr)
                        putString(getString(R.string.timestamp_key), timestamp)
                        commit()
                    }
                    val intent = Intent(this@LoginActivity, AttendanceActivity::class.java)
                    intent.putExtra("username", username)
                    intent.putExtra("password", password)
                    finish()
                    startActivity(intent)
                }

                override fun onFailure(call: Call<List<Subject>>, t: Throwable) {
                    Log.v("onFailure", t.message!!)
                    if (t.message == "timeout") {
                        Toast.makeText(
                            this@LoginActivity,
                            "Connection timed out!",
                            Toast.LENGTH_SHORT
                        ).show()
                        submit.isEnabled = true
                    }
                }
            })
           // this@LoginActivity.runOnUiThread(java.lang.Runnable { progressBar1.visibility=View.GONE })
            //this@LoginActivity.progressBar1.visibility=View.GONE
        }
    }
}

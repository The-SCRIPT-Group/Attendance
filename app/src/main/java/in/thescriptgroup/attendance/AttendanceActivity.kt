package `in`.thescriptgroup.attendance

import `in`.thescriptgroup.attendance.models.Subject
import `in`.thescriptgroup.attendance.models.SubjectList
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_attendance.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class AttendanceActivity : AppCompatActivity() {

    lateinit var sharedPref: SharedPreferences

    val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance)

        // val context: Context = this

        title = "Attendance"
        sharedPref = this.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )
        Toast.makeText(this, "Click on the button to check attendance!", Toast.LENGTH_SHORT).show()
        progress.visibility = View.GONE
        updateAttendance()
        attendanceButton.setOnClickListener {
            progress.visibility = View.VISIBLE
            val username = intent.getStringExtra("username")!!
            val password = intent.getStringExtra("password")!!
            val call =
                ApiClient.client.create(Attendance::class.java).getAttendance(username, password)

            call.enqueue(object : Callback<List<Subject>> {
                override fun onResponse(
                    call: Call<List<Subject>>,
                    response: Response<List<Subject>>
                ) {
                    Objects.requireNonNull<List<Subject>>(response.body(), "Response body is null")
                    val attendanceData: List<Subject> = response.body()!!
                    Log.v("Stuff", response.body().toString())
                    val attendanceStr = gson.toJson(attendanceData)
                    val timestamp = Calendar.getInstance().time.toString()
                    progress.visibility = View.GONE
                    with(sharedPref.edit()) {
                        putString(getString(R.string.attendance_key), attendanceStr)
                        putString(getString(R.string.timestamp_key), timestamp)
                        commit()
                    }
                    updateAttendance()
                }

                override fun onFailure(call: Call<List<Subject>>, t: Throwable) {
                    progress.visibility = View.GONE
                    Log.v("onFailure", t.message!!)
                }
            })
        }
    }

    fun updateAttendance() {
        val timestamp = sharedPref.getString(getString(R.string.timestamp_key), "")!!

        if (timestamp == "") return

        val attendanceStr = sharedPref.getString(getString(R.string.attendance_key), "")
        val attendance: ArrayList<Subject> =
            gson.fromJson(attendanceStr, SubjectList::class.java)

        var total: Subject? = null
        total?.name = "Total"
        attendance.forEach {
            total?.plus(it)
        }
        if (total != null) {
            attendance.add(total)
        }


        attendanceView.text = getString(R.string.last_checked, timestamp)
        val recyclerView = findViewById<RecyclerView>(R.id.attendanceRecycler)
        recyclerView.apply {
            this.layoutManager = LinearLayoutManager(context)
            this.adapter = ListAdapter(attendance)
        }

        recyclerView.affectOnItemClicks { position, _ ->
            val subject = attendance[position]
            val customDialog = AlertDialog.Builder(this)
            var message = "Attended :-\n"

            if (subject.th_total != 0) {
                message += "\tTheory: ${subject.th_present} / ${subject.th_total} ( ${String.format(
                    "%.2f",
                    (subject.th_present / subject.th_total.toDouble()) * 100
                )}% )\n"
            }
            if (subject.pr_total != 0) {
                message += "\tPractical: ${subject.pr_present} / ${subject.pr_total} ( ${String.format(
                    "%.2f",
                    (subject.pr_present / subject.pr_total.toDouble()) * 100
                )}% )\n"
            }
            if (subject.tu_total != 0) {
                message += "\tTutorial: ${subject.tu_present} / ${subject.tu_total} ( ${String.format(
                    "%.2f",
                    (subject.tu_present / subject.tu_total.toDouble()) * 100
                )}% )\n"
            }

            customDialog
                .setMessage(message)
                .setNeutralButton("Dismiss") { dialog, _ ->
                    dialog.cancel()
                }
                .setPositiveButton("Calculate Lectures") { dialog, _ ->

                }
            val dialog = customDialog.create()
            dialog.setTitle(subject.name)
            dialog.show()
        }
    }
}
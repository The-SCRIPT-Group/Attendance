package `in`.thescriptgroup.attendance

import `in`.thescriptgroup.attendance.models.Subject
import `in`.thescriptgroup.attendance.models.SubjectList
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_attendance.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class AttendanceActivity : AppCompatActivity() {

    lateinit var sharedPref: SharedPreferences

    val gson = Gson()

    lateinit var attendance: ArrayList<Subject>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(R.string.title_attendance)
        setContentView(R.layout.activity_attendance)

        sharedPref = this.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )
        val username = sharedPref.getString(getString(R.string.username_key), "")!!
        val password = sharedPref.getString(getString(R.string.password_key), "")!!
        if (username.isEmpty() || password.isEmpty()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        Toast.makeText(this, "Pull down to refresh attendance!", Toast.LENGTH_SHORT).show()
        updateAttendance(update = false)

        swipeContainer.setOnRefreshListener {

            val call =
                ApiClient.client.create(Attendance::class.java).getAttendance(username, password)

            call.enqueue(object : Callback<List<Subject>> {
                override fun onResponse(
                    call: Call<List<Subject>>,
                    response: Response<List<Subject>>
                ) {
                    Objects.requireNonNull<List<Subject>>(response.body(), "Response body is null")
                    val attendanceData: List<Subject> = response.body()!!
                    val err: String? = attendanceData[0].response
                    if (err != null) {
                        Toast.makeText(
                            this@AttendanceActivity,
                            err,
                            Toast.LENGTH_SHORT
                        ).show()
                        if (err == "Wrong credentials!") {
                            with(sharedPref.edit()) {
                                putString(getString(R.string.username_key), "")
                                putString(getString(R.string.password_key), "")
                                putString(getString(R.string.attendance_key), "")
                                putString(getString(R.string.timestamp_key), "")
                                commit()
                            }
                            startActivity(
                                Intent(
                                    this@AttendanceActivity,
                                    LoginActivity::class.java
                                )
                            )
                            finish()
                        }
                    } else {
                        val attendanceStr = gson.toJson(attendanceData)
                        val timestamp = Calendar.getInstance().time.toString()
                        with(sharedPref.edit()) {
                            putString(getString(R.string.attendance_key), attendanceStr)
                            putString(getString(R.string.timestamp_key), timestamp)
                            commit()
                        }
                        updateAttendance()
                    }
                    swipeContainer.isRefreshing = false
                }

                override fun onFailure(call: Call<List<Subject>>, t: Throwable) {
                    Log.v("onFailure", t.message!!)
                    if (t.message == "timeout") {
                        Toast.makeText(
                            this@AttendanceActivity,
                            "Connection timed out!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    swipeContainer.isRefreshing = false
                }
            })
        }
        swipeContainer.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.attendance_settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // The back arrow in the action bar should act the same as the back button.
                onBackPressed()
                true
            }
            R.id.menu_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            R.id.refresh -> {
                updateAttendance()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    fun updateAttendance(update: Boolean = true) {
        val timestamp = sharedPref.getString(getString(R.string.timestamp_key), "")!!

        if (timestamp == "") return

        val attendanceStr = sharedPref.getString(getString(R.string.attendance_key), "")
        attendance = gson.fromJson(attendanceStr, SubjectList::class.java)

        val total = Subject(name = "Total")
        attendance.forEach {
            total.plus(it)
        }
        attendance.add(total)

        if (update) {
            Toast.makeText(this@AttendanceActivity, "Updated attendance!", Toast.LENGTH_SHORT)
                .show()
        }

        supportActionBar?.subtitle = getString(R.string.last_checked, timestamp)

        (attendanceRecycler.getItemAnimator() as SimpleItemAnimator).supportsChangeAnimations =
            false
        attendanceRecycler.apply {
            this.layoutManager = LinearLayoutManager(context)
            this.adapter = ListAdapter(attendance)
            this.adapter?.notifyDataSetChanged()
        }
    }
}

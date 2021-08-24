package `in`.thescriptgroup.attendance

import `in`.thescriptgroup.attendance.databinding.ActivityAttendanceBinding
import `in`.thescriptgroup.attendance.models.Subject
import `in`.thescriptgroup.attendance.models.SubjectList
import `in`.thescriptgroup.attendance.utils.viewBinding
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class AttendanceActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityAttendanceBinding::inflate)

    lateinit var sharedPref: SharedPreferences

    val gson = Gson()

    lateinit var attendance: ArrayList<Subject>
    lateinit var username: String
    lateinit var password: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(R.string.title_attendance)
        val view = binding.root
        setContentView(view)
        sharedPref = this.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )
        username = sharedPref.getString(getString(R.string.username_key), "")!!
        password = sharedPref.getString(getString(R.string.password_key), "")!!
        if (username.isEmpty() || password.isEmpty()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        Toast.makeText(this, "Pull down to refresh attendance!", Toast.LENGTH_SHORT).show()
        updateAttendance(update = false)

        binding.swipeContainer.setOnRefreshListener {
            fetchAndUpdateAttendance()
        }

        binding.swipeContainer.setColorSchemeResources(
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
                fetchAndUpdateAttendance()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun fetchAndUpdateAttendance() {
        val call =
            ApiClient.client.create(Attendance::class.java).getAttendance(username, password)

        binding.swipeContainer.isRefreshing = true

        call.enqueue(object : Callback<List<Subject>> {
            override fun onResponse(
                call: Call<List<Subject>>,
                response: Response<List<Subject>>
            ) {
                if (response.body() == null) {
                    Toast.makeText(
                        this@AttendanceActivity,
                        "Error occurred fetching data from server!",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.swipeContainer.isRefreshing = false
                    return
                }
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
                    val simpleDateFormat = SimpleDateFormat(
                        "yyyy-mm-dd hh:mm:ss",
                        Locale.US
                    )
                    val time = Calendar.getInstance().time
                    val timestamp = simpleDateFormat.format(time)
                    with(sharedPref.edit()) {
                        putString(getString(R.string.attendance_key), attendanceStr)
                        putString(getString(R.string.timestamp_key), timestamp)
                        commit()
                    }
                    updateAttendance()
                }
                binding.swipeContainer.isRefreshing = false
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
                binding.swipeContainer.isRefreshing = false
            }
        })
    }


    fun updateAttendance(update: Boolean = true) {
        val timestamp = sharedPref.getString(getString(R.string.timestamp_key), "")!!

        if (timestamp == "") return

        val attendanceStr = sharedPref.getString(getString(R.string.attendance_key), "")
        attendance = gson.fromJson(attendanceStr, SubjectList::class.java)

        if (attendance.size > 1) {
            val total = Subject(name = "Total")
            attendance.forEach {
                total.plus(it)
            }
            attendance.add(total)
        }

        if (update) {
            Toast.makeText(this@AttendanceActivity, "Updated attendance!", Toast.LENGTH_SHORT)
                .show()
        }

        supportActionBar?.subtitle = getString(R.string.last_checked, timestamp)

        (binding.attendanceRecycler.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
            false
        binding.attendanceRecycler.apply {
            this.layoutManager = LinearLayoutManager(context)
            this.adapter = ListAdapter(attendance)
            this.adapter?.notifyDataSetChanged()
        }
    }
}

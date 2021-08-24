package `in`.thescriptgroup.attendance.fragment

import `in`.thescriptgroup.attendance.R
import `in`.thescriptgroup.attendance.adapter.ListAdapter
import `in`.thescriptgroup.attendance.api.ApiClient
import `in`.thescriptgroup.attendance.api.Attendance
import `in`.thescriptgroup.attendance.databinding.FragmentAttendanceBinding
import `in`.thescriptgroup.attendance.models.Subject
import `in`.thescriptgroup.attendance.models.SubjectList
import `in`.thescriptgroup.attendance.utils.viewBinding
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AttendanceFragment : Fragment(R.layout.fragment_attendance) {
    private val binding by viewBinding(FragmentAttendanceBinding::bind)

    @Inject
    lateinit var sharedPref: SharedPreferences

    val gson = Gson()

    lateinit var attendance: ArrayList<Subject>
    private lateinit var username: String
    private lateinit var password: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun init() {
        setHasOptionsMenu(true)

        username = sharedPref.getString(getString(R.string.username_key), "")!!
        password = sharedPref.getString(getString(R.string.password_key), "")!!

        Toast.makeText(context, "Pull down to refresh attendance!", Toast.LENGTH_SHORT).show()
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

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.attendance_settings, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // The back arrow in the action bar should act the same as the back button.
                requireActivity().finish()
                true
            }
            R.id.menu_settings -> {
                navigateToSettings()
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
                        context,
                        "Error occurred fetching data from server!",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.swipeContainer.isRefreshing = false
                    return
                }
                val attendanceData: List<Subject> = response.body()!!
                val err: String = attendanceData[0].response
                if (!err.isNullOrEmpty()) {
                    Toast.makeText(context, err, Toast.LENGTH_SHORT).show()

                    if (err == "Wrong credentials!") {
                        with(sharedPref.edit()) {
                            putString(getString(R.string.username_key), "")
                            putString(getString(R.string.password_key), "")
                            putString(getString(R.string.attendance_key), "")
                            putString(getString(R.string.timestamp_key), "")
                            commit()
                        }
                        navigateToLogin()
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
                        context,
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
            Toast.makeText(context, "Updated attendance!", Toast.LENGTH_SHORT)
                .show()
        }

        (activity as AppCompatActivity).supportActionBar?.subtitle =
            getString(R.string.last_checked, timestamp)

        (binding.attendanceRecycler.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
            false

        binding.attendanceRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ListAdapter(attendance)
        }
    }

    private fun navigateToLogin() {
        (activity as AppCompatActivity).supportActionBar?.subtitle = ""
        Navigation.findNavController(requireView()).navigate(R.id.loginFragment)
    }

    private fun navigateToSettings() {
        (activity as AppCompatActivity).supportActionBar?.subtitle = ""
        Navigation.findNavController(requireView()).navigate(R.id.settingsFragment)
    }
}
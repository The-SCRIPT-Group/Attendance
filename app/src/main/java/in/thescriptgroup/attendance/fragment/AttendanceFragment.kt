package `in`.thescriptgroup.attendance.fragment

import `in`.thescriptgroup.attendance.R
import `in`.thescriptgroup.attendance.adapter.ListAdapter
import `in`.thescriptgroup.attendance.databinding.FragmentAttendanceBinding
import `in`.thescriptgroup.attendance.models.Subject
import `in`.thescriptgroup.attendance.models.SubjectViewModel
import `in`.thescriptgroup.attendance.utils.Utils
import `in`.thescriptgroup.attendance.utils.viewBinding
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.moshi.JsonAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AttendanceFragment : Fragment(R.layout.fragment_attendance) {
    private val binding by viewBinding(FragmentAttendanceBinding::bind)
    private val viewModel: SubjectViewModel by activityViewModels()

    @Inject
    lateinit var sharedPref: SharedPreferences

    @Inject
    lateinit var adapter: JsonAdapter<List<Subject>>

    private var refreshing = false
    lateinit var attendance: List<Subject>

    private lateinit var username: String
    private lateinit var password: String

    private lateinit var listAdapter: ListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun init() {
        setHasOptionsMenu(true)

        username = sharedPref.getString(getString(R.string.username_key), "")!!
        password = sharedPref.getString(getString(R.string.password_key), "")!!

        Toast.makeText(context, getText(R.string.pull_down_refresh), Toast.LENGTH_SHORT).show()

        binding.swipeContainer.apply {
            setOnRefreshListener {
                this.isRefreshing = true
                refreshing = true
                viewModel.getSubject(username, password)
            }

            setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
            )
        }

        listAdapter = ListAdapter(requireContext())
        binding.attendanceRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = listAdapter
        }

        viewModel.getSubject(username, password)

        viewModel.apiData.observe(viewLifecycleOwner, {
            binding.swipeContainer.isRefreshing = false
            if (it != null) {
                if (it[0].response.isNotEmpty()) {
                    Toast.makeText(context, it[0].response, Toast.LENGTH_LONG).show()
                } else {
                    listAdapter.setList(it)
                    attendance = it
                    Utils.updateTime(sharedPref.edit(), requireContext())
                    updateAttendance()
                }
            } else {
                Toast.makeText(context, getText(R.string.interent_issue), Toast.LENGTH_LONG)
                    .show()
            }
        })

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
                binding.swipeContainer.isRefreshing = true
                viewModel.getSubject(username, password)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun updateAttendance() {
        val timestamp = sharedPref.getString(getString(R.string.timestamp_key), "")!!

        sharedPref.edit().putString(getString(R.string.attendance_key), adapter.toJson(attendance))
            .apply()

        (activity as AppCompatActivity).supportActionBar?.subtitle =
            getString(R.string.last_checked, timestamp)

        // (binding.attendanceRecycler.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
        //    false


    }

    private fun navigateToSettings() {
        (activity as AppCompatActivity).supportActionBar?.subtitle = ""
        Navigation.findNavController(requireView()).navigate(R.id.settingsFragment)
    }
}
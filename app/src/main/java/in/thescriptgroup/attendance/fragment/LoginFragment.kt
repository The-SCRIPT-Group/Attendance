package `in`.thescriptgroup.attendance.fragment

import `in`.thescriptgroup.attendance.R
import `in`.thescriptgroup.attendance.databinding.FragmentLoginBinding
import `in`.thescriptgroup.attendance.models.Subject
import `in`.thescriptgroup.attendance.models.SubjectViewModel
import `in`.thescriptgroup.attendance.utils.Constants
import `in`.thescriptgroup.attendance.utils.Utils
import `in`.thescriptgroup.attendance.utils.viewBinding
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.squareup.moshi.JsonAdapter
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {
    private val binding by viewBinding(FragmentLoginBinding::bind)

    @Inject
    lateinit var sharedPref: SharedPreferences

    @Inject
    lateinit var adapter: JsonAdapter<List<Subject>>

    private val viewModel: SubjectViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun init() {
        val sharedUsername = sharedPref.getString(Constants.username_key, null)
        val sharedPassword = sharedPref.getString(Constants.password_key, null)

        if (!sharedUsername.isNullOrEmpty() || !sharedPassword.isNullOrEmpty()) {
            navigateToAttendance()
            return
        }

        binding.submit.setOnClickListener {
            binding.submit.isEnabled = false
            val username = binding.usernameInput.text.toString()
            val password = binding.passwordInput.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, getText(R.string.details_missing), Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (!(username[0] in "Ss" && username.length == 11)) {
                Toast.makeText(context, getText(R.string.invalid_id), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.getSubject(username, password)

            viewModel.apiData.observe(viewLifecycleOwner, {
                if (it != null) {
                    if (it[0].response.isNotEmpty()) {
                        Toast.makeText(context, it[0].response, Toast.LENGTH_LONG).show()
                        binding.submit.isEnabled = true
                    } else {
                        savePerf(username, password, it)
                        navigateToAttendance()
                    }
                } else {
                    Toast.makeText(context, getText(R.string.interent_issue), Toast.LENGTH_LONG)
                        .show()
                    binding.submit.isEnabled = true
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
    }

    private fun savePerf(username: String, password: String, subject: List<Subject>) {
        with(sharedPref.edit()) {
            Utils.updateTime(this)
            putString(Constants.username_key, username)
            putString(Constants.password_key, password)
            putString(Constants.attendance_key, adapter.toJson(subject))
            commit()
        }
    }

    private fun navigateToAttendance() {
        Navigation.findNavController(requireView()).navigate(R.id.attendanceFragment)
    }
}
package `in`.thescriptgroup.attendance.fragment

import `in`.thescriptgroup.attendance.R
import `in`.thescriptgroup.attendance.api.ApiClient
import `in`.thescriptgroup.attendance.api.Attendance
import `in`.thescriptgroup.attendance.databinding.FragmentLoginBinding
import `in`.thescriptgroup.attendance.models.Subject
import `in`.thescriptgroup.attendance.utils.viewBinding
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {
    private val binding by viewBinding(FragmentLoginBinding::bind)

    @Inject
    lateinit var sharedPref: SharedPreferences

    val gson = Gson()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun init() {
        val sharedUsername = sharedPref.getString(getString(R.string.username_key), "")!!
        val sharedPassword = sharedPref.getString(getString(R.string.password_key), "")!!

        if (sharedUsername.isNotEmpty() || sharedPassword.isNotEmpty()) {
            navigateToAttendance()
        }

        binding.submit.setOnClickListener {
            Toast.makeText(context, "Verifying credentials!", Toast.LENGTH_SHORT).show()
            val username = binding.usernameInput.text.toString()
            val password = binding.passwordInput.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Please enter the details!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!(username[0] in "Ss" && username.length == 11)) {
                Toast.makeText(context, "Invalid ID!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.submit.isEnabled = false
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
                            context,
                            err,
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.passwordInput.text.clear()
                        binding.submit.isEnabled = true
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
                    navigateToAttendance()
                }

                override fun onFailure(call: Call<List<Subject>>, t: Throwable) {
                    Log.v("onFailure", t.message!!)
                    if (t.message == "timeout") {
                        Toast.makeText(
                            context,
                            "Connection timed out!",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.submit.isEnabled = true
                    }
                }
            })
        }

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun navigateToAttendance() {
        Navigation.findNavController(requireView()).navigate(R.id.attendanceFragment)
    }
}
package `in`.thescriptgroup.attendance.fragment

import `in`.thescriptgroup.attendance.R
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

class SettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener,
    Preference.OnPreferenceClickListener {
    lateinit var desiredAttendance: EditTextPreference
    lateinit var logout: Preference
    lateinit var sharedPref: SharedPreferences
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        sharedPref = requireActivity().getSharedPreferences(
            getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
        desiredAttendance =
            preferenceManager.findPreference("desired_attendance")!!
        desiredAttendance.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_CLASS_NUMBER
            editText.filters = arrayOf(InputFilter.LengthFilter(2))
            editText.hint = "Between 0 - 99"
        }
        desiredAttendance.onPreferenceChangeListener = this
        logout = preferenceManager.findPreference("logout")!!
        logout.onPreferenceClickListener = this
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {

        when (preference) {
            desiredAttendance -> {
                val value = newValue.toString().toInt()
                if (desiredAttendance.text.toInt() == value) {
                    return false
                }
                with(sharedPref.edit()) {
                    putInt(getString(R.string.desired_attendance_key), value)
                    commit()
                }
                Toast.makeText(
                    activity,
                    "Desired attendance set to $value!",
                    Toast.LENGTH_SHORT
                ).show()
                return true
            }

            else -> {
                return false
            }
        }
    }

    override fun onPreferenceClick(preference: Preference?): Boolean {
        when (preference) {
            logout -> {
                with(sharedPref.edit()) {
                    putString(getString(R.string.username_key), "")
                    putString(getString(R.string.password_key), "")
                    putString(getString(R.string.attendance_key), "")
                    putString(getString(R.string.timestamp_key), "")
                    commit()
                }
                Toast.makeText(activity, "Logging out!", Toast.LENGTH_SHORT).show()
                navigateToLogin()
                return true
            }
            else -> {
                return false
            }
        }
    }

    private fun navigateToLogin() {
        (activity as AppCompatActivity).supportActionBar?.subtitle = ""
        Navigation.findNavController(requireView()).navigate(R.id.loginFragment)
    }
}

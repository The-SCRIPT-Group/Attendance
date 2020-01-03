package `in`.thescriptgroup.attendance

import android.content.Context
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // The back arrow in the action bar should act the same as the back button.
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    class SettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            val desiredAttendance =
                preferenceManager.findPreference<EditTextPreference>("desired_attendance")!!
            desiredAttendance.setOnBindEditTextListener { editText ->
                editText.inputType =
                    InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
                editText.filters = arrayOf(InputFilter.LengthFilter(3))
                editText.hint = "Between 0 - 100"
            }
            desiredAttendance.onPreferenceChangeListener = this
        }

        override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
            val sharedPref = activity!!.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE
            )
            when (preference) {
                preferenceManager.findPreference<EditTextPreference>("desired_attendance") -> {
                    val editTextPreference =
                        preferenceManager.findPreference<EditTextPreference>("desired_attendance")!!
                    val oldValue = editTextPreference.text.toInt()
                    val value = newValue.toString().toInt()
                    if (value < 0 || value > 100) {
                        Toast.makeText(
                            activity,
                            "Please choose something from 0-100",
                            Toast.LENGTH_SHORT
                        ).show()
                        return false
                        //editTextPreference.text = oldValue.toString()
                    }
                    if (oldValue != value) {
                        with(sharedPref!!.edit()) {
                            putInt(getString(R.string.desired_attendance_key), value)
                            commit()
                        }
                        Toast.makeText(
                            activity,
                            "Desired attendance set to $value!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    return true
                }
                else -> {
                    return false
                }
            }
        }
    }
}
package `in`.thescriptgroup.attendance

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sharedPref = this.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )
        val username = sharedPref.getString(getString(R.string.username_key), "")!!
        val password = sharedPref.getString(getString(R.string.password_key), "")!!
        if (username.isEmpty() || password.isEmpty()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            startActivity(Intent(this, AttendanceActivity::class.java))
            finish()
        }
    }
}
package `in`.thescriptgroup.attendance

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        title = "Login"
        val username = username.text.toString()
        val password = password.text.toString()
        val sharedPref = this.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )
        submit.setOnClickListener {
            with(sharedPref.edit()) {
                putString(getString(R.string.username_key), username)
                putString(getString(R.string.password_key), password)
                commit()
            }
            finish()
            val intent = Intent(this, AttendanceActivity::class.java)
            intent.putExtra("username", username)
            intent.putExtra("password", password)
            startActivity(intent)
        }
    }
}

package `in`.thescriptgroup.attendance

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        title = "Login"

        val sharedPref = this.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )
        submit.setOnClickListener {
            val username = username.text.toString()
            val password = password.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter the details!", Toast.LENGTH_SHORT).show()
            } else {
                with(sharedPref.edit()) {
                    putString(getString(R.string.username_key), username)
                    putString(getString(R.string.password_key), password)
                    commit()
                }
                val intent = Intent(this, AttendanceActivity::class.java)
                intent.putExtra("username", username)
                intent.putExtra("password", password)
                finish()
                startActivity(intent)
            }
        }
    }
}

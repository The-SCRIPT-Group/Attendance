package `in`.thescriptgroup.attendance

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import org.json.JSONArray

class AttendanceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance)
        title = "Attendance"
        val username = intent.getStringExtra("username")!!
        val password = intent.getStringExtra("password")!!
        val url = "https://tsg-erp-api.herokuapp.com/api/attendance"
        VolleyService.initialize(this)
        val request = object : JsonArrayRequest(
            Method.POST, url, null,
            Response.Listener<JSONArray> { response ->
                for (i in 0 until response.length()) {
                    Toast.makeText(
                        this,
                        response.getJSONObject(i).getString("subject"),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            Response.ErrorListener {
                Toast.makeText(this, "That didn't work!", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["username"] = username
                params["password"] = password
                params["desired_attendance"] = "100"
                return params
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/text"
                return headers
            }
        }
        VolleyService.requestQueue.add(request)
        VolleyService.requestQueue.start()
    }
}

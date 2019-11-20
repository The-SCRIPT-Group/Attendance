package `in`.thescriptgroup.attendance

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

object VolleyService {

    private lateinit var context: Context

    val requestQueue: RequestQueue by lazy { Volley.newRequestQueue(context) }

}
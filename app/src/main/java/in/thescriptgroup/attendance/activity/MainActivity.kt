package `in`.thescriptgroup.attendance.activity

import `in`.thescriptgroup.attendance.BuildConfig
import `in`.thescriptgroup.attendance.databinding.ActivityMainBinding
import `in`.thescriptgroup.attendance.utils.viewBinding
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging


class MainActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityMainBinding::inflate)

    private val TAG = this::class.qualifiedName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (BuildConfig.DEBUG) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get and log new Instance ID token
                Log.d(TAG, "Token is ${task.result}")
            })
        }
    }
}
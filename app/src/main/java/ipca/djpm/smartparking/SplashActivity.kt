package ipca.djpm.smartparking

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val progressBarHorizontal = findViewById<ProgressBar>(R.id.progressBarHorizontal)
        progressBarHorizontal.visibility = View.VISIBLE
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        Handler(Looper.getMainLooper()).postDelayed(
            {
                currentUser?.let {
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    //finish()
                }?:run{
                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                    //finish()
                }
            },
            1000 // value in milliseconds
        )
    }
}
package ipca.djpm.smartparking

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private var savedUser : String? = null
    private var savedPass : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val progressBarHorizontal = findViewById<ProgressBar>(R.id.progressBarHorizontal)
        progressBarHorizontal.visibility = View.VISIBLE
        Handler(Looper.getMainLooper()).postDelayed(
        {
            var databaseHelper = DatabaseHelper()
            loadData()
            if(!savedUser.isNullOrEmpty() && !savedPass.isNullOrEmpty()) {
                var result = databaseHelper.login(savedUser!!, savedPass!!, this)
                if (result == -1) {
                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                    finish()
                } else {
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finish()
                }
            }else{
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                finish()
            }
        }, 500)
    }

    private fun loadData(){
        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        savedUser = sharedPreferences.getString("USER", null)
        savedPass = sharedPreferences.getString("PASS", null)
    }
}
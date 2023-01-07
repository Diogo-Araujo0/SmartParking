package ipca.djpm.smartparking

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class LoginActivity: AppCompatActivity() {
    var resultLauncher : ActivityResultLauncher<Intent>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_login)
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){

        }

        val buttonLogin = findViewById<Button>(R.id.buttonLogin)
        val buttonNewUser = findViewById<Button>(R.id.buttonNewUser)

        buttonLogin.setOnClickListener{
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        buttonNewUser.setOnClickListener{
            resultLauncher?.launch(Intent(this@LoginActivity, UserRegisterActivity::class.java))
        }
    }
}
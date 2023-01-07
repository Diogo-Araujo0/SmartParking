package ipca.djpm.smartparking

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class UserRegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_register)

        val buttonRegister = findViewById<Button>(R.id.buttonRegister)
        val buttonExistingUser = findViewById<Button>(R.id.buttonExistingUser)

        buttonRegister.setOnClickListener{
            val intent = Intent(this@UserRegisterActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        buttonExistingUser.setOnClickListener{
            val intent = Intent()
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}
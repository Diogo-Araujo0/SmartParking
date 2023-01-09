package ipca.djpm.smartparking

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity: AppCompatActivity() {
    var resultLauncher : ActivityResultLauncher<Intent>? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_login)

        auth = FirebaseAuth.getInstance()
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){}

        val buttonLogin = findViewById<Button>(R.id.buttonLogin)
        val buttonNewUser = findViewById<Button>(R.id.buttonNewUser)
        val editTextLoginEmail = findViewById<EditText>(R.id.editTextLoginEmail)
        val editTextLoginPassword = findViewById<EditText>(R.id.editTextLoginPassword)

        buttonLogin.setOnClickListener{
            val email = editTextLoginEmail.text.toString()
            val password = editTextLoginPassword.text.toString()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user = auth.currentUser
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        buttonNewUser.setOnClickListener{
            resultLauncher?.launch(Intent(this@LoginActivity, UserRegisterActivity::class.java))
        }
    }
}
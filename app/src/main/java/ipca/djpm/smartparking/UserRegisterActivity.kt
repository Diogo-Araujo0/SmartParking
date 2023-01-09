package ipca.djpm.smartparking

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class UserRegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_register)

        auth = Firebase.auth

        val buttonRegister = findViewById<Button>(R.id.buttonRegister)
        val buttonExistingUser = findViewById<Button>(R.id.buttonExistingUser)
        val editTextRegisterEmail = findViewById<EditText>(R.id.editTextRegisterEmail)
        val editTextRegisterPassword = findViewById<EditText>(R.id.editTextRegisterPassword)


        buttonRegister.setOnClickListener{
            val email = editTextRegisterEmail.text.toString()
            val pass = editTextRegisterPassword.text.toString()

            if (!email.isBlank() && !pass.isBlank()) {
                if (pass.length < 6){
                    Toast.makeText(
                        this,
                        "Password muito curta (min. 6 caracteres)",
                        Toast.LENGTH_SHORT
                    ).show()
                }else {
                    auth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                val user = auth.currentUser
                                Toast.makeText(
                                    this,
                                    "Registo efetuado com sucesso",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent = Intent()
                                setResult(RESULT_OK, intent)
                                finish()
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.exception)
                                Toast.makeText(
                                    this,
                                    "Falha ao fazer o registo!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
           }
            else{
                Toast.makeText(this, "Email e Password não podem estar vazios!", Toast.LENGTH_SHORT).show()
            }
        }

        buttonExistingUser.setOnClickListener{
            val intent = Intent()
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}
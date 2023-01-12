package ipca.djpm.smartparking

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

class ForgotPassActivity: AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val buttonRegisterPassword = findViewById<Button>(R.id.buttonRegisterPassword)
        val buttonExistingUser2 = findViewById<Button>(R.id.buttonExistingUser2)
        val editTextUsernamePass = findViewById<EditText>(R.id.editTextUsernamePass)
        val editTextEditPassword = findViewById<EditText>(R.id.editTextEditPassword)
        val editTextEditPassword2 = findViewById<EditText>(R.id.editTextEditPassword2)


        buttonRegisterPassword.setOnClickListener{
            val username = editTextUsernamePass.text.toString()
            val password = editTextEditPassword.text.toString()
            val password2 = editTextEditPassword2.text.toString()
            if (username.isNotBlank() && password.isNotBlank() && password2.isNotBlank()) {
                if(password == password2){
                    if (password.length < 6) {
                        Toast.makeText(
                            this,
                            "Password muito curta (min. 6 caracteres)",
                            Toast.LENGTH_SHORT
                        ).show()
                    }else{
                        val query = "UPDATE Utilizador SET password='${password}' WHERE username='${username}'"
                        val databaseHelper = DatabaseHelper()
                        val result = databaseHelper.executeQuery(query, this)
                        if (result) {
                            Toast.makeText(this, "Password alterada", Toast.LENGTH_SHORT).show()
                            val intent = Intent()
                            setResult(RESULT_OK, intent)
                            finish()
                        }else{
                            Toast.makeText(this, "Erro ao alterar password", Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    Toast.makeText(this, "Passwords não são iguais!", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "Username e/ou passwords não podem estar vazios!", Toast.LENGTH_SHORT).show()
            }
        }

        buttonExistingUser2.setOnClickListener{
            val intent = Intent()
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}
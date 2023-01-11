package ipca.djpm.smartparking

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class UserRegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_register)

        val buttonRegister = findViewById<Button>(R.id.buttonRegister)
        val buttonExistingUser = findViewById<Button>(R.id.buttonExistingUser)
        val editTextRegisterUsername = findViewById<EditText>(R.id.editTextRegisterUsername)
        val editTextRegisterPassword = findViewById<EditText>(R.id.editTextRegisterPassword)


        buttonRegister.setOnClickListener{
            val username = editTextRegisterUsername.text.toString()
            val password = editTextRegisterPassword.text.toString()

            if (username.isNotBlank() && password.isNotBlank()) {
                if (password.length < 6){
                    Toast.makeText(
                        this,
                        "Password muito curta (min. 6 caracteres)",
                        Toast.LENGTH_SHORT
                    ).show()
                }else {
                    val query = "INSERT INTO Utilizador(tipoUtilizadorID, username, password, tempoLimite) VALUES(1,'${username}', '${password}', 0)"
                    val databaseHelper = DatabaseHelper()
                    val result = databaseHelper.executeQuery(query, this)
                    if (result) {
                        Toast.makeText(this, "Utilizador registado com sucesso", Toast.LENGTH_SHORT).show()
                        val intent = Intent()
                        setResult(RESULT_OK, intent)
                        finish()
                    }else{
                        Toast.makeText(this, "Erro ao registar utilizador", Toast.LENGTH_SHORT).show()
                    }
                }
           }else{
                Toast.makeText(this, "Username e/ou password não podem estar vazios!", Toast.LENGTH_SHORT).show()
            }
        }

        buttonExistingUser.setOnClickListener{
            val intent = Intent()
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}
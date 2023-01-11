package ipca.djpm.smartparking

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

class LoginActivity: AppCompatActivity() {
    private var resultLauncher : ActivityResultLauncher<Intent>? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_login)

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){}

        val buttonLogin = findViewById<Button>(R.id.buttonLogin)
        val buttonNewUser = findViewById<Button>(R.id.buttonNewUser)
        val editTextLoginUsername = findViewById<EditText>(R.id.editTextLoginUsername)
        val editTextLoginPassword = findViewById<EditText>(R.id.editTextLoginPassword)
        val checkBoxSaveLogin = findViewById<CheckBox>(R.id.checkBoxSaveLogin)

        buttonLogin.setOnClickListener{
            val username = editTextLoginUsername.text.toString()
            val password = editTextLoginPassword.text.toString()
            if(!username.isNullOrEmpty() && !password.isNullOrEmpty()){
                if(checkBoxSaveLogin.isChecked) {
                    saveData(username, password)
                }
                val databaseHelper = DatabaseHelper()
                val result = databaseHelper.login(username, password, this)
                if(result != -1){
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }else{
                    Toast.makeText(this, "Email e/ou password errados", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "Username e/ou password não podem estar vazio(s)", Toast.LENGTH_SHORT).show()
            }
        }

        buttonNewUser.setOnClickListener{
            resultLauncher?.launch(Intent(this@LoginActivity, UserRegisterActivity::class.java))
        }
    }

    private fun saveData(username: String, password: String){
        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.apply{
            putString("USER", username)
            putString("PASS", password)
        }.apply()
    }
}
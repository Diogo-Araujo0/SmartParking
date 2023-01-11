package ipca.djpm.smartparking

import android.content.Context
import android.os.StrictMode
import android.widget.Toast
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

class DatabaseHelper {
    private var con: Connection? = null
    private val ip = "sql5053.site4now.net:1433"
    private val classes = "net.sourceforge.jtds.jdbc.Driver"
    private val database = "db_a92e06_smartparking"
    private val username = "db_a92e06_smartparking_admin"
    private val password = "SmartParking1"


    private fun connectionToServer(context: Context) {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        try {
            Class.forName(classes).newInstance()
            val connString = "jdbc:jtds:sqlserver://$ip;databaseName=$database;user=$username;password=$password"
            con = DriverManager.getConnection(connString)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
            Toast.makeText(context, "Erro de ligação à base de dados", Toast.LENGTH_SHORT).show()
        } catch (e: SQLException) {
            e.printStackTrace()
            Toast.makeText(context, "Erro de ligação à base de dados", Toast.LENGTH_SHORT).show()
        }
    }

    fun selectQuery(query : String, context: Context): ResultSet? {
        var result: ResultSet? = null
        if(con == null){
            connectionToServer(context)
        }
        try {
            val statement: Statement = con!!.createStatement()
            result = statement.executeQuery(query)
        }
        catch (e: SQLException) {
            e.printStackTrace()
            Toast.makeText(context, "Erro ao executar pedido", Toast.LENGTH_SHORT).show()
        }
        return result
    }

    fun executeQuery(query : String, context: Context): Boolean{
        if(con == null){
            connectionToServer(context)
        }
        try {
            val statement: Statement = con!!.createStatement()
            statement.execute(query)
            return true
        }
        catch (e: SQLException) {
            e.printStackTrace()
            Toast.makeText(context, "Erro ao executar pedido", Toast.LENGTH_SHORT).show()
        }
        return false
    }

    fun login(email: String, password: String, context: Context): Int{
        val query = "SELECT utilizadorID FROM Utilizador WHERE username='$email' AND password='$password'"
        val result = selectQuery(query, context)
        if (result!!.next()) {
            val utilizadorID = result.getInt("utilizadorID")
            val sharedPreferences = context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.apply{
                putInt("USER_ID", utilizadorID)
            }.apply()
            return utilizadorID
        }else{
            Toast.makeText(context, "Erro ao obter utilizador", Toast.LENGTH_SHORT).show()
        }

        return -1
    }

}
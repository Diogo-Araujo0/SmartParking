package ipca.djpm.smartparking

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.StrictMode
import android.widget.Toast
import androidx.annotation.RequiresApi
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


    @RequiresApi(Build.VERSION_CODES.M)
    private fun connectionToServer(context: Context) {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
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
                }else{
                    Toast.makeText(context, "Verifique a ligação à Internet", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(context, "Verifique a ligação à Internet", Toast.LENGTH_SHORT).show()
            }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun selectQuery(query : String, context: Context): ResultSet? {
        var result: ResultSet? = null
        if(con == null){
            connectionToServer(context)
        }
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                try {
                    val statement: Statement = con!!.createStatement()
                    result = statement.executeQuery(query)
                } catch (e: SQLException) {
                    e.printStackTrace()
                    Toast.makeText(context, "Erro ao executar pedido", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(context, "Verifique a ligação à Internet", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            Toast.makeText(context, "Verifique a ligação à Internet", Toast.LENGTH_SHORT).show()
        }
        return result
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun executeQuery(query : String, context: Context): Boolean {
        if (con == null) {
            connectionToServer(context)
        }
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                try {
                    val statement: Statement = con!!.createStatement()
                    statement.execute(query)
                    return true
                } catch (e: SQLException) {
                    e.printStackTrace()
                    Toast.makeText(context, "Erro ao executar pedido", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(context, "Verifique a ligação à Internet", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            Toast.makeText(context, "Verifique a ligação à Internet", Toast.LENGTH_SHORT).show()
        }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun login(email: String, password: String, context: Context): Boolean {
            val query = "SELECT utilizadorID, tipoUtilizadorID FROM Utilizador WHERE username='$email' AND password='$password'"
            val result = selectQuery(query, context)
            if (result != null) {
                if (result.next()) {
                    val utilizadorID = result.getInt("utilizadorID")
                    val tipoUtilizadorID = result.getInt("tipoUtilizadorID")
                    val sharedPreferences =
                        context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.apply {
                        putInt("USER_ID", utilizadorID)
                        putInt("TIPO_USER_ID", tipoUtilizadorID)
                    }.apply()
                    return true
                } else {
                    Toast.makeText(context, "Erro ao obter utilizador", Toast.LENGTH_SHORT).show()
                }
            }
            else {
                Toast.makeText(context, "Erro ao obter utilizador", Toast.LENGTH_SHORT).show()
            }
            return false
        }
}
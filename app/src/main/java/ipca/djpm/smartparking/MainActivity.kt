package ipca.djpm.smartparking

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import ipca.djpm.smartparking.databinding.ActivityMainBinding

//import com.google.zxing.BarcodeFormat
//import com.google.zxing.MultiFormatWriter
//import com.google.zxing.WriterException
//import com.journeyapps.barcodescanner.BarcodeEncoder

class MainActivity : AppCompatActivity() {

    /*var edit_input: EditText? = null
    lateinit var bt_generate: Button
    var iv_qr: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        edit_input = findViewById(R.id.edit_input)
        bt_generate = findViewById(R.id.bt_generate)
        iv_qr = findViewById(R.id.iv_qr)
        bt_generate.setOnClickListener(View.OnClickListener { v: View? -> generateQR() })
    }

    private fun generateQR() {
        val text = edit_input!!.text.toString().trim { it <= ' ' }
        val writer = MultiFormatWriter()
        try {
            val matrix = writer.encode(text, BarcodeFormat.QR_CODE, 600, 600)
            val encoder = BarcodeEncoder()
            val bitmap = encoder.createBitmap(matrix)
            iv_qr!!.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }*/

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_parque, R.id.navigation_perfil
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }


}
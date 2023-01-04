package ipca.djpm.smartparking


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_login)
    }
}
package ipca.djpm.smartparking.ui.home.ui.home.contactos

import android.R
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ipca.djpm.smartparking.DatabaseHelper
import ipca.djpm.smartparking.databinding.FragmentContactosAddBinding

import java.util.regex.Pattern

class ContactosFragmentAdd: Fragment() {
    private var _binding: FragmentContactosAddBinding? = null
    private val binding get() = _binding!!
    private var userID: Int? = null
    private var tipoContactoID: Int = -1
    private var arrayTipoContacto = arrayListOf<String>()
    private var arrayNumeros = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentContactosAddBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val sharedPreferences = requireContext().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        userID = sharedPreferences.getInt("USER_ID", -1)

        val progressBar = binding.progressBarAddContacto
        val spinnerTipoContacto = binding.spinnerTipoContacto
        val buttonAdd = binding.buttonAdd
        val textViewNovoContacto = binding.textViewNovoContacto
        val textViewAddTipoContacto = binding.textViewAddTipoContacto
        val editTextAddContactoNumero = binding.editTextAddContactoNumero
        val textViewAddNumero = binding.textViewAddNumero

        Handler(Looper.getMainLooper()).postDelayed(
            {
                var query = "SELECT descricao FROM TipoContacto ORDER BY tipoContactoID"
                val databaseHelper = DatabaseHelper()
                var result = context?.let { databaseHelper.selectQuery(query, it) }
                if (result != null) {
                    while (result.next()) {
                        var descricao = result.getString("descricao")
                        descricao = descricao.replace("\\s+".toRegex(), "")
                        arrayTipoContacto.add(descricao)
                    }
                    val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, arrayTipoContacto)
                    spinnerTipoContacto.adapter = adapter

                    query = "SELECT numero FROM ContactoAluno"
                    result = context?.let { databaseHelper.selectQuery(query, it) }
                    if (result != null){
                        while (result.next()) {
                            var numero = result.getString("numero")
                            numero = numero.replace("\\s+".toRegex(), "")
                            arrayNumeros.add(numero)
                        }
                        progressBar.visibility = View.INVISIBLE
                        spinnerTipoContacto.visibility = View.VISIBLE
                        buttonAdd.visibility = View.VISIBLE
                        textViewNovoContacto.visibility = View.VISIBLE
                        textViewAddTipoContacto.visibility = View.VISIBLE
                        editTextAddContactoNumero.visibility = View.VISIBLE
                        textViewAddNumero.visibility = View.VISIBLE
                    }
                    else{
                        Toast.makeText(context, "Erro ao obter dados de contactos", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(context, "Erro ao obter dados de contactos", Toast.LENGTH_SHORT).show()
                }
            }, 1)

        spinnerTipoContacto.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                tipoContactoID = position+1
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                tipoContactoID = -1
            }
        }

        buttonAdd.setOnClickListener{
            var numero = editTextAddContactoNumero.text.toString()
            numero = numero.uppercase()
            if(tipoContactoID != -1){
                val databaseHelper = DatabaseHelper()
                if(!arrayNumeros.contains(numero)){
                    val query2 = "Select numAluno From Aluno_Utilizador Where utilizadorID = ${userID}"
                    var result2 = context?.let { databaseHelper.selectQuery(query2, it) }
                    if(result2!!.next() ) {
                        var numAluno = result2.getInt("numAluno")
                        val query = "INSERT INTO ContactoAluno(tipoContactoID, numAluno,numero) VALUES('${tipoContactoID}',${numAluno}, ${numero})"
                        var result = context?.let { databaseHelper.executeQuery(query, it) }
                        if (result == true) {
                            val query = "INSERT INTO ContactoAluno(tipoContactoID, numAluno,numero) VALUES('${tipoContactoID}',${numAluno}, ${numero})"
                            result = context?.let { databaseHelper.executeQuery(query, it) }
                            if (result == true) {
                                Toast.makeText(context, "Contacto adicionado com sucesso", Toast.LENGTH_SHORT).show()
                                findNavController().popBackStack()
                            }else{
                                Toast.makeText(context, "Erro ao adicionar contacto", Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            Toast.makeText(context, "Erro ao adicionar contacto", Toast.LENGTH_SHORT).show()
                        }
                    }


                }else{
                    Toast.makeText(context, "Número já existente", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(context, "Número e/ou tipo de contacto inválido(s)", Toast.LENGTH_SHORT).show()
            }
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home -> {
                findNavController().popBackStack()
                true
            }
            else ->{
                false
            }
        }
    }


}
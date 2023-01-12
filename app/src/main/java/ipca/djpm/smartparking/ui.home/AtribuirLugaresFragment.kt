package ipca.djpm.smartparking.ui.home

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ipca.djpm.smartparking.DatabaseHelper
import ipca.djpm.smartparking.databinding.FragmentAtribuirLugarBinding

class AtribuirLugaresFragment: Fragment() {
    private var _binding: FragmentAtribuirLugarBinding? = null
    private val binding get() = _binding!!
    private val nomeEscolas = arrayListOf<String>()
    private val numLugares = arrayListOf<Int>()
    private val usernames = arrayListOf<String>()
    private var numLugar: Int = -1
    private var escolaID: Int = -1
    private var utilizadorID: Int = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAtribuirLugarBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val spinnerEscola = binding.spinnerEscola
        val spinnerLugarAdmin = binding.spinnerLugarAdmin
        val spinnerUsername = binding.spinnerUsername
        val buttonAtribuirLugarAdmin = binding.buttonAtribuirLugarAdmin
        val buttonDesassociarLugar = binding.buttonDesassociarLugar
        val progressBarAtribuirLugar = binding.progressBarAtribuirLugar
        val textViewAtribuirLugar = binding.textViewAtribuirLugar
        val textView4 = binding.textView4
        val textView7 = binding.textView7
        val textView8 = binding.textView8

        val databaseHelper = DatabaseHelper()
        Handler(Looper.getMainLooper()).postDelayed(
            {
                var query = "SELECT nomeEscola FROM Escola ORDER BY escolaID"
                var resultQuery = context?.let { databaseHelper.selectQuery(query, it) }
                if (resultQuery != null) {
                    while(resultQuery.next()){
                        var nomeEscola = resultQuery.getString("nomeEscola")
                        nomeEscola = nomeEscola.replace("\\s+".toRegex(), " ")
                        nomeEscolas.add(nomeEscola)
                    }
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, nomeEscolas)
                    spinnerEscola.adapter = adapter
                    query = "SELECT username FROM Utilizador ORDER BY utilizadorID"
                    resultQuery = context?.let { databaseHelper.selectQuery(query, it) }
                    if (resultQuery != null) {
                        while(resultQuery.next()){
                            var username = resultQuery.getString("username")
                            username = username.replace("\\s+".toRegex(), "")
                            usernames.add(username)
                        }
                        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, usernames)
                        spinnerUsername.adapter = adapter
                        progressBarAtribuirLugar.visibility = View.INVISIBLE
                        buttonDesassociarLugar.visibility = View.VISIBLE
                        spinnerEscola.visibility = View.VISIBLE
                        spinnerLugarAdmin.visibility = View.VISIBLE
                        spinnerUsername.visibility = View.VISIBLE
                        buttonAtribuirLugarAdmin.visibility = View.VISIBLE
                        textView4.visibility = View.VISIBLE
                        textView7.visibility = View.VISIBLE
                        textView8.visibility = View.VISIBLE
                        textViewAtribuirLugar.visibility = View.VISIBLE
                    }else{
                        Toast.makeText(context, "Erro ao nomes de alunos", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(context, "Erro ao nomes de alunos", Toast.LENGTH_SHORT).show()
                }
            }, 100)


        spinnerUsername.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                utilizadorID = position+1
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                utilizadorID = -1
            }
        }

        spinnerEscola.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                numLugares.clear()
                getLugares(position+1)
                escolaID = position+1
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                escolaID = -1
            }
        }

        spinnerLugarAdmin.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                numLugar = position+1
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                numLugar = -1
            }
        }

        buttonAtribuirLugarAdmin.setOnClickListener{
            if(numLugar != -1 && escolaID != -1 && utilizadorID != -1){
                val databaseHelper = DatabaseHelper()
                progressBarAtribuirLugar.visibility = View.VISIBLE
                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        var query = "SELECT COUNT(DISTINCT utilizadorID) AS contador from Utilizador WHERE numLugar = ${numLugar} AND escolaID = ${escolaID}"
                        val resultQuery = context?.let { databaseHelper.selectQuery(query, it) }
                        if (resultQuery != null) {
                            resultQuery.next()
                            val contador = resultQuery.getInt("contador")
                            if(contador > 0){
                                Toast.makeText(context, "Utilizador/lugar já  atribuido", Toast.LENGTH_SHORT).show()
                                progressBarAtribuirLugar.visibility=View.INVISIBLE
                            }else{
                                query = "UPDATE Utilizador SET numLugar = ${numLugar}, escolaID=${escolaID} WHERE utilizadorID=${utilizadorID}"
                                val result = context?.let { databaseHelper.executeQuery(query, it) }
                                if (result == true) {
                                    Toast.makeText(context, "Lugar atribuido com sucesso", Toast.LENGTH_SHORT).show()
                                    progressBarAtribuirLugar.visibility=View.INVISIBLE
                                }else{
                                    Toast.makeText(context, "Erro ao atribuir lugar", Toast.LENGTH_SHORT).show()
                                    progressBarAtribuirLugar.visibility=View.INVISIBLE
                                }
                            }
                        }

                    },100)
            }else{
                Toast.makeText(context, "Dados selecionados inválidos!", Toast.LENGTH_SHORT).show()
            }
        }

        buttonDesassociarLugar.setOnClickListener{
            if(numLugar != -1 && escolaID != -1 && utilizadorID != -1){
                val databaseHelper = DatabaseHelper()
                progressBarAtribuirLugar.visibility = View.VISIBLE

                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        var query = "SELECT numLugar, escolaID FROM Utilizador WHERE utilizadorID = ${utilizadorID}"
                        val resultQuery = context?.let { databaseHelper.selectQuery(query, it) }
                        if (resultQuery != null) {
                            resultQuery.next()
                            val numLugar1 = resultQuery.getInt("numLugar")
                            val escolaID1 = resultQuery.getInt("escolaID")

                            if((numLugar1 > 0 || escolaID > 0) && (numLugar1 == numLugar && escolaID1==escolaID)){
                                query = "UPDATE Utilizador SET numLugar = NULL, escolaID=NULL WHERE utilizadorID=${utilizadorID}"
                                val result = context?.let { databaseHelper.executeQuery(query, it) }
                                if (result == true) {
                                    Toast.makeText(context, "Lugar desassociado com sucesso", Toast.LENGTH_SHORT).show()
                                    progressBarAtribuirLugar.visibility=View.INVISIBLE
                                }else{
                                    Toast.makeText(context, "Erro ao desassociar lugar", Toast.LENGTH_SHORT).show()
                                    progressBarAtribuirLugar.visibility=View.INVISIBLE
                                }

                            }else{
                                Toast.makeText(context, "Utilizador não tem este lugar atribuido", Toast.LENGTH_SHORT).show()
                                progressBarAtribuirLugar.visibility=View.INVISIBLE
                            }
                        }

                    },100)
            }else{
                Toast.makeText(context, "Dados selecionados inválidos!", Toast.LENGTH_SHORT).show()
            }

        }
        return root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getLugares(escolaID: Int){
        val databaseHelper = DatabaseHelper()
        val spinnerLugarAdmin = binding.spinnerLugarAdmin
        val progressBarAtribuirLugar = binding.progressBarAtribuirLugar
        progressBarAtribuirLugar.visibility = View.VISIBLE
        Handler(Looper.getMainLooper()).postDelayed(
            {
                val query = "SELECT numLugar FROM Estacionamento WHERE escolaID = ${escolaID} ORDER BY escolaID"
                val resultQuery = context?.let { databaseHelper.selectQuery(query, it) }
                if (resultQuery != null) {
                    while(resultQuery.next()){
                        val numLugar = resultQuery.getInt("numLugar")
                        numLugares.add(numLugar)
                    }
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, numLugares)
                    spinnerLugarAdmin.adapter = adapter
                    progressBarAtribuirLugar.visibility = View.INVISIBLE
                }else{
                    Toast.makeText(context, "Erro ao obter números de lugar", Toast.LENGTH_SHORT).show()
                    progressBarAtribuirLugar.visibility = View.INVISIBLE

                }
            }, 100)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
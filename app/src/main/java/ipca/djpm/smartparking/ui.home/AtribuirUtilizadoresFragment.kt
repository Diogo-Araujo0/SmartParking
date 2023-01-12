package ipca.djpm.smartparking.ui.home

import android.R
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
import ipca.djpm.smartparking.databinding.FragmentAtribuirUtilizadorBinding

class AtribuirUtilizadoresFragment: Fragment() {
    private var _binding: FragmentAtribuirUtilizadorBinding? = null
    private val binding get() = _binding!!
    private val nomesAlunos = arrayListOf<String>()
    private val usernames = arrayListOf<String>()
    private var numAluno: Int = -1
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
        _binding = FragmentAtribuirUtilizadorBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val spinnerAluno = binding.spinnerAluno
        val spinnerUsername = binding.spinnerUsername
        val buttonAtribuirUtilizador = binding.buttonAtribuirUtilizador
        val buttonDesassociarUtilizador = binding.buttonDesassociarUtilizador
        val progressBarAtribuirUser = binding.progressBarAtribuirUser
        val textViewAtribuirUtilizador = binding.textViewAtribuirUtilizador
        val textView4 = binding.textView4
        val textView7 = binding.textView7

        val databaseHelper = DatabaseHelper()
        Handler(Looper.getMainLooper()).postDelayed(
            {
                var query = "SELECT nome FROM Aluno ORDER BY numAluno"
                var resultQuery = context?.let { databaseHelper.selectQuery(query, it) }
                if (resultQuery != null) {
                    while(resultQuery.next()){
                        var nome = resultQuery.getString("nome")
                        nome = nome.replace("\\s+".toRegex(), " ")
                        nomesAlunos.add(nome)
                    }
                    val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, nomesAlunos)
                    spinnerAluno.adapter = adapter
                    query = "SELECT username FROM Utilizador ORDER BY utilizadorID"
                    resultQuery = context?.let { databaseHelper.selectQuery(query, it) }
                    if (resultQuery != null) {
                        while(resultQuery.next()){
                            var username = resultQuery.getString("username")
                            username = username.replace("\\s+".toRegex(), "")
                            usernames.add(username)
                        }
                        val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, usernames)
                        spinnerUsername.adapter = adapter
                        progressBarAtribuirUser.visibility = View.INVISIBLE
                        buttonDesassociarUtilizador.visibility = View.VISIBLE
                        spinnerAluno.visibility = View.VISIBLE
                        spinnerUsername.visibility = View.VISIBLE
                        buttonAtribuirUtilizador.visibility = View.VISIBLE
                        textView4.visibility = View.VISIBLE
                        textView7.visibility = View.VISIBLE
                        textViewAtribuirUtilizador.visibility = View.VISIBLE
                    }else{
                        Toast.makeText(context, "Erro ao obter usernames", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(context, "Erro ao obter nomes de alunos", Toast.LENGTH_SHORT).show()
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

        spinnerAluno.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                numAluno = position+1
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                numAluno = -1
            }
        }


        buttonAtribuirUtilizador.setOnClickListener{
            if(numAluno != -1 && utilizadorID != -1){
                val databaseHelper = DatabaseHelper()
                progressBarAtribuirUser.visibility = View.VISIBLE

                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        var query = "SELECT COUNT(*) AS contador from Aluno_Utilizador WHERE numAluno = ${numAluno} OR utilizadorID = ${utilizadorID}"
                        var resultQuery = context?.let { databaseHelper.selectQuery(query, it) }
                        if (resultQuery != null) {
                            resultQuery!!.next()
                            val contador = resultQuery!!.getInt("contador")
                            if(contador > 0){
                                Toast.makeText(context, "Utilizador e/ou aluno já atribuido", Toast.LENGTH_SHORT).show()
                                progressBarAtribuirUser.visibility=View.INVISIBLE
                            }else{
                                query = "INSERT INTO Aluno_Utilizador(numAluno, utilizadorID) VALUES(${numAluno}, ${utilizadorID})"
                                val result = context?.let { databaseHelper.executeQuery(query, it) }
                                if (result == true) {
                                    Toast.makeText(context, "Utilizador atribuido com sucesso", Toast.LENGTH_SHORT).show()
                                    progressBarAtribuirUser.visibility=View.INVISIBLE
                                }else{
                                    Toast.makeText(context, "Erro ao atribuir utilizador", Toast.LENGTH_SHORT).show()
                                    progressBarAtribuirUser.visibility=View.INVISIBLE
                                }
                            }
                        }

                    },100)
            }
        }

        buttonDesassociarUtilizador.setOnClickListener{
            if(numAluno != -1 && utilizadorID != -1){
                val databaseHelper = DatabaseHelper()
                progressBarAtribuirUser.visibility = View.VISIBLE

                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        var query = "SELECT COUNT(*) AS contador FROM Aluno_Utilizador WHERE numAluno = ${numAluno} AND utilizadorID = ${utilizadorID}"
                        var resultQuery = context?.let { databaseHelper.selectQuery(query, it) }
                        if (resultQuery != null) {
                            resultQuery!!.next()
                            val contador = resultQuery!!.getInt("contador")
                            if(contador > 0){
                                query = "DELETE FROM Aluno_Utilizador WHERE numAluno = ${numAluno} AND utilizadorID = ${utilizadorID}"
                                val result = context?.let { databaseHelper.executeQuery(query, it) }
                                if (result == true) {
                                    Toast.makeText(context, "Utilizador desassociado com sucesso", Toast.LENGTH_SHORT).show()
                                    progressBarAtribuirUser.visibility=View.INVISIBLE
                                }else{
                                    Toast.makeText(context, "Erro ao desassociar utilizador", Toast.LENGTH_SHORT).show()
                                    progressBarAtribuirUser.visibility=View.INVISIBLE
                                }

                            }else{
                                Toast.makeText(context, "Utilizador e aluno não atribuidos", Toast.LENGTH_SHORT).show()
                                progressBarAtribuirUser.visibility=View.INVISIBLE
                            }
                        }

                    },100)
            }
        }
        return root
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
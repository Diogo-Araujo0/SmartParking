package ipca.djpm.smartparking.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ipca.djpm.smartparking.DatabaseHelper
import ipca.djpm.smartparking.R
import ipca.djpm.smartparking.databinding.FragmentEditarPerfilBinding

class EditarPerfilFragment: Fragment() {

    private var _binding: FragmentEditarPerfilBinding? = null
    private val binding get() = _binding!!
    private var userID : Int? = null
    private var arrayCartaoCidadao = arrayListOf<String>()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEditarPerfilBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val sharedPreferences = requireContext().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        userID = sharedPreferences.getInt("USER_ID", -1)

        val editTextNome = binding.editTextNome
        val editTextUsername = binding.editTextUsername
        val editTextCartaoCidadao = binding.editTextCartaoCidadao
        val editTextMorada = binding.editTextMorada
        val editTextContacto = binding.editTextContacto
        val editTextSexo = binding.editTextSexo
        val editTextCodPostal = binding.editTextCodPostal

        val args = arguments

        val nome = args?.getString("nome")
        val username = args?.getString("username")
        val cartaoCidadao = args?.getInt("cartaoCidadao")
        val morada = args?.getString("morada")
        val numero = args?.getInt("numero")
        val sexo = args?.getString("sexo")
        val codPostal = args?.getInt("codPostal")

        editTextNome.setText(nome)
        editTextUsername.setText(username)
        editTextCartaoCidadao.setText(cartaoCidadao.toString())
        editTextMorada.setText(morada)
        editTextContacto.setText(numero.toString())
        editTextSexo.setText(sexo)
        editTextCodPostal.setText(codPostal.toString())

        var buttonEditar = binding.buttonEditar
        val databaseHelper = DatabaseHelper()

        val query = "SELECT cartaoCidadao FROM Aluno"
        var result = context?.let { databaseHelper.selectQuery(query, it) }
        if (result != null){
            while (result.next()) {
                var cartaoCidadao = result.getInt("cartaoCidadao").toString()
                arrayCartaoCidadao.add(cartaoCidadao)
            }
        }else{
            Toast.makeText(context, "Erro ao obter dados do Aluno", Toast.LENGTH_SHORT).show()
        }

        buttonEditar.setOnClickListener{
            var username = editTextNome.text.toString()
            var cartaoCidadao = editTextCartaoCidadao.text.toString()
            var morada = editTextMorada.text.toString()
            var numero = editTextContacto.text.toString()
            val databaseHelper = DatabaseHelper()
            if(!arrayCartaoCidadao.contains(cartaoCidadao)) {
                val query = "Update Aluno Set nome= ${nome}, cartaoCidadao= ${cartaoCidadao}, morada= ${morada}, sexo = ${sexo}, codPostal = ${codPostal} From Aluno Join Aluno_Utilizador on Aluno_Utilizador.numAluno = Aluno.numAluno Where Aluno_Utilizador.utilizadorID = ${userID}"
                var result = context?.let { databaseHelper.executeQuery(query, it) }
                if (result == true) {
                   val query = "Update ContactoAluno set numero= ${numero} From ContactoAluno Join Aluno_Utilizador on Aluno_Utilizador.numAluno = ContactoAluno.numAluno Where Aluno_Utilizador.utilizadorID = ${userID}"
                    result = context?.let { databaseHelper.executeQuery(query, it) }
                    if (result == true) {
                        val query = "Update Utilizador set username = ${username} From Utilizador Where Utilizador.utilizadorID = ${userID}"
                        result = context?.let { databaseHelper.executeQuery(query, it) }
                        if (result == true) {
                            Toast.makeText(context, "Perfil alterado com sucesso", Toast.LENGTH_SHORT).show()
                            findNavController().popBackStack()
                        }else{
                            Toast.makeText(context, "Erro ao alterar dados", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(context, "Erro ao alterar dados", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(context, "Erro ao alterar dados", Toast.LENGTH_SHORT).show()
                }
            }else {
                Toast.makeText(context, "Cartão de cidadão já existente", Toast.LENGTH_SHORT).show()
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
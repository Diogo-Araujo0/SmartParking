package ipca.djpm.smartparking.ui.home

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ipca.djpm.smartparking.DatabaseHelper
import ipca.djpm.smartparking.databinding.FragmentEditarPerfilBinding

class EditarPerfilFragment: Fragment() {

    private var _binding: FragmentEditarPerfilBinding? = null
    private val binding get() = _binding!!
    private var userID : Int? = null
    private var savedUser : String? = null
    private var arrayCodPostais = arrayListOf<Int>()
    private var codPostal : Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEditarPerfilBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val sharedPreferences = requireContext().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        userID = sharedPreferences.getInt("USER_ID", -1)
        savedUser = sharedPreferences.getString("USER", null)

        val editTextNome = binding.editTextNome
        val editTextUsername = binding.editTextUsername
        val editTextPassword = binding.editTextPassword
        val editTextCartaoCidadao = binding.editTextCartaoCidadao
        val editTextMorada = binding.editTextMorada
        val radioGroupSexo = binding.radioGroupSexo
        val spinnerCodPostais = binding.spinnerCodPostais
        val buttonEditar = binding.buttonEditar
        val progressBarEditUser = binding.progressBarEditUser
        val imageViewNome = binding.imageViewNome
        val imageViewUser = binding.imageViewUser
        val imageViewPassword = binding.imageViewPassword
        val imageViewCC = binding.imageViewCC
        val imageViewMorada = binding.imageViewMorada
        val imageViewSexo = binding.imageViewSexo
        val imageViewCodPostal = binding.imageViewCodPostal

        val args = arguments
        var nome = args?.getString("nome")
        var username = args?.getString("username")
        var cartaoCidadao = args?.getString("cartaoCidadao")
        var morada = args?.getString("morada")
        var sexo = args?.getString("sexo")
        codPostal = args?.getInt("codPostal")!!

        nome = nome!!.replace("\\s+".toRegex(), " ")
        username = username!!.replace("\\s+".toRegex(), "")
        cartaoCidadao = cartaoCidadao!!.replace("\\s+".toRegex(), "")
        morada = morada!!.replace("\\s+".toRegex(), " ")
        sexo = sexo!!.replace("\\s+".toRegex(), "")

        editTextNome.setText(nome)
        editTextUsername.setText(username)
        editTextCartaoCidadao.setText(cartaoCidadao)
        editTextMorada.setText(morada)

        if(sexo == "Masculino"){
            binding.radioButtonMasculino.isChecked = true
        }else if(sexo == "Feminino"){
            binding.radioButtonFeminino.isChecked = true
        }

        val databaseHelper = DatabaseHelper()
        Handler(Looper.getMainLooper()).postDelayed(
            {
                val query = "SELECT codPostal FROM CodPostal"
                val resultQuery = context?.let { databaseHelper.selectQuery(query, it) }
                if (resultQuery != null) {
                    while(resultQuery.next()){
                        var codPostal = resultQuery.getInt("codPostal")
                        arrayCodPostais.add(codPostal)
                    }
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, arrayCodPostais)
                    spinnerCodPostais.adapter = adapter
                    spinnerCodPostais.setSelection(arrayCodPostais.indexOf(codPostal))
                    progressBarEditUser.visibility = View.INVISIBLE
                    editTextNome.visibility = View.VISIBLE
                    editTextUsername.visibility = View.VISIBLE
                    editTextPassword.visibility = View.VISIBLE
                    editTextCartaoCidadao.visibility = View.VISIBLE
                    editTextMorada.visibility = View.VISIBLE
                    radioGroupSexo.visibility = View.VISIBLE
                    spinnerCodPostais.visibility = View.VISIBLE
                    buttonEditar.visibility = View.VISIBLE
                    imageViewNome.visibility = View.VISIBLE
                    imageViewUser.visibility = View.VISIBLE
                    imageViewCC.visibility = View.VISIBLE
                    imageViewMorada.visibility = View.VISIBLE
                    imageViewSexo.visibility = View.VISIBLE
                    imageViewCodPostal.visibility = View.VISIBLE
                    imageViewPassword.visibility = View.VISIBLE
                }else{
                    Toast.makeText(context, "Erro ao obter códigos postais", Toast.LENGTH_SHORT).show()
                }
            },100)
        spinnerCodPostais.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                codPostal = spinnerCodPostais.selectedItem.toString().toInt()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }


        buttonEditar.setOnClickListener{
            val selectedOption: Int = radioGroupSexo.checkedRadioButtonId
            val radioButton = view?.findViewById<RadioButton>(selectedOption)
            if(editTextCartaoCidadao.text.isNotBlank() && editTextMorada.text.isNotBlank() && radioButton!!.text.isNotBlank() && editTextUsername.text.isNotBlank()){
                var query = "UPDATE Aluno SET nome= '${editTextNome.text}', cartaoCidadao= ${editTextCartaoCidadao.text}, morada='${editTextMorada.text}', sexo = '${radioButton!!.text}', codPostal = ${codPostal} FROM Aluno JOIN Aluno_Utilizador ON Aluno_Utilizador.numAluno = Aluno.numAluno WHERE Aluno_Utilizador.utilizadorID = ${userID}"
                var result = context?.let { databaseHelper.executeQuery(query, it) }
                if (result == true) {

                    if(savedUser != editTextUsername.text.toString()){
                        query = "UPDATE Utilizador SET username = '${editTextUsername.text}' WHERE utilizadorID=${userID}"
                        result = context?.let { databaseHelper.executeQuery(query, it) }
                        if (result == true) {
                            val sharedPreferences = context?.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
                            val editor = sharedPreferences?.edit()
                            editor?.apply{
                                putString("USER", editTextUsername.text.toString())
                            }?.apply()
                        }else{
                            Toast.makeText(context, "Erro ao alterar username", Toast.LENGTH_SHORT).show()
                        }
                    }
                    if(editTextPassword.text.isNotBlank()){
                        query = "UPDATE Utilizador SET password = '${editTextPassword.text}' WHERE utilizadorID=${userID}"
                        result = context?.let { databaseHelper.executeQuery(query, it) }
                        if (result == true) {
                            val sharedPreferences = context?.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
                            val editor = sharedPreferences?.edit()
                            editor?.apply{
                                putString("PASS", editTextUsername.text.toString())
                            }?.apply()
                        }else{
                            Toast.makeText(context, "Erro ao alterar username", Toast.LENGTH_SHORT).show()
                        }
                    }
                    Toast.makeText(context, "Perfil alterado com sucesso", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }else{
                    Toast.makeText(context, "Erro ao alterar dados", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(context, "Dados em branco", Toast.LENGTH_SHORT).show()
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
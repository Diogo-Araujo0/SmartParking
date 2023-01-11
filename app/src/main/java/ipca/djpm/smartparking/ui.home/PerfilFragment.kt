package ipca.djpm.smartparking.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ipca.djpm.smartparking.DatabaseHelper
import ipca.djpm.smartparking.LoginActivity
import ipca.djpm.smartparking.R
import ipca.djpm.smartparking.databinding.FragmentPerfilBinding


class PerfilFragment: Fragment() {
    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!
    private var userID : Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        var root = binding.root

        val textViewTempUser = binding.textViewTempUser
        val textViewNome = binding.textViewNome
        val textViewUsername = binding.textViewUsername
        val textViewCartaoCidadao = binding.textViewCartaoCidadao
        val textViewMorada = binding.textViewMorada
        val textViewContactos = binding.textViewContactos
        val textViewSexo = binding.textViewSexo
        val textViewCodPostal = binding.textViewCodPostal


        val sharedPreferences = requireContext().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        userID = sharedPreferences.getInt("USER_ID", -1)
        Handler(Looper.getMainLooper()).postDelayed(
            {
                if(userID != -1){
                    val query = "Select Utilizador.username,Aluno.cartaoCidadao,Aluno.morada,Aluno.nome,Aluno.sexo,Aluno.codPostal,ContactoAluno.numero FROM Aluno Join Aluno_Utilizador on Aluno_Utilizador.numAluno = Aluno.numAluno Join Utilizador on Utilizador.utilizadorId = Aluno_Utilizador.utilizadorID Join ContactoAluno on ContactoAluno.numAluno = Aluno.numAluno Where Aluno_Utilizador.utilizadorID = ${userID} "
                    val databaseHelper = DatabaseHelper()
                    val result = context?.let { databaseHelper.selectQuery(query, it) }
                    if (result != null) {
                        while(result.next()){


                            textViewNome.text = result.getString("nome")
                            textViewUsername.text = result.getString("username")
                            textViewCartaoCidadao.text = result.getInt("cartaoCidadao").toString()
                            textViewMorada.text = result.getString("morada")
                            textViewContactos.text = result.getInt("numero").toString()
                            textViewSexo.text = result.getString("sexo")
                            textViewCodPostal.text = result.getInt("codPostal").toString()

                            val bundle = Bundle()

                            bundle.putString("nome", result.getString("nome"))
                            bundle.putString("username", result.getString("username"))
                            bundle.putInt("cartaoCidadao", result.getInt("cartaoCidadao"))
                            bundle.putString("morada", result.getString("morada"))
                            bundle.putInt("numero", result.getInt("numero"))
                            bundle.putString("sexo", result.getString("sexo"))
                            bundle.putInt("codPostal", result.getInt("codPostal"))




                            var buttonAlterar = binding.buttonAlterar


                            buttonAlterar.setOnClickListener{
                                findNavController().navigate(R.id.action_navigation_perfil_to_navigation_perfil_editar,bundle)
                            }


                        }

                    }else{

                        textViewTempUser.visibility = View.VISIBLE

                    }
                }
            }, 1)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var buttonLogout = binding.buttonLogout

        buttonLogout.setOnClickListener{
            val sharedPreferences = requireContext().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear().apply()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }




}
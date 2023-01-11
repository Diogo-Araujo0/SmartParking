package ipca.djpm.smartparking.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ipca.djpm.smartparking.DatabaseHelper
import ipca.djpm.smartparking.LoginActivity
import ipca.djpm.smartparking.R
import ipca.djpm.smartparking.Veiculo
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
        val textViewUsername = binding.textViewUsername
        val textViewCartaoCidadao = binding.textViewCartaoCidadao
        val textViewMorada = binding.textViewMorada
        val textViewContactos = binding.textViewContactos

        val sharedPreferences = requireContext().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        userID = sharedPreferences.getInt("USER_ID", -1)
        Handler(Looper.getMainLooper()).postDelayed(
            {
                if(userID != -1){
                    val query = "Select Utilizador.username,Aluno.cartaoCidadao,Aluno.morada,ContactoAluno.numero FROM Aluno Join Aluno_Utilizador on Aluno_Utilizador.numAluno = Aluno.numAluno Join Utilizador on Utilizador.utilizadorId = Aluno_Utilizador.utilizadorID Join ContactoAluno on ContactoAluno.numAluno = Aluno.numAluno Where Aluno_Utilizador.utilizadorID = ${userID} "
                    val databaseHelper = DatabaseHelper()
                    val result = context?.let { databaseHelper.executeQuery(query, it) }
                    if (result != null) {
                        while(result.next()){

                            textViewUsername.text = result.getString("username")
                            textViewCartaoCidadao.text = result.getString("cartaoCidadao")
                            textViewMorada.text = result.getString("morada")
                            textViewContactos.text = result.getInt("numero").toString()

                            var buttonLogout = binding.buttonAlterar

                            buttonLogout.setOnClickListener{
                                findNavController().navigate(R.id.action_navigation_home_to_navigation_editarPerfil)
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
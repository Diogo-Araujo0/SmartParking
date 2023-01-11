package ipca.djpm.smartparking.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.strictmode.Violation
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

        val buttonContactos = binding.buttonContactos
        val textViewTempUser = binding.textViewTempUser
        val textViewNome = binding.textViewNome
        val textViewUsername = binding.textViewUsername
        val textViewCartaoCidadao = binding.textViewCartaoCidadao
        val textViewMorada = binding.textViewMorada
        val textViewSexo = binding.textViewSexo
        val textViewCodPostal = binding.textViewCodPostal
        val textViewDataNascimento = binding.textViewDataNascimento
        val buttonAlterar = binding.buttonAlterar
        val progressBarPerfil = binding.progressBarPerfil

        val imageView7 = binding.imageView7
        val imageView2 = binding.imageView2
        val imageView10 = binding.imageView10
        val imageView11 = binding.imageView11
        val imageView5 = binding.imageView5
        val imageView6 = binding.imageView6
        val imageView9 = binding.imageView9

        val sharedPreferences = requireContext().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        userID = sharedPreferences.getInt("USER_ID", -1)
        Handler(Looper.getMainLooper()).postDelayed(
            {
                if(userID != -1){
                    val query = "SELECT username, cartaoCidadao, morada, nome, sexo, codPostal, CONVERT(date, dataNascimento) as dataNascimento FROM Aluno JOIN Aluno_Utilizador ON Aluno_Utilizador.numAluno = Aluno.numAluno JOIN Utilizador ON Utilizador.utilizadorId = Aluno_Utilizador.utilizadorID WHERE Utilizador.utilizadorID = ${userID}"
                    val databaseHelper = DatabaseHelper()
                    val result = context?.let { databaseHelper.selectQuery(query, it) }
                    if (result!!.next()) {
                        textViewNome.text = result.getString("nome")
                        textViewUsername.text = result.getString("username")
                        textViewCartaoCidadao.text = result.getInt("cartaoCidadao").toString()
                        textViewMorada.text = result.getString("morada")
                        textViewDataNascimento.text = result.getString("dataNascimento").toString()
                        textViewSexo.text = result.getString("sexo")
                        textViewCodPostal.text = result.getInt("codPostal").toString()

                        textViewNome.visibility = View.VISIBLE
                        textViewUsername.visibility = View.VISIBLE
                        textViewCartaoCidadao.visibility = View.VISIBLE
                        textViewMorada.visibility = View.VISIBLE
                        textViewSexo.visibility = View.VISIBLE
                        textViewCodPostal.visibility = View.VISIBLE
                        textViewDataNascimento.visibility = View.VISIBLE

                        imageView9.visibility = View.VISIBLE
                        imageView6.visibility = View.VISIBLE
                        imageView5.visibility = View.VISIBLE
                        imageView11.visibility = View.VISIBLE
                        imageView10.visibility = View.VISIBLE
                        imageView2.visibility = View.VISIBLE
                        imageView7.visibility = View.VISIBLE

                        buttonAlterar.visibility = View.VISIBLE
                        buttonContactos.visibility = View.VISIBLE
                    }else{
                        textViewTempUser.visibility = View.VISIBLE
                    }
                    progressBarPerfil.visibility = View.INVISIBLE
                }
            }, 100)
        buttonAlterar.setOnClickListener{
            val bundle = Bundle()
            bundle.putString("nome", textViewNome.text.toString())
            bundle.putString("username", textViewUsername.text.toString())
            bundle.putString("cartaoCidadao", textViewCartaoCidadao.text.toString())
            bundle.putString("morada", textViewMorada.text.toString())
            bundle.putString("sexo", textViewSexo.text.toString())
            bundle.putInt("codPostal", textViewCodPostal.text.toString().toInt())
            findNavController().navigate(R.id.action_navigation_perfil_to_navigation_perfil_editar,bundle)
        }
        buttonContactos.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_perfil_to_navigation_contactos)
        }

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
package ipca.djpm.smartparking.ui.home.ui.home.veiculos

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ipca.djpm.smartparking.DatabaseHelper
import ipca.djpm.smartparking.R
import ipca.djpm.smartparking.Veiculo
import ipca.djpm.smartparking.databinding.FragmentVeiculosAdminBinding

class VeiculosAdminFragment: Fragment() {
    private var _binding: FragmentVeiculosAdminBinding? = null
    private val binding get() = _binding!!

    var veiculos = arrayListOf<Veiculo>()

    val adapter = VeiculoAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentVeiculosAdminBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val progressBar = binding.progressBar
        val textViewSemVeiculos = binding.textViewSemVeiculos
        val floatingActionButtonAddVeiculo = binding.floatingActionButtonAddVeiculo

        Handler(Looper.getMainLooper()).postDelayed(
            {
                val query = "SELECT Veiculo.matricula, TipoVeiculo.descricao, username FROM Veiculo JOIN Utilizador_Veiculo ON Veiculo.matricula = Utilizador_Veiculo.matricula JOIN Utilizador ON Utilizador_Veiculo.utilizadorID = Utilizador.utilizadorID JOIN TipoVeiculo ON Veiculo.tipoVeiculoID = TipoVeiculo.tipoVeiculoID GROUP BY Veiculo.matricula, TipoVeiculo.descricao, username"
                val databaseHelper = DatabaseHelper()
                val result = context?.let { databaseHelper.selectQuery(query, it) }
                if (result != null) {
                    while(result.next()){
                        var matricula = result.getString("matricula")
                        var tipoVeiculo = result.getString("descricao")
                        var username = result.getString("username")
                        matricula = matricula.replace("\\s+".toRegex(), "")
                        tipoVeiculo = tipoVeiculo.replace("\\s+".toRegex(), "")
                        username = username.replace("\\s+".toRegex(), "")
                        val veiculo = Veiculo(matricula, tipoVeiculo, username)
                        veiculos.add(veiculo)
                    }
                    progressBar.visibility = View.INVISIBLE

                    if(veiculos.size == 0){
                        textViewSemVeiculos.visibility = View.VISIBLE
                    }
                    adapter.notifyDataSetChanged()
                }
            }, 1)


        floatingActionButtonAddVeiculo.setOnClickListener{
            findNavController().navigate(R.id.action_navigation_veiculos_admin_to_navigation_veiculos_admin_add)
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        binding.listViewVeiculos.adapter = adapter

    }

    override fun onDestroyView() {
        super.onDestroyView()
        veiculos.clear()
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

    inner class VeiculoAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return veiculos.size
        }

        override fun getItem(position: Int): Any {
            return veiculos[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        @RequiresApi(Build.VERSION_CODES.M)
        override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
            val rootView = layoutInflater.inflate(R.layout.row_veiculo_admin,parent,false)

            val textViewMatricula = rootView.findViewById<TextView>(R.id.textViewMatricula)
            val textViewTipoVeiculo = rootView.findViewById<TextView>(R.id.textViewTipoVeiculo)
            val textViewUsernameVeiculos = rootView.findViewById<TextView>(R.id.textViewUsernameVeiculos)

            val floatingActionButtonDeleteVeiculo = rootView.findViewById<FloatingActionButton>(R.id.floatingActionButtonDeleteVeiculo)
            val progressBar = binding.progressBar

            textViewMatricula.text = veiculos[position].matricula
            textViewTipoVeiculo.text = veiculos[position].tipoveiculo
            textViewUsernameVeiculos.text = veiculos[position].username

            floatingActionButtonDeleteVeiculo.setOnClickListener{
                progressBar.visibility = View.VISIBLE
                Handler(Looper.getMainLooper()).postDelayed(
                    {
                            val databaseHelper = DatabaseHelper()
                            var query = "DELETE FROM Utilizador_Veiculo WHERE matricula='${veiculos[position].matricula}'"
                            var result = context?.let { databaseHelper.executeQuery(query, it) }
                            if (result == true) {
                                query = "DELETE FROM Veiculo WHERE matricula='${veiculos[position].matricula}'"
                                result = context?.let { databaseHelper.executeQuery(query, it) }
                                if (result == true) {
                                    Toast.makeText(context, "Veículo removido com sucesso", Toast.LENGTH_SHORT).show()
                                    progressBar.visibility = View.INVISIBLE
                                    veiculos.remove(veiculos[position])
                                    adapter.notifyDataSetChanged()
                                } else {
                                    Toast.makeText(context, "Erro ao remover veículo", Toast.LENGTH_SHORT).show()
                                    progressBar.visibility = View.INVISIBLE
                                }
                            }
                            else {
                                Toast.makeText(context, "Erro ao remover veículo", Toast.LENGTH_SHORT).show()
                                progressBar.visibility = View.INVISIBLE
                            }
                    }, 1)
            }

            return rootView
        }
    }
}
package ipca.djpm.smartparking.ui.home

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ipca.djpm.smartparking.DatabaseHelper
import ipca.djpm.smartparking.R
import ipca.djpm.smartparking.Veiculo
import ipca.djpm.smartparking.databinding.FragmentVeiculosBinding

class VeiculosFragment: Fragment() {
    private var _binding: FragmentVeiculosBinding? = null
    private val binding get() = _binding!!
    private var userID : Int? = null

    var veiculos = arrayListOf<Veiculo>()

    val adapter = VeiculoAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = requireContext().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        userID = sharedPreferences.getInt("USER_ID", -1)

        if(userID != -1){
            val query = "SELECT Veiculo.matricula, TipoVeiculo.descricao FROM Veiculo JOIN Utilizador_Veiculo ON Veiculo.matricula = Utilizador_Veiculo.matricula JOIN TipoVeiculo ON Veiculo.tipoVeiculoID = TipoVeiculo.tipoVeiculoID WHERE Utilizador_Veiculo.utilizadorID = ${userID} GROUP BY Veiculo.matricula, TipoVeiculo.descricao"
            val databaseHelper = DatabaseHelper()
            val result = context?.let { databaseHelper.executeQuery(query, it) }
            if (result != null) {
                while(result.next()){
                    var matricula = result.getString("matricula")
                    var tipoVeiculo = result.getString("descricao")
                    var veiculo = Veiculo(matricula, tipoVeiculo)
                    veiculos.add(veiculo)
                }
                adapter.notifyDataSetChanged()
            }
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentVeiculosBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        binding.listViewVeiculos.adapter = adapter

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

        override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
            val rootView = layoutInflater.inflate(R.layout.row_veiculo,parent,false)

            val textViewMatricula = rootView.findViewById<TextView>(R.id.textViewMatricula)
            val textViewTipoVeiculo = rootView.findViewById<TextView>(R.id.textViewTipoVeiculo)

            textViewMatricula.text = veiculos[position].matricula
            textViewTipoVeiculo.text = veiculos[position].tipoveiculo

            return rootView
        }
    }
}
package ipca.djpm.smartparking.ui.home.ui.home.veiculos

import android.content.Context
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
import ipca.djpm.smartparking.databinding.FragmentVeiculosBinding

class VeiculosFragment: Fragment() {
    private var _binding: FragmentVeiculosBinding? = null
    private val binding get() = _binding!!
    private var userID : Int? = null

    var veiculos = arrayListOf<Veiculo>()

    val adapter = VeiculoAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentVeiculosBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val sharedPreferences = requireContext().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        userID = sharedPreferences.getInt("USER_ID", -1)

        val progressBar = binding.progressBar
        val textViewSemVeiculos = binding.textViewSemVeiculos
        val floatingActionButtonAddVeiculo = binding.floatingActionButtonAddVeiculo

        Handler(Looper.getMainLooper()).postDelayed(
            {
                if(userID != -1){
                    val query = "SELECT Veiculo.matricula, TipoVeiculo.descricao FROM Veiculo JOIN Utilizador_Veiculo ON Veiculo.matricula = Utilizador_Veiculo.matricula JOIN TipoVeiculo ON Veiculo.tipoVeiculoID = TipoVeiculo.tipoVeiculoID WHERE Utilizador_Veiculo.utilizadorID = ${userID} GROUP BY Veiculo.matricula, TipoVeiculo.descricao"
                    val databaseHelper = DatabaseHelper()
                    val result = context?.let { databaseHelper.selectQuery(query, it) }
                    if (result != null) {
                        while(result.next()){
                            var matricula = result.getString("matricula")
                            var tipoVeiculo = result.getString("descricao")
                            matricula = matricula.replace("\\s+".toRegex(), "")
                            tipoVeiculo = tipoVeiculo.replace("\\s+".toRegex(), "")
                            val veiculo = Veiculo(matricula, tipoVeiculo)
                            veiculos.add(veiculo)
                        }
                        progressBar.visibility = View.INVISIBLE

                        if(veiculos.size == 0){
                            textViewSemVeiculos.visibility = View.VISIBLE
                        }
                        adapter.notifyDataSetChanged()
                    }
                }
            }, 1)


        floatingActionButtonAddVeiculo.setOnClickListener{
            findNavController().navigate(R.id.action_navigation_veiculos_to_navigation_veiculos_add)
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
            val rootView = layoutInflater.inflate(R.layout.row_veiculo,parent,false)

            val textViewMatricula = rootView.findViewById<TextView>(R.id.textViewMatricula)
            val textViewTipoVeiculo = rootView.findViewById<TextView>(R.id.textViewTipoVeiculo)
            val floatingActionButtonDeleteVeiculo = rootView.findViewById<FloatingActionButton>(R.id.floatingActionButtonDeleteVeiculo)
            val progressBar = binding.progressBar

            textViewMatricula.text = veiculos[position].matricula
            textViewTipoVeiculo.text = veiculos[position].tipoveiculo

            floatingActionButtonDeleteVeiculo.setOnClickListener{
                progressBar.visibility = View.VISIBLE
                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        if (userID != -1) {
                            val databaseHelper = DatabaseHelper()
                            var query = "SELECT COUNT(utilizadorID) as contador from Utilizador_Veiculo WHERE matricula='${veiculos[position].matricula}'"
                            val resultSelect = context?.let { databaseHelper.selectQuery(query, it) }
                            if (resultSelect != null) {
                                if(resultSelect.next()){
                                    val contador = resultSelect.getInt("contador")
                                    var result: Boolean

                                    if(contador > 1) {
                                        query = "DELETE FROM Utilizador_Veiculo WHERE matricula='${veiculos[position].matricula}' AND utilizadorID=${userID}"
                                        result = context?.let { databaseHelper.executeQuery(query, it) }!!
                                    }else{
                                        query = "DELETE FROM Utilizador_Veiculo WHERE matricula='${veiculos[position].matricula}' AND utilizadorID=${userID}"
                                        result = context?.let { databaseHelper.executeQuery(query, it) }!!
                                        if (result) {
                                            query = "DELETE FROM Veiculo WHERE matricula='${veiculos[position].matricula}'"
                                            result = context?.let { databaseHelper.executeQuery(query, it) }!!
                                        }
                                    }

                                    if (result) {
                                        Toast.makeText(context, "Veículo removido com sucesso", Toast.LENGTH_SHORT).show()
                                        progressBar.visibility = View.INVISIBLE
                                        veiculos.remove(veiculos[position])
                                        adapter.notifyDataSetChanged()
                                    } else {
                                        Toast.makeText(context, "Erro ao remover veículo", Toast.LENGTH_SHORT).show()
                                        progressBar.visibility = View.INVISIBLE
                                    }
                                }
                            }
                            else {
                                Toast.makeText(context, "Erro ao remover veículo", Toast.LENGTH_SHORT).show()
                                progressBar.visibility = View.INVISIBLE
                            }
                        }
                    }, 1)
            }

            return rootView
        }
    }
}
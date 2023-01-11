package ipca.djpm.smartparking.ui.home

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import ipca.djpm.smartparking.DatabaseHelper
import ipca.djpm.smartparking.Lugar
import ipca.djpm.smartparking.R
import ipca.djpm.smartparking.databinding.FragmentParqueBinding

class ParqueFragment : Fragment() {
    private var _binding: FragmentParqueBinding? = null
    private val binding get() = _binding!!
    private var lugares = arrayListOf<Lugar>()
    private var arrayEscolas = arrayListOf<String>()
    private var lugaresLivres : Int = 0

    private val adapter = LugarAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentParqueBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val progressBarParque = binding.progressBarParque
        val textViewSemLugares = binding.textViewSemLugares
        val textViewEscola = binding.textViewEscola
        val textViewLugares = binding.textViewLugares
        val spinnerEscolas = binding.spinnerEscolas

        val databaseHelper = DatabaseHelper()
        Handler(Looper.getMainLooper()).postDelayed(
            {
                    val query = "SELECT nomeEscola FROM Escola"
                    val resultQuery = context?.let { databaseHelper.selectQuery(query, it) }
                    if (resultQuery != null) {
                        while(resultQuery.next()){
                            var nomeEscola = resultQuery.getString("nomeEscola")
                            nomeEscola = nomeEscola.replace("\\s+".toRegex(), " ")
                            arrayEscolas.add(nomeEscola)
                        }
                        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, arrayEscolas)
                        spinnerEscolas.adapter = adapter
                        progressBarParque.visibility = View.INVISIBLE
                        textViewEscola.visibility = View.VISIBLE
                        textViewLugares.visibility = View.VISIBLE
                        spinnerEscolas.visibility = View.VISIBLE

                    }else{
                        Toast.makeText(context, "Erro ao obter escolas", Toast.LENGTH_SHORT).show()
                        textViewSemLugares.visibility = View.VISIBLE
                    }
            },100)
        spinnerEscolas.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                lugaresLivres = 0
                lugares.clear()
                getLugares(position+1)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.listViewLugares.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        lugares.clear()
        _binding = null
    }

    fun getLugares(escolaID: Int){
        val databaseHelper = DatabaseHelper()
        binding.progressBarParque.visibility = View.VISIBLE
        Handler(Looper.getMainLooper()).postDelayed(
            {
                    val query = "SELECT numLugar, ocupado FROM Estacionamento WHERE escolaID=${escolaID}"
                    val resultQuery = context?.let { databaseHelper.selectQuery(query, it) }
                    if (resultQuery != null) {
                        while(resultQuery.next()){
                            val numLugar = resultQuery.getInt("numLugar")
                            val ocupado = resultQuery.getInt("ocupado")
                            if(ocupado == 0){
                                lugaresLivres += 1
                            }
                            val lugar = Lugar(numLugar, ocupado)
                            lugares.add(lugar)
                        }
                        binding.textViewLugares.text = "Lugares livres: ${lugaresLivres}"
                        binding.progressBarParque.visibility = View.INVISIBLE
                        adapter.notifyDataSetChanged()

                    }else{
                        binding.textViewSemLugares.visibility = View.VISIBLE
                }
            },100)
    }

    inner class LugarAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return lugares.size
        }

        override fun getItem(position: Int): Any {
            return lugares[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
            val rootView = layoutInflater.inflate(R.layout.row_lugar,parent,false)

            val textViewNumLugar = rootView.findViewById<TextView>(R.id.textViewNumLugar)
            val textViewEstado = rootView.findViewById<TextView>(R.id.textViewEstado)

            textViewNumLugar.text = "Lugar: ${lugares[position].numLugar}"
            if (lugares[position].ocupado == 0){
                textViewEstado.setTextColor(Color.GREEN)
                textViewEstado.text = "Estado: livre"
            }else if(lugares[position].ocupado == 1){
                textViewEstado.setTextColor(Color.RED)
                textViewEstado.text = "Estado: ocupado"
            }


            return rootView
        }
    }
}
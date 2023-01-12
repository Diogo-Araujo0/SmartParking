package ipca.djpm.smartparking.ui.home

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ipca.djpm.smartparking.DatabaseHelper
import ipca.djpm.smartparking.R
import ipca.djpm.smartparking.databinding.FragmentHomeBinding
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var userID : Int? = null
    private var tipoUserID : Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val sharedPreferences = requireContext().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        userID = sharedPreferences.getInt("USER_ID", -1)
        tipoUserID = sharedPreferences.getInt("TIPO_USER_ID", -1)

        val buttonVeiculos = binding.buttonVeiculos
        val buttonEntrarSair = binding.buttonEntrarSair
        val buttonPedirLugar = binding.buttonPedirLugar
        val buttonVeiculosAdmin = binding.buttonVeiculosAdmin
        val buttonUtilizadores = binding.buttonUtilizadores
        val buttonAtribuirLugar = binding.buttonAtribuirLugar
        val textViewBoasVindas = binding.textViewBoasVindas
        val progressBar = binding.progressBarHome
        val textViewLugar = binding.textViewLugar
        progressBar.visibility = View.VISIBLE

        val databaseHelper = DatabaseHelper()
        Handler(Looper.getMainLooper()).postDelayed(
            {
                if(userID != -1) {
                    var query = "SELECT nome FROM Aluno JOIN Aluno_Utilizador ON Aluno.numAluno=Aluno_Utilizador.numAluno JOIN Utilizador ON Aluno_Utilizador.utilizadorID=Utilizador.utilizadorID WHERE Utilizador.utilizadorID=${userID}"
                    var resultQuery = context?.let { databaseHelper.selectQuery(query, it) }
                    if (resultQuery != null) {
                        if (resultQuery.next()) {
                            var nome = resultQuery.getString("nome")
                            nome = nome.replace("\\s+".toRegex(), " ")
                            textViewBoasVindas.text = "Olá, ${nome}"
                        }
                    }else {
                        Toast.makeText(context, "Erro ao obter nome", Toast.LENGTH_SHORT).show()
                        progressBar.visibility = View.INVISIBLE
                    }
                    query = "SELECT nomeEscola, numLugar FROM Utilizador JOIN Escola ON Utilizador.escolaID=Escola.escolaID WHERE Utilizador.utilizadorID=${userID}"
                    resultQuery = context?.let { databaseHelper.selectQuery(query, it) }
                    if (resultQuery != null) {
                        if (resultQuery.next()) {
                            var nomeEscola = resultQuery.getString("nomeEscola")
                            var numLugar = resultQuery.getString("numLugar")
                            nomeEscola = nomeEscola.replace("\\s+".toRegex(), " ")
                            numLugar = numLugar.replace("\\s+".toRegex(), "")
                            textViewLugar.text = "Lugar: ${nomeEscola} - ${numLugar}"
                        }
                    }else {
                        Toast.makeText(context, "Erro ao obter lugar", Toast.LENGTH_SHORT).show()
                        progressBar.visibility = View.INVISIBLE
                    }

                    progressBar.visibility = View.INVISIBLE
                    if(tipoUserID != 1) { //0-temp 1-admin
                        buttonVeiculos.visibility = View.VISIBLE
                        buttonEntrarSair.visibility = View.VISIBLE
                        buttonPedirLugar.visibility = View.VISIBLE
                        textViewLugar.visibility = View.VISIBLE
                    }else{
                        buttonVeiculosAdmin.visibility = View.VISIBLE
                        buttonUtilizadores.visibility = View.VISIBLE
                        buttonAtribuirLugar.visibility = View.VISIBLE
                    }

                    textViewBoasVindas.visibility = View.VISIBLE


                }
            }, 100)

        buttonVeiculos.setOnClickListener{
            findNavController().navigate(R.id.action_navigation_home_to_navigation_veiculos)
        }

        buttonVeiculosAdmin.setOnClickListener{
            findNavController().navigate(R.id.action_navigation_home_to_navigation_veiculos_admin)
        }

        buttonUtilizadores.setOnClickListener{
            findNavController().navigate(R.id.action_navigation_home_to_navigation_atribuir_utilizadores)
        }

        buttonAtribuirLugar.setOnClickListener{
            findNavController().navigate(R.id.action_navigation_home_to_navigation_atribuir_lugares)
        }

        buttonPedirLugar.setOnClickListener{
            val calendar: Calendar = Calendar.getInstance()
            val formatter = SimpleDateFormat("yyyy-MM-dd")
            val time = calendar.time
            val currentDate = formatter.format(time)
            val day: Int = calendar.get(Calendar.DAY_OF_WEEK)
            val hour: Int =calendar.get(Calendar.HOUR_OF_DAY)

            var query = "SELECT numLugar, escolaID FROM Utilizador WHERE utilizadorID=${userID}"
            var resultQuery = context?.let { databaseHelper.selectQuery(query, it) }
            if (resultQuery != null) {
                if (resultQuery!!.next()) {
                    val numLugar = resultQuery!!.getInt("numLugar")
                    val escolaID = resultQuery!!.getInt("escolaID")
                    if (numLugar == 0 && escolaID == 0){
                        pedirLugar(day, hour, currentDate)
                    }else{
                        Toast.makeText(context, "Já tem lugar atribuido", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        buttonEntrarSair.setOnClickListener{
            progressBar.visibility = View.VISIBLE
            Handler(Looper.getMainLooper()).postDelayed(
                {
                    if(userID != -1) {
                        var query = "SELECT ocupado FROM Estacionamento JOIN Utilizador on Utilizador.numLugar=Estacionamento.numLugar AND Utilizador.escolaID=Estacionamento.escolaID WHERE Utilizador.utilizadorID=${userID}"
                        val resultQuery = context?.let { databaseHelper.selectQuery(query, it) }
                        if (resultQuery != null) {
                            if (resultQuery.next()) {
                                var ocupado = resultQuery.getInt("ocupado")
                                if (ocupado == 1){
                                    ocupado = 0
                                }else{
                                    ocupado = 1
                                }
                                query = "UPDATE Estacionamento SET Estacionamento.ocupado=${ocupado} FROM Estacionamento INNER JOIN Utilizador Util on Util.numLugar=Estacionamento.numLugar AND Util.escolaID=Estacionamento.escolaID WHERE Util.utilizadorID=${userID}"
                                val result = context?.let { databaseHelper.executeQuery(query, it) }
                                if (result == true){
                                    if(ocupado == 1){
                                        Toast.makeText(context, "Lugar marcado como ocupado", Toast.LENGTH_SHORT).show()
                                    }else{
                                        Toast.makeText(context, "Lugar marcado como livre", Toast.LENGTH_SHORT).show()
                                        if(tipoUserID == 0) {
                                            query = "UPDATE Utilizador SET numLugar = NULL, escolaID = NULL WHERE utilizadorID = ${userID}"
                                            context?.let { databaseHelper.executeQuery(query, it) }
                                            textViewLugar.text = "Lugar: sem lugar"
                                        }
                                    }
                                }else{
                                    Toast.makeText(context, "Erro ao alterar ocupação de lugar", Toast.LENGTH_SHORT).show()
                                }
                            }else{
                                Toast.makeText(context, "Erro ao obter ocupação de lugar", Toast.LENGTH_SHORT).show()
                            }
                        }
                        else{
                            Toast.makeText(context, "Erro ao alterar ocupação de lugar", Toast.LENGTH_SHORT).show()
                        }
                        progressBar.visibility = View.INVISIBLE
                    }
                }, 1)
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun pedirLugar(dayID: Int, hour: Int, currentDate:String){
        var query = "SELECT DISTINCT e.numLugar, e.escolaID  FROM Estacionamento e WHERE e.ocupado = 0 AND NOT EXISTS(SELECT 1 FROM Utilizador u JOIN Aluno_Utilizador au ON u.utilizadorID = au.utilizadorID JOIN Aluno_Horario ah ON au.numAluno = ah.numAluno JOIN Horario h ON ah.horarioID = h.horarioID JOIN DiaSemana d ON h.diaSemanaID = d.diaSemanaID WHERE e.escolaID = u.escolaID AND e.numLugar = u.numLugar AND e.numLugar = e.numLugar AND d.diaSemanaID = ${dayID} AND DATEPART(hour, horaInicio) >= ${hour} AND DATEPART(hour, horaInicio) < 23 AND e.ocupado = 1) EXCEPT SELECT DISTINCT Estacionamento.numLugar, Estacionamento.escolaID  FROM Estacionamento JOIN Utilizador on Estacionamento.numLugar = Utilizador.numLugar AND Utilizador.escolaID=Estacionamento.escolaID WHERE Estacionamento.ocupado = 0 GROUP BY Estacionamento.numLugar, Estacionamento.escolaID ORDER BY escolaID, numLugar"
        var progressBarHome = binding.progressBarHome
        var textViewLugar = binding.textViewLugar
        progressBarHome.visibility = View.VISIBLE
        val databaseHelper = DatabaseHelper()
        Handler(Looper.getMainLooper()).postDelayed(
            {
                var resultQuery = context?.let { databaseHelper.selectQuery(query, it) }
                if (resultQuery != null) {
                    if (resultQuery.next()) {
                        val numLugar = resultQuery.getInt("numLugar")
                        val escolaID = resultQuery.getInt("escolaID")
                        if(tipoUserID == 0 || tipoUserID == 1){
                            query = "UPDATE Utilizador set numLugar=${numLugar}, escolaID=${escolaID} WHERE utilizadorID=${userID}"
                            val result = context?.let { databaseHelper.executeQuery(query, it) }
                            if(result==true){
                                query = "SELECT nomeEscola FROM Escola WHERE escolaID=${escolaID}"
                                resultQuery = context?.let { databaseHelper.selectQuery(query, it) }
                                if (resultQuery != null) {
                                    if (resultQuery.next()) {
                                        var nomeEscola = resultQuery.getString("nomeEscola")
                                        nomeEscola = nomeEscola.replace("\\s+".toRegex(), " ")
                                        textViewLugar.text = "Lugar: ${nomeEscola} - ${numLugar}"
                                        progressBarHome.visibility = View.INVISIBLE
                                    }
                                }
                            }else{
                                Toast.makeText(context, "Erro ao atribuir lugar", Toast.LENGTH_SHORT).show()
                                progressBarHome.visibility = View.INVISIBLE
                            }
                        }else if(tipoUserID != 1){
                            query = "SELECT MAX(DISTINCT DATEPART(hour, horaFim)) as hora FROM Horario JOIN Aluno_Horario ON Aluno_Horario.horarioID = horario.horarioID JOIN Aluno_Utilizador ON Aluno_Horario.numAluno = Aluno_Utilizador.numAluno WHERE Horario.diaSemanaID=${dayID} AND utilizadorID = ${userID}"
                            resultQuery = context?.let { databaseHelper.selectQuery(query, it) }
                            if (resultQuery != null) {
                                resultQuery.next()
                                if(resultQuery.getInt("hora") != 0){
                                    val hora = resultQuery.getInt("hora")
                                    val tempoLimite = "${currentDate} ${hora}:00"
                                    query = "UPDATE Utilizador set numLugar=${numLugar}, escolaID=${escolaID}, tempoLimite='${tempoLimite}' WHERE utilizadorID=${userID}"
                                    val result = context?.let { databaseHelper.executeQuery(query, it) }
                                    if(result==true){
                                        query = "SELECT nomeEscola FROM Escola WHERE escolaID=${escolaID}"
                                        resultQuery = context?.let { databaseHelper.selectQuery(query, it) }
                                        if (resultQuery != null) {
                                            if (resultQuery.next()) {
                                                var nomeEscola = resultQuery.getString("nomeEscola")
                                                nomeEscola = nomeEscola.replace("\\s+".toRegex(), " ")
                                                textViewLugar.text = "Lugar: ${nomeEscola} - ${numLugar}"
                                                progressBarHome.visibility = View.INVISIBLE
                                            }
                                        }
                                    }else{
                                        Toast.makeText(context, "Erro ao atribuir lugar", Toast.LENGTH_SHORT).show()
                                    }
                                }else{
                                    query = "UPDATE Utilizador set numLugar=${numLugar}, escolaID=${escolaID} WHERE utilizadorID=${userID}"
                                    val result = context?.let { databaseHelper.executeQuery(query, it) }
                                    if(result==true){
                                        query = "SELECT nomeEscola FROM Escola WHERE escolaID=${escolaID}"
                                        resultQuery = context?.let { databaseHelper.selectQuery(query, it) }
                                        if (resultQuery != null) {
                                            if (resultQuery.next()) {
                                                var nomeEscola = resultQuery.getString("nomeEscola")
                                                nomeEscola = nomeEscola.replace("\\s+".toRegex(), " ")
                                                textViewLugar.text = "Lugar: ${nomeEscola} - ${numLugar}"
                                                progressBarHome.visibility = View.INVISIBLE
                                            }
                                        }
                                    }else{
                                        Toast.makeText(context, "Erro ao atribuir lugar", Toast.LENGTH_SHORT).show()
                                        progressBarHome.visibility = View.INVISIBLE
                                    }
                                }
                            }
                        }
                    }
                }else {
                    Toast.makeText(context, "Sem lugares disponíveis", Toast.LENGTH_SHORT).show()
                    progressBarHome.visibility = View.INVISIBLE
                }
            }, 100)
    }
}
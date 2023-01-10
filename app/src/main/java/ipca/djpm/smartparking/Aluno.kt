package ipca.djpm.smartparking

import java.util.Date

class Aluno {
    var numAluno : Int
    var nome: String
    var cartaoCidadao: Int
    var dataNascimento: Date
    var morada : String
    var sexo : String
    var codPostal : Int

    constructor(numAluno: Int, nome: String, cartaoCidadao: Int, dataNascimento: Date, morada: String, sexo: String, codPostal: Int) {
        this.numAluno = numAluno
        this.nome = nome
        this.cartaoCidadao = cartaoCidadao
        this.dataNascimento = dataNascimento
        this.morada = morada
        this.sexo = sexo
        this.codPostal = codPostal
    }
}
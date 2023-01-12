package ipca.djpm.smartparking

class Veiculo {
    var matricula : String
    var tipoveiculo: String
    var username : String?

    constructor(matricula: String, tipoveiculo: String, username: String?) {
        this.matricula = matricula
        this.tipoveiculo = tipoveiculo
        this.username = username
    }
}
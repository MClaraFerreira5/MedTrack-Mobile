package com.example.piec_1.ui.navigation

object AppRoutes {
    const val INICIAL = "TelaInicial"
    const val LOGIN = "TelaLogin"
    const val PRINCIPAL = "TelaPrincipal"
    const val ESQUECI_SENHA = "TelaEsqueciSenha"
    const val REDEFINIR_SENHA = "TelaRedefinirSenha"
    const val CONFIRMACAO = "TelaConfirmacao"
    const val CAMERA = "TelaCamera"
    const val CAMERA_FROM_NOTIFICATION = "TelaCamera/{medicamentoId}/{horario}"

    fun cameraDeepLink(medicamentoId: Long, horario: String): String {
        return "app://telaCamera/$medicamentoId/$horario"
    }
}

package com.example.piec_1.ui.navigation

import android.util.Log
import androidx.navigation.NavController
import com.example.piec_1.domain.model.Medicamento

object NavigationManager {
    private var navController: NavController? = null
    private var pendingMedicamento: Medicamento? = null
    private var isNavigating = false

    fun init(controller: NavController) {
        Log.d("NavigationManager", "🎮 NavController inicializado")
        navController = controller
        tryNavigate()
    }

    fun setMedicamento(medicamento: Medicamento) {
        Log.d("NavigationManager", "📦 Medicamento recebido: ${medicamento.nome}")
        pendingMedicamento = medicamento
        isNavigating = false
        tryNavigate()
    }

    private fun tryNavigate() {
        if (isNavigating) {
            Log.d("NavigationManager", "⏳ Já está navegando, ignorando")
            return
        }

        if (navController != null && pendingMedicamento != null) {
            isNavigating = true
            val medicamento = pendingMedicamento!!
            pendingMedicamento = null

            Log.d("NavigationManager", "🚀 Navegando para confirmação: ${medicamento.nome}")

            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                try {
                    navController?.navigate("TelaConfirmacao") {
                        popUpTo("TelaInicial") { inclusive = true }
                        launchSingleTop = true
                    }
                    Log.d("NavigationManager", "✅ Navegação executada com sucesso")
                } catch (e: Exception) {
                    Log.e("NavigationManager", "❌ Erro na navegação: ${e.message}")
                    isNavigating = false
                }
            }, 300)
        } else {
            Log.d("NavigationManager", "⏳ Aguardando... NavController: ${navController != null}, Medicamento: ${pendingMedicamento != null}")
        }
    }
}
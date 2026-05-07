package com.example.piec_1.ui.navigation

import android.util.Log
import androidx.navigation.NavController
import com.example.piec_1.domain.model.Medicamento
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object NavigationManager {
    private var navController: NavController? = null
    private var pendingMedicamento: Medicamento? = null
    private var isNavigating = false

    private val _shouldNavigate = MutableStateFlow<Medicamento?>(null)
    val shouldNavigate = _shouldNavigate.asStateFlow()

    fun init(controller: NavController) {
        Log.d("NavigationManager", "🎮 NavController inicializado")
        navController = controller
        // Tenta navegar se houver medicamento pendente
        pendingMedicamento?.let {
            setMedicamento(it)
        }
    }

    fun clearController() {
        Log.d("NavigationManager", "🗑️ Limpando NavController")
        navController = null
    }

    fun setMedicamento(medicamento: Medicamento) {
        Log.d("NavigationManager", "📦 Medicamento recebido: ${medicamento.nome}")

        if (navController == null) {
            Log.d("NavigationManager", "NavController é nulo, salvando para depois")
            pendingMedicamento = medicamento
            return
        }

        pendingMedicamento = null
        _shouldNavigate.value = medicamento
    }

    fun navigateToConfirmation(medicamento: Medicamento) {
        if (isNavigating) {
            Log.d("NavigationManager", "⏳ Já está navegando, ignorando")
            return
        }

        if (navController == null) {
            Log.d("NavigationManager", "NavController é nulo, salvando para depois")
            pendingMedicamento = medicamento
            return
        }

        isNavigating = true
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
            } finally {
                isNavigating = false
                _shouldNavigate.value = null
            }
        }, 500)
    }

    fun reset() {
        pendingMedicamento = null
        isNavigating = false
        _shouldNavigate.value = null
    }
}
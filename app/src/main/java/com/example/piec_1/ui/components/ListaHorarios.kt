package com.example.piec_1.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.piec_1.domain.model.Medicamento
import com.example.piec_1.domain.usecase.organizeMedicationsByDay
import com.example.piec_1.ui.components.EmptyCard
import com.example.piec_1.ui.components.HorarioCard
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ListaHorarios(medicamentos: List<Medicamento>) {
    val medicamentosAgrupados = remember(medicamentos) {
        organizeMedicationsByDay(medicamentos, LocalDate.now())
    }

    if (medicamentos.isEmpty()) {
        EmptyCard()
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            medicamentosAgrupados.forEach { (date, itemsDoDia) ->
                stickyHeader {
                    DayHeader(date)
                }

                items(
                    items = itemsDoDia,
                    key = { item -> item.id }
                ) { item ->
                    HorarioCard(item = item)
                }
            }
        }
    }
}

@Composable
fun DayHeader(date: LocalDate) {
    val title = when {
        date.isEqual(LocalDate.now()) -> "Hoje"
        date.isEqual(LocalDate.now().plusDays(1)) -> "Amanhã"
        else -> date.format(DateTimeFormatter.ofPattern("EEEE, dd/MM"))
    }

    Text(
        text = title.replaceFirstChar { it.uppercase() },
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.secondary,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background) // Evita sobreposição no stickyHeader
            .padding(vertical = 8.dp, horizontal = 4.dp)
    )
}
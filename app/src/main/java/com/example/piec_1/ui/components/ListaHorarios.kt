package com.example.piec_1.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.piec_1.R
import com.example.piec_1.model.Medicamento
import com.example.piec_1.notifications.NotificationHelper.formatarHorario
import com.example.piec_1.ui.theme.ButtonColor
import com.example.piec_1.ui.theme.PrimaryColor
import com.example.piec_1.ui.theme.SecondaryColor
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ListaHorarios(medicamentos: List<Medicamento>, currentDate: LocalDate = LocalDate.now()) {
    if (medicamentos.isEmpty()) {
        EmptyState()
    } else {
        val medicamentosAgrupados = organizeMedicationsByDay(medicamentos, currentDate)

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            medicamentosAgrupados.forEach { (dayOffset, medicamentosDoDia) ->
                if (dayOffset == 0) {
                    Text(
                        text = "Hoje",
                        style = MaterialTheme.typography.titleMedium,
                        color = SecondaryColor,
                        modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                    )
                } else {
                    val date = currentDate.plusDays(dayOffset.toLong())
                    Text(
                        text = date.format(DateTimeFormatter.ofPattern("EEEE, dd/MM")),
                        style = MaterialTheme.typography.titleMedium,
                        color = SecondaryColor,
                        modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                    )
                }

                medicamentosDoDia.sortedBy { it.horario }.forEach { item ->
                    BlocoHorario(item.nomeMedicamento, item.horario, item.isContinuous)
                }
            }
        }
    }
}

private fun organizeMedicationsByDay(medicamentos: List<Medicamento>, currentDate: LocalDate): Map<Int, List<MedicationItem>> {
    val result = mutableMapOf<Int, MutableList<MedicationItem>>()

    medicamentos.forEach { medicamento ->
        if (medicamento.usoContinuo) {
            medicamento.horarios.forEach { horario ->
                result.getOrPut(0) { mutableListOf() }.add(
                    MedicationItem(medicamento.nome, horario.toFomattedTime(), true))
            }
        } else {
            val horariosPorDia = medicamento.horarios.chunked(medicamento.horarios.size / medicamento.horarios.distinct().size)

            horariosPorDia.forEachIndexed { dayOffset, horariosDoDia ->
                horariosDoDia.distinct().forEach { horario ->
                    result.getOrPut(dayOffset) { mutableListOf() }.add(
                        MedicationItem(medicamento.nome, horario.toFomattedTime(), false)
                    )
                }
            }
        }
    }

    return result.toSortedMap()
}

private fun String.toFomattedTime(): String {
    return formatarHorario(this)}

data class MedicationItem(
    val nomeMedicamento: String,
    val horario: String,
    val isContinuous: Boolean
)

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .border(1.dp, Color.LightGray, shape = RoundedCornerShape(8.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_empty),
                contentDescription = "Nenhum medicamento cadastrado",
                tint = PrimaryColor,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Nenhum medicamento cadastrado.",
                fontSize = 16.sp,
                color = PrimaryColor
            )
        }
    }
}

@Composable
private fun BlocoHorario(nomeMedicamento: String, horario: String, isContinuous: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = if (isContinuous) R.drawable.ic_continuous else R.drawable.ic_temporary),
            contentDescription = "Tipo de medicamento",
            tint = if (isContinuous) PrimaryColor else ButtonColor,
            modifier = Modifier
                .size(36.dp)
                .padding(end = 12.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = nomeMedicamento,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            if (!isContinuous) {
                Text(
                    text = "Uso temporário",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        Text(
            text = horario,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = if (isContinuous) PrimaryColor else ButtonColor,
        )
    }


}
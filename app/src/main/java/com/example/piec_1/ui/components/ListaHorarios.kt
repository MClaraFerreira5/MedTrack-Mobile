package com.example.piec_1.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.piec_1.R
import com.example.piec_1.model.Medicamento
import com.example.piec_1.notifications.NotificationHelper.formatarHorario
import com.example.piec_1.ui.theme.ButtonColor
import com.example.piec_1.ui.theme.PrimaryColor
import com.example.piec_1.ui.theme.SecondaryColor
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun ListaHorarios(medicamentos: List<Medicamento>, currentDate: LocalDate = LocalDate.now()) {
    if (medicamentos.isEmpty()) {
        EmptyState()
    } else {
        val maxDaysToShow = 7
        val medicamentosAgrupados = organizeMedicationsByDay(medicamentos, currentDate, maxDaysToShow)

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            medicamentosAgrupados.forEach { (date, medicamentosDoDia) ->
                val dayTitle = when {
                    date.isEqual(currentDate) -> "Hoje"
                    date.isEqual(currentDate.plusDays(1)) -> "Amanhã"
                    else -> date.format(DateTimeFormatter.ofPattern("EEEE, dd/MM"))
                }

                Text(
                    text = dayTitle,
                    style = MaterialTheme.typography.titleMedium,
                    color = SecondaryColor,
                    modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                )

                medicamentosDoDia.sortedBy { it.horario }.forEach { item ->
                    val isGenerico = medicamentos.any {
                        it.nome.equals("MEDICAMENTO GENÉRICO", ignoreCase = true) &&
                                it.compostoAtivo == item.nomeMedicamento
                    }
                    BlocoHorario(
                        nomeMedicamento = item.nomeMedicamento,
                        horario = item.horario,
                        isContinuous = item.isContinuous,
                        isGenerico = isGenerico
                    )
                }
            }
        }
    }
}

fun organizeMedicationsByDay(
    medicamentos: List<Medicamento>,
    currentDate: LocalDate,
    maxDaysToShow: Int = 7
): Map<LocalDate, List<MedicationItem>> {
    val result = mutableMapOf<LocalDate, MutableList<MedicationItem>>()
    val daysToShow = getDatesBetween(currentDate, currentDate.plusDays(maxDaysToShow.toLong() - 1))

    medicamentos.forEach { medicamento ->
        val nomeExibicao = if (medicamento.nome.equals("MEDICAMENTO GENÉRICO", ignoreCase = true)) {
            medicamento.compostoAtivo ?: medicamento.nome
        } else {
            medicamento.nome
        }

        if (medicamento.usoContinuo) {
            daysToShow.forEach { date ->
                medicamento.horarios.forEach { horario ->
                    result.getOrPut(date) { mutableListOf() }.add(
                        MedicationItem(nomeExibicao, horario.toFormattedTime(), true)
                    )
                }
            }
        } else {
            var currentDayIndex = 0
            var horariosPorDia = mutableListOf<LocalTime>()

            medicamento.horarios.forEach { horarioStr ->
                val horario = LocalTime.parse(horarioStr)

                if (horariosPorDia.isNotEmpty() && horario <= horariosPorDia.last()) {
                    currentDayIndex++
                    horariosPorDia.clear()
                }

                if (currentDayIndex < daysToShow.size) {
                    val targetDate = daysToShow[currentDayIndex]
                    result.getOrPut(targetDate) { mutableListOf() }.add(
                        MedicationItem(nomeExibicao, horarioStr.toFormattedTime(), false)
                    )
                    horariosPorDia.add(horario)
                }
            }
        }
    }

    // Ordena os horários dentro de cada dia
    result.forEach { (_, items) ->
        items.sortBy { LocalTime.parse(it.horario) }
    }

    return result.toSortedMap()
}

private fun String.toFormattedTime(): String {
    return formatarHorario(this)}

data class MedicationItem(
    val nomeMedicamento: String,
    val horario: String,
    val isContinuous: Boolean
)

private fun getDatesBetween(startDate: LocalDate, endDate: LocalDate): List<LocalDate> {
    val dates = mutableListOf<LocalDate>()
    var currentDate = startDate

    while (!currentDate.isAfter(endDate)) {
        dates.add(currentDate)
        currentDate = currentDate.plusDays(1)
    }

    return dates
}

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
                tint = Color.Gray,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Nenhum medicamento cadastrado.",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun BlocoHorario(
    nomeMedicamento: String,
    horario: String,
    isContinuous: Boolean,
    isGenerico: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 80.dp)
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
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
                color = Color.Black,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isGenerico) {
                    Text(
                        text = "Genérico",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(end = if (!isContinuous) 8.dp else 0.dp)
                    )
                }
                if (!isContinuous) {
                    Text(
                        text = "Uso temporário",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
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
package com.example.piec_1.domain.usecase

import com.example.piec_1.domain.model.Medicamento
import com.example.piec_1.domain.model.MedicationItem
import com.example.piec_1.utils.toFormattedTime
import java.time.LocalDate
import java.time.LocalTime

fun organizeMedicationsByDay(
    medicamentos: List<Medicamento>,
    currentDate: LocalDate,
    maxDaysToShow: Int = 7
): Map<LocalDate, List<MedicationItem>> {
    val result = mutableMapOf<LocalDate, MutableList<MedicationItem>>()
    val daysToShow = getDatesBetween(currentDate, currentDate.plusDays(maxDaysToShow.toLong() - 1))

    medicamentos.forEach { medicamento ->
        val ehGenerico = medicamento.nome.equals("MEDICAMENTO GENÉRICO", ignoreCase = true)
        val nomeExibicao = if (ehGenerico) medicamento.compostoAtivo else medicamento.nome

        if (medicamento.usoContinuo) {
            daysToShow.forEach { date ->
                medicamento.horarios.forEach { horario ->
                    result.getOrPut(date) { mutableListOf() }.add(
                        MedicationItem(
                            nomeExibicao = nomeExibicao,
                            horario = horario.toFormattedTime(),
                            isContinuous = true,
                            isGenerico = ehGenerico
                        )
                    )
                }
            }
        } else {
            var currentDayIndex = 0
            val horariosPorDia = mutableListOf<LocalTime>()

            medicamento.horarios.forEach { horarioStr ->
                val horario = LocalTime.parse(horarioStr)

                if (horariosPorDia.isNotEmpty() && horario <= horariosPorDia.last()) {
                    currentDayIndex++
                    horariosPorDia.clear()
                }

                if (currentDayIndex < daysToShow.size) {
                    val targetDate = daysToShow[currentDayIndex]
                    result.getOrPut(targetDate) { mutableListOf() }.add(
                        MedicationItem(
                            nomeExibicao = nomeExibicao,
                            horario = horarioStr.toFormattedTime(),
                            isContinuous = false,
                            isGenerico = ehGenerico
                        )
                    )
                    horariosPorDia.add(horario)
                }
            }
        }
    }

    result.forEach { (_, items) ->
        items.sortBy { LocalTime.parse(it.horario) }
    }

    return result.toSortedMap()
}

fun getDatesBetween(startDate: LocalDate, endDate: LocalDate): List<LocalDate> {
    val dates = mutableListOf<LocalDate>()
    var currentDate = startDate

    while (!currentDate.isAfter(endDate)) {
        dates.add(currentDate)
        currentDate = currentDate.plusDays(1)
    }

    return dates
}
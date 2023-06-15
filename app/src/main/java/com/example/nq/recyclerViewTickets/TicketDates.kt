package com.example.nq.recyclerViewTickets

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*

class TicketDates {

    // Función para obtener el timestamp de la compra del ticket
    fun getCurrentTimestamp(): String {
        return DateTimeFormatter.ISO_INSTANT.format(Instant.now()).toString()
    }

    // Función para obtener el mes y el año
    fun getCurrentMonthAndYear(): String {
        val calendar = Calendar.getInstance()
        val month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale("es", "ES"))
        val year = calendar.get(Calendar.YEAR).toString()
        return "$month-$year"
    }

    // Función para obtener el mes y el año
    fun getCurrentMonthAndYearNumberFormat(): String {
        val calendar = Calendar.getInstance()
        // Calendar.MONTH is zero-based, so we add 1 to it to get the actual month.
        val month = (calendar.get(Calendar.MONTH) + 1).toString().padStart(2, '0')
        val year = calendar.get(Calendar.YEAR).toString()
        return "$year$month"
    }

    // Función para obtener el mes y el año
    fun getPreviousMonthAndYear(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -1)
        val month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale("es", "ES"))
        val year = calendar.get(Calendar.YEAR).toString()
        return "$month-$year"
    }

    fun formatDate(dateStr: String): String {
        // Definimos el formato de entrada
        val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        // Parseamos la fecha en formato "DD/MM/YYYY" a objeto Date
        val date = inputFormat.parse(dateStr) ?: return ""
        // Definimos el formato de salida
        val outputFormat = SimpleDateFormat("EEEE, d 'de' MMMM 'de' yyyy", Locale("es", "ES"))
        // Devolvemos la fecha formateada como "Día de la semana, Día de mes de Año"
        return outputFormat.format(date)
    }
}
package com.example.quanlydatve_sqlite.utils

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

object FormatDate {
    fun formatNgay(ngayGoc: String): String {
        return try {
            val inputFormats = listOf(
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()),
                SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            )
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            val parsedDate = inputFormats.firstNotNullOfOrNull { format ->
                try {
                    format.parse(ngayGoc)
                } catch (e: Exception) {
                    null
                }
            }

            parsedDate?.let { outputFormat.format(it) } ?: ngayGoc
        } catch (e: Exception) {
            ngayGoc
        }
    }
    fun convertToISO8601(input: String): String {
        val possibleFormats = listOf(
            "yyyy-MM-dd",
            "yyyy/MM/dd",
            "dd/MM/yyyy",
            "yyyy-MM-dd HH:mm",
            "yyyy-MM-dd HH:mm:ss"
        )
        val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        for (formatStr in possibleFormats) {
            try {
                val sdf = SimpleDateFormat(formatStr, Locale.getDefault())
                val date = sdf.parse(input)
                if (date != null) {
                    return outputFormat.format(date)
                }
            } catch (_: Exception) {}
        }
        return "" // Trả về rỗng nếu không parse được
    }

}
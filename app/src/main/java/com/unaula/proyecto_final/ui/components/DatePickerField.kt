package com.unaula.proyecto_final.ui.components

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun DatePickerField(
    label: String,
    selectedTimestamp: Long?,
    onDateSelected: (Long) -> Unit,
    modifier: Modifier = Modifier,
    minDate: Long? = null,
    maxDate: Long? = null
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    selectedTimestamp?.let { calendar.timeInMillis = it }

    val formattedDate = selectedTimestamp?.let {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it))
    } ?: "Seleccionar fecha"

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val cal = Calendar.getInstance()
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            onDateSelected(cal.timeInMillis)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    minDate?.let { datePickerDialog.datePicker.minDate = it }
    maxDate?.let { datePickerDialog.datePicker.maxDate = it }

    OutlinedTextField(
        value = formattedDate,
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.Event,
                contentDescription = label,
                modifier = Modifier.clickable { datePickerDialog.show() }
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .clickable { datePickerDialog.show() }
    )
}

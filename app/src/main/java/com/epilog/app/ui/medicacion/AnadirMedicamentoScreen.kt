package com.epilog.app.ui.medicacion

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.epilog.app.data.entity.Medicamento
import com.epilog.app.data.entity.Toma
import com.epilog.app.ui.components.SeccionTitulo
import com.epilog.app.ui.components.SelectorBoton
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnadirMedicamentoScreen(
    onVolver: () -> Unit,
    onGuardar: (Medicamento, List<Toma>) -> Unit
) {
    val context = LocalContext.current
    var nombre by remember { mutableStateOf("") }
    var dosis by remember { mutableStateOf("") }
    var tomasAlDia by remember { mutableIntStateOf(1) }
    
    val defaultHoras = listOf("08:00", "14:00", "20:00", "23:00")
    val horasSeleccionadas = remember { mutableStateListOf(*defaultHoras.toTypedArray()) }

    val verdePrimary = Color(0xFF2E7D32)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Añadir Medicamento", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = verdePrimary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del medicamento") },
                placeholder = { Text("Ej: Levetiracetam") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = dosis,
                onValueChange = { dosis = it },
                label = { Text("Dosis") },
                placeholder = { Text("Ej: 500 mg") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            SeccionTitulo("Tomas al día")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (i in 1..4) {
                    SelectorBoton(
                        text = "$i",
                        selected = tomasAlDia == i,
                        modifier = Modifier.weight(1f)
                    ) {
                        tomasAlDia = i
                    }
                }
            }

            SeccionTitulo("Horarios de las tomas")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                for (i in 0 until tomasAlDia) {
                    TomaHoraRow(
                        index = i,
                        hora = horasSeleccionadas[i],
                        onHoraClick = {
                            val (h, m) = horasSeleccionadas[i].split(":").map { it.toInt() }
                            TimePickerDialog(context, { _, hour, minute ->
                                horasSeleccionadas[i] = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
                            }, h, m, true).show()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    if (nombre.isNotBlank() && dosis.isNotBlank()) {
                        val med = Medicamento(
                            nombre = nombre,
                            dosis = dosis,
                            tomasAlDia = tomasAlDia,
                            fechaInicio = System.currentTimeMillis(),
                            activo = true
                        )
                        val tomas = (0 until tomasAlDia).map { i ->
                            Toma(medicamentoId = 0, hora = horasSeleccionadas[i], alarmaActivada = true)
                        }
                        onGuardar(med, tomas)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = verdePrimary),
                shape = RoundedCornerShape(12.dp),
                enabled = nombre.isNotBlank() && dosis.isNotBlank()
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("GUARDAR MEDICAMENTO", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun TomaHoraRow(index: Int, hora: String, onHoraClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Toma ${index + 1}", fontWeight = FontWeight.Medium)
            
            Button(
                onClick = onHoraClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = hora, fontWeight = FontWeight.Bold)
            }
        }
    }
}

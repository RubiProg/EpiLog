package com.epilog.app.ui.inicio

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.epilog.app.data.dao.TomaConMedicamento
import com.epilog.app.data.entity.Perfil

@Composable
fun InicioScreen(
    perfil: Perfil?,
    crisisEsteMes: Int,
    ultimaCrisisTexto: String,
    tomasHoy: List<TomaConMedicamento>,
    onRegistrarCrisis: () -> Unit,
    onVerPerfil: () -> Unit
) {
    val verdePrimary = Color(0xFF0C6445) // Verde oscuro del mockup
    val verde100 = Color(0xFFC8E6C9)
    val coralMockup = Color(0xFFE05F34) // Color naranja del botón
    val rojo50 = Color(0xFFFDE8E8)
    val beigeCard = Color(0xFFF7F2E9)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    verdePrimary,
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                )
                .padding(top = 24.dp, start = 20.dp, end = 20.dp, bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    val nombre = perfil?.nombre?.substringBefore(" ") ?: "Jenifer"
                    Text(
                        text = "Buenos días, $nombre",
                        color = Color(0xFFB0D5C1),
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Mi epilepsia",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {
            // Resumen en tarjetas
            Row(modifier = Modifier.fillMaxWidth()) {
                ResumenCard(
                    label = "Última crisis",
                    valor = ultimaCrisisTexto,
                    modifier = Modifier.weight(1f),
                    backgroundColor = beigeCard
                )
                Spacer(modifier = Modifier.width(16.dp))
                ResumenCard(
                    label = "Este mes",
                    valor = "$crisisEsteMes crisis",
                    modifier = Modifier.weight(1f),
                    backgroundColor = beigeCard
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón Registrar
            Button(
                onClick = onRegistrarCrisis,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                colors = ButtonDefaults.buttonColors(containerColor = coralMockup),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("+ Registrar crisis ahora", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Contacto Emergencia
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = rojo50)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Contacto de emergencia",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF9B2C2C)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${perfil?.contactoNombre ?: "María García"} · mamá",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFC53030)
                    )
                    Text(
                        text = perfil?.contactoTelefono ?: "+34 612 345 678",
                        fontSize = 16.sp,
                        color = Color(0xFFE53E3E)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "MEDICACIÓN DE HOY",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Lista de medicación
            tomasHoy.forEach { tomaConMed ->
                MedicationItem(tomaConMed)
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            // Mock de medicación si está vacío para visualización
            if (tomasHoy.isEmpty()) {
                MedicationItemMock("Levetiracetam 500mg", "08:00 - mañana", true)
                Spacer(modifier = Modifier.height(12.dp))
                MedicationItemMock("Levetiracetam 500mg", "21:00 - noche", false)
            }
        }
    }
}

@Composable
fun ResumenCard(label: String, valor: String, backgroundColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = label, fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = valor, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}

@Composable
fun MedicationItem(tomaConMed: TomaConMedicamento) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F2E9))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "${tomaConMed.medicamento.nombre} ${tomaConMed.medicamento.dosis}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = tomaConMed.toma.hora,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            Icon(
                imageVector = if (tomaConMed.toma.tomada) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (tomaConMed.toma.tomada) Color(0xFF0C6445) else Color.LightGray,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun MedicationItemMock(nombre: String, hora: String, tomada: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F2E9))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = nombre,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = hora,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            Icon(
                imageVector = if (tomada) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (tomada) Color(0xFF0C6445) else Color.LightGray,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

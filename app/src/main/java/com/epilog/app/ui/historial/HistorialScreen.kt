package com.epilog.app.ui.historial

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.epilog.app.data.entity.Crisis
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.layout.FlowRow as FlowRowLayout

@Composable
fun HistorialScreen(
    todasLasCrisis: List<Crisis>,
    crisisEsteMes: Int,
    onExportarPDF: () -> Unit,
    onCrisisClick: (Crisis) -> Unit
) {
    val verdePrimary = Color(0xFF2E7D32)
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Lista, 1: Gráfica

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FA))) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(verdePrimary)
                .padding(top = 40.dp, bottom = 20.dp, start = 20.dp, end = 20.dp)
        ) {
            Text(
                text = "Historial de crisis",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Tarjetas de Resumen
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ResumenHistorialCard(
                label = "ESTE MES",
                valor = crisisEsteMes.toString(),
                icon = Icons.Default.CalendarMonth,
                modifier = Modifier.weight(1f)
            )
            ResumenHistorialCard(
                label = "TOTAL REGISTRADAS",
                valor = todasLasCrisis.size.toString(),
                icon = Icons.Default.Assessment,
                modifier = Modifier.weight(1f)
            )
        }

        // Selector de vista
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = verdePrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = verdePrimary
                )
            },
            divider = {}
        ) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(12.dp)) {
                    Icon(Icons.AutoMirrored.Filled.List, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Lista", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal)
                }
            }
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(12.dp)) {
                    Icon(Icons.Default.BarChart, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Gráfica", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal)
                }
            }
            Tab(selected = false, onClick = onExportarPDF) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(12.dp)) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color(0xFFD32F2F))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Exportar", color = Color(0xFFD32F2F))
                }
            }
        }

        // Contenido según pestaña
        Box(modifier = Modifier.weight(1f)) {
            if (selectedTab == 0) {
                if (todasLasCrisis.isEmpty()) {
                    EmptyHistorial()
                } else {
                    val crisisAgrupadas = remember(todasLasCrisis) {
                        val sdfMes = SimpleDateFormat("MMMM yyyy", Locale("es"))
                        todasLasCrisis.sortedByDescending { it.fecha }
                            .groupBy { sdfMes.format(Date(it.fecha)).replaceFirstChar { c -> c.uppercase() } }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        crisisAgrupadas.forEach { (mes, crisisEnMes) ->
                            item {
                                Text(
                                    text = mes,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                            items(crisisEnMes) { crisis ->
                                CrisisItem(
                                    crisis = crisis,
                                    onClick = { onCrisisClick(crisis) }
                                )
                            }
                        }
                    }
                }
            } else {
                // Mockup de gráfica
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Vista de gráfica próximamente", color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun ResumenHistorialCard(label: String, valor: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Text(text = valor, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CrisisItem(crisis: Crisis, onClick: () -> Unit) {
    val sdfFecha = SimpleDateFormat("dd MMM", Locale("es"))
    val sdfHora = SimpleDateFormat("HH:mm", Locale("es"))
    
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${sdfFecha.format(Date(crisis.fecha))} · ${sdfHora.format(Date(crisis.fecha))}",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "Duración: ${crisis.duracion}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                // Etiqueta Origen ILAE
                val (bgIlae, txtIlae) = when (crisis.origenCrisis.lowercase()) {
                    "focal" -> Color(0xFFE3F2FD) to Color(0xFF1976D2) // Azul
                    "generalizada" -> Color(0xFFFFF3E0) to Color(0xFFF57C00) // Naranja
                    else -> Color(0xFFF5F5F5) to Color(0xFF616161)
                }
                HistorialChip(text = crisis.origenCrisis.replaceFirstChar { it.uppercase() }, containerColor = bgIlae, contentColor = txtIlae)
            }

            Spacer(modifier = Modifier.height(12.dp))
            
            // Etiquetas de aura, desencadenante, etc.
            FlowRowLayout(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Aura (Verde)
                if (crisis.sintomas.isNotEmpty()) {
                    crisis.sintomas.split(",").forEach { aura ->
                        HistorialChip(text = aura.trim(), containerColor = Color(0xFFE8F5E9), contentColor = Color(0xFF2E7D32))
                    }
                }
                
                // Desencadenantes (Rojo si es estrés, Ámbar otros)
                if (crisis.desencadenantes.isNotEmpty()) {
                    crisis.desencadenantes.split(",").forEach { des ->
                        val cleanDes = des.trim()
                        val (bg, txt) = if (cleanDes.lowercase().contains("estrés") || cleanDes.lowercase().contains("estres")) {
                            Color(0xFFFFEBEE) to Color(0xFFC62828) // Rojo
                        } else {
                            Color(0xFFFFF8E1) to Color(0xFFF9A825) // Ámbar
                        }
                        HistorialChip(text = cleanDes, containerColor = bg, contentColor = txt)
                    }
                }
                
                // Manifestación
                if (crisis.manifestaciones.isNotEmpty()) {
                    crisis.manifestaciones.split(",").forEach { man ->
                        HistorialChip(text = man.trim())
                    }
                }
            }
        }
    }
}

@Composable
fun HistorialChip(text: String, containerColor: Color = Color(0xFFF5F5F5), contentColor: Color = Color(0xFF616161)) {
    Surface(
        color = containerColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = contentColor
        )
    }
}

@Composable
fun EmptyHistorial() {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Aún no has registrado ninguna crisis",
            textAlign = TextAlign.Center,
            color = Color.Gray,
            fontSize = 16.sp
        )
    }
}

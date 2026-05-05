package com.epilog.app.ui.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.epilog.app.data.entity.Crisis
import com.epilog.app.data.entity.Perfil
import com.epilog.app.data.entity.PruebaMedica
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerarInformeScreen(
    perfil: Perfil?,
    onVolver: () -> Unit,
    onCompartir: (String) -> Unit,
    generarResumen: suspend (Long, Long) -> Triple<List<Crisis>, List<PruebaMedica>, Perfil?>
) {
    val sdf = remember { SimpleDateFormat("dd MMM yyyy", Locale("es")) }
    val sdfCrisis = remember { SimpleDateFormat("d MMM · HH:mm", Locale("es")) }
    
    var fechaDesde by remember { mutableLongStateOf(0L) }
    var fechaHasta by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var informeTexto by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }

    val verdePrimary = Color(0xFF2E7D32)
    val coral400 = Color(0xFFFF7043)

    // Inicializar fechaDesde si hay perfil
    LaunchedEffect(perfil) {
        if (fechaDesde == 0L) {
            fechaDesde = perfil?.ultimaVisita ?: Calendar.getInstance().apply { add(Calendar.MONTH, -3) }.timeInMillis
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Generar Informe", fontWeight = FontWeight.Bold) },
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
        },
        floatingActionButton = {
            if (informeTexto.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    onClick = { onCompartir(informeTexto) },
                    containerColor = coral400,
                    contentColor = Color.White,
                    icon = { Icon(Icons.Default.Share, contentDescription = null) },
                    text = { Text("COMPARTIR") }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Configuración del informe
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Periodo del informe", fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Desde: ${sdf.format(Date(fechaDesde))}", fontSize = 14.sp)
                            Text("Hasta: ${sdf.format(Date(fechaHasta))}", fontSize = 14.sp)
                        }
                        Button(
                            onClick = {
                                cargando = true
                                // Aquí se llamaría a la lógica de generación
                                // En una app real, esto sería disparado por un evento del ViewModel
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = verdePrimary),
                            enabled = !cargando
                        ) {
                            if (cargando) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("GENERAR")
                            }
                        }
                    }
                    
                    if (perfil?.ultimaVisita != null) {
                        Text(
                            "Sugerido: desde última visita (${sdf.format(Date(perfil.ultimaVisita))})",
                            fontSize = 12.sp,
                            color = verdePrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Vista previa del informe
            Text("VISTA PREVIA", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                    .padding(12.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                if (informeTexto.isEmpty() && !cargando) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.LightGray)
                        Text("Pulsa 'Generar' para crear el resumen", color = Color.Gray, fontSize = 14.sp)
                    }
                } else {
                    Text(
                        text = informeTexto,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }

    // Efecto para "simular" o disparar la generación real (en el NavGraph se pasa la función)
    LaunchedEffect(cargando) {
        if (cargando) {
            val (crisis, pruebas, p) = generarResumen(fechaDesde, fechaHasta)
            informeTexto = buildString {
                appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                appendLine("       EPILOG - INFORME MÉDICO")
                appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                appendLine("PACIENTE: ${p?.nombre?.uppercase() ?: "---"}")
                appendLine("EMAIL: ${p?.email ?: "---"}")
                appendLine("TIPO: ${p?.tipoEpilepsia ?: "---"}")
                appendLine("PERIODO: ${sdf.format(Date(fechaDesde))} – ${sdf.format(Date(fechaHasta))}")
                appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                appendLine()
                appendLine("RESUMEN DE CRISIS (${crisis.size})")
                if (crisis.isEmpty()) {
                    appendLine("Sin registros de crisis en este periodo.")
                } else {
                    crisis.forEach { c ->
                        val m = c.manifestaciones.split(",").firstOrNull() ?: ""
                        appendLine("• ${sdfCrisis.format(Date(c.fecha))} | ${c.origenCrisis} | ${c.duracion} | $m")
                    }
                }
                appendLine()
                appendLine("PRUEBAS REALIZADAS (${pruebas.size})")
                if (pruebas.isEmpty()) {
                    appendLine("Sin pruebas médicas registradas.")
                } else {
                    pruebas.forEach { pr ->
                        appendLine("• ${sdf.format(Date(pr.fecha))} - ${pr.tipo}")
                    }
                }
                appendLine()
                appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                appendLine("Generado automáticamente por EpiLog")
                appendLine(SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date()))
            }
            cargando = false
        }
    }
}

package com.epilog.app.ui.estadisticas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.shape.Shape
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstadisticasScreen(
    viewModel: EstadisticasViewModel,
    onVolver: () -> Unit
) {
    val crisisData by viewModel.crisisUltimos7Dias.observeAsState(emptyMap())
    val crisisPorHora by viewModel.crisisPorHora.observeAsState(emptyMap())
    val duracionMedia by viewModel.duracionMedia.observeAsState("0 min")
    val verdePrimary = Color(0xFF0C6445)
    val coralMockup = Color(0xFFE05F34)
    
    val model7Dias = remember { CartesianChartModelProducer() }
    val modelHoras = remember { CartesianChartModelProducer() }
    
    // Preparar etiquetas para los últimos 7 días
    val etiquetas7Dias = remember(crisisData) {
        val sdf = SimpleDateFormat("EEE", Locale.getDefault())
        val cal = Calendar.getInstance()
        crisisData.keys.map { dayOfYear ->
            cal.set(Calendar.DAY_OF_YEAR, dayOfYear)
            sdf.format(cal.time).replaceFirstChar { it.uppercase() }
        }
    }

    LaunchedEffect(crisisData) {
        if (crisisData.isNotEmpty()) {
            model7Dias.runTransaction {
                columnSeries { series(crisisData.values.toList()) }
            }
        }
    }

    LaunchedEffect(crisisPorHora) {
        if (crisisPorHora.isNotEmpty()) {
            modelHoras.runTransaction {
                columnSeries { series(crisisPorHora.values.toList()) }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Análisis de crisis", fontWeight = FontWeight.Bold) },
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
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Card de resumen total
            val totalPeriodo = crisisData.values.sum().toInt()
            Row(modifier = Modifier.fillMaxWidth()) {
                Card(
                    modifier = Modifier.weight(1.3f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F2E9)),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Total crisis", color = Color.Gray, fontSize = 12.sp)
                        Text("$totalPeriodo", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = verdePrimary)
                        Text("Últimos 7 días", color = Color.Gray, fontSize = 10.sp)
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE0D6)),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Duración media", color = Color(0xFF8D4A32), fontSize = 12.sp)
                        Text(duracionMedia, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = coralMockup)
                        Text("Promedio", color = Color(0xFF8D4A32), fontSize = 10.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Gráfica 1: Últimos 7 días
            SectionTitle("Evolución semanal")
            if (crisisData.isNotEmpty()) {
                ChartCard(
                    modelProducer = model7Dias, 
                    color = coralMockup,
                    bottomLabels = etiquetas7Dias
                )
            } else {
                EmptyStateCard("Aún no hay suficientes datos para mostrar la evolución semanal.")
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Gráfica 2: Distribución por hora
            SectionTitle("Franjas horarias más frecuentes")
            if (crisisPorHora.isNotEmpty() && crisisPorHora.values.any { it > 0 }) {
                ChartCard(
                    modelProducer = modelHoras, 
                    color = verdePrimary,
                    bottomLabels = (0..23).map { "${it}h" }
                )
            } else {
                EmptyStateCard("Registra crisis con su hora exacta para identificar patrones horarios.")
            }

            Spacer(modifier = Modifier.height(32.dp))
            
            // Sugerencia/Insight
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "💡 Consejo: Intenta mantener un horario regular de sueño para ayudar a reducir la frecuencia de las crisis.",
                        fontSize = 14.sp,
                        color = verdePrimary
                    )
                }
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF333333),
        modifier = Modifier.padding(bottom = 16.dp)
    )
}

@Composable
fun ChartCard(
    modelProducer: CartesianChartModelProducer, 
    color: Color,
    bottomLabels: List<String>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Box(modifier = Modifier.padding(top = 16.dp, bottom = 8.dp, start = 8.dp, end = 16.dp)) {
            CartesianChartHost(
                chart = rememberCartesianChart(
                    rememberColumnCartesianLayer(
                        columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                            rememberLineComponent(
                                color = color,
                                thickness = 12.dp,
                                shape = Shape.rounded(topLeftDp = 4f, topRightDp = 4f)
                            )
                        )
                    ),
                    startAxis = rememberStartAxis(
                        label = rememberTextComponent(color = Color.Gray, textSize = 10.sp),
                        guideline = rememberLineComponent(color = Color(0xFFF0F0F0), thickness = 1.dp)
                    ),
                    bottomAxis = rememberBottomAxis(
                        label = rememberTextComponent(color = Color.Gray, textSize = 10.sp),
                        valueFormatter = { value, _, _ -> 
                            bottomLabels.getOrNull(value.toInt()) ?: ""
                        },
                        guideline = null
                    ),
                ),
                modelProducer = modelProducer,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun EmptyStateCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
    ) {
        Text(
            message,
            modifier = Modifier.padding(20.dp),
            color = Color.Gray,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
    }
}

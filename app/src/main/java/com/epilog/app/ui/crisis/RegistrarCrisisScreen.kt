package com.epilog.app.ui.crisis

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.epilog.app.data.entity.Crisis
import java.text.SimpleDateFormat
import java.util.*
import android.app.DatePickerDialog
import android.app.TimePickerDialog

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RegistrarCrisisScreen(
    crisisAEditar: Crisis? = null,
    onVolver: () -> Unit,
    onGuardar: (Crisis) -> Unit,
    onEliminar: ((Crisis) -> Unit)? = null
) {
    var origen by remember { mutableStateOf(crisisAEditar?.origenCrisis ?: "") }
    var conscienciaPreservada by remember { mutableStateOf(crisisAEditar?.conscienciaPreservada) }
    var duracion by remember { mutableStateOf(crisisAEditar?.duracion ?: "") }
    var manifestacion by remember { 
        mutableStateOf(crisisAEditar?.manifestaciones?.split(", ")?.filter { it.isNotEmpty() }?.toSet() ?: emptySet()) 
    }
    var aura by remember { 
        mutableStateOf(crisisAEditar?.sintomas?.split(", ")?.filter { it.isNotEmpty() }?.toSet() ?: emptySet()) 
    }
    var desencadenante by remember { 
        mutableStateOf(crisisAEditar?.desencadenantes?.split(", ")?.filter { it.isNotEmpty() }?.toSet() ?: emptySet()) 
    }
    var notas by remember { mutableStateOf(crisisAEditar?.notas ?: "") }

    val verdeMockup = Color(0xFF0C6445)
    val beigeMockup = Color(0xFFF7F2E9)
    
    // Colores ILAE
    val colorFocal = Color(0xFF1976D2) // Azul
    val colorGeneralizada = Color(0xFFF57C00) // Naranja
    val colorDesconocido = Color(0xFF616161) // Gris

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (crisisAEditar == null) "Registrar crisis" else "Editar crisis", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (crisisAEditar != null && onEliminar != null) {
                        IconButton(onClick = { onEliminar(crisisAEditar) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = verdeMockup,
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
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Fecha y Hora
            SeccionTituloMockup("FECHA Y HORA")
            val context = LocalContext.current
            var fechaSeleccionada by remember { mutableLongStateOf(crisisAEditar?.fecha ?: System.currentTimeMillis()) }
            val calendar = remember { Calendar.getInstance() }
            
            val sdf = SimpleDateFormat("dd MMM yyyy - HH:mm", Locale("es"))
            
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val currentCal = Calendar.getInstance().apply { timeInMillis = fechaSeleccionada }
                        DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                TimePickerDialog(
                                    context,
                                    { _, hourOfDay, minute ->
                                        val newCal = Calendar.getInstance().apply {
                                            set(year, month, dayOfMonth, hourOfDay, minute)
                                        }
                                        fechaSeleccionada = newCal.timeInMillis
                                    },
                                    currentCal.get(Calendar.HOUR_OF_DAY),
                                    currentCal.get(Calendar.MINUTE),
                                    true
                                ).show()
                            },
                            currentCal.get(Calendar.YEAR),
                            currentCal.get(Calendar.MONTH),
                            currentCal.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    },
                color = beigeMockup,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(sdf.format(Date(fechaSeleccionada)), color = Color.DarkGray)
                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                }
            }

            // Duración
            SeccionTituloMockup("DURACIÓN")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("<1 min", "1-3 min", "3-5 min", ">5 min").forEach { d ->
                    SelectorBotonMockup(text = d, selected = duracion == d, modifier = Modifier.weight(1f)) { duracion = d }
                }
            }

            // Nivel 1 - Origen
            SeccionTituloMockup("NIVEL 1 — ORIGEN (ILAE)")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SelectorBotonMockup(
                    text = "Focal",
                    selected = origen == "Focal",
                    modifier = Modifier.weight(1f),
                    selectedColor = colorFocal
                ) { origen = "Focal" }
                
                SelectorBotonMockup(
                    text = "Generalizada",
                    selected = origen == "Generalizada",
                    modifier = Modifier.weight(1f),
                    selectedColor = colorGeneralizada
                ) { origen = "Generalizada" }
                
                SelectorBotonMockup(
                    text = "Desconocido",
                    selected = origen == "Desconocido",
                    modifier = Modifier.weight(1f),
                    selectedColor = colorDesconocido
                ) { origen = "Desconocido" }
            }

            // Nivel 2 - Consciencia (Solo si es focal)
            SeccionTituloMockup("NIVEL 2 — CONSCIENCIA (solo si focal)")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    SelectorBotonMockup(text = "Preservada", selected = conscienciaPreservada == true, subtext = "antes parcial simple") { 
                        conscienciaPreservada = true 
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    SelectorBotonMockup(text = "Alterada", selected = conscienciaPreservada == false, subtext = "antes parcial compleja") { 
                        conscienciaPreservada = false 
                    }
                }
            }

            // Nivel 3 - Manifestación
            SeccionTituloMockup("NIVEL 3 — MANIFESTACIÓN (múltiple)")
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Cognitiva / lenguaje", "Motora", "Sensitiva", "Autonómica", "Emocional", "Ausencia", "Tónico-clónica").forEach { m ->
                    SelectorBotonMockup(
                        text = m,
                        selected = manifestacion.contains(m),
                        isSmall = true
                    ) {
                        manifestacion = if (manifestacion.contains(m)) manifestacion - m else manifestacion + m
                    }
                }
            }

            // Síntomas Previos (Aura)
            SeccionTituloMockup("SÍNTOMAS PREVIOS (AURA)")
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Alteración del habla", "Náuseas", "Deja vu", "Luces", "Mareos", "Miedo", "Irrealidad", "Automatismos", "Cambio de voz").forEach { a ->
                    SelectorBotonMockup(
                        text = a,
                        selected = aura.contains(a),
                        isSmall = true
                    ) {
                        aura = if (aura.contains(a)) aura - a else aura + a
                    }
                }
            }

            // Posible Desencadenante
            SeccionTituloMockup("POSIBLE DESENCADENANTE")
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Falta de sueño", "Estrés", "Alcohol", "Luz intensa", "No sé").forEach { de ->
                    SelectorBotonMockup(
                        text = de,
                        selected = desencadenante.contains(de),
                        isSmall = true
                    ) {
                        desencadenante = if (desencadenante.contains(de)) desencadenante - de else desencadenante + de
                    }
                }
            }

            // Notas
            SeccionTituloMockup("NOTAS (OPCIONAL)")
            OutlinedTextField(
                value = notas,
                onValueChange = { notas = it },
                placeholder = { Text("Añade cualquier detalle relevante...", fontSize = 14.sp) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = beigeMockup,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = verdeMockup
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Botón Guardar
            Button(
                onClick = {
                    onGuardar(Crisis(
                        fecha = fechaSeleccionada,
                        duracion = duracion,
                        origenCrisis = origen,
                        conscienciaPreservada = conscienciaPreservada,
                        manifestaciones = manifestacion.joinToString(", "),
                        sintomas = aura.joinToString(", "),
                        desencadenantes = desencadenante.joinToString(", "),
                        notas = notas
                    ))
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = verdeMockup),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Guardar crisis", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun SeccionTituloMockup(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Gray,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

@Composable
fun SelectorBotonMockup(
    text: String, 
    selected: Boolean, 
    modifier: Modifier = Modifier, 
    subtext: String? = null,
    isSmall: Boolean = false,
    selectedColor: Color = Color(0xFF0C6445),
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clickable { onClick() }
            .then(if (isSmall) Modifier.wrapContentWidth() else Modifier.fillMaxWidth()),
        shape = RoundedCornerShape(12.dp),
        color = if (selected) selectedColor else Color.White,
        border = if (selected) null else BorderStroke(1.dp, Color.LightGray)
    ) {
        Column(
            modifier = Modifier.padding(vertical = if (isSmall) 8.dp else 10.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                color = if (selected) Color.White else Color.DarkGray,
                fontSize = if (isSmall) 13.sp else 14.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
            if (subtext != null) {
                Text(
                    text = subtext,
                    color = if (selected) Color(0xFFB0D5C1) else Color.Gray,
                    fontSize = 10.sp
                )
            }
        }
    }
}

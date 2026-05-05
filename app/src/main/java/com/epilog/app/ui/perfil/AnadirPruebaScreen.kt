package com.epilog.app.ui.perfil

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.epilog.app.data.entity.PruebaMedica
import com.epilog.app.ui.components.SeccionTitulo
import com.epilog.app.ui.components.SelectorBoton

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AnadirPruebaScreen(
    onVolver: () -> Unit,
    onGuardar: (PruebaMedica) -> Unit
) {
    var tipo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }
    var uriPdf by remember { mutableStateOf<Uri?>(null) }
    
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uriPdf = uri
    }

    val verdePrimary = Color(0xFF2E7D32)
    val coral400 = Color(0xFFFF7043)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Añadir Prueba Médica", fontWeight = FontWeight.Bold) },
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
            // Tipo de prueba
            SeccionTitulo("Tipo de prueba")
            val tipos = listOf("RM", "TAC", "Analítica", "EEG", "ECG", "Otra")
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tipos.forEach { t ->
                    SelectorBoton(
                        text = t,
                        selected = tipo == t,
                        modifier = Modifier.width(100.dp)
                    ) {
                        tipo = t
                    }
                }
            }

            // Descripción
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción corta") },
                placeholder = { Text("Ej: RM craneal de control") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // PDF
            SeccionTitulo("Documento adjunto")
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.PictureAsPdf,
                        contentDescription = null,
                        tint = if (uriPdf != null) coral400 else Color.Gray,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (uriPdf != null) "PDF seleccionado" else "No hay archivo adjunto",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        if (uriPdf != null) {
                            Text(
                                text = uriPdf!!.lastPathSegment ?: "archivo.pdf",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Button(
                        onClick = { launcher.launch("application/pdf") },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(if (uriPdf == null) "Subir" else "Cambiar", fontSize = 12.sp)
                    }
                }
            }

            // Notas
            OutlinedTextField(
                value = notas,
                onValueChange = { notas = it },
                label = { Text("Notas adicionales") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            // Botón Guardar
            Button(
                onClick = {
                    if (tipo.isNotEmpty()) {
                        onGuardar(PruebaMedica(
                            tipo = tipo,
                            descripcion = descripcion.takeIf { it.isNotBlank() },
                            fecha = System.currentTimeMillis(),
                            notas = notas.takeIf { it.isNotBlank() },
                            uriPdf = uriPdf?.toString()
                        ))
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = coral400),
                shape = RoundedCornerShape(12.dp),
                enabled = tipo.isNotEmpty()
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("GUARDAR PRUEBA", fontWeight = FontWeight.Bold)
            }
        }
    }
}

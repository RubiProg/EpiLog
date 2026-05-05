package com.epilog.app.ui.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.epilog.app.data.entity.Perfil
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.provider.ContactsContract
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    perfil: Perfil?,
    pruebasCount: Int,
    onVolver: () -> Unit,
    onGuardarPerfil: (Perfil) -> Unit,
    onAnadirPrueba: () -> Unit,
    onGenerarInforme: () -> Unit
) {
    val verdePrimary = Color(0xFF2E7D32)
    val verde50 = Color(0xFFE8F5E9)
    val context = LocalContext.current

    var showEditDialog by remember { mutableStateOf<String?>(null) }
    var editValue by remember { mutableStateOf("") }

    val contactPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickContact()
    ) { uri ->
        uri?.let {
            val cursor = context.contentResolver.query(it, null, null, null, null)
            cursor?.use { c ->
                if (c.moveToFirst()) {
                    val nameIndex = c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                    val idIndex = c.getColumnIndex(ContactsContract.Contacts._ID)
                    val name = c.getString(nameIndex)
                    val id = c.getString(idIndex)

                    val phoneCursor = context.contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(id),
                        null
                    )
                    phoneCursor?.use { pc ->
                        if (pc.moveToFirst()) {
                            val phoneIndex = pc.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                            val phone = pc.getString(phoneIndex)
                            
                            val nuevoPerfil = (perfil ?: Perfil()).copy(
                                contactoNombre = name,
                                contactoTelefono = phone
                            )
                            onGuardarPerfil(nuevoPerfil)
                        }
                    }
                }
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            contactPickerLauncher.launch(null)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = verdePrimary)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header con Avatar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(verdePrimary)
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = CircleShape,
                        color = verde50
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = perfil?.nombre?.take(2)?.uppercase() ?: "??",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = verdePrimary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = perfil?.nombre ?: "Sin nombre",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = perfil?.email ?: "correo@ejemplo.com",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                SeccionTitulo("DATOS MÉDICOS")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column {
                        FilaEditable(
                            label = "Tipo de epilepsia",
                            valor = perfil?.tipoEpilepsia ?: "No definido",
                            onClick = { 
                                editValue = perfil?.tipoEpilepsia ?: ""
                                showEditDialog = "Tipo de epilepsia" 
                            }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp))
                        FilaEditable(
                            label = "Neurólogo",
                            valor = perfil?.neurologo ?: "No definido",
                            onClick = { 
                                editValue = perfil?.neurologo ?: ""
                                showEditDialog = "Neurólogo" 
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                SeccionTitulo("CONTACTO DE EMERGENCIA")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column {
                        FilaEditable(
                            label = "Nombre",
                            valor = perfil?.contactoNombre ?: "No definido",
                            onClick = { 
                                editValue = perfil?.contactoNombre ?: ""
                                showEditDialog = "Nombre contacto" 
                            }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp))
                        FilaEditable(
                            label = "Teléfono",
                            valor = perfil?.contactoTelefono ?: "No definido",
                            onClick = { 
                                editValue = perfil?.contactoTelefono ?: ""
                                showEditDialog = "Teléfono contacto" 
                            }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedButton(
                    onClick = {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                            contactPickerLauncher.launch(null)
                        } else {
                            permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Seleccionar de la agenda")
                }

                Spacer(modifier = Modifier.height(24.dp))

                SeccionTitulo("PRUEBAS MÉDICAS")
                Text("$pruebasCount prueba(s) registrada(s)", fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onAnadirPrueba,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("+ Añadir prueba médica")
                }

                Spacer(modifier = Modifier.height(24.dp))

                SeccionTitulo("INFORME")
                Button(
                    onClick = onGenerarInforme,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = verdePrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Generar informe por periodo")
                }
            }
        }

        // Diálogo de edición
        if (showEditDialog != null) {
            AlertDialog(
                onDismissRequest = { showEditDialog = null },
                title = { Text("Editar ${showEditDialog}") },
                text = {
                    OutlinedTextField(
                        value = editValue,
                        onValueChange = { editValue = it },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = if (showEditDialog == "Teléfono contacto") 
                            KeyboardOptions(keyboardType = KeyboardType.Phone) 
                            else KeyboardOptions.Default
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        val nuevoPerfil = when (showEditDialog) {
                            "Tipo de epilepsia" -> (perfil ?: Perfil()).copy(tipoEpilepsia = editValue)
                            "Neurólogo" -> (perfil ?: Perfil()).copy(neurologo = editValue)
                            "Nombre contacto" -> (perfil ?: Perfil()).copy(contactoNombre = editValue)
                            "Teléfono contacto" -> (perfil ?: Perfil()).copy(contactoTelefono = editValue)
                            else -> perfil
                        }
                        if (nuevoPerfil != null) onGuardarPerfil(nuevoPerfil)
                        showEditDialog = null
                    }) {
                        Text("Guardar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditDialog = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
fun SeccionTitulo(titulo: String) {
    Text(
        text = titulo,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Gray,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun FilaEditable(label: String, valor: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 14.sp)
        Text(text = valor, fontSize = 14.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Medium)
    }
}

package com.epilog.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Inicio : Screen("inicio", "Inicio", Icons.Default.Home)
    object Historial : Screen("historial", "Historial", Icons.Default.History)
    object Medicacion : Screen("medicacion", "Medicación", Icons.Default.Medication)
    object Perfil : Screen("perfil", "Perfil", Icons.Default.Person)
    
    // Rutas secundarias (sin icono en el bottom bar)
    object RegistrarCrisis : Screen("registrar_crisis", "Registrar Crisis", Icons.Default.Home)
    object EditarCrisis : Screen("editar_crisis/{crisisId}", "Editar Crisis", Icons.Default.History)
    object AnadirMedicamento : Screen("anadir_medicamento", "Añadir Medicamento", Icons.Default.Medication)
    object AnadirPrueba : Screen("anadir_prueba", "Añadir Prueba", Icons.Default.Person)
    object GenerarInforme : Screen("generar_informe", "Generar Informe", Icons.Default.Person)
}

val bottomNavItems = listOf(
    Screen.Inicio,
    Screen.Historial,
    Screen.Medicacion,
    Screen.Perfil
)

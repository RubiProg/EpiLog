package com.epilog.app.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.epilog.app.EpiLogApplication
import com.epilog.app.ui.inicio.InicioScreen
import com.epilog.app.ui.inicio.InicioViewModel
import com.epilog.app.ui.inicio.InicioViewModelFactory
import com.epilog.app.ui.crisis.RegistrarCrisisScreen
import com.epilog.app.ui.crisis.CrisisViewModel
import com.epilog.app.ui.crisis.CrisisViewModelFactory
import com.epilog.app.ui.historial.HistorialScreen
import com.epilog.app.ui.medicacion.AnadirMedicamentoScreen
import com.epilog.app.ui.medicacion.MedicacionScreen
import com.epilog.app.ui.medicacion.MedicacionViewModel
import com.epilog.app.ui.medicacion.MedicacionViewModelFactory
import com.epilog.app.ui.perfil.AnadirPruebaScreen
import com.epilog.app.ui.perfil.GenerarInformeScreen
import com.epilog.app.ui.perfil.PerfilScreen
import android.content.Intent
import com.epilog.app.ui.perfil.PerfilViewModel
import com.epilog.app.ui.perfil.PerfilViewModelFactory

@Composable
fun EpiLogNavGraph(navController: NavHostController, application: EpiLogApplication) {
    NavHost(
        navController = navController,
        startDestination = Screen.Inicio.route
    ) {
        composable(Screen.Inicio.route) {
            val viewModel: InicioViewModel = viewModel(factory = InicioViewModelFactory(application))
            val perfil by viewModel.perfil.observeAsState()
            val crisisEsteMes by viewModel.crisisEsteMes.observeAsState(0)
            val todasLasCrisis by viewModel.todasLasCrisis.observeAsState(emptyList())
            val tomasHoy by viewModel.tomasHoy.observeAsState(emptyList())

            val ultimaCrisisTexto = if (todasLasCrisis.isNotEmpty()) {
                val dias = ((System.currentTimeMillis() - todasLasCrisis.first().fecha) / (1000 * 60 * 60 * 24)).toInt()
                when {
                    dias == 0 -> "hoy"
                    dias == 1 -> "ayer"
                    else -> "hace $dias días"
                }
            } else "hace - días"

            InicioScreen(
                perfil = perfil,
                crisisEsteMes = crisisEsteMes,
                ultimaCrisisTexto = ultimaCrisisTexto,
                tomasHoy = tomasHoy,
                onRegistrarCrisis = { navController.navigate(Screen.RegistrarCrisis.route) },
                onVerPerfil = { navController.navigate(Screen.Perfil.route) }
            )
        }
        composable(Screen.Historial.route) {
            val viewModel: CrisisViewModel = viewModel(factory = CrisisViewModelFactory(application))
            val todasLasCrisis by viewModel.todasLasCrisis.observeAsState(emptyList())
            
            val crisisEsteMes = todasLasCrisis.count { fecha ->
                val cal = java.util.Calendar.getInstance()
                val calFecha = java.util.Calendar.getInstance()
                calFecha.timeInMillis = fecha.fecha
                cal.get(java.util.Calendar.YEAR) == calFecha.get(java.util.Calendar.YEAR) &&
                        cal.get(java.util.Calendar.MONTH) == calFecha.get(java.util.Calendar.MONTH)
            }

            HistorialScreen(
                todasLasCrisis = todasLasCrisis,
                crisisEsteMes = crisisEsteMes,
                onExportarPDF = { navController.navigate(Screen.GenerarInforme.route) },
                onCrisisClick = { crisis ->
                    navController.navigate("editar_crisis/${crisis.fecha}")
                }
            )
        }
        composable(Screen.Medicacion.route) {
            val viewModel: MedicacionViewModel = viewModel(factory = MedicacionViewModelFactory(application))
            val medicamentos by viewModel.todosMedicamentos.observeAsState(emptyList())

            MedicacionScreen(
                medicamentos = medicamentos,
                onAnadirMedicamento = { navController.navigate(Screen.AnadirMedicamento.route) },
                onMedicamentoClick = { id -> /* Ver detalle */ }
            )
        }
        composable(Screen.Perfil.route) {
            val viewModel: PerfilViewModel = viewModel(factory = PerfilViewModelFactory(application))
            val perfil by viewModel.perfil.observeAsState()
            val pruebas by viewModel.pruebas.observeAsState(emptyList())

            PerfilScreen(
                perfil = perfil,
                pruebasCount = pruebas.size,
                onVolver = { navController.popBackStack() },
                onGuardarPerfil = { nuevoPerfil -> viewModel.guardar(nuevoPerfil) },
                onAnadirPrueba = { navController.navigate(Screen.AnadirPrueba.route) },
                onGenerarInforme = { navController.navigate(Screen.GenerarInforme.route) }
            )
        }
        
        // Rutas secundarias
        composable(Screen.RegistrarCrisis.route) {
            val viewModel: CrisisViewModel = viewModel(factory = CrisisViewModelFactory(application))
            RegistrarCrisisScreen(
                onVolver = { navController.popBackStack() },
                onGuardar = { crisis ->
                    viewModel.insertar(crisis)
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = Screen.EditarCrisis.route,
            arguments = listOf(androidx.navigation.navArgument("crisisId") { type = androidx.navigation.NavType.LongType })
        ) { backStackEntry ->
            val crisisId = backStackEntry.arguments?.getLong("crisisId") ?: 0L
            val viewModel: CrisisViewModel = viewModel(factory = CrisisViewModelFactory(application))
            val todasLasCrisis by viewModel.todasLasCrisis.observeAsState(emptyList())
            val crisisAEditar = todasLasCrisis.find { it.fecha == crisisId }

            if (crisisAEditar != null) {
                RegistrarCrisisScreen(
                    crisisAEditar = crisisAEditar,
                    onVolver = { navController.popBackStack() },
                    onGuardar = { crisis ->
                        viewModel.insertar(crisis) // Room @Insert(onConflict = REPLACE) handles update
                        navController.popBackStack()
                    },
                    onEliminar = { crisis ->
                        viewModel.eliminar(crisis)
                        navController.popBackStack()
                    }
                )
            } else {
                androidx.compose.runtime.LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
            }
        }
        composable(
            route = Screen.EditarCrisis.route,
            arguments = listOf(androidx.navigation.navArgument("crisisId") { type = androidx.navigation.NavType.LongType })
        ) { backStackEntry ->
            val crisisId = backStackEntry.arguments?.getLong("crisisId") ?: 0L
            val viewModel: CrisisViewModel = viewModel(factory = CrisisViewModelFactory(application))
            val todasLasCrisis by viewModel.todasLasCrisis.observeAsState(emptyList())
            val crisisAEditar = todasLasCrisis.find { it.fecha == crisisId }

            if (crisisAEditar != null) {
                RegistrarCrisisScreen(
                    crisisAEditar = crisisAEditar,
                    onVolver = { navController.popBackStack() },
                    onGuardar = { crisis ->
                        viewModel.insertar(crisis) // Room @Insert(onConflict = REPLACE) handles update
                        navController.popBackStack()
                    },
                    onEliminar = { crisis ->
                        viewModel.eliminar(crisis)
                        navController.popBackStack()
                    }
                )
            } else {
                androidx.compose.runtime.LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
            }
        }
        composable(
            route = Screen.EditarCrisis.route,
            arguments = listOf(androidx.navigation.navArgument("crisisId") { type = androidx.navigation.NavType.LongType })
        ) { backStackEntry ->
            val crisisId = backStackEntry.arguments?.getLong("crisisId") ?: 0L
            val viewModel: CrisisViewModel = viewModel(factory = CrisisViewModelFactory(application))
            val todasLasCrisis by viewModel.todasLasCrisis.observeAsState(emptyList())
            val crisisAEditar = todasLasCrisis.find { it.fecha == crisisId }

            if (crisisAEditar != null) {
                RegistrarCrisisScreen(
                    crisisAEditar = crisisAEditar,
                    onVolver = { navController.popBackStack() },
                    onGuardar = { crisis ->
                        viewModel.insertar(crisis) // Room @Insert(onConflict = REPLACE) handles update
                        navController.popBackStack()
                    },
                    onEliminar = { crisis ->
                        viewModel.eliminar(crisis)
                        navController.popBackStack()
                    }
                )
            } else {
                // Si por alguna razón no se encuentra, volver
                androidx.compose.runtime.LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
            }
        }
        composable(Screen.AnadirMedicamento.route) {
            val viewModel: MedicacionViewModel = viewModel(factory = MedicacionViewModelFactory(application))
            AnadirMedicamentoScreen(
                onVolver = { navController.popBackStack() },
                onGuardar = { med, tomas ->
                    viewModel.insertarMedicamento(med, tomas)
                    // Nota: La programación de alarmas se debería mover a un UseCase o al ViewModel
                    // Por ahora, para mantener paridad con el fragmento, el VM podría manejarlo
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.AnadirPrueba.route) {
            val viewModel: PerfilViewModel = viewModel(factory = PerfilViewModelFactory(application))
            AnadirPruebaScreen(
                onVolver = { navController.popBackStack() },
                onGuardar = { prueba ->
                    viewModel.guardarPrueba(prueba)
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.GenerarInforme.route) {
            val viewModel: PerfilViewModel = viewModel(factory = PerfilViewModelFactory(application))
            val perfil by viewModel.perfil.observeAsState()
            val context = androidx.compose.ui.platform.LocalContext.current

            GenerarInformeScreen(
                perfil = perfil,
                onVolver = { navController.popBackStack() },
                onCompartir = { texto ->
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, "EpiLog - Resumen Médico")
                        putExtra(Intent.EXTRA_TEXT, texto)
                    }
                    context.startActivity(Intent.createChooser(intent, "Enviar informe vía..."))
                },
                generarResumen = { desde, hasta ->
                    val crisis = application.repository.crisisPorPeriodoSync(desde, hasta)
                    val pruebas = application.repository.pruebaPorPeriodoSync(desde, hasta)
                    val p = application.repository.perfilSync()
                    Triple(crisis, pruebas, p)
                }
            )
        }
    }
}

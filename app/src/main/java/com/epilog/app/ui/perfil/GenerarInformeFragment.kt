package com.epilog.app.ui.perfil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.epilog.app.EpiLogApplication
import com.epilog.app.databinding.FragmentGenerarInformeBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class GenerarInformeFragment : Fragment() {

    private var _binding: FragmentGenerarInformeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PerfilViewModel by viewModels {
        PerfilViewModelFactory(requireActivity().application as EpiLogApplication)
    }

    private var fechaDesde: Long = 0L
    private var fechaHasta: Long = System.currentTimeMillis()
    private val sdf = SimpleDateFormat("dd MMM yyyy", Locale("es"))

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGenerarInformeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnVolver.setOnClickListener { findNavController().navigateUp() }

        // Autocompletar desde última visita
        viewModel.perfil.observe(viewLifecycleOwner) { perfil ->
            perfil?.ultimaVisita?.let {
                fechaDesde = it
                binding.tvDesde.text = sdf.format(Date(it))
                binding.tvInfoUltimaVisita.text = "Desde la última visita: ${sdf.format(Date(it))}"
            } ?: run {
                // Si no hay visita, usar hace 3 meses
                val tresMeses = Calendar.getInstance().apply {
                    add(Calendar.MONTH, -3)
                }.timeInMillis
                fechaDesde = tresMeses
                binding.tvDesde.text = sdf.format(Date(tresMeses))
            }
            binding.tvHasta.text = sdf.format(Date(fechaHasta))
        }

        binding.btnGenerar.setOnClickListener { generarInforme() }
    }

    private fun generarInforme() {
        if (fechaDesde == 0L) {
            Toast.makeText(requireContext(), "Selecciona el periodo", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val app = requireActivity().application as EpiLogApplication
            val crisis = app.repository.crisisPorPeriodoSync(fechaDesde, fechaHasta)
            val pruebas = app.repository.pruebaPorPeriodoSync(fechaDesde, fechaHasta)
            val perfil = app.repository.perfilSync()

            val resumen = buildString {
                appendLine("EpiLog — Resumen médico")
                appendLine("Paciente: ${perfil?.nombre ?: "—"}")
                appendLine("Periodo: ${sdf.format(Date(fechaDesde))} – ${sdf.format(Date(fechaHasta))}")
                appendLine()
                appendLine("CRISIS: ${crisis.size}")
                crisis.forEach { c ->
                    val fecha = SimpleDateFormat("d MMM · HH:mm", Locale("es")).format(Date(c.fecha))
                    val origen = c.origenCrisis
                    val manifestacion = c.manifestaciones.split(",").firstOrNull()?.trim() ?: ""
                    appendLine("· $fecha · $origen · ${c.duracion} · $manifestacion")
                }
                appendLine()
                appendLine("PRUEBAS MÉDICAS: ${pruebas.size}")
                pruebas.forEach { p ->
                    appendLine("· ${p.tipo} — ${sdf.format(Date(p.fecha))}")
                }
                appendLine()
                appendLine("MEDICACIÓN: sin cambios en el periodo")
            }

            binding.tvVistaPrevia.text = resumen

            Toast.makeText(requireContext(), "Informe generado", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

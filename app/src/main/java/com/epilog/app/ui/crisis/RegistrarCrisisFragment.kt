package com.epilog.app.ui.crisis

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.epilog.app.EpiLogApplication
import com.epilog.app.data.entity.Crisis
import com.epilog.app.databinding.FragmentRegistrarCrisisBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class RegistrarCrisisFragment : Fragment() {

    private var _binding: FragmentRegistrarCrisisBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CrisisViewModel by viewModels {
        CrisisViewModelFactory(requireActivity().application as EpiLogApplication)
    }

    // Estado ILAE
    private var origenSeleccionado: String = ""
    private var conscienciaPreservada: Boolean? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrarCrisisBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configurarOrigen()
        configurarConsciencia()
        configurarFechaHora()

        binding.btnGuardar.setOnClickListener { guardarCrisis() }
        binding.btnVolver.setOnClickListener { findNavController().navigateUp() }
    }

    private fun configurarFechaHora() {
        val ahora = System.currentTimeMillis()
        val sdf = java.text.SimpleDateFormat("dd MMM yyyy · HH:mm", java.util.Locale("es"))
        binding.tvFechaHora.text = "Hoy, ${sdf.format(java.util.Date(ahora))}"
    }

    private fun configurarOrigen() {
        val botones = mapOf(
            "focal" to binding.btnFocal,
            "generalizada" to binding.btnGeneralizada,
            "desconocido" to binding.btnDesconocido
        )
        botones.forEach { (origen, btn) ->
            btn.setOnClickListener {
                origenSeleccionado = origen
                botones.values.forEach { it.isSelected = false }
                btn.isSelected = true
                // Mostrar/ocultar nivel 2
                binding.layoutConsciencia.visibility =
                    if (origen == "focal") View.VISIBLE else View.GONE
                if (origen != "focal") conscienciaPreservada = null
            }
        }
    }

    private fun configurarConsciencia() {
        binding.btnPreservada.setOnClickListener {
            conscienciaPreservada = true
            binding.btnPreservada.isSelected = true
            binding.btnAlterada.isSelected = false
        }
        binding.btnAlterada.setOnClickListener {
            conscienciaPreservada = false
            binding.btnPreservada.isSelected = false
            binding.btnAlterada.isSelected = true
        }
    }

    private fun getChipsSeleccionados(chipGroup: ChipGroup): String {
        val seleccionados = mutableListOf<String>()
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as? Chip ?: continue
            if (chip.isChecked) seleccionados.add(chip.text.toString())
        }
        return seleccionados.joinToString(",")
    }

    private fun getDuracionSeleccionada(): String {
        return when {
            binding.btnDur1.isSelected -> "<1 min"
            binding.btnDur2.isSelected -> "1-3 min"
            binding.btnDur3.isSelected -> "3-5 min"
            binding.btnDur4.isSelected -> ">5 min"
            else -> ""
        }
    }

    private fun guardarCrisis() {
        val duracion = getDuracionSeleccionada()
        if (duracion.isEmpty()) {
            Toast.makeText(requireContext(), "Selecciona la duración", Toast.LENGTH_SHORT).show()
            return
        }
        if (origenSeleccionado.isEmpty()) {
            Toast.makeText(requireContext(), "Selecciona el origen (Nivel 1 ILAE)", Toast.LENGTH_SHORT).show()
            return
        }

        val crisis = Crisis(
            fecha = System.currentTimeMillis(),
            duracion = duracion,
            origenCrisis = origenSeleccionado,
            conscienciaPreservada = conscienciaPreservada,
            manifestaciones = getChipsSeleccionados(binding.chipGroupManifestaciones),
            sintomas = getChipsSeleccionados(binding.chipGroupSintomas),
            desencadenantes = getChipsSeleccionados(binding.chipGroupDesencadenantes),
            notas = binding.etNotas.text?.toString()?.takeIf { it.isNotBlank() }
        )

        viewModel.insertar(crisis)
        Toast.makeText(requireContext(), "Crisis registrada", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package com.epilog.app.ui.perfil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.epilog.app.EpiLogApplication
import com.epilog.app.data.entity.PruebaMedica
import com.epilog.app.databinding.FragmentAnadirPruebaBinding

class AnadirPruebaFragment : Fragment() {

    private var _binding: FragmentAnadirPruebaBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PerfilViewModel by viewModels {
        PerfilViewModelFactory(requireActivity().application as EpiLogApplication)
    }

    private var tipoSeleccionado = ""
    private var uriPdfSeleccionado: String? = null

    private val selectPdfLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            uriPdfSeleccionado = it.toString()
            binding.tvNombreArchivo.text = "PDF seleccionado: ${it.lastPathSegment ?: "archivo.pdf"}"
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnadirPruebaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnVolver.setOnClickListener { findNavController().navigateUp() }

        val tipoBotones = mapOf(
            "RM" to binding.btnRm,
            "TAC" to binding.btnTac,
            "Analítica" to binding.btnAnalitica,
            "EEG" to binding.btnEeg,
            "ECG" to binding.btnEcg,
            "Otra" to binding.btnOtra
        )

        tipoBotones.forEach { (tipo, btn) ->
            btn.setOnClickListener {
                tipoSeleccionado = tipo
                tipoBotones.values.forEach { it.isSelected = false }
                btn.isSelected = true
            }
        }

        binding.btnSubirPdf.setOnClickListener {
            selectPdfLauncher.launch("application/pdf")
        }

        binding.btnGuardar.setOnClickListener {
            if (tipoSeleccionado.isEmpty()) {
                Toast.makeText(requireContext(), "Selecciona el tipo de prueba", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val prueba = PruebaMedica(
                tipo = tipoSeleccionado,
                descripcion = binding.etDescripcion.text?.toString()?.takeIf { it.isNotBlank() },
                fecha = System.currentTimeMillis(),
                notas = binding.etNotas.text?.toString()?.takeIf { it.isNotBlank() },
                uriPdf = uriPdfSeleccionado
            )
            viewModel.guardarPrueba(prueba)
            Toast.makeText(requireContext(), "Prueba guardada", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

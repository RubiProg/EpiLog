package com.epilog.app.ui.inicio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.epilog.app.EpiLogApplication
import com.epilog.app.R
import com.epilog.app.databinding.FragmentInicioBinding
import java.text.SimpleDateFormat
import java.util.*

class InicioFragment : Fragment() {

    private var _binding: FragmentInicioBinding? = null
    private val binding get() = _binding!!

    private val viewModel: InicioViewModel by viewModels {
        InicioViewModelFactory(requireActivity().application as EpiLogApplication)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInicioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.perfil.observe(viewLifecycleOwner) { perfil ->
            val nombre = perfil?.nombre?.substringBefore(" ") ?: ""
            val saludo = if (nombre.isNotEmpty()) "Buenos días, $nombre" else "Buenos días"
            binding.tvSaludo.text = saludo
            binding.tvContactoNombre.text = perfil?.contactoNombre ?: "Sin configurar"
            binding.tvContactoTelefono.text = perfil?.contactoTelefono ?: ""
        }

        viewModel.crisisEsteMes.observe(viewLifecycleOwner) { count ->
            binding.tvCrisisEsteMes.text = count.toString()
        }

        viewModel.todasLasCrisis.observe(viewLifecycleOwner) { lista ->
            val ultima = lista.firstOrNull()
            if (ultima != null) {
                val dias = diasDesde(ultima.fecha)
                binding.tvUltimaCrisis.text = when {
                    dias == 0 -> "Hoy"
                    dias == 1 -> "Ayer"
                    else -> "hace $dias días"
                }
            } else {
                binding.tvUltimaCrisis.text = "Sin registros"
            }
        }

        binding.btnRegistrarCrisis.setOnClickListener {
            findNavController().navigate(R.id.action_inicio_to_registrarCrisis)
        }

        binding.ivPerfilHeader.setOnClickListener {
            findNavController().navigate(R.id.perfilFragment)
        }
    }

    private fun diasDesde(timestamp: Long): Int {
        val ahora = System.currentTimeMillis()
        return ((ahora - timestamp) / (1000 * 60 * 60 * 24)).toInt()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

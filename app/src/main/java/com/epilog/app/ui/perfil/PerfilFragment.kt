package com.epilog.app.ui.perfil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.epilog.app.EpiLogApplication
import com.epilog.app.R

class PerfilFragment : Fragment() {

    private val viewModel: PerfilViewModel by viewModels {
        PerfilViewModelFactory(requireActivity().application as EpiLogApplication)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val perfil by viewModel.perfil.observeAsState()
                val pruebas by viewModel.pruebas.observeAsState(emptyList())

                PerfilScreen(
                    perfil = perfil,
                    pruebasCount = pruebas.size,
                    onVolver = { findNavController().popBackStack() },
                    onGuardarPerfil = { viewModel.guardar(it) },
                    onAnadirPrueba = { findNavController().navigate(R.id.action_perfil_to_anadirPrueba) },
                    onGenerarInforme = { findNavController().navigate(R.id.action_perfil_to_generarInforme) }
                )
            }
        }
    }
}

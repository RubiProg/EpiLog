package com.epilog.app.ui.estadisticas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.epilog.app.EpiLogApplication

class EstadisticasFragment : Fragment() {

    private val viewModel: EstadisticasViewModel by viewModels {
        EstadisticasViewModelFactory(requireActivity().application as EpiLogApplication)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                EstadisticasScreen(
                    viewModel = viewModel,
                    onVolver = { findNavController().popBackStack() }
                )
            }
        }
    }
}

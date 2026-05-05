package com.epilog.app.ui.historial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.epilog.app.EpiLogApplication
import com.epilog.app.databinding.FragmentHistorialBinding
import com.epilog.app.ui.crisis.CrisisViewModel
import com.epilog.app.ui.crisis.CrisisViewModelFactory

class HistorialFragment : Fragment() {

    private var _binding: FragmentHistorialBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CrisisViewModel by viewModels {
        CrisisViewModelFactory(requireActivity().application as EpiLogApplication)
    }

    private lateinit var adapter: CrisisAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistorialBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = CrisisAdapter()
        binding.recyclerCrisis.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerCrisis.adapter = adapter

        viewModel.todasLasCrisis.observe(viewLifecycleOwner) { lista ->
            adapter.submitList(lista)
            binding.tvTotalCrisis.text = lista.size.toString()
            val esteMes = lista.count { esEsteMes(it.fecha) }
            binding.tvCrisisEsteMes.text = esteMes.toString()
            binding.tvEmpty.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun esEsteMes(fecha: Long): Boolean {
        val cal = java.util.Calendar.getInstance()
        val calFecha = java.util.Calendar.getInstance()
        calFecha.timeInMillis = fecha
        return cal.get(java.util.Calendar.YEAR) == calFecha.get(java.util.Calendar.YEAR) &&
                cal.get(java.util.Calendar.MONTH) == calFecha.get(java.util.Calendar.MONTH)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

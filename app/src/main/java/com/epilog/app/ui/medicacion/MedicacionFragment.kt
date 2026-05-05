package com.epilog.app.ui.medicacion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.epilog.app.EpiLogApplication
import com.epilog.app.R
import com.epilog.app.databinding.FragmentMedicacionBinding

class MedicacionFragment : Fragment() {

    private var _binding: FragmentMedicacionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MedicacionViewModel by viewModels {
        MedicacionViewModelFactory(requireActivity().application as EpiLogApplication)
    }

    private lateinit var adapter: MedicamentoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMedicacionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = MedicamentoAdapter(viewModel)
        binding.recyclerMedicamentos.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerMedicamentos.adapter = adapter

        viewModel.todosMedicamentos.observe(viewLifecycleOwner) { lista ->
            adapter.submitList(lista)
            binding.tvEmpty.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.btnAnadirMedicamento.setOnClickListener {
            findNavController().navigate(R.id.action_medicacion_to_anadirMedicamento)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

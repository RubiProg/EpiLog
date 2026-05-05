package com.epilog.app.ui.medicacion

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.epilog.app.data.entity.Medicamento
import com.epilog.app.databinding.ItemMedicamentoBinding

class MedicamentoAdapter(private val viewModel: MedicacionViewModel) :
    ListAdapter<Medicamento, MedicamentoAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMedicamentoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemMedicamentoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(med: Medicamento) {
            binding.tvNombre.text = med.nombre
            binding.tvDosis.text = "${med.dosis} · ${med.tomasAlDia} veces al día"
            binding.chipEstado.text = if (med.activo) "activo" else "pausado"
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Medicamento>() {
        override fun areItemsTheSame(a: Medicamento, b: Medicamento) = a.id == b.id
        override fun areContentsTheSame(a: Medicamento, b: Medicamento) = a == b
    }
}

package com.epilog.app.ui.historial

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.epilog.app.data.entity.Crisis
import com.epilog.app.databinding.ItemCrisisBinding
import java.text.SimpleDateFormat
import java.util.*

class CrisisAdapter : ListAdapter<Crisis, CrisisAdapter.CrisisViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrisisViewHolder {
        val binding = ItemCrisisBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CrisisViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CrisisViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CrisisViewHolder(private val binding: ItemCrisisBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val sdf = SimpleDateFormat("d MMM · HH:mm", Locale("es"))

        fun bind(crisis: Crisis) {
            // Título según clasificación ILAE
            val titulo = when (crisis.origenCrisis) {
                "focal" -> {
                    val cons = when (crisis.conscienciaPreservada) {
                        true -> "focal · consciencia preservada"
                        false -> "focal · consciencia alterada"
                        null -> "focal"
                    }
                    "Crisis $cons"
                }
                "generalizada" -> "Crisis generalizada"
                else -> "Crisis de origen desconocido"
            }
            binding.tvTipoCrisis.text = titulo
            binding.tvMeta.text = "${sdf.format(Date(crisis.fecha))} · ${crisis.duracion}"

            // Etiqueta ILAE de origen
            when (crisis.origenCrisis) {
                "focal" -> {
                    binding.chipOrigen.text = "Focal"
                    binding.chipOrigen.setChipBackgroundColorResource(com.epilog.app.R.color.chip_focal_bg)
                    binding.chipOrigen.setTextColor(
                        binding.root.context.getColor(com.epilog.app.R.color.chip_focal_text)
                    )
                }
                "generalizada" -> {
                    binding.chipOrigen.text = "Generalizada"
                    binding.chipOrigen.setChipBackgroundColorResource(com.epilog.app.R.color.chip_generalizada_bg)
                    binding.chipOrigen.setTextColor(
                        binding.root.context.getColor(com.epilog.app.R.color.chip_generalizada_text)
                    )
                }
                else -> binding.chipOrigen.visibility = View.GONE
            }

            // Manifestación principal
            val manifestacion = crisis.manifestaciones.split(",").firstOrNull()?.trim()
            if (!manifestacion.isNullOrEmpty()) {
                binding.chipManifestacion.text = manifestacion
                binding.chipManifestacion.visibility = View.VISIBLE
            } else {
                binding.chipManifestacion.visibility = View.GONE
            }

            // Aura
            val aura = crisis.sintomas.split(",").firstOrNull()?.trim()
            if (!aura.isNullOrEmpty()) {
                binding.chipAura.text = "aura: $aura"
                binding.chipAura.visibility = View.VISIBLE
            } else {
                binding.chipAura.visibility = View.GONE
            }

            // Desencadenante
            val desenc = crisis.desencadenantes.split(",").firstOrNull()?.trim()
            if (!desenc.isNullOrEmpty()) {
                binding.chipDesenc.text = "desenc: $desenc"
                binding.chipDesenc.visibility = View.VISIBLE
            } else {
                binding.chipDesenc.visibility = View.GONE
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Crisis>() {
        override fun areItemsTheSame(oldItem: Crisis, newItem: Crisis) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Crisis, newItem: Crisis) = oldItem == newItem
    }
}

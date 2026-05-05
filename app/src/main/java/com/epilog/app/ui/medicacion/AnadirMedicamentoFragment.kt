package com.epilog.app.ui.medicacion

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.epilog.app.EpiLogApplication
import com.epilog.app.R
import com.epilog.app.data.entity.Medicamento
import com.epilog.app.data.entity.Toma
import com.epilog.app.databinding.FragmentAnadirMedicamentoBinding
import com.epilog.app.util.MedicacionReceiver
import com.google.android.material.button.MaterialButton
import java.util.Calendar

class AnadirMedicamentoFragment : Fragment() {

    private var _binding: FragmentAnadirMedicamentoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MedicacionViewModel by viewModels {
        MedicacionViewModelFactory(requireActivity().application as EpiLogApplication)
    }

    private var tomasAlDia = 1
    private val horasSeleccionadas = mutableListOf("08:00", "14:00", "20:00", "23:00")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnadirMedicamentoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnVolver.setOnClickListener { findNavController().navigateUp() }

        // Selector tomas al día
        val botonesTomas = listOf(binding.btn1Toma, binding.btn2Tomas, binding.btn3Tomas, binding.btn4Tomas)
        botonesTomas.forEachIndexed { index, btn ->
            btn.setOnClickListener {
                actualizarSeleccionTomas(index + 1, botonesTomas)
            }
        }

        // Inicializar con 1 toma
        actualizarSeleccionTomas(1, botonesTomas)

        binding.btnGuardar.setOnClickListener { guardar() }
    }

    private fun actualizarSeleccionTomas(cantidad: Int, botones: List<MaterialButton>) {
        tomasAlDia = cantidad
        botones.forEach { it.isSelected = false }
        botones[cantidad - 1].isSelected = true
        generarSelectoresHora()
    }

    private fun generarSelectoresHora() {
        binding.containerHoras.removeAllViews()
        for (i in 0 until tomasAlDia) {
            val view = LayoutInflater.from(requireContext()).inflate(R.layout.item_hora_selector, binding.containerHoras, false)
            val tvLabel = view.findViewById<TextView>(R.id.tv_toma_label)
            val btnHora = view.findViewById<MaterialButton>(R.id.btn_seleccionar_hora)
            
            tvLabel.text = "Toma ${i + 1}"
            btnHora.text = horasSeleccionadas[i]
            
            btnHora.setOnClickListener {
                mostrarTimePicker(i, btnHora)
            }
            binding.containerHoras.addView(view)
        }
    }

    private fun mostrarTimePicker(index: Int, button: MaterialButton) {
        val (h, m) = horasSeleccionadas[index].split(":").map { it.toInt() }
        TimePickerDialog(requireContext(), { _, hour, minute ->
            val horaFormateada = String.format("%02d:%02d", hour, minute)
            horasSeleccionadas[index] = horaFormateada
            button.text = horaFormateada
        }, h, m, true).show()
    }

    private fun guardar() {
        val nombre = binding.etNombre.text?.toString()?.trim() ?: ""
        val dosis = binding.etDosis.text?.toString()?.trim() ?: ""

        if (nombre.isEmpty() || dosis.isEmpty()) {
            Toast.makeText(requireContext(), "Nombre y dosis son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        val medicamento = Medicamento(
            nombre = nombre,
            dosis = dosis,
            tomasAlDia = tomasAlDia,
            fechaInicio = System.currentTimeMillis(),
            activo = true
        )

        val tomas = (0 until tomasAlDia).map { i ->
            Toma(medicamentoId = 0, hora = horasSeleccionadas[i], alarmaActivada = true)
        }

        viewModel.insertarMedicamento(medicamento, tomas)

        // Programar alarmas
        tomas.forEachIndexed { i, toma ->
            programarAlarma(nombre, dosis, toma.hora, i)
        }

        Toast.makeText(requireContext(), "Medicamento guardado con alarmas", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    private fun programarAlarma(nombre: String, dosis: String, hora: String, id: Int) {
        val (h, m) = hora.split(":").map { it.toInt() }
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, h)
            set(Calendar.MINUTE, m)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) add(Calendar.DAY_OF_YEAR, 1)
        }

        val intent = Intent(requireContext(), MedicacionReceiver::class.java).apply {
            putExtra(MedicacionReceiver.EXTRA_NOMBRE, nombre)
            putExtra(MedicacionReceiver.EXTRA_DOSIS, dosis)
        }
        val pending = PendingIntent.getBroadcast(
            requireContext(), id, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarm = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.timeInMillis, AlarmManager.INTERVAL_DAY, pending)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

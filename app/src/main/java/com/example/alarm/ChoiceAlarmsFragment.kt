package com.example.alarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alarm.databinding.ChoiceAlarmsFragmentBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChoiceAlarmsFragment: Fragment(R.layout.choice_alarms_fragment), ShowChoiceAlarmsAdapter.OnAlarmClickListener {

    private var binding: ChoiceAlarmsFragmentBinding? = null
    private val args: ChoiceAlarmsFragmentArgs by navArgs()
    // Множество из выбранных будильников
    private val selectedAlarms: MutableSet<Long> = mutableSetOf()

    // Добавляет или удаляет будильник из selectedAlarms и меняет интерфейс 
    override fun changeSelectedAlarm(alarmId: Long, option: Boolean, countAlarms: Int) {
        if (option) {
            selectedAlarms.add(alarmId)

            if (selectedAlarms.size == countAlarms)
                binding!!.selectAllAlarms.isChecked = true
        }
        else {
            selectedAlarms.remove(alarmId)

            if (selectedAlarms.isEmpty())
                findNavController().navigate(ChoiceAlarmsFragmentDirections.actionChoiceToAlarms())
            else if (binding!!.selectAllAlarms.isChecked)
                binding!!.selectAllAlarms.isChecked = false
        }

        binding?.countSelectedAlarmText?.text = "Выбрано: " + selectedAlarms.size.toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ChoiceAlarmsFragmentBinding.inflate(inflater, container, false)
        val alarmViewModel = AlarmViewModel(requireActivity().application)

        val recyclerView: RecyclerView = binding!!.alarms
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        CoroutineScope(Dispatchers.IO).launch {
            val alarms = alarmViewModel.allAlarms()
            withContext(Dispatchers.Main) {
                recyclerView.adapter = ShowChoiceAlarmsAdapter(requireContext(), args.alarmId, alarms, this@ChoiceAlarmsFragment)
            }
        }

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val alarmViewModel = AlarmViewModel(requireActivity().application)

        val recyclerView: RecyclerView = binding!!.alarms
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding?.selectAllAlarms?.setOnClickListener {
            if (binding!!.selectAllAlarms.isChecked) {
                CoroutineScope(Dispatchers.IO).launch {
                    val alarms = alarmViewModel.allAlarms()
                    withContext(Dispatchers.Main) {
                        recyclerView.adapter = ShowChoiceAlarmsAdapter(requireContext(), -1, alarms, this@ChoiceAlarmsFragment)
                    }
                }
            }
            else
                findNavController().navigate(ChoiceAlarmsFragmentDirections.actionChoiceToAlarms())
        }

        binding?.deleteButton?.setOnClickListener {
            // Удаляем будильники из бд
            alarmViewModel.delById(selectedAlarms)
            // Удаляем AlarmReceiver-ы)
            for (alarmId in selectedAlarms) {
                Utils.delAlarmReceiver(requireContext(), alarmId)
            }
            findNavController().navigate(ChoiceAlarmsFragmentDirections.actionChoiceToAlarms())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}

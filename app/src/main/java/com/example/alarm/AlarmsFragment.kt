package com.example.alarm

import android.os.Bundle
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alarm.databinding.AlarmsFragmentBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlarmsFragment: Fragment(R.layout.alarms_fragment), ShowAlarmsAdapter.OnAlarmClickListener {

    private var binding: AlarmsFragmentBinding? = null

    override fun changeUiToChooseAlarms(alarmId: Long) {
        findNavController().navigate(AlarmsFragmentDirections.actionAlarmsToChoiceAlarms(alarmId))
    }

    override fun changeAlarm(alarmId: Long) {
        findNavController().navigate(AlarmsFragmentDirections.actionAlarmsToBuild(alarmId))
    }

    override fun changeNextAlarmText(text1: String, text2: String) {
        val newText = SpannableString(text1 + "\n" + text2)
        newText.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.light_grey)),
            text1.length + 1, text1.length + 1 + text2.length, 0)
        newText.setSpan(AbsoluteSizeSpan(16, true),
            text1.length + 1, text1.length + 1 + text2.length, 0)
        binding?.nextAlarmText?.text = newText
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AlarmsFragmentBinding.inflate(inflater, container, false)
        val alarmViewModel = AlarmViewModel(requireActivity().application)

        val recyclerView: RecyclerView = binding!!.alarms
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        CoroutineScope(Dispatchers.IO).launch {
            val alarms = alarmViewModel.allAlarms()
            withContext(Dispatchers.Main) {
                recyclerView.adapter = ShowAlarmsAdapter(requireContext(), alarms, this@AlarmsFragment)
            }
        }

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val alarmViewModel = AlarmViewModel(requireActivity().application)

        binding?.newAlarmButton?.setOnClickListener {
            findNavController().navigate(AlarmsFragmentDirections.actionAlarmsToBuild())
        }



//
//        moreAction.setOnClickListener() {
//
//        }
//
//        alarms.setOnClickListener() {
//
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
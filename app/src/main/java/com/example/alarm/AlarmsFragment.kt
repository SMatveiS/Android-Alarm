package com.example.alarm

import android.os.Bundle
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alarm.databinding.AlarmsFragmentBinding

class AlarmsFragment: Fragment(R.layout.alarms_fragment), ShowAlarmsAdapter.OnAlarmClickListener {

    private var binding: AlarmsFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AlarmsFragmentBinding.inflate(inflater, container, false)

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val alarmViewModel = AlarmViewModel(requireActivity().application)

        val recyclerView: RecyclerView = binding!!.alarms
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val alarms = alarmViewModel.allAlarms()
        // Подписываемся на обновление БД
        alarms.observe(viewLifecycleOwner) { newAlarms ->
            recyclerView.adapter = ShowAlarmsAdapter(requireContext(), newAlarms, this@AlarmsFragment)
        }

        binding?.newAlarmButton?.setOnClickListener {
            findNavController().navigate(AlarmsFragmentDirections.actionAlarmsToBuild())
        }

        binding?.moreActionButton?.setOnClickListener { buttonView ->
            showPopupMenu(buttonView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


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

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.popupmenu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId) {
                R.id.action1 -> {
                    findNavController().navigate(AlarmsFragmentDirections.actionAlarmsToChoiceAlarms(-2))
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }
}
package com.example.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.alarm.databinding.BuildAlarmFragmentBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

class BuildAlarmsFragment: Fragment(R.layout.build_alarm_fragment) {

    private var binding: BuildAlarmFragmentBinding? = null
    private val args: BuildAlarmsFragmentArgs by navArgs()



    private fun changeAlarmDayText(alarm: Alarm) {
        var newText: String
        when (alarm.weekDaysEnabledSet.size) {
            0 -> {
                newText = if (Utils.getAlarmDate(alarm) == LocalDate.now())
                    ContextCompat.getString(requireContext(), R.string.today) + Utils.getDayText(requireContext(), LocalDate.now())
                else
                    ContextCompat.getString(requireContext(), R.string.yesterday) + Utils.getDayText(requireContext(), LocalDate.now().plusDays(1))
            }

            7 -> newText = ContextCompat.getString(requireContext(), R.string.everyday)

            else -> {
                newText = resources.getString(R.string.every)

                if (DayOfWeek.MONDAY in alarm.weekDaysEnabledSet) { newText += ContextCompat.getString(requireContext(), R.string.mon) }
                if (DayOfWeek.TUESDAY in alarm.weekDaysEnabledSet) { newText += ContextCompat.getString(requireContext(), R.string.tue) }
                if (DayOfWeek.WEDNESDAY in alarm.weekDaysEnabledSet) { newText += ContextCompat.getString(requireContext(), R.string.wed) }
                if (DayOfWeek.THURSDAY in alarm.weekDaysEnabledSet) { newText += ContextCompat.getString(requireContext(), R.string.thu) }
                if (DayOfWeek.FRIDAY in alarm.weekDaysEnabledSet) { newText += ContextCompat.getString(requireContext(), R.string.fri) }
                if (DayOfWeek.SATURDAY in alarm.weekDaysEnabledSet) { newText += ContextCompat.getString(requireContext(), R.string.sat) }
                if (DayOfWeek.SUNDAY in alarm.weekDaysEnabledSet) { newText += ContextCompat.getString(requireContext(), R.string.sun) }
                newText = newText.substring(0, newText.length - 2)
            }
        }

        binding?.alarmDayText?.text = newText
    }

    private fun changeWeekDay(button: CompoundButton, isChecked: Boolean, alarm: Alarm, dayOfWeek: DayOfWeek) {
        if (isChecked) {
            button.setTextColor(ContextCompat.getColor(requireContext(), R.color.purple_500))
            alarm.weekDaysEnabledSet.add(dayOfWeek)
        }

        else {
            button.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            alarm.weekDaysEnabledSet.remove(dayOfWeek)
        }

        changeAlarmDayText(alarm)
    }

    private fun changeSoundSwitch(isChecked: Boolean, alarm: Alarm) {
        if (isChecked) {
            alarm.soundIsEnabled = true
            binding!!.soundSwitch.trackTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.purple_500))
            binding!!.soundName.text = "default"


        }

        else {
            alarm.soundIsEnabled = false
            binding!!.soundSwitch.trackTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.light_grey))
            binding!!.soundName.text = ContextCompat.getString(requireContext(), R.string.state_off)


        }
    }

    private fun changeVibrationSwitch(isChecked: Boolean, alarm: Alarm) {
        if (isChecked) {
            alarm.vibrationIsEnabled = true
            binding!!.vibrationSwitch.trackTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.purple_500))
            binding!!.vibrationName.text = "default"


        }

        else {
            alarm.vibrationIsEnabled = false
            binding!!.vibrationSwitch.trackTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.light_grey))
            binding!!.vibrationName.text = ContextCompat.getString(requireContext(), R.string.state_off)


        }
    }

    private fun changeDelAfterUseSwitch(isChecked: Boolean, alarm: Alarm) {
        if (isChecked) {
            alarm.delAfterUseIsEnabled = true
            binding!!.delAfterUseSwitch.trackTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.purple_500))
            binding!!.delAfterUseState.text = ContextCompat.getString(requireContext(), R.string.state_on)

        }

        else {
            alarm.delAfterUseIsEnabled = false
            binding!!.delAfterUseSwitch.trackTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.light_grey))
            binding!!.delAfterUseState.text = ContextCompat.getString(requireContext(), R.string.state_off)


        }
    }

    fun getAlarmTime(): String {
        return binding!!.hour.text.toString() + ":" + binding!!.minute.text
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = BuildAlarmFragmentBinding.inflate(inflater, container, false)
        val alarmViewModel = AlarmViewModel(requireActivity().application)
        if (args.alarmId != (-1).toLong()) {
            CoroutineScope(Dispatchers.IO).launch {
                val alarm = alarmViewModel.getAlarm(args.alarmId)

                binding!!.alarmName.setText(alarm.name)
                binding!!.soundSwitch.isChecked = alarm.soundIsEnabled
                binding!!.vibrationSwitch.isChecked = alarm.vibrationIsEnabled
                binding!!.delAfterUseSwitch.isChecked = alarm.delAfterUseIsEnabled

                changeSoundSwitch(alarm.soundIsEnabled, alarm)
                changeVibrationSwitch(alarm.vibrationIsEnabled, alarm)

                binding!!.monday.isChecked = '1' in alarm.weekDaysEnabled
                binding!!.tuesday.isChecked = '2' in alarm.weekDaysEnabled
                binding!!.wednesday.isChecked = '3' in alarm.weekDaysEnabled
                binding!!.thursday.isChecked = '4' in alarm.weekDaysEnabled
                binding!!.friday.isChecked = '5' in alarm.weekDaysEnabled
                binding!!.saturday.isChecked = '6' in alarm.weekDaysEnabled
                binding!!.sunday.isChecked = '7' in alarm.weekDaysEnabled


                val time = alarm.time
                binding!!.hour.setText(time.substring(0, 2))
                binding!!.minute.setText(time.substring(3, 5))
            }
        }

        return binding!!.root
    }

    @SuppressLint("ScheduleExactAlarm")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val alarmViewModel = AlarmViewModel(requireActivity().application)

        val newAlarm = Alarm()
        newAlarm.time = "${binding!!.hour.text}:${binding!!.minute.text}"
        newAlarm.soundIsEnabled = binding!!.soundSwitch.isChecked
        newAlarm.vibrationIsEnabled = binding!!.vibrationSwitch.isChecked
        newAlarm.delAfterUseIsEnabled = binding!!.delAfterUseSwitch.isChecked
        newAlarm.soundName = binding!!.soundName.text.toString()
        newAlarm.vibrationName = binding!!.vibrationName.text.toString()

        changeWeekDay(binding!!.monday, binding!!.monday.isChecked, newAlarm, DayOfWeek.MONDAY)
        changeWeekDay(binding!!.tuesday, binding!!.tuesday.isChecked, newAlarm, DayOfWeek.TUESDAY)
        changeWeekDay(binding!!.wednesday, binding!!.wednesday.isChecked, newAlarm, DayOfWeek.WEDNESDAY)
        changeWeekDay(binding!!.thursday, binding!!.thursday.isChecked, newAlarm, DayOfWeek.THURSDAY)
        changeWeekDay(binding!!.friday, binding!!.friday.isChecked, newAlarm, DayOfWeek.FRIDAY)
        changeWeekDay(binding!!.saturday, binding!!.saturday.isChecked, newAlarm, DayOfWeek.SATURDAY)
        changeWeekDay(binding!!.sunday, binding!!.sunday.isChecked, newAlarm, DayOfWeek.SUNDAY)

        newAlarm.time = getAlarmTime()

        
        changeAlarmDayText(newAlarm)

        binding!!.hour.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count  == 1) {
                    var first: Char
                    if (start == 2)
                        first = s?.get(1)!!
                    else
                        first = s?.get(2)!!

                    if (first !in "012" || (first == '2' && s.get(start) !in "0123"))
                        first = '0'
                    binding!!.hour.setText("$first${s.get(start)}")
                }

                if (before == 1) {
                    binding!!.hour.setText("0${s?.get((start + 1) % 2)}")
                }

                newAlarm.time = getAlarmTime()
                changeAlarmDayText(newAlarm)
            }

            override fun afterTextChanged(s: Editable?) { }
        })

        binding!!.minute.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count  == 1) {
                    var first: Char
                    if (start == 2)
                        first = s?.get(1)!!
                    else
                        first = s?.get(2)!!

                    if (first in "6789")
                        first = '0'
                    binding!!.minute.setText("$first${s.get(start)}")
                }

                if (before == 1) {
                    binding!!.minute.setText("0${s?.get((start + 1) % 2)}")
                }

                newAlarm.time = getAlarmTime()
                changeAlarmDayText(newAlarm)
            }

            override fun afterTextChanged(s: Editable?) { }
        })

        binding!!.monday.setOnCheckedChangeListener { button, isChecked ->
            changeWeekDay(button, isChecked, newAlarm, DayOfWeek.MONDAY)
        }
        binding!!.tuesday.setOnCheckedChangeListener { button, isChecked ->
            changeWeekDay(button, isChecked, newAlarm, DayOfWeek.TUESDAY)
        }
        binding!!.wednesday.setOnCheckedChangeListener { button, isChecked ->
            changeWeekDay(button, isChecked, newAlarm, DayOfWeek.WEDNESDAY)
        }
        binding!!.thursday.setOnCheckedChangeListener { button, isChecked ->
            changeWeekDay(button, isChecked, newAlarm, DayOfWeek.THURSDAY)
        }
        binding!!.friday.setOnCheckedChangeListener { button, isChecked ->
            changeWeekDay(button, isChecked, newAlarm, DayOfWeek.FRIDAY)
        }
        binding!!.saturday.setOnCheckedChangeListener { button, isChecked ->
            changeWeekDay(button, isChecked, newAlarm, DayOfWeek.SATURDAY)
        }
        binding!!.sunday.setOnCheckedChangeListener { button, isChecked ->
            changeWeekDay(button, isChecked, newAlarm, DayOfWeek.SUNDAY)
        }


        binding!!.soundSwitch.setOnCheckedChangeListener { _, isChecked ->
            changeSoundSwitch(isChecked, newAlarm)
        }

        binding!!.vibrationSwitch.setOnCheckedChangeListener { _, isChecked ->
            changeVibrationSwitch(isChecked, newAlarm)
        }

        binding!!.delAfterUseSwitch.setOnCheckedChangeListener { _, isChecked ->
            changeDelAfterUseSwitch(isChecked, newAlarm)
        }


        binding!!.cancelButton.setOnClickListener {
            findNavController().navigate(BuildAlarmsFragmentDirections.actionBuildToAlarm())
        }

        binding!!.saveButton.setOnClickListener {
            newAlarm.name = binding!!.alarmName.text.toString().trim()
            Utils.weekSetToString(newAlarm)

            // Если есть будильник с тем же временем, то удаляем его AlarmReceiver
            CoroutineScope(Dispatchers.IO).launch {
                val idOfSimilarAlarm = alarmViewModel.getSimilarAlarm(args.alarmId, newAlarm.time, newAlarm.weekDaysEnabled)
                idOfSimilarAlarm?.let {  Utils.delAlarmReceiver(requireContext(), idOfSimilarAlarm) }
            }
            // И удаляем его из бд (Нельзя сделать оба действия сразу,
            // так как будильник не успеет удалиться из бд, до того как его извлекут в AlarmsFragment)
            alarmViewModel.delSimilarAlarm(args.alarmId, newAlarm.time, newAlarm.weekDaysEnabled)

            // Добавляем либо обновляем будильник в бд
            if (args.alarmId == (-1).toLong()) {
                CoroutineScope(Dispatchers.IO).launch {
                    val alarmId = alarmViewModel.insert(newAlarm)
                    Utils.createAlarmReceiver(requireContext(), alarmId, newAlarm)
                }
            }
            else {
                newAlarm.id = args.alarmId
                alarmViewModel.updateAlarm(newAlarm)
                Utils.createAlarmReceiver(requireContext(), newAlarm.id, newAlarm)
            }

            findNavController().navigate(BuildAlarmsFragmentDirections.actionBuildToAlarm())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
package com.example.alarm

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.alarm.databinding.BuildAlarmFragmentBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate

class BuildAlarmsFragment: Fragment(R.layout.build_alarm_fragment) {
    private var binding: BuildAlarmFragmentBinding? = null
    private val args: BuildAlarmsFragmentArgs by navArgs()
    private lateinit var ringtoneManager: RingtonePreferencesManager
    private var soundUriString: String = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString()

    private var isNotificationPermissionGranted = false
    private var isReadMediaAudioPermissionGranted = false
    private var isReadExternalStoragePermissionGranted = false


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

                soundUriString = alarm.soundName

                binding!!.alarmName.setText(alarm.name)
                binding!!.soundSwitch.isChecked = alarm.soundIsEnabled
                binding!!.vibrationSwitch.isChecked = alarm.vibrationIsEnabled
                binding!!.delAfterUseSwitch.isChecked = alarm.delAfterUseIsEnabled

                changeSoundBlock(alarm.soundIsEnabled, alarm)
                changeVibrationBlock(alarm.vibrationIsEnabled, alarm)
                changeDelAfterUseBlock(alarm.delAfterUseIsEnabled, alarm)

                binding!!.monday.isChecked = '1' in alarm.weekDaysEnabled
                binding!!.tuesday.isChecked = '2' in alarm.weekDaysEnabled
                binding!!.wednesday.isChecked = '3' in alarm.weekDaysEnabled
                binding!!.thursday.isChecked = '4' in alarm.weekDaysEnabled
                binding!!.friday.isChecked = '5' in alarm.weekDaysEnabled
                binding!!.saturday.isChecked = '6' in alarm.weekDaysEnabled
                binding!!.sunday.isChecked = '7' in alarm.weekDaysEnabled


                val time = alarm.time
                binding?.hour?.setText(time.substring(0, 2))
                binding?.minute?.setText(time.substring(3, 5))
            }
        }

        return binding!!.root
    }

    @SuppressLint("ScheduleExactAlarm")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val alarmViewModel = AlarmViewModel(requireActivity().application)

        val newAlarm = Alarm()
        
        binding?.soundSwitch?.let { changeSoundBlock(it.isChecked, newAlarm) }
        binding?.soundSwitch?.let { changeVibrationBlock(it.isChecked, newAlarm) }
        binding?.soundSwitch?.let { changeDelAfterUseBlock(it.isChecked, newAlarm) }

        // Изменяем UI, если мы работаем с уже существующим будильником
        if (args.alarmId != (-1).toLong()) {
            newAlarm.time = "${binding?.hour?.text}:${binding?.minute?.text}"

            changeWeekDay(binding!!.monday, binding!!.monday.isChecked, newAlarm, DayOfWeek.MONDAY)
            changeWeekDay(binding!!.tuesday, binding!!.tuesday.isChecked, newAlarm, DayOfWeek.TUESDAY)
            changeWeekDay(binding!!.wednesday, binding!!.wednesday.isChecked, newAlarm, DayOfWeek.WEDNESDAY)
            changeWeekDay(binding!!.thursday, binding!!.thursday.isChecked, newAlarm, DayOfWeek.THURSDAY)
            changeWeekDay(binding!!.friday, binding!!.friday.isChecked, newAlarm, DayOfWeek.FRIDAY)
            changeWeekDay(binding!!.saturday, binding!!.saturday.isChecked, newAlarm, DayOfWeek.SATURDAY)
            changeWeekDay(binding!!.sunday, binding!!.sunday.isChecked, newAlarm, DayOfWeek.SUNDAY)

            newAlarm.time = getAlarmTime()
        }

        
        changeAlarmDayText(newAlarm)

        binding?.hour?.addTextChangedListener(object: TextWatcher {
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
                    binding?.hour?.setText("$first${s.get(start)}")
                }

                if (before == 1) {
                    binding?.hour?.setText("0${s?.get((start + 1) % 2)}")
                }

                newAlarm.time = getAlarmTime()
                changeAlarmDayText(newAlarm)
            }

            override fun afterTextChanged(s: Editable?) { }
        })

        binding?.minute?.addTextChangedListener(object: TextWatcher {
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
                    binding?.minute?.setText("$first${s.get(start)}")
                }

                if (before == 1) {
                    binding?.minute?.setText("0${s?.get((start + 1) % 2)}")
                }

                newAlarm.time = getAlarmTime()
                changeAlarmDayText(newAlarm)
            }

            override fun afterTextChanged(s: Editable?) { }
        })

        binding?.monday?.setOnCheckedChangeListener { button, isChecked ->
            changeWeekDay(button, isChecked, newAlarm, DayOfWeek.MONDAY)
        }
        binding?.tuesday?.setOnCheckedChangeListener { button, isChecked ->
            changeWeekDay(button, isChecked, newAlarm, DayOfWeek.TUESDAY)
        }
        binding?.wednesday?.setOnCheckedChangeListener { button, isChecked ->
            changeWeekDay(button, isChecked, newAlarm, DayOfWeek.WEDNESDAY)
        }
        binding?.thursday?.setOnCheckedChangeListener { button, isChecked ->
            changeWeekDay(button, isChecked, newAlarm, DayOfWeek.THURSDAY)
        }
        binding?.friday?.setOnCheckedChangeListener { button, isChecked ->
            changeWeekDay(button, isChecked, newAlarm, DayOfWeek.FRIDAY)
        }
        binding?.saturday?.setOnCheckedChangeListener { button, isChecked ->
            changeWeekDay(button, isChecked, newAlarm, DayOfWeek.SATURDAY)
        }
        binding?.sunday?.setOnCheckedChangeListener { button, isChecked ->
            changeWeekDay(button, isChecked, newAlarm, DayOfWeek.SUNDAY)
        }


        binding?.soundSwitch?.setOnCheckedChangeListener { _, isChecked ->
            changeSoundBlock(isChecked, newAlarm)
        }
        binding?.vibrationSwitch?.setOnCheckedChangeListener { _, isChecked ->
            changeVibrationBlock(isChecked, newAlarm)
        }
        binding?.delAfterUseSwitch?.setOnCheckedChangeListener { _, isChecked ->
            changeDelAfterUseBlock(isChecked, newAlarm)
        }

        // Вызываем выбор мелодии будильника
        binding?.alarmSound?.setOnClickListener {
            ringtoneManager = RingtonePreferencesManager(requireContext())
            openRingtonePicker()

            lifecycleScope.launch {
                ringtoneManager.ringtoneUri.collect { uriString ->
                    uriString?.let {
                        newAlarm.soundName = uriString
                        soundUriString = uriString
                    }
                    changeSoundBlock(newAlarm.soundIsEnabled, newAlarm)
                }
            }
        }

        binding?.cancelButton?.setOnClickListener {
            findNavController().navigate(BuildAlarmsFragmentDirections.actionBuildToAlarms())
        }

        binding?.saveButton?.setOnClickListener {
            checkAndRequestPermissions()

            newAlarm.name = binding?.alarmName?.text.toString().trim()
            Utils.parseWeekSetToString(newAlarm)

            // Если есть будильник с тем же временем, то удаляем его AlarmReceiver и удаляем его из бд
            CoroutineScope(Dispatchers.IO).launch {
                val idOfSimilarAlarm = alarmViewModel.getSimilarAlarm(
                    args.alarmId,
                    newAlarm.time,
                    newAlarm.weekDaysEnabled
                )
                idOfSimilarAlarm?.let {
                    alarmViewModel.delById(idOfSimilarAlarm)
                    Utils.delAlarmReceiver(requireContext(), idOfSimilarAlarm)
                }
            }

            // Добавляем либо обновляем будильник в бд
            if (args.alarmId == (-1).toLong()) {
                CoroutineScope(Dispatchers.IO).launch {
                    val alarmId = alarmViewModel.insert(newAlarm)
                    Utils.createAlarmReceiver(requireContext(), alarmId, newAlarm)
                }
            } else {
                newAlarm.id = args.alarmId
                alarmViewModel.updateAlarm(newAlarm)
                Utils.createAlarmReceiver(requireContext(), newAlarm.id, newAlarm)
            }

            findNavController().navigate(BuildAlarmsFragmentDirections.actionBuildToAlarms())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


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
            button.setTextColor(ContextCompat.getColor(requireContext(), R.color.purple))
            alarm.weekDaysEnabledSet.add(dayOfWeek)
        } else {
            button.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            alarm.weekDaysEnabledSet.remove(dayOfWeek)
        }

        changeAlarmDayText(alarm)
    }

    private fun changeSoundBlock(newState: Boolean, alarm: Alarm) {
        alarm.soundIsEnabled = newState
        if (newState) {
            binding?.soundSwitch?.trackTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.purple))
            // Получаем название мелодии из URI
            binding?.soundName?.text = RingtoneManager.getRingtone(
                requireContext(), Uri.parse(soundUriString)).getTitle(requireContext())
        } else {
            binding?.soundSwitch?.trackTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.light_grey))
            binding?.soundName?.text = ContextCompat.getString(requireContext(), R.string.state_off)
        }
    }

    private fun changeVibrationBlock(newState: Boolean, alarm: Alarm) {
        alarm.vibrationIsEnabled = newState
        if (newState) {
            binding?.vibrationSwitch?.trackTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.purple))
            binding?.vibrationName?.text = ContextCompat.getString(requireContext(), R.string.state_on)
        } else {
            binding?.vibrationSwitch?.trackTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.light_grey))
            binding?.vibrationName?.text = ContextCompat.getString(requireContext(), R.string.state_off)
        }
    }

    private fun changeDelAfterUseBlock(newState: Boolean, alarm: Alarm) {
        alarm.delAfterUseIsEnabled = newState
        if (newState) {
            binding?.delAfterUseSwitch?.trackTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.purple))
            binding?.delAfterUseState?.text = ContextCompat.getString(requireContext(), R.string.state_on)
        } else {
            binding?.delAfterUseSwitch?.trackTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.light_grey))
            binding?.delAfterUseState?.text = ContextCompat.getString(requireContext(), R.string.state_off)
        }
    }

    private fun getAlarmTime(): String {
        return binding?.hour?.text.toString() + ":" + binding?.minute?.text
    }


    // Выбор мелодии для будильника
    private val ringtonePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val uri: Uri? = result.data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            // Сохраняем URI
            uri?.let {
                lifecycleScope.launch {
                    ringtoneManager.saveRingtoneUri(it.toString())
                }
            }
        }
    }

    private fun openRingtonePicker() {
        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
            putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
            putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Выберите мелодию будильника")
            putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
            putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
        }
        ringtonePickerLauncher.launch(intent)
    }

    // Запрос разрешений
    private val permissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) {permissions ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            isNotificationPermissionGranted = permissions[Manifest.permission.POST_NOTIFICATIONS] ?: isNotificationPermissionGranted
            isReadMediaAudioPermissionGranted = permissions[Manifest.permission.READ_MEDIA_AUDIO] ?: isReadMediaAudioPermissionGranted
        }
        isReadExternalStoragePermissionGranted = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: isReadExternalStoragePermissionGranted
    }

    private fun checkAndRequestPermissions() {
        val permissionRequest: MutableList<String> = ArrayList()
        // Если API 33 или больше, то явное разрешение нужно только для READ_EXTERNAL_STORAGE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Сначала проверим и попросим разрешение на уведомления
            isNotificationPermissionGranted = ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            isReadMediaAudioPermissionGranted = ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED

            if (!isNotificationPermissionGranted)
                permissionRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            if (!isReadMediaAudioPermissionGranted)
                permissionRequest.add(Manifest.permission.READ_MEDIA_AUDIO)

            if (permissionRequest.isNotEmpty())
                permissionsLauncher.launch(permissionRequest.toTypedArray())
        }
        else if (!isReadExternalStoragePermissionGranted) {
            permissionRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            permissionsLauncher.launch(permissionRequest.toTypedArray())
        }
    }
}
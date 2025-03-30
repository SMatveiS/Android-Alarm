package com.example.alarm

import android.app.Application
import android.content.Context
import android.content.res.ColorStateList
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ShowAlarmsAdapter(
    private val context: Context,
    private val alarms: List<Alarm>,
    private val listener: OnAlarmClickListener
): RecyclerView.Adapter<ShowAlarmsAdapter.ViewHolder>() {

    private val purple = ContextCompat.getColor(context, R.color.purple_500)
    private val lightGrey = ContextCompat.getColor(context, R.color.light_grey)

    private val enabledAlarms: MutableList<Alarm> = mutableListOf()
    private val alarmViewModel = AlarmViewModel(context.applicationContext as Application)

    interface OnAlarmClickListener {
        fun changeNextAlarmText(text1: String, text2: String)
        fun changeAlarm(alarmId: Long)
        fun changeUiToChooseAlarms(alarmId: Long)
    }

    // Возвращает время, оставшееся до будильника
    private fun getTimeBeforeAlarm(alarm: Alarm): Duration {
        val alarmDate = Utils.getAlarmDate(alarm)
        val alarmDateTime = alarmDate.atTime(LocalTime.parse(alarm.time, DateTimeFormatter.ofPattern("HH:mm")))

        return Duration.between(LocalDateTime.now(), alarmDateTime)
    }

    // Находит ближайший будильник
    private fun getNearestAlarm(): Alarm {
        var nearestAlarm: Alarm? = null
        var timeBeforeNearestAlarm: Duration? = null
        for (alarm in enabledAlarms) {
            val timeBeforeAlarm = getTimeBeforeAlarm(alarm)
            if (nearestAlarm == null) {
                nearestAlarm = alarm
                timeBeforeNearestAlarm = timeBeforeAlarm
            }
            else if (timeBeforeNearestAlarm!! > timeBeforeAlarm) {
                timeBeforeNearestAlarm = timeBeforeAlarm
                nearestAlarm = alarm
            }
        }

        return nearestAlarm!!
    }

    // Создаёт и изменяет время для nextAlarmText из AlarmsFragment
    private fun createAndChangeNextAlarmText() {
        var nextAlarmText = "Все будильники отключены"
        var text2 = ""
        if (enabledAlarms.isNotEmpty()) {
            val nearestAlarm = getNearestAlarm()
            val timeBeforeNearestAlarm = getTimeBeforeAlarm(nearestAlarm)
            nextAlarmText = "Будильник"
            val days = timeBeforeNearestAlarm.toDays().toInt()
            nextAlarmText += if (days >= 5)
                "\nчерез $days дней"
            else if (days >= 2)
                "\nчерез $days дня"
            else if (days == 1)
                "\nчерез 1 день"
            else
                " через\n${timeBeforeNearestAlarm.toHours() % 24} ч. ${timeBeforeNearestAlarm.toMinutes() % 60} мин."

            text2 = "${Utils.getDayText(context, Utils.getAlarmDate(nearestAlarm))}, ${nearestAlarm.time}"
        }

        listener.changeNextAlarmText(nextAlarmText, text2)
    }

    // Возвращает текст для alarmDate в зависимости от weekDaysEnabledSet
    private fun getDateText(alarm: Alarm): SpannableString {
        val newText: SpannableString
        when (alarm.weekDaysEnabledSet.size) {
            0 -> newText = SpannableString(Utils.getDayText(context, Utils.getAlarmDate(alarm)))
            7 -> newText = SpannableString(ContextCompat.getString(context, R.string.everyday))
            else -> {
                newText = SpannableString("П В С Ч П C В")
                for (day in alarm.weekDaysEnabledSet) {
                    val dayPositionStart = (day.value - 1) * 2
                    newText.setSpan(ForegroundColorSpan(purple), dayPositionStart, dayPositionStart + 1, 0)
                }
            }
        }
        return newText
    }

    // Меняем цвета надписей и переключателя в зависимости от состояния будильника
    private fun changeAlarmUI(holder: ViewHolder, state: Boolean) {
        val color: Int
        if (state) {
            color = ContextCompat.getColor(context, R.color.white)
            holder.alarmState.trackTintList = ColorStateList.valueOf(purple)
        }
        else {
            color = lightGrey
            holder.alarmState.trackTintList = ColorStateList.valueOf(lightGrey)
        }

        holder.alarmName.setTextColor(color)
        holder.alarmTime.setTextColor(color)
        holder.alarmDate.setTextColor(color)
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val alarm: ConstraintLayout = view.findViewById(R.id.alarm)
        val alarmName: TextView = view.findViewById(R.id.alarm_name)
        val alarmTime: TextView = view.findViewById(R.id.alarm_time)
        val alarmDate: TextView = view.findViewById(R.id.alarm_date)
        val alarmState: SwitchMaterial = view.findViewById(R.id.alarm_state)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.saved_alarm, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return alarms.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == 0) {
            alarms.forEach { alarm ->
                if (alarm.alarmIsEnabled)
                    enabledAlarms.add(alarm)
            }
            createAndChangeNextAlarmText()
        }

        val alarm = alarms[position]
        Utils.parseWeekStringToSet(alarm)

        // Если у будильника нет имени, то меняем его UI
        if (alarm.name == "") {
            val layoutParams = holder.alarmTime.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.topToBottom = ConstraintLayout.LayoutParams.UNSET
            layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        }
        else
            holder.alarmName.text = alarm.name

        holder.alarmDate.text = getDateText(alarm)
        holder.alarmTime.text = alarm.time
        changeAlarmUI(holder, alarm.alarmIsEnabled)

        if (!alarm.alarmIsEnabled)
            holder.alarmState.isChecked = false


        holder.alarmState.setOnCheckedChangeListener {_, isChecked ->
            alarmViewModel.changeAlarmState(alarm.id, isChecked)
            alarm.alarmIsEnabled = isChecked
            changeAlarmUI(holder, isChecked)

            if (isChecked) {
                enabledAlarms.add(alarm)
                Utils.createAlarmReceiver(context, alarm.id, alarm)
            }
            else {
                enabledAlarms.remove(enabledAlarms.find { it.id == alarm.id })
                Utils.delAlarmReceiver(context, alarm.id)
            }

            createAndChangeNextAlarmText()
        }

        holder.alarm.setOnClickListener {
            listener.changeAlarm(alarm.id)
        }

        holder.alarm.setOnLongClickListener {
            listener.changeUiToChooseAlarms(alarm.id)
            true
        }
    }
}
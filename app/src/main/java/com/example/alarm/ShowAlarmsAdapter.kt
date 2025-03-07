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
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ShowAlarmsAdapter(private val context: Context, private val alarms: List<Alarm>, private val listener: OnAlarmClickListener): RecyclerView.Adapter<ShowAlarmsAdapter.ViewHolder>() {

    private val purple = ContextCompat.getColor(context, R.color.purple_500)
    private val lightGrey = ContextCompat.getColor(context, R.color.light_grey)

    private var enabledAlarms: MutableList<Alarm> = mutableListOf()
    private val alarmViewModel = AlarmViewModel(context.applicationContext as Application)
    private var nearestAlarm: Alarm? = null

    interface OnAlarmClickListener{
        fun changeNextAlarmText(text1: String, text2: String)
        fun changeAlarm(alarmId: Long)
    }

    private fun getTimeBeforeAlarm(alarm: Alarm): Duration {
        val alarmDate = Utils.getAlarmDate(alarm)
        val alarmDateTime = alarmDate.atTime(LocalTime.parse(alarm.time, DateTimeFormatter.ofPattern("HH:mm")))

        return Duration.between(LocalDateTime.now(), alarmDateTime)
    }

    private fun getTimeBeforeNearestAlarm(alarms: List<Alarm>): Duration {
        nearestAlarm = alarms[0]
        var timeBeforeNearestAlarm: Duration = getTimeBeforeAlarm(alarms[0])
        for (i in 1..< alarms.size) {
            val timeBeforeAlarm = getTimeBeforeAlarm(alarms[i])

            if (timeBeforeNearestAlarm > timeBeforeAlarm) {
                timeBeforeNearestAlarm = timeBeforeAlarm
                nearestAlarm = alarms[i]
            }
        }

        return timeBeforeNearestAlarm
    }

    private fun createAndChangeNextAlarmText(alarms: List<Alarm>) {
        var nextAlarmText: String
        if (alarms.isEmpty()) {
            nextAlarmText = "Все будильники отключены"
            listener.changeNextAlarmText(nextAlarmText, "")
        }

        else {
            val timeBeforeNearestAlarm = getTimeBeforeNearestAlarm(alarms)
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

            listener.changeNextAlarmText(nextAlarmText,
                "${Utils.getDayText(context, Utils.getAlarmDate(nearestAlarm!!))}, ${nearestAlarm!!.time}")
        }
    }

    private fun getDateText(alarm: Alarm): SpannableString {
        val newText: SpannableString
        when (alarm.weekDaysEnabledSet.size) {
            0 -> newText = SpannableString(Utils.getDayText(context, Utils.getAlarmDate(alarm)))
            7 -> newText = SpannableString(ContextCompat.getString(context, R.string.everyday))
            else -> {
                newText = SpannableString("П В С Ч П C В")
                if (DayOfWeek.MONDAY in alarm.weekDaysEnabledSet) { newText.setSpan(ForegroundColorSpan(purple), 0, 1, 0) }
                if (DayOfWeek.TUESDAY in alarm.weekDaysEnabledSet) { newText.setSpan(ForegroundColorSpan(purple), 2, 3, 0) }
                if (DayOfWeek.WEDNESDAY in alarm.weekDaysEnabledSet) { newText.setSpan(ForegroundColorSpan(purple), 4, 5, 0) }
                if (DayOfWeek.THURSDAY in alarm.weekDaysEnabledSet) { newText.setSpan(ForegroundColorSpan(purple), 6, 7, 0) }
                if (DayOfWeek.FRIDAY in alarm.weekDaysEnabledSet) { newText.setSpan(ForegroundColorSpan(purple), 8, 9, 0) }
                if (DayOfWeek.SATURDAY in alarm.weekDaysEnabledSet) { newText.setSpan(ForegroundColorSpan(purple), 10, 11, 0) }
                if (DayOfWeek.SUNDAY in alarm.weekDaysEnabledSet) { newText.setSpan(ForegroundColorSpan(purple), 12, 13, 0) }
            }
        }
        return newText
    }

    private fun changeTextColor(holder: ViewHolder, color: Int) {
        holder.alarmName.setTextColor(color)
        holder.alarmTime.setTextColor(color)
        holder.alarmDate.setTextColor(color)
    }

    private fun changeAlarmUI(holder: ViewHolder, state: Boolean) {
        if (state) {
            holder.alarmState.trackTintList = ColorStateList.valueOf(purple)
            changeTextColor(holder, ContextCompat.getColor(context, R.color.white))
        }

        else {
            holder.alarmState.trackTintList = ColorStateList.valueOf(lightGrey)
            changeTextColor(holder, lightGrey)
        }
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
            .inflate(R.layout.saved_alarms, parent, false)
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
            createAndChangeNextAlarmText(enabledAlarms)
        }

        val alarm = alarms[position]
        Utils.weekStringToSet(alarm)

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

            createAndChangeNextAlarmText(enabledAlarms)
        }

        holder.alarm.setOnClickListener {
            listener.changeAlarm(alarm.id)
        }
    }
}
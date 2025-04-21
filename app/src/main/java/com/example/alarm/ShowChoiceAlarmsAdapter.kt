package com.example.alarm

import android.content.Context
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class ShowChoiceAlarmsAdapter(
    private val context: Context,
    private val firstSelectedAlarmId: Long,
    private val alarms: List<Alarm>,
    private val listener: OnAlarmClickListener
): RecyclerView.Adapter<ShowChoiceAlarmsAdapter.ViewHolder>() {

    interface OnAlarmClickListener {
        // Изменяет масиив выбранных будильников
        fun changeSelectedAlarm(alarmId: Long, option: Boolean, countAlarms: Int)
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val alarm: ConstraintLayout = view.findViewById(R.id.alarm)
        val alarmName: TextView = view.findViewById(R.id.alarm_name)
        val alarmTime: TextView = view.findViewById(R.id.alarm_time)
        val alarmDate: TextView = view.findViewById(R.id.alarm_date)
        val alarmIsSelected: CheckBox = view.findViewById(R.id.is_selected)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.choice_saved_alarm, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return alarms.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val alarm = alarms[position]
        Utils.parseWeekStringToSet(alarm)

        // Делаем нужный будильник выбранным, если firstSelectedAlarm == -1, то выбраны все будильники, если -2, то ни одного
        if (alarm.id == firstSelectedAlarmId || firstSelectedAlarmId.toInt() == -1)
            changeSelectionState(holder, alarm.id)

        // Если у будильника нет имени, то меняем его UI
        if (alarm.name == "") {
            val layoutParams = holder.alarmTime.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.topToBottom = ConstraintLayout.LayoutParams.UNSET
            layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        } else
            holder.alarmName.text = alarm.name

        holder.alarmDate.text = getDateText(alarm)
        holder.alarmTime.text = alarm.time
        changeTextColor(holder, alarm.alarmIsEnabled)

        holder.alarm.setOnClickListener {
            changeSelectionState(holder, alarm.id)
        }
    }


    // Изменяет состояние выбранности будильника
    private fun changeSelectionState(holder: ViewHolder, alarmId: Long) {
        val newState = !holder.alarmIsSelected.isChecked
        holder.alarmIsSelected.isChecked = newState

        listener.changeSelectedAlarm(alarmId, newState, itemCount)
    }

    // Возвращает текст для alarmDate в зависимости от weekDaysEnabledSet
    private fun getDateText(alarm: Alarm): SpannableString {
        val newText: SpannableString
        when (alarm.weekDaysEnabledSet.size) {
            0 -> newText = SpannableString(Utils.getDayText(context, Utils.getAlarmDate(alarm)))
            7 -> newText = SpannableString(ContextCompat.getString(context, R.string.everyday))
            else -> {
                val purple = ContextCompat.getColor(context, R.color.purple)
                newText = SpannableString("П В С Ч П C В")
                for (day in alarm.weekDaysEnabledSet) {
                    val dayPositionStart = (day.value - 1) * 2
                    newText.setSpan(ForegroundColorSpan(purple), dayPositionStart, dayPositionStart + 1, 0)
                }
            }
        }
        return newText
    }

    // Меняем цвета надписей в зависимости от состояния будильника
    private fun changeTextColor(holder: ViewHolder, state: Boolean) {
        val color = if (state)
            ContextCompat.getColor(context, R.color.white)
        else ContextCompat.getColor(context, R.color.light_grey)

        holder.alarmName.setTextColor(color)
        holder.alarmTime.setTextColor(color)
        holder.alarmDate.setTextColor(color)
    }
}
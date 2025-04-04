package com.example.alarm

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.alarm.databinding.SelectAlarmSoundFragmentBinding

class SelectAlarmSoundFragment: Fragment(R.layout.select_alarm_sound_fragment) {
    private var binding: SelectAlarmSoundFragmentBinding? = null
    val REQUEST_CODE_SOUND = 111

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = SelectAlarmSoundFragmentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.addSound?.setOnClickListener {
            openSoundSelector()
        }
    }

    private fun openSoundSelector() {
        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Выберите мелодию будильника")
        startActivityForResult(intent, REQUEST_CODE_SOUND)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SOUND && resultCode == RESULT_OK) {
            val uri: Uri? = data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            // Для варианта с ACTION_GET_CONTENT используйте: val uri = data?.data

//            if (uri != null) {
//                // Сохраните URI выбранной мелодии (например, в SharedPreferences)
//                saveAlarmSound(uri)
//            }
        }
    }

//    private fun saveAlarmSound(uri: Uri) {
//        // Пример сохранения URI в SharedPreferences
//        val prefs = getSharedPreferences("AlarmPrefs", MODE_PRIVATE)
//        prefs.edit().putString("alarm_sound_uri", uri.toString()).apply()
//    }
}
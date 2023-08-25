package com.AppOrganizerFinalizer.apptest2

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.GridLayout
import android.widget.SeekBar
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class SettingActivity : AppCompatActivity() {

    private lateinit var appSettingsDAO: AppSettingsDAO
    private var isSettingsChanged = false // 설정 변경 여부를 판별하는 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        appSettingsDAO = AppSettingsDAO(this)

        val seekBar: SeekBar = findViewById(R.id.columnCountSeekBar)

        seekBar.progress = appSettingsDAO.getColumnCount()

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (progress < 1) return
                isSettingsChanged = true // 설정 값 변경 시 isSettingsChanged를 true로 설정

                appSettingsDAO.updateSettingsAndViews(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        // 정렬 관련

        val items = arrayOf("이름순", "최신순", "색깔순", "시스템기본정렬순")
        val spinner: Spinner = findViewById(R.id.spinner)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val sharedPreferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val savedSortSetting = sharedPreferences.getString("sortSetting", "최신순") // 기본값은 "최신순"으로 설정

        // 저장된 값에 해당하는 위치 찾기
        val savedPosition = items.indexOf(savedSortSetting)
        if (savedPosition != -1) {
            spinner.setSelection(savedPosition)
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = items[position]

                val editor = sharedPreferences.edit()
                editor.putString("sortSetting", selectedItem)
                editor.apply()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }


    }

    override fun onBackPressed() {
        // 설정 값이 변경되었을 경우 결과 코드를 설정하여 MainActivity로 돌아갑니다.
        if (isSettingsChanged) {
            setResult(RESULT_OK)
        }
        super.onBackPressed()
    }
}

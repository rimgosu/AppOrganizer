package com.AppOrganizerFinalizer.apptest2

import android.os.Bundle
import android.widget.GridLayout
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var appSettingsDAO: AppSettingsDAO
    private lateinit var container: GridLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appSettingsDAO = AppSettingsDAO(this)

        container = findViewById(R.id.appListContainer)
        container.columnCount = appSettingsDAO.getColumnCount()

        val seekBar: SeekBar = findViewById(R.id.columnCountSeekBar)
        seekBar.progress = appSettingsDAO.getColumnCount()
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (progress < 1) return
                appSettingsDAO.updateSettingsAndViews(progress, container)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        appSettingsDAO.updateLauncherAppsViews(container)
    }
}
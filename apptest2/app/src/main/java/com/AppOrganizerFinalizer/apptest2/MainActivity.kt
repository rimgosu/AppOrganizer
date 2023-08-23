package com.AppOrganizerFinalizer.apptest2

import android.os.Bundle
import android.widget.GridLayout
import android.widget.ImageView
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



        appSettingsDAO.updateLauncherAppsViews(container)

        val settingIcon: ImageView = findViewById(R.id.setting)
        SettingDAO.setupSettingIcon(settingIcon, this) // Use SettingDAO to set up click listener
    }

}
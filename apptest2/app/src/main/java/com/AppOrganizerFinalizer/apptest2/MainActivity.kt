package com.AppOrganizerFinalizer.apptest2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.GridLayout
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
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
        Log.d("appcount","${appSettingsDAO.getColumnCount()}")

        appSettingsDAO.updateLauncherAppsViews(container)

        val settingIcon: ImageView = findViewById(R.id.setting)
        settingIcon.setOnClickListener {
            val intent = Intent(this@MainActivity, SettingActivity::class.java)
            settingActivityResultLauncher.launch(intent) // Use ActivityResultLauncher to start SettingActivity
        }
    }


    // ActivityResultLauncher 선언
    // 설정에서 뒤로가기를 했을 때 대응해주는 함수
    private val settingActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // 새로운 columnCount 값을 가져옵니다.
            val newColumnCount = appSettingsDAO.getColumnCount()

            // GridLayout의 모든 자식 뷰를 제거합니다.
            container.removeAllViews()

            // columnCount 값을 업데이트 합니다.
            container.columnCount = newColumnCount

            // 뷰들을 다시 추가 또는 업데이트 합니다.
            appSettingsDAO.updateLauncherAppsViews(container)
        }
    }

}
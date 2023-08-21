package com.example.apptest2

import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout

class AppSettingsDAO(private val context: Context) {

    private val PREFS_NAME = "AppSettings"
    private val COLUMN_COUNT_KEY = "ColumnCount"
    private val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var individualIconSize: Int = 105 // 각 이미지의 길이를 설정하는 전역 변수
    var appIconPadding: Int = 2 // 어플 이미지와 이미지 사이의 패딩을 설정하는 전역 변수 (in dp)

    fun saveColumnCount(count: Int) {
        with(sharedPref.edit()) {
            putInt(COLUMN_COUNT_KEY, count)
            apply()
        }
    }

    fun getColumnCount(): Int {
        return sharedPref.getInt(COLUMN_COUNT_KEY, 4)
    }

    private fun getLauncherApps(): List<ResolveInfo> {
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        return context.packageManager.queryIntentActivities(intent, 0)
    }

    private fun loadLauncherAppsViews(): List<LinearLayout> {
        val launcherApps = getLauncherApps()
        val appViews = mutableListOf<LinearLayout>()
        val paddingInPixels = (context.resources.displayMetrics.density * appIconPadding).toInt()

        for (app in launcherApps) {
            val packageName = app.activityInfo.packageName // 패키지 이름 가져오기
            val className = app.activityInfo.name // 클래스 이름 가져오기

            val appIcon: Drawable = app.activityInfo.loadIcon(context.packageManager)
            val appContainer = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = GridLayout.LayoutParams().apply {
                    width = GridLayout.LayoutParams.WRAP_CONTENT
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                }
            }
            val imageView = ImageView(context)
            imageView.setImageDrawable(appIcon)
            imageView.setPadding(paddingInPixels, paddingInPixels, paddingInPixels, paddingInPixels)
            imageView.layoutParams = LinearLayout.LayoutParams(individualIconSize, individualIconSize)

            // 이미지 뷰에 클릭 리스너 추가
            imageView.setOnClickListener {
                val launchIntent = Intent(Intent.ACTION_MAIN).apply {
                    setClassName(packageName, className)
                    addCategory(Intent.CATEGORY_LAUNCHER)
                }
                context.startActivity(launchIntent)
            }

            appContainer.addView(imageView)
            appViews.add(appContainer)
        }

        return appViews
    }


    fun updateLauncherAppsViews(container: GridLayout) {
        container.removeAllViews()
        val appViews = loadLauncherAppsViews()

        for (view in appViews) {
            container.addView(view)
        }
    }

    fun updateSettingsAndViews(columnCount: Int, container: GridLayout) {
        saveColumnCount(columnCount)
        updateLauncherAppsViews(container)
        container.columnCount = columnCount
    }


}

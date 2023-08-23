package com.AppOrganizerFinalizer.apptest2

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
    var appIconPadding: Int = 2 // 앱 아이콘 간의 패딩 설정 (단위: dp)

    // 런처 앱들의 뷰 생성 및 리스트 반환
    private fun loadLauncherAppsViews(): List<LinearLayout> {
        val launcherApps = getLauncherApps()
        val appViews = mutableListOf<LinearLayout>()
        val paddingInPixels = (context.resources.displayMetrics.density * appIconPadding).toInt()

        for (app in launcherApps) {
            val packageName = app.activityInfo.packageName
            val className = app.activityInfo.name

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
            imageView.layoutParams = LinearLayout.LayoutParams(getIndividualIconSize(getColumnCount()), getIndividualIconSize(getColumnCount()))

            // 아이콘 클릭시 해당 앱 실행
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

    // 주어진 열 개수에 따른 개별 아이콘의 크기 계산
    fun getIndividualIconSize(columnCount: Int): Int {
        return 1050 / columnCount
    }

    // 열 개수를 공유 환경 설정에 저장
    fun saveColumnCount(count: Int) {
        with(sharedPref.edit()) {
            putInt(COLUMN_COUNT_KEY, count)
            apply()
        }
    }

    // 공유 환경 설정에서 열 개수 가져오기 (기본값: 4)
    fun getColumnCount(): Int {
        return sharedPref.getInt(COLUMN_COUNT_KEY, 4)
    }

    // 사용자의 기기에 설치된 런처 앱 목록 가져오기
    private fun getLauncherApps(): List<ResolveInfo> {
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        return context.packageManager.queryIntentActivities(intent, 0)
    }


    // 기존에 있던 런처 앱 뷰들을 제거하고 새로운 앱 뷰들로 업데이트
    fun updateLauncherAppsViews(container: GridLayout) {
        container.removeAllViews()
        val appViews = loadLauncherAppsViews()

        for (view in appViews) {
            container.addView(view)
        }
    }

    // 열 개수 설정 및 런처 앱 뷰 업데이트
    fun updateSettingsAndViews(columnCount: Int, container: GridLayout) {
        saveColumnCount(columnCount)
        updateLauncherAppsViews(container)
        container.columnCount = columnCount
    }
}

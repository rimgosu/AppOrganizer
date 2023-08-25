package com.AppOrganizerFinalizer.apptest2

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout


class AppSettingsDAO(private val context: Context) {

    private val PREFS_NAME = "AppSettings"
    private val COLUMN_COUNT_KEY = "ColumnCount"
    private val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    var appIconPadding: Int = 2 // 앱 아이콘 간의 패딩 설정 (단위: dp)
    var wholeSize: Int = 1068

    // 런처 앱들의 뷰 생성 및 리스트 반환
    private fun loadLauncherAppsViews(): List<LinearLayout> {
        val launcherApps = getLauncherApps()
        val appViews = mutableListOf<LinearLayout>()
        val paddingInPixels = (context.resources.displayMetrics.density * appIconPadding).toInt()

        // 3개의 세팅 중 하나가 선택될거임
        // SettingActivity => AppsettingsDAO
        val sharedPreferences = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val sortSetting = sharedPreferences.getString("sortSetting", "최신순")

        var sortedLauncherApps = launcherApps.sortedBy { it.activityInfo.packageName } // 패키지 이름으로 정렬


        when (sortSetting) {
            "이름순" -> {
                // 이름순으로 정렬하는 로직을 적용
                sortedLauncherApps = launcherApps.sortedBy { it.activityInfo.packageName } // 패키지 이름으로 정렬
                Log.d("aaaaaa", "이름순으로 설정")
            }
            "최신순" -> {
                // 최신순으로 정렬하는 로직을 적용
                sortedLauncherApps = launcherApps.sortedBy { app ->
                    val packageInfo = context.packageManager.getPackageInfo(
                        app.activityInfo.packageName,
                        PackageManager.GET_META_DATA
                    )
                    packageInfo.firstInstallTime
                }
                Log.d("aaaaaa", "최신순으로 설정")
            }
            "색깔순" -> {
                sortedLauncherApps = launcherApps.sortedBy { app ->
                    val appIcon: Drawable = app.activityInfo.loadIcon(context.packageManager)
                    val averageColor = getAverageColorOfDrawable(appIcon)
                    averageColor
                }
                Log.d("aaaaaa", "색깔순으로 설정")
            }
            else -> {
                // 기본적으로 처리할 로직을 적용 (예: 이름순으로 처리)
                sortedLauncherApps = launcherApps
                Log.d("aaaaaa", "예외")
            }
        }

        for (app in sortedLauncherApps) {
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
        return wholeSize / columnCount
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

    fun updateSettingsAndViews(columnCount: Int) {
        saveColumnCount(columnCount)
    }

    private fun getAverageColorOfDrawable(drawable: Drawable): Int {
        val bitmap = convertDrawableToBitmap(drawable)
        val width = bitmap.width
        val height = bitmap.height

        var totalRed = 0
        var totalGreen = 0
        var totalBlue = 0

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)
                totalRed += android.graphics.Color.red(pixel)
                totalGreen += android.graphics.Color.green(pixel)
                totalBlue += android.graphics.Color.blue(pixel)
            }
        }

        val pixelCount = width * height

        val averageRed = totalRed / pixelCount
        val averageGreen = totalGreen / pixelCount
        val averageBlue = totalBlue / pixelCount

        return android.graphics.Color.rgb(averageRed, averageGreen, averageBlue)
    }

    private fun convertDrawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }


}

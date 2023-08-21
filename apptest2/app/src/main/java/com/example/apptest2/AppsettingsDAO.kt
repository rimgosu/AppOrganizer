// AppSettingsDAO.kt
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

        for (app in launcherApps) {
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
            imageView.layoutParams = LinearLayout.LayoutParams(100, 100) // You might want to adjust this
            appContainer.addView(imageView)
            appViews.add(appContainer)
        }

        return appViews
    }

    fun getIconSizeBasedOnColumnCount(columnCount: Int): Int {
        val displayMetrics = context.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val totalWidthForIcons = (0.8 * screenWidth).toInt()
        return totalWidthForIcons / columnCount
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

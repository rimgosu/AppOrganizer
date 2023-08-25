package com.AppOrganizerFinalizer.apptest2

import android.content.Context
import android.content.SharedPreferences

class AppCachingDAO(private val context: Context) {
    private val CACHING_PREFS_NAME = "AppCaching"
    private val sharedPref: SharedPreferences = context.getSharedPreferences(CACHING_PREFS_NAME, Context.MODE_PRIVATE)
    private val APPS_SORTED_ORDER_KEY = "AppsSortedOrder"
    private val APPS_LIST_KEY = "AppsList"

    fun getSortedAppOrder(): List<String>? {
        return sharedPref.getStringList(APPS_SORTED_ORDER_KEY, null)
    }

    fun saveSortedAppOrder(sortedApps: List<String>) {
        with(sharedPref.edit()) {
            putStringList(APPS_SORTED_ORDER_KEY, sortedApps)
            apply()
        }
    }

    fun getSavedAppList(): Set<String>? {
        return sharedPref.getStringSet(APPS_LIST_KEY, null)
    }

    fun saveAppList(appList: Set<String>) {
        with(sharedPref.edit()) {
            putStringSet(APPS_LIST_KEY, appList)
            apply()
        }
    }

    // SharedPreferences에 List<String>을 저장하고 가져오는 확장 함수
    private fun SharedPreferences.Editor.putStringList(key: String, list: List<String>) {
        putString(key, list.joinToString(","))
    }

    private fun SharedPreferences.getStringList(key: String, default: List<String>?): List<String>? {
        val savedString = getString(key, null)
        return savedString?.split(",") ?: default
    }
}

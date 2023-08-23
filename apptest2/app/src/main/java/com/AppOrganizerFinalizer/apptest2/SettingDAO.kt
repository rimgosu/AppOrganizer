package com.AppOrganizerFinalizer.apptest2
import android.content.Context
import android.content.Intent
import android.widget.ImageView

class SettingDAO {
    companion object {
        fun setupSettingIcon(settingIcon: ImageView, context: Context) {
            settingIcon.setOnClickListener {
                val intent = Intent(context, SettingActivity::class.java)
                context.startActivity(intent)
            }
        }
    }
}

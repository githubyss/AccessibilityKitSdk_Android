package com.githubyss.accessibility.kit.util

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.view.accessibility.AccessibilityManager
import com.githubyss.mobile.common.kit.ComkitApplicationConfig
import com.githubyss.mobile.common.kit.util.logEnd
import com.githubyss.mobile.common.kit.util.logMiddle
import com.githubyss.mobile.common.kit.util.logStart


/**
 * AccessibilityCheckUtils
 *
 * @author Ace Yan
 * @github githubyss
 * @createdTime 2022/06/08 17:19:30
 */

/** ****************************** Properties ****************************** */

private const val TAG: String = "AccessibilityCheckUtils"


/** ****************************** Functions ****************************** */

/** ******************** Checker ******************** */

/**
 * 判断是否开启某项无障碍辅助服务
 *
 * @param context 上下文
 * @param serviceName 无障碍辅助服务名
 * @return
 */
fun isAccessibilityServiceEnable(serviceName: String = "", context: Context? = ComkitApplicationConfig.getApp()): Boolean {
    logStart("isAccessibilityServiceEnable", 5)
    val am = context?.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
    val serviceInfos = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC)
    var isServiceEnable = false
    for (info in serviceInfos) {
        val id = info.id
        if (id.contains(serviceName)) {
            logMiddle("info.id: $id")
            isServiceEnable = true
        }
    }
    logMiddle("isAccessibilityServiceEnable >> isServiceEnable: $isServiceEnable")
    logEnd("isAccessibilityServiceEnable", 5)
    return isServiceEnable
}

// fun isAccessibilitySettingsOn(mContext: Context, clazz: Class<out AccessibilityService?>): Boolean {
//     var accessibilityEnabled = 0
//     val service = mContext.packageName + "/" + clazz.canonicalName
//     try {
//         accessibilityEnabled = Settings.Secure.getInt(mContext.applicationContext.contentResolver, Settings.Secure.ACCESSIBILITY_ENABLED)
//     } catch (e: SettingNotFoundException) {
//         logE(TAG, t = e)
//     }
//     val mStringColonSplitter = SimpleStringSplitter(':')
//     if (accessibilityEnabled == 1) {
//         val settingValue = Settings.Secure.getString(mContext.applicationContext.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
//         if (settingValue != null) {
//             mStringColonSplitter.setString(settingValue)
//             while (mStringColonSplitter.hasNext()) {
//                 val accessibilityService = mStringColonSplitter.next()
//                 if (accessibilityService.equals(service, ignoreCase = true)) {
//                     return true
//                 }
//             }
//         }
//     }
//     return false
// }

package com.githubyss.accessibility.kit.util

import android.view.accessibility.AccessibilityEvent
import com.githubyss.mobile.common.kit.util.logEnd
import com.githubyss.mobile.common.kit.util.logMiddle
import com.githubyss.mobile.common.kit.util.logStart


/**
 * AccessibilityPrintUtils
 * 无障碍工具类（打印输出）
 *
 * @author Ace Yan
 * @github githubyss
 * @createdTime 2022/06/08 17:20:30
 */

/** ****************************** Properties ****************************** */

private const val TAG: String = "AccessibilityPrintUtils"


/** ****************************** Functions ****************************** */

/** ********** Print ********** */

/**
 * 打印事件日志
 *
 * @param event 无障碍辅助事件
 * @return
 */
fun printEventLog(event: AccessibilityEvent? = null) {
    logStart("printEventLog", 5)
    event?.let {
        val eventType = event.eventType // 事件类型
        // 响应事件的包名 | 事件源的类名 | 事件源描述
        logMiddle("Package Name (包名): ${event.packageName} | Source Class (类名): ${event.className} | Description (描述): ${event.contentDescription}")
        val eventTypeString = when (eventType) {
            AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED -> "TYPE_NOTIFICATION_STATE_CHANGED (通知栏事件)" // 通知栏事件
            AccessibilityEvent.TYPE_VIEW_CLICKED -> "TYPE_VIEW_CLICKED (点击事件)" // 点击事件
            AccessibilityEvent.TYPE_VIEW_FOCUSED -> "TYPE_VIEW_FOCUSED (获取焦点事件)" // 获取焦点事件
            AccessibilityEvent.TYPE_VIEW_LONG_CLICKED -> "TYPE_VIEW_LONG_CLICKED (长按事件)" // 长按事件
            AccessibilityEvent.TYPE_VIEW_SCROLLED -> "TYPE_VIEW_SCROLLED (页面滚屏事件)" // 页面滚屏事件
            AccessibilityEvent.TYPE_VIEW_SELECTED -> "TYPE_VIEW_SELECTED (控件选中)" // 控件选中
            AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED -> "TYPE_VIEW_TEXT_CHANGED (输入框文本改变)" // 输入框文本改变
            AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED -> "TYPE_VIEW_TEXT_SELECTION_CHANGED (输入框文本 selection 改变)" // 输入框文本 selection 改变
            AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED -> "TYPE_VIEW_ACCESSIBILITY_FOCUSED" //
            AccessibilityEvent.TYPE_GESTURE_DETECTION_START -> "TYPE_GESTURE_DETECTION_START" //
            AccessibilityEvent.TYPE_GESTURE_DETECTION_END -> "TYPE_GESTURE_DETECTION_END" //
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> "TYPE_WINDOW_CONTENT_CHANGED (窗口内容改变)" // 窗口内容改变
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> "TYPE_WINDOW_STATE_CHANGED (窗口状态改变)" // 窗口状态改变
            else -> "No listen event"
        }
        // 事件类型
        logMiddle("Event Type (类型)(int): $eventType | Event Type (类型)(String): $eventTypeString")
        logMiddle("Event Texts: ${event.text}")
        // for (txt in event.text) {
        //     logD(TAG, "Event text: $txt")
        // }
    }
    logEnd("printEventLog", 5)
}

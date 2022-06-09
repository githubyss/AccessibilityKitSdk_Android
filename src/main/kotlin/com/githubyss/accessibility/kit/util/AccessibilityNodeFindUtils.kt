package com.githubyss.accessibility.kit.util

import android.accessibilityservice.AccessibilityService
import android.os.Build
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.githubyss.mobile.common.kit.enumeration.VersionCode
import com.githubyss.mobile.common.kit.util.logE
import com.githubyss.mobile.common.kit.util.logMiddle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/**
 * AccessibilityNodeFindUtils
 * 无障碍工具类（节点匹配）
 *
 * @author Ace Yan
 * @github githubyss
 * @createdTime 2022/05/31 13:06:08
 */

/** ****************************** Properties ****************************** */

private const val TAG: String = "AccessibilityNodeFindUtils"


/** ****************************** Functions ****************************** */

/** ******************** Getter ******************** */

/**
 * 获取根节点信息
 *
 * @param service 无障碍辅助服务
 * @param event 无障碍辅助事件
 * @return
 */
suspend fun getRootNodeInfo(service: AccessibilityService?, event: AccessibilityEvent?): AccessibilityNodeInfo? = withContext(Dispatchers.Default) {
    // logStart("getRootNodeInfo")
    var rootNodeInfo: AccessibilityNodeInfo? = null
    try {
        // logMiddle("service?.rootInActiveWindow: ${service?.rootInActiveWindow}")
        // logMiddle("event?.source: ${event?.source}")
        // logMiddle("service?.windows: ${service?.windows}")
        rootNodeInfo = if (Build.VERSION.SDK_INT >= VersionCode.JELLY_BEAN) {
            service?.rootInActiveWindow
        }
        else {
            // 最好不用，这个 source 不准
            event?.source
            null
        }
    }
    catch (e: Exception) {
        logE(TAG, t = e)
    }

    // logMiddle("rootNodeInfo: ${if (rootNodeInfo == null) "空" else "不空"}")
    // logEnd("getRootNodeInfo")
    rootNodeInfo
}

/**
 * 根据 Id 获取节点列表
 *
 * @param viewId 待查找视图 Id
 * @param service 无障碍辅助服务
 * @param event 无障碍辅助事件
 * @param rootNodeInfo 根节点信息
 * @return
 */
suspend fun findNodeInfoListById(viewId: String = "", rootNodeInfo: AccessibilityNodeInfo? = null, service: AccessibilityService? = null, event: AccessibilityEvent? = null): List<AccessibilityNodeInfo?> = withContext(Dispatchers.Default) {
    // logStart("findNodeInfoListById", 5)
    var rootNodeInfoCopy = rootNodeInfo
    // 传入的 rootNodeInfo 为 null，则根据 service 和 event 重新获取
    if (rootNodeInfoCopy == null) {
        rootNodeInfoCopy = getRootNodeInfo(service, event)
        // logMiddle("rootNodeInfo: ${if (rootNodeInfo == null) "空" else "不空"}")
    }
    var nodeInfoList = emptyList<AccessibilityNodeInfo?>()
    if (rootNodeInfoCopy != null) {
        if (Build.VERSION.SDK_INT >= VersionCode.JELLY_BEAN_MR2) {
            // 需要在 xml 文件中声明权限 android:accessibilityFlags="flagReportViewIds"
            // 并且版本大于 4.3 才能获取到 view 的 ID
            // logMiddle("viewIdResourceName: ${rootNodeInfoCopy.viewIdResourceName}")
            nodeInfoList = rootNodeInfoCopy.findAccessibilityNodeInfosByViewId(viewId)
            // logMiddle("nodeInfoList: $nodeInfoList")
        }
    }
    else {
        logMiddle("nodeInfo is null")
    }

    // logEnd("findNodeInfoListById", 5)
    nodeInfoList
}

/**
 * 根据 Id 获取节点
 *
 * @param viewId 待查找视图 Id
 * @param service 无障碍辅助服务
 * @param event 无障碍辅助事件
 * @param rootNodeInfo 根节点信息
 * @return
 */
suspend fun findNodeInfoById(viewId: String = "", rootNodeInfo: AccessibilityNodeInfo? = null, service: AccessibilityService? = null, event: AccessibilityEvent? = null): AccessibilityNodeInfo? = withContext(Dispatchers.Default) {
    // logStart("findNodeInfoById", 5)
    var nodeInfo: AccessibilityNodeInfo? = null
    val nodeInfos: List<AccessibilityNodeInfo?> = findNodeInfoListById(viewId, rootNodeInfo, service, event)
    when {
        nodeInfos.isNotEmpty() -> {
            nodeInfo = nodeInfos[0]
        }
    }
    // logEnd("findNodeInfoById", 5)
    nodeInfo
}

/**
 * 根据文案获取节点列表
 *
 * @param text 待查找文案
 * @param service 无障碍辅助服务
 * @param event 无障碍辅助事件
 * @param rootNodeInfo 根节点信息
 * @return
 */
suspend fun findNodeInfoListByText(text: String = "", rootNodeInfo: AccessibilityNodeInfo? = null, service: AccessibilityService? = null, event: AccessibilityEvent? = null): List<AccessibilityNodeInfo?> = withContext(Dispatchers.Default) {
    // logStart("findNodeInfoListByText")
    var rootNodeInfoCopy = rootNodeInfo
    // 传入的 rootNodeInfo 为 null，则根据 service 和 event 重新获取
    if (rootNodeInfoCopy == null) {
        rootNodeInfoCopy = getRootNodeInfo(service, event)
        // logMiddle("rootNodeInfo: ${if (rootNodeInfo == null) "空" else "不空"}")
    }
    var nodeInfoList = emptyList<AccessibilityNodeInfo?>()
    if (rootNodeInfoCopy != null) {
        if (Build.VERSION.SDK_INT >= VersionCode.JELLY_BEAN_MR2) {
            // 需要在 xml 文件中声明权限 android:accessibilityFlags="flagReportViewIds"
            // 并且版本大于 4.3 才能获取到 view 的 ID
            // logMiddle("viewIdResourceName: ${rootNodeInfoCopy.viewIdResourceName}")
            nodeInfoList = rootNodeInfoCopy.findAccessibilityNodeInfosByText(text)
            // logMiddle("nodeInfoList: $nodeInfoList")
        }
    }
    else {
        logMiddle("nodeInfo is null")
    }

    // logEnd("findNodeInfoListByText")
    nodeInfoList
}

/**
 * 根据文案获取节点
 *
 * @param text 待查找文案
 * @param service 无障碍辅助服务
 * @param event 无障碍辅助事件
 * @param rootNodeInfo 根节点信息
 * @return
 */
suspend fun findNodeInfoByText(text: String = "", rootNodeInfo: AccessibilityNodeInfo? = null, service: AccessibilityService? = null, event: AccessibilityEvent? = null): AccessibilityNodeInfo? = withContext(Dispatchers.Default) {
    // logStart("findNodeInfoByText")
    var nodeInfo: AccessibilityNodeInfo? = null
    val nodeInfos: List<AccessibilityNodeInfo?> = findNodeInfoListByText(text, rootNodeInfo, service, event)
    when {
        nodeInfos.isNotEmpty() -> {
            nodeInfo = nodeInfos[0]
        }
    }
    // logEnd("findNodeInfoByText")
    nodeInfo
}

suspend fun findNodeInfoByClassName(viewClassName: String, rootNodeInfo: AccessibilityNodeInfo?): AccessibilityNodeInfo? = withContext(Dispatchers.Default) {
    // logStart("findNodeInfoByClassName", 5)

    var findNodeInfo: AccessibilityNodeInfo? = null
    // 根节点不为空，则继续，进行寻找
    rootNodeInfo?.let {
        // 递归寻找
        for (i in 0 until rootNodeInfo.childCount) {
            // 获取子节点
            val childNodeInfo: AccessibilityNodeInfo? = rootNodeInfo.getChild(i)
            // 子节点不为空，则继续，进行类型匹配
            if (childNodeInfo != null) {
                val className: String = childNodeInfo.className?.toString() ?: ""
                // 匹配到指定类型的节点
                if (className == viewClassName) {
                    // logMiddle("寻到指定类型的节点")
                    findNodeInfo = childNodeInfo
                    return childNodeInfo
                }
                else {
                    // 递归寻找
                    findNodeInfo = findNodeInfoByClassName(viewClassName, childNodeInfo)
                    if (findNodeInfo != null) {
                        // logMiddle("寻到指定类型的节点")
                        return findNodeInfo
                    }
                }
            }
        }
    }

    // logEnd("findNodeInfoByClassName", 5)
    return findNodeInfo
}

suspend fun findCheckboxByParentId(viewId: String = "", rootNodeInfo: AccessibilityNodeInfo? = null, service: AccessibilityService? = null, event: AccessibilityEvent? = null): AccessibilityNodeInfo? = withContext(Dispatchers.Default) {
    // logStart("findCheckboxByParentId", 5)
    findNodeInfoByClassName("android.widget.CheckBox", findNodeInfoById(viewId, rootNodeInfo, service, event))
    // logEnd("findCheckboxByParentId", 5)
}

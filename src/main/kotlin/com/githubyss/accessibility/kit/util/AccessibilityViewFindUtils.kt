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

/** */
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
fun getRootNodeInfo(service: AccessibilityService?, event: AccessibilityEvent?): AccessibilityNodeInfo? {
    // logStart("getRootNodeInfo")

    var rootNodeInfo: AccessibilityNodeInfo? = null

    try {
        // logMiddle("service?.rootInActiveWindow: ${service?.rootInActiveWindow}")
        // logMiddle("event?.source: ${event?.source}")
        // logMiddle("service?.windows: ${service?.windows}")
        if (Build.VERSION.SDK_INT >= VersionCode.JELLY_BEAN) {
            rootNodeInfo = service?.rootInActiveWindow
        }
        else {
            // 最好不用，这个 source 不准
            // rootNodeInfo = event?.source
            logMiddle("rootNodeInfo is null.")
        }
    }
    catch (e: Exception) {
        logE(TAG, t = e)
    }

    // logMiddle("rootNodeInfo: ${if (rootNodeInfo == null) "空" else "不空"}")
    // logEnd("getRootNodeInfo")

    return rootNodeInfo
}

/**
 * 根据 Id 获取节点列表
 *
 * @param viewId 待查找视图 Id
 * @param rootNodeInfo 根节点信息
 * @param service 无障碍辅助服务
 * @param event 无障碍辅助事件
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
        logMiddle("rootNodeInfo is null.")
    }

    // logEnd("findNodeInfoListById", 5)

    nodeInfoList
}

/**
 * 根据 Id 获取节点（匹配到的第一个）
 *
 * @param viewId 待查找视图 Id
 * @param rootNodeInfo 根节点信息
 * @param service 无障碍辅助服务
 * @param event 无障碍辅助事件
 * @return
 */
suspend fun findNodeInfoByIdFirstMatched(viewId: String = "", rootNodeInfo: AccessibilityNodeInfo? = null, service: AccessibilityService? = null, event: AccessibilityEvent? = null): AccessibilityNodeInfo? = withContext(Dispatchers.Default) {
    // logStart("findNodeInfoByIdFirstMatched", 5)

    var nodeInfo: AccessibilityNodeInfo? = null

    val nodeInfoList: List<AccessibilityNodeInfo?> = findNodeInfoListById(viewId, rootNodeInfo, service, event)
    when {
        nodeInfoList.isNotEmpty() -> {
            nodeInfo = nodeInfoList[0]
        }
        else -> {
            // logMiddle("$viewId nodeInfo is not found.")
        }
    }

    // logEnd("findNodeInfoByIdFirstMatched", 5)

    nodeInfo
}

/**
 * 根据文案获取节点列表
 *
 * @param text 待查找文案
 * @param rootNodeInfo 根节点信息
 * @param service 无障碍辅助服务
 * @param event 无障碍辅助事件
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
        logMiddle("rootNodeInfo is null.")
    }

    // logEnd("findNodeInfoListByText")

    nodeInfoList
}

/**
 * 根据文案获取节点（匹配到的第一个）
 *
 * @param text 待查找文案
 * @param rootNodeInfo 根节点信息
 * @param service 无障碍辅助服务
 * @param event 无障碍辅助事件
 * @return
 */
suspend fun findNodeInfoByTextFirstMatched(text: String = "", rootNodeInfo: AccessibilityNodeInfo? = null, service: AccessibilityService? = null, event: AccessibilityEvent? = null): AccessibilityNodeInfo? = withContext(Dispatchers.Default) {
    // logStart("findNodeInfoByTextFirstMatched")

    var nodeInfo: AccessibilityNodeInfo? = null

    val nodeInfoList: List<AccessibilityNodeInfo?> = findNodeInfoListByText(text, rootNodeInfo, service, event)
    when {
        nodeInfoList.isNotEmpty() -> {
            nodeInfo = nodeInfoList[0]
        }
        else -> {
            // logMiddle("$text nodeInfo is not found.")
        }
    }

    // logEnd("findNodeInfoByTextFirstMatched")

    nodeInfo
}

/**
 * 根据节点类型名获取节点（匹配到的第一个）
 *
 * @param viewClassName 待查找节点类型名
 * @param rootNodeInfo 根节点信息
 * @return
 */
suspend fun findNodeInfoByClassNameFirstMatched(viewClassName: String, rootNodeInfo: AccessibilityNodeInfo?): AccessibilityNodeInfo? = withContext(Dispatchers.Default) {
    // logStart("findNodeInfoByClassNameFirstMatched", 5)
    // logMiddle("viewClassName: $viewClassName")

    var findNodeInfo: AccessibilityNodeInfo? = null

    // 根节点不为空，则继续，进行寻找
    if (rootNodeInfo != null) {
        val childCount: Int = rootNodeInfo.childCount
        // logMiddle("childCount: $childCount")

        // 遍历寻找
        // logMiddle("进行遍历寻找")
        for (i in 0 until childCount) {
            // 获取子节点
            val childNodeInfo: AccessibilityNodeInfo? = rootNodeInfo.getChild(i)
            // 子节点不为空，则继续，进行类型匹配
            if (childNodeInfo != null) {
                val childClassName: String = childNodeInfo.className?.toString() ?: ""
                // logMiddle("childIndex: $i, childClassName: $childClassName")

                // 匹配到指定类型的节点
                if (childClassName == viewClassName) {
                    logMiddle("寻到指定类型的节点")
                    findNodeInfo = childNodeInfo
                    break
                }
                else {
                    // 递归寻找
                    // logMiddle("进行递归寻找")
                    val nodeInfo: AccessibilityNodeInfo? = findNodeInfoByClassNameFirstMatched(viewClassName, childNodeInfo)
                    if (nodeInfo != null) {
                        logMiddle("递归寻到指定类型的节点")
                        findNodeInfo = nodeInfo
                        break
                    }
                }
            }
        }
    }

    // logEnd("findNodeInfoByClassNameFirstMatched", 5)

    findNodeInfo
}

/**
 * 根据祖先节点 Id 获取 Checkbox 节点（匹配到的第一个）
 *
 * @param ancestorViewId 待查找节点的祖先节点视图 Id
 * @param rootNodeInfo 根节点信息
 * @param service 无障碍辅助服务
 * @param event 无障碍辅助事件
 * @return
 */
suspend fun findCheckboxByParentId(ancestorViewId: String = "", rootNodeInfo: AccessibilityNodeInfo? = null, service: AccessibilityService? = null, event: AccessibilityEvent? = null): AccessibilityNodeInfo? = withContext(Dispatchers.Default) {
    // logStart("findCheckboxByParentId", 5)
    findNodeInfoByClassNameFirstMatched("android.widget.CheckBox", findNodeInfoByIdFirstMatched(ancestorViewId, rootNodeInfo, service, event))
    // logEnd("findCheckboxByParentId", 5)
}

/**
 * 根据祖先节点文案获取 Checkbox 节点（匹配到的第一个）
 *
 * @param ancestorText 待查找节点的祖先节点文案
 * @param rootNodeInfo 根节点信息
 * @param service 无障碍辅助服务
 * @param event 无障碍辅助事件
 * @return
 */
suspend fun findCheckboxByParentText(ancestorText: String = "", rootNodeInfo: AccessibilityNodeInfo? = null, service: AccessibilityService? = null, event: AccessibilityEvent? = null): AccessibilityNodeInfo? = withContext(Dispatchers.Default) {
    // logStart("findCheckboxByParentId", 5)
    findNodeInfoByClassNameFirstMatched("android.widget.CheckBox", findNodeInfoByTextFirstMatched(ancestorText, rootNodeInfo, service, event))
    // logEnd("findCheckboxByParentId", 5)
}

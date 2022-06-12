package com.githubyss.accessibility.kit.util

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.app.Notification
import android.app.PendingIntent
import android.graphics.Path
import android.graphics.Point
import android.os.Build
import android.view.ViewConfiguration
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.githubyss.accessibility.kit.enumeration.SlideState
import com.githubyss.accessibility.kit.enumeration.TapState
import com.githubyss.mobile.common.kit.enumeration.VersionCode
import com.githubyss.mobile.common.kit.util.logE
import com.githubyss.mobile.common.kit.util.logEnd
import com.githubyss.mobile.common.kit.util.logMiddle
import com.githubyss.mobile.common.kit.util.logStart
import kotlinx.coroutines.*


/**
 * AccessibilityViewOperatorUtils
 * 无障碍工具类（视图操作）
 *
 * @author Ace Yan
 * @github githubyss
 * @createdTime 2022/05/31 12:58:02
 */

/** ****************************** Properties ****************************** */

private const val TAG: String = "AccessibilityViewOperatorUtils"

private val TAP_TIMEOUT: Int = ViewConfiguration.getTapTimeout()
private val LONG_PRESS_TIMEOUT: Int = ViewConfiguration.getLongPressTimeout()
private val DOUBLE_TAP_TIMEOUT: Int = ViewConfiguration.getDoubleTapTimeout()

private const val DEFAULT_TAP_DURATION: Long = 100
private const val DEFAULT_LONG_TAP_DURATION: Long = 500
private const val DEFAULT_TAP_INTERVAL: Long = 150


/** ****************************** Functions ****************************** */

/** ******************** Operator ******************** */

/** ********** Open App ********** */

/**
 * 监听通知栏，打开应用
 *
 * @param event 无障碍辅助事件
 * @return
 */
suspend fun openAppByNotification(event: AccessibilityEvent? = null) = withContext(Dispatchers.Default) {
    logStart("openAppByNotification", 5)

    event?.let {
        if (event.parcelableData != null && event.parcelableData is Notification) {
            val notification: Notification = event.parcelableData as Notification
            try {
                val pendingIntent: PendingIntent = notification.contentIntent
                pendingIntent.send()
            }
            catch (e: PendingIntent.CanceledException) {
                logE(TAG, t = e)
            }
        }
    }

    logEnd("openAppByNotification", 5)
}

/** ********** Tap Node ********** */

/**
 * 点击可点击节点
 * 点击指定的节点，通过入参决定是否需要判断节点的可点击性
 *
 * @param tapNodeInfo 待点击节点信息
 * @param isTapForcibly 是否强制点击
 * @param onTap 点击回调
 * @return
 */
suspend fun tapClickableSelf(tapNodeInfo: AccessibilityNodeInfo?, isTapForcibly: Boolean = false, onTap: (tapState: String) -> Unit = {}): Boolean = withContext(Dispatchers.Default) {
    logStart("tapClickableSelf", 1)
    // logMiddle("tapNodeInfo: $tapNodeInfo")

    // 默认点击成功标志-未成功
    var isTapSucceed: Boolean = false
    // 默认点击状态-未点击
    var tapState: String = TapState.NO_CLICKED

    // 节点为空，则状态置为空
    if (tapNodeInfo == null) {
        TapState.NULL
    }
    // 节点不为空，则尝试点击
    else if (tapNodeInfo.isClickable || isTapForcibly) {
        logMiddle("尝试操作节点单击-自身")
        isTapSucceed = tapNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)

        tapState = if (isTapSucceed) {
            logMiddle("操作单击成功-自身")
            TapState.CLICKED
        }
        else {
            logMiddle("操作单击失败-自身")
            TapState.CLICK_FAILED
        }
    }

    // logMiddle("点击状态『$tapState』")
    logEnd("tapClickableSelf", 1)

    // 回调点击接口，传回点击状态
    onTap(tapState)
    // 返回点击成功标志
    isTapSucceed
}

/**
 * 点击可点击节点
 * 递归节点本身及其父节点，向父层递归，一层一层判断可点击性，点击最近一个可点击的节点
 *
 * @param tapNodeInfo 待点击节点信息
 * @param onTap 点击回调
 * @return
 */
suspend fun tapClickableParent(tapNodeInfo: AccessibilityNodeInfo?, onTap: (tapState: String) -> Unit = {}): Boolean = withContext(Dispatchers.Default) {
    logStart("tapClickableParent", 1)
    // logMiddle("tapNodeInfo: $tapNodeInfo")

    // 默认点击成功标志-未成功
    var isTapSucceed: Boolean = false
    // 默认点击状态-未点击
    var tapState: String = TapState.NO_CLICKED

    // 节点为空，则状态置为空
    if (tapNodeInfo == null) {
        tapState = TapState.NULL
    }
    // 节点不为空，则递归父节点，尝试点击
    else {
        var parentNodeInfo = tapNodeInfo
        // 递归父节点寻找可点击节点
        while (parentNodeInfo != null) {
            // 找到可点击节点，进行点击
            if (parentNodeInfo.isClickable) {
                logMiddle("尝试操作节点单击-递归父节点")
                isTapSucceed = parentNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)

                tapState = if (isTapSucceed) {
                    logMiddle("操作单击成功-递归父节点")
                    TapState.CLICKED
                }
                else {
                    logMiddle("操作单击失败-递归父节点")
                    TapState.CLICK_FAILED
                }

                break
            }
            parentNodeInfo = parentNodeInfo.parent
        }
    }

    // logMiddle("点击状态『$tapState』")
    logEnd("tapClickableParent", 1)

    // 回调点击接口，传回点击状态
    onTap(tapState)
    // 返回点击成功标志
    isTapSucceed
}

/** ********** Tap Point ********** */

/**
 * 单击指定坐标
 * 默认短按
 *
 * tap(service, Point(0, 400), 100L) {}
 *
 * @param service 无障碍辅助服务
 * @param point 坐标点
 * @param duration 持续时长
 * @param onTap 点击回调
 * @return
 */
suspend fun tap(service: AccessibilityService?, point: Point, duration: Long = DEFAULT_TAP_DURATION, onTap: (tapState: String) -> Unit = {}): Boolean = withContext(Dispatchers.Default) {
    // logStart("tap", 5)
    // logMiddle("point: $point")

    // 默认点击成功标志-未成功
    var isTapSucceed: Boolean = false
    // 默认点击状态-未点击
    var tapState: String = TapState.NO_CLICKED

    val path = Path()
    path.moveTo(point.x.toFloat(), point.y.toFloat())
    if (Build.VERSION.SDK_INT >= VersionCode.N) {
        val strokeDesc = GestureDescription.StrokeDescription(path, 0, duration)
        val gestureDesc = GestureDescription.Builder().addStroke(strokeDesc).build()
        logMiddle("尝试操作坐标单击")
        isTapSucceed = service?.dispatchGesture(gestureDesc, null, null) ?: false
    }

    tapState = if (isTapSucceed) {
        logMiddle("操作单击成功")
        when {
            duration <= TAP_TIMEOUT -> logMiddle("短按 point: $point")
            duration >= LONG_PRESS_TIMEOUT -> logMiddle("长按 point: {${point.x}, ${point.y}}")
        }

        TapState.CLICKED
    }
    else {
        logMiddle("操作单击失败")
        TapState.CLICK_FAILED
    }

    // logMiddle("点击状态『$tapState』")
    // logEnd("tap", 5)

    // 回调点击接口，传回点击状态
    onTap(tapState)
    // 返回点击成功标志
    isTapSucceed
}

suspend fun tapShort(service: AccessibilityService?, point: Point, onTap: (tapState: String) -> Unit = {}): Boolean = withContext(Dispatchers.Default) {
    logMiddle("尝试操作坐标短按单击")
    tap(service, point, DEFAULT_TAP_DURATION, onTap)
}

suspend fun tapLong(service: AccessibilityService?, point: Point, onTap: (tapState: String) -> Unit = {}): Boolean = withContext(Dispatchers.Default) {
    logMiddle("尝试操作坐标长按单击")
    tap(service, point, DEFAULT_LONG_TAP_DURATION, onTap)
}

/**
 * 双击指定坐标（使用 points 列表循环实现）
 * 默认短按
 *
 * tapDouble(service, Point(0, 400), 100L, 150L) {}
 *
 * @param service 无障碍辅助服务
 * @param point 坐标点
 * @param duration 持续时长
 * @param interval 两次按下之间的停顿间隔
 * @param onTap 点击回调
 * @return
 */
suspend fun tapDouble(service: AccessibilityService?, point: Point, duration: Long = DEFAULT_TAP_DURATION, interval: Long = DEFAULT_TAP_INTERVAL, onTap: (tapState: String) -> Unit = {}): Boolean = withContext(Dispatchers.Default) {
    val points: ArrayList<Point> = arrayListOf(point, point)
    logMiddle("尝试操作坐标双击")
    tapMulti(service, points, duration, interval, onTap)
}

suspend fun tapDoubleShort(service: AccessibilityService?, point: Point, onTap: (tapState: String) -> Unit = {}): Boolean = withContext(Dispatchers.Default) {
    logMiddle("尝试操作坐标短按双击")
    tapDouble(service, point, DEFAULT_TAP_DURATION, DEFAULT_TAP_INTERVAL, onTap)
}

suspend fun tapDoubleLong(service: AccessibilityService?, point: Point, onTap: (tapState: String) -> Unit = {}): Boolean = withContext(Dispatchers.Default) {
    logMiddle("尝试操作坐标长按双击")
    tapDouble(service, point, DEFAULT_LONG_TAP_DURATION, DEFAULT_TAP_INTERVAL, onTap)
}

/**
 * 连击指定坐标（使用 points 列表循环实现）
 * 默认短按
 * 依次单击指定的坐标点们
 *
 * tapMulti(service, arrayListOf(Point(0, 400), Point(400, 400)), 100L, 150L) {}
 *
 * @param service 无障碍辅助服务
 * @param points 坐标点
 * @param duration 持续时长
 * @param interval 两次按下之间的停顿间隔
 * @param onTap 点击回调
 * @return
 */
suspend fun tapMulti(service: AccessibilityService?, points: List<Point>, duration: Long = DEFAULT_TAP_DURATION, interval: Long = DEFAULT_TAP_INTERVAL, onTap: (tapState: String) -> Unit = {}): Boolean = withContext(Dispatchers.Default) {
    // logStart("tap", 5)
    // logMiddle("points: $points")

    // 默认点击成功标志-未成功
    var isTapSucceed: Boolean = false
    // 默认点击状态-未点击
    var tapState: String = TapState.NO_CLICKED

    logMiddle("尝试操作坐标列表连击")
    run points@{
        points.forEach {
            if (!tap(service, it, duration)) {
                isTapSucceed = false
                return@points
            }
            delay(interval)
            isTapSucceed = true
        }
    }

    tapState = if (isTapSucceed) {
        logMiddle("操作连击成功")
        when {
            duration <= TAP_TIMEOUT -> logMiddle("短按 points: $points")
            duration >= LONG_PRESS_TIMEOUT -> logMiddle("长按 points: {${points.map { it.x }}, ${points.map { it.y }}")
        }

        TapState.CLICKED
    }
    else {
        logMiddle("操作连击失败")
        TapState.CLICK_FAILED
    }

    // logMiddle("点击状态『$tapState』")
    // logEnd("tap", 5)

    // 回调点击接口，传回点击状态
    onTap(tapState)
    // 返回点击成功标志
    isTapSucceed
}

suspend fun tapMultiShort(service: AccessibilityService?, points: List<Point>, onTap: (tapState: String) -> Unit = {}): Boolean = withContext(Dispatchers.Default) {
    logMiddle("尝试操作坐标列表短按连击")
    tapMulti(service, points, DEFAULT_TAP_DURATION, DEFAULT_TAP_INTERVAL, onTap)
}

suspend fun tapMultiLong(service: AccessibilityService?, points: List<Point>, onTap: (tapState: String) -> Unit = {}): Boolean = withContext(Dispatchers.Default) {
    logMiddle("尝试操作坐标列表长按连击")
    tapMulti(service, points, DEFAULT_LONG_TAP_DURATION, DEFAULT_TAP_INTERVAL, onTap)
}

// /**
//  * 双击指定坐标（使用 GestureResultCallback 实现）
//  *
//  * tapDoubleByGestureResult(service, Point(0, 400), 100L, 150L)
//  *
//  * @param service 无障碍辅助服务
//  * @param point 坐标点
//  * @param duration 持续时长
//  * @param interval 两次按下之间的停顿间隔
//  * @param onTap 点击回调
//  * @return
//  */
// suspend fun tapDoubleByGestureResult(service: AccessibilityService?, point: Point, duration: Long = DEFAULT_TAP_DURATION, interval: Long = DEFAULT_DOUBLE_TAP_DURATION, onTap: (tapState: String) -> Unit = {}): Boolean = withContext(Dispatchers.Default) {
//     // logStart("tapDouble", 5)
//     // logMiddle("point: $point")
//
//     // 默认点击成功标志-未成功
//     var isTapSucceed: Boolean = false
//     // 默认点击状态-未点击
//     var tapState: String = TapState.NO_CLICKED
//
//     if (Build.VERSION.SDK_INT >= VersionCode.N) {
//         val path = Path()
//         path.moveTo(point.x.toFloat(), point.y.toFloat())
//         val strokeDesc = GestureDescription.StrokeDescription(path, 0, duration)
//         val gestureDesc = GestureDescription.Builder().addStroke(strokeDesc).build()
//
//         logMiddle("尝试操作坐标双击")
//         logMiddle("尝试操作坐标双击第一次")
//         service?.dispatchGesture(gestureDesc, object : AccessibilityService.GestureResultCallback() {
//             override fun onCompleted(gestureDescription: GestureDescription?) {
//                 super.onCompleted(gestureDescription)
//                 logMiddle("短按 point: $point 完成第一次")
//                 logMiddle("操作成功")
//                 when {
//                     duration <= TAP_TIMEOUT -> logMiddle("短按 points: $point")
//                     duration >= LONG_PRESS_TIMEOUT -> logMiddle("长按 point: {${point.x}, ${point.y}}")
//                 }
//
//                 CoroutineScope(Dispatchers.Main).launch {
//                     // 延时 gap
//                     delay(interval)
//
//                     logMiddle("尝试操作坐标双击第二次")
//                     service.dispatchGesture(gestureDesc, object : AccessibilityService.GestureResultCallback() {
//                         override fun onCompleted(gestureDescription: GestureDescription?) {
//                             super.onCompleted(gestureDescription)
//                             logMiddle("短按 point: $point 完成第二次")
//                             logMiddle("操作成功")
//                             when {
//                                 duration <= TAP_TIMEOUT -> logMiddle("短按 points: $points")
//                                 duration >= LONG_PRESS_TIMEOUT -> logMiddle("长按 points: {${points.map { it.x }}, ${points.map { it.y }}")
//                             }
//
//                             isTapSucceed = true
//                             tapState = TapState.CLICKED
//                         }
//
//                         override fun onCancelled(gestureDescription: GestureDescription?) {
//                             super.onCancelled(gestureDescription)
//                             logMiddle("短按 point: $point 取消第二次")
//                         }
//                     }, null)
//                 }
//             }
//
//             override fun onCancelled(gestureDescription: GestureDescription?) {
//                 super.onCancelled(gestureDescription)
//                 logMiddle("短按 point: $point 取消第一次")
//             }
//         }, null) ?: false
//     }
//
//     // logMiddle("点击状态『$tapState』")
//     // logEnd("tapDouble", 5)
//
//     // 回调点击接口，传回点击状态
//     onTap(tapState)
//     // 返回点击成功标志
//     isTapSucceed
// }

/** ********** Slide ********** */

/**
 * 滑动一次
 * slideOnce(service, Point(0, 400), Point(400, 400), 0L, 500L)
 *
 * @param pointFrom 起点坐标
 * @param pointTo 终点坐标
 * @param service 无障碍辅助服务
 * @param startTime 启动延迟
 * @param duration 持续时长
 * @return
 */
suspend fun slideOnce(pointFrom: Point, pointTo: Point, service: AccessibilityService?, startTime: Long = 0, duration: Long = 1000, onSlide: (slideState: String) -> Unit = {}): Boolean = withContext(Dispatchers.Default) {
    // logStart("slideOnce", 5)
    // logMiddle("pointFrom: $pointFrom, pointTo: $pointTo")

    // 默认滑动成功标志-未成功
    var isSlideSucceed: Boolean = false
    // 默认滑动状态-未滑动
    var slideState: String = SlideState.NO_SLID

    // 线性的 path 代表手势路径，点代表按下，封闭的没用
    val path = Path()
    path.moveTo(pointFrom.x.toFloat(), pointFrom.y.toFloat())
    path.lineTo(pointTo.x.toFloat(), pointTo.y.toFloat())
    if (Build.VERSION.SDK_INT >= VersionCode.N) {
        val strokeDesc = GestureDescription.StrokeDescription(path, startTime, duration)
        val gestureDesc = GestureDescription.Builder().addStroke(strokeDesc).build()
        logMiddle("尝试操作坐标手势滑动一次")
        isSlideSucceed = service?.dispatchGesture(gestureDesc, null, null) ?: false
    }

    if (isSlideSucceed) {
        logMiddle("操作成功")
        slideState = SlideState.SLID
    }

    // logMiddle("滑动状态『$slideState』")
    // logEnd("slideOnce", 5)

    // 回调滑动接口，传回滑动状态
    onSlide(slideState)
    // 返回滑动成功标志
    isSlideSucceed
}

/**
 * 滑动两次（使用 GestureResultCallback 实现）
 *
 * slideTwice(service, Point(0, 400), Point(400, 400), 0L, 500L, Point(600, 600), Point(600, 800), 1000L, 500L)
 *
 * @param service 无障碍辅助服务
 * @param pointFrom1 起点坐标1
 * @param pointTo1 终点坐标1
 * @param pointFrom2 起点坐标2
 * @param pointTo2 终点坐标2
 * @param startTime1 启动延迟1
 * @param duration1 持续时长1
 * @param startTime2 启动延迟2
 * @param duration2 持续时长2
 *
 * @return
 */
fun slideTwiceByGestureResult(service: AccessibilityService?, pointFrom1: Point, pointTo1: Point, pointFrom2: Point, pointTo2: Point, startTime1: Long = 0, duration1: Long, startTime2: Long = 0, duration2: Long = 1000) {
    logStart("slideTwice", 5)
    if (Build.VERSION.SDK_INT >= VersionCode.N) {
        val path1 = Path()
        path1.moveTo(pointFrom1.x.toFloat(), pointFrom1.y.toFloat())
        path1.lineTo(pointTo1.x.toFloat(), pointTo1.y.toFloat())
        val strokeDesc1 = GestureDescription.StrokeDescription(path1, startTime1, duration1)
        val gestureDesc1 = GestureDescription.Builder()
            .addStroke(strokeDesc1)
            .build()

        // 第一个手势动作
        logMiddle("手势滑动第一次")
        service?.dispatchGesture(gestureDesc1, object : AccessibilityService.GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription) {
                super.onCompleted(gestureDescription)
                logMiddle("手势滑动第一次完成")

                // 也可以使用 handler 延时 1.5 秒就不用在这里回调了
                val path2 = Path()
                path2.moveTo(pointFrom2.x.toFloat(), pointFrom2.y.toFloat())
                path2.lineTo(pointTo2.x.toFloat(), pointTo2.y.toFloat())
                val strokeDesc2 = GestureDescription.StrokeDescription(path2, startTime2, duration2)
                val gestureDesc2 = GestureDescription.Builder()
                    .addStroke(strokeDesc2)
                    .build()

                // 第一个手势动作后，再过 startTime2 秒，进行第二个动作
                logMiddle("手势滑动第二次")
                service.dispatchGesture(gestureDesc2, null, null)
            }

            override fun onCancelled(gestureDescription: GestureDescription) {
                logMiddle("手势滑动第一次被取消")
                super.onCancelled(gestureDescription)
            }
        }, null)
    }
    logEnd("slideTwice", 5)
}

/** ********** Back ********** */

/**
 * 返回一次
 *
 * @param service 无障碍辅助服务
 * @param delay 延迟时长
 * @return
 */
suspend fun backOnce(service: AccessibilityService?, delay: Long = 0): Boolean = withContext(Dispatchers.Default) {
    // logStart("backOnce", 5)
    // logMiddle("返回一次")

    delay(delay)
    val isBackSucceed: Boolean = service?.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK) ?: false

    // logMiddle("延迟 ${delay}ms > GLOBAL_ACTION_BACK (执行返回一次)")
    // logEnd("backOnce", 5)

    isBackSucceed
}

/** ********** Home Screen ********** */

/**
 * 进入桌面
 *
 * @param service 无障碍辅助服务
 * @param delay 延迟时长
 * @return
 */
fun goHomeScreen(service: AccessibilityService?, delay: Long = 0) {
    logStart("goHomeScreen", 5)
    logMiddle("进入桌面")
    CoroutineScope(Dispatchers.Main).launch {
        delay(delay)
        service?.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME)
        logMiddle("延迟 ${delay}ms > GLOBAL_ACTION_HOME (执行进入桌面)")
    }
    logEnd("goHomeScreen", 5)
}

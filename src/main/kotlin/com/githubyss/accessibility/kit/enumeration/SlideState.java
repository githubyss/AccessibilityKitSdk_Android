package com.githubyss.accessibility.kit.enumeration;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import androidx.annotation.StringDef;


/**
 * SlideState
 * 滑动状态
 *
 * @author Ace Yan
 * @github githubyss
 * @createdTime 2022/06/09 14:30:28
 */
@Documented
@StringDef({SlideState.SLID, SlideState.NO_SLID, SlideState.NULL})
@Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface SlideState {
    final String SLID = "已滑动";
    final String NO_SLID = "未滑动";
    final String NULL = "空节点";
}

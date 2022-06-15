package com.githubyss.accessibility.kit.enumeration;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import androidx.annotation.StringDef;


/**
 * TapState
 * 点击状态
 *
 * @author Ace Yan
 * @github githubyss
 * @createdTime 2022/06/09 11:15:43
 */
@Documented
@StringDef({TapState.CLICKABLE,
            TapState.UNCLICKABLE,
            TapState.CLICKED,
            TapState.NO_CLICKED,
            TapState.CLICK_FAILED,
            TapState.NULL,})
@Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface TapState {
    String CLICKABLE = "可点击";
    String UNCLICKABLE = "不可点击";
    String CLICKED = "已点击";
    String NO_CLICKED = "未点击";
    String CLICK_FAILED = "未点击";
    String NULL = "空节点";
}

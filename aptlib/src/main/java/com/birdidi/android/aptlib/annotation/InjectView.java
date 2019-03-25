package com.birdidi.android.aptlib.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author xuyu.chen
 * @date 2019/03/25
 * @email xuyu.chen@ucarinc.com
 * @desc
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface InjectView {
    int value() default 0;
}

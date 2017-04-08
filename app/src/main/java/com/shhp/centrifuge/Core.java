package com.shhp.centrifuge;

import com.shhp.centrifuge.annotation.CodeExtractor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Created by herculewang on 2017/3/15.
 */

@Documented
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.TYPE, ElementType.LOCAL_VARIABLE})
@CodeExtractor
public @interface Core {
}

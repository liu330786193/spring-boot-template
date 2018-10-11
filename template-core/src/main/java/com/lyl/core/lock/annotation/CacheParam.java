package com.lyl.core.lock.annotation;

import java.lang.annotation.*;

/**
 * @Author lyl
 * @Description
 * @Date 2018/9/30 上午10:18
 */
@Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface CacheParam {

    /**
     * 字段名称
     */
    String name() default "";

}

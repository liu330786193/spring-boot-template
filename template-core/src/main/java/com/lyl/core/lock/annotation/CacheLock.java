package com.lyl.core.lock.annotation;

import java.lang.annotation.*;

/**
 * @Author lyl
 * @Description
 * @Date 2018/9/30 上午10:15
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface CacheLock {


    /**
     * redis前缀
     */
    String prefix() default "";

    /**
     * 过期秒数
     */
    int expire() default 5;

    /**
     * <p>
     *     key的分隔符
     * </p>
     */
    String delimiter() default ":";
}

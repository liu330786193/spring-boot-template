package com.lyl.core.dulplicate.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 防止重复提交注解,
 * 第一步将AvoidDuplicateSubmissionInterceptor
 * 加入到拦截器
 * 添加相应的拦截方法
 * @Override
 * public void addInterceptors(InterceptorRegistry registry) {
 *     registry.addInterceptor(new AvoidDuplicateSubmissionInterceptor())
 *             .addPathPatterns("/test/avoid");
 * }
 *
 * 第二部 在Method直接使用 @AvoidDuplicateSubmission注解
 * @AvoidDuplicateSubmission
 * @PostMapping("/test/avoid")
 * public void testAvoid(){
 *     System.out.println("/test/avoid");
 * }
 *
 * 现在的防重是通过url传入的token防重的 使用的是synchronize(key)
 * key采用的就是uri+token规则并不复杂 由于内部使用 不存在对外传输
 * 对key的编解码应该也没有必要 徒增性能消耗
 * 将key存入redis 通过判断redis相应key值是否存在达到防重的效果
 * key的过期时间为30秒 防止认为或者不可抗力因素导致由于相同接口无法完成操作
 * Created by ylc on 2018/9/18.
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AvoidDuplicateSubmission {

    /**
     * 注解，添加token
     */
    boolean needSaveToken() default false;

    /**
     * 注解，验证token，后移除token
     */
    boolean needRemoveToken() default false;

    /**
     * redis前缀
     */
    String prefix() default "";

    /**
     * 过期秒数 默认1分钟
     */
    int expire() default 60;

}

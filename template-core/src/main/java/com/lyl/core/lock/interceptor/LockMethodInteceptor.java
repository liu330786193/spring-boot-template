package com.lyl.core.lock.interceptor;

import com.lyl.core.enable.RedisTemplate;
import com.lyl.core.lock.annotation.CacheLock;
import com.lyl.core.lock.annotation.CacheParam;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @Author lyl
 * @Description
 * @Date 2018/9/30 上午10:31
 */
@Aspect
public class LockMethodInteceptor {

    private RedisTemplate redisTemplate;

    @Autowired
    public LockMethodInteceptor(RedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    @Around("execution(public * *(..)) && @annotation(com.lyl.core.lock.annotation.CacheLock)")
    public Object interceptor(ProceedingJoinPoint pjp){
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        CacheLock lock = method.getAnnotation(CacheLock.class);
        if (StringUtils.isEmpty(lock)){
            throw new RuntimeException();
        }
        final String lockKey = getLockKey(pjp);
        try {
            final Boolean success = redisTemplate.set(lockKey, "", lock.expire());
            if (!success){
                throw new RuntimeException("请勿重复请求");
            }
            try {
                return pjp.proceed();
            } catch (Throwable throwable){
                throw new RuntimeException("系统异常");
            }

        } finally {
            redisTemplate.delete(lockKey);
        }
    }

    public String getLockKey(ProceedingJoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        CacheLock localAnnotation = method.getAnnotation(CacheLock.class);
        final Object[] args = pjp.getArgs();
        final Parameter[] parameters = method.getParameters();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < parameters.length; i++){
            final CacheParam annotation = parameters[i].getAnnotation(CacheParam.class);
            if (annotation == null){
                continue;
            }
            builder.append(localAnnotation.delimiter()).append(args[i]);
        }
        if (StringUtils.isEmpty(builder.toString())){
            final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            for (int i = 0; i < parameterAnnotations.length; i++){
                final Object object = args[i];
                final Field[] fields = object.getClass().getDeclaredFields();
                for (Field field : fields){
                    final CacheParam annotation = field.getAnnotation(CacheParam.class);
                    if (annotation == null){
                        continue;
                    }
                    field.setAccessible(true);
                    builder.append(localAnnotation.delimiter()).append(ReflectionUtils.getField(field, object));
                }
            }
        }
        return localAnnotation.prefix() + builder.toString();
    }

}

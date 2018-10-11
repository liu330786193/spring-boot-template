package com.lyl.core.dulplicate.inteceptor;

import com.lyl.core.dulplicate.annotation.AvoidDuplicateSubmission;
import com.lyl.core.enable.RedisTemplate;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * lyl 20180907
 * 防重提交拦截器
 *
 */
@Aspect
@Slf4j
public class AvoidDuplicateSubmissionInterceptor{

    @Value("${spring.avoid.dulplicate.prefix}")
    private String prefix;

    @Autowired
    private final RedisTemplate redisTemplate;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    @Autowired
    public AvoidDuplicateSubmissionInterceptor(RedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    @Around("execution(public * *(..)) && @annotation(com.lyl.core.dulplicate.annotation.AvoidDuplicateSubmission)")
    public Object interceptor(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        //校验注解是否存在
        AvoidDuplicateSubmission avoidDuplicateSubmission = method.getAnnotation(AvoidDuplicateSubmission.class);
        if (StringUtils.isEmpty(avoidDuplicateSubmission)){
            throw new RuntimeException();
        }
        //如果两个注解同时为false 视为无效
        if (avoidDuplicateSubmission.needRemoveToken() == avoidDuplicateSubmission.needSaveToken()){
            return pjp.proceed();
        }

        //如果需要移除key值 表示用户表单提交成功
        if (avoidDuplicateSubmission.needRemoveToken()){
            if (redisTemplate.delete(validateKey(request, avoidDuplicateSubmission.prefix()))){
                return pjp.proceed();
            }
            throw new RuntimeException("请勿重复提交表单");
        }

        //需要保存key值 通常用在提交表单，支付等插入数据库操作的前置操作
        if (avoidDuplicateSubmission.needSaveToken()){
            final String lockKey = generateKey(response, avoidDuplicateSubmission.prefix());
            try {
                final Boolean success = redisTemplate.set(lockKey, "", avoidDuplicateSubmission.expire());
                if (success){
                    return pjp.proceed();
                }
            } catch (Exception ex){
                log.error("redis系统异常", ex);
                return pjp.proceed();
            }
        }
        throw new RuntimeException("系统异常");
    }

    public String generateKey(HttpServletResponse response, String annotationPrefix){
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        String key = new StringBuffer()
                .append(annotationPrefix.equals("") ? prefix : annotationPrefix)
                .append(":")
                .append(token)
                .toString();
        response.setHeader("d-token", token);
        return key;
    }

    public String validateKey(HttpServletRequest request, String annotationPrefix){
        String token = request.getHeader("d-token");
        return new StringBuffer()
                .append(annotationPrefix.equals("") ? prefix : annotationPrefix)
                .append(":")
                .append(token)
                .toString();
    }




}

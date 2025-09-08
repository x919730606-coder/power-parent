package com.powernode.common.aspect;

import com.powernode.common.constant.RedisConstant;
import com.powernode.common.execption.PowerException;
import com.powernode.common.result.ResultCodeEnum;
import com.powernode.common.util.AuthContextHolder;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class PowerLoginAspect {

    @Resource
    private RedisTemplate redisTemplate;;

    @Around("execution(* com.powernode.*.controller.*.*(..)) && @annotation(com.powernode.common.annotation.PowerLogin)")
    public Object process(ProceedingJoinPoint pjp){
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String token = request.getHeader("token");
        if (!StringUtils.hasText( token)){
            throw new PowerException(ResultCodeEnum.LOGIN_AUTH);
        }
        String userId = (String) redisTemplate.opsForValue().get(RedisConstant.USER_LOGIN_KEY_PREFIX + token);
        if (!StringUtils.hasText(userId)){
            throw new PowerException(ResultCodeEnum.LOGIN_AUTH);
        }
        AuthContextHolder.setUserId(Long.valueOf(userId));
        try {
            return pjp.proceed();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}

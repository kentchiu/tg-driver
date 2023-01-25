package com.kent;

import com.kent.base.service.LoggerHelper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private LoggerHelper loggerHelper;

    @Autowired
    public void setLoggerHelper(LoggerHelper loggerHelper) {
        this.loggerHelper = loggerHelper;
    }


    /**
     * 配置切入点
     */
    @Pointcut("@annotation(io.swagger.v3.oas.annotations.Operation) && !@annotation(com.kent.base.service.LogIgnore)")
    public void logPointcut() {
        // 该方法无方法体,主要为了让同类中其他方法使用此切入点
    }

    @Around("logPointcut()")
    public Object logTimeMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        return loggerHelper.log(joinPoint);
    }

}


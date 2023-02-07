package com.kent;

import com.kent.base.service.LoggerHelper;
import org.aspectj.lang.annotation.Aspect;
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


//    @Pointcut("@annotation(io.swagger.v3.oas.annotations.Operation) && !@annotation(com.kent.base.service.LogIgnore)")
//    public void logPointcut() {
//    }

//    @Around("logPointcut()")
//    public Object logTimeMethod(ProceedingJoinPoint joinPoint) throws Throwable {
//        return loggerHelper.log(joinPoint);
//    }

}


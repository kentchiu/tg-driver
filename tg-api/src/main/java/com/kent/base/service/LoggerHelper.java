package com.kent.base.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kent.base.domain.DomainUtil;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Component
public class LoggerHelper {

    private final Logger logger = LoggerFactory.getLogger(LoggerHelper.class);


    public static String getContextId() {
        return "UNKNOWN";
    }

    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object retVal = null;
        Throwable error = null;
        try {
            retVal = joinPoint.proceed();
        } catch (Throwable t) {
            error = t;
            throw t;
        } finally {
            stopWatch.stop();
            long time = stopWatch.getTime();
            logging(joinPoint, time, error);
        }
        return retVal;
    }

    private void logging(ProceedingJoinPoint joinPoint, long time, Throwable error) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Operation apiOperation = method.getAnnotation(Operation.class);
        String methodName = joinPoint.getTarget().getClass().getName() + "." + signature.getName() + "()"; // 方法路径
        String paramsStr = null; // 方法参数
        try {
            paramsStr = buildParams(joinPoint);
        } catch (JsonProcessingException e) {
            paramsStr = "(?)";
            e.printStackTrace();
        }
        String username = getContextId(); // 用户名 或 client id
        ApiLog apiLog = new ApiLog();
        apiLog.setUsername(username);
        apiLog.setDescription(apiOperation.description()); // 描述
        apiLog.setMethod(methodName);
        apiLog.setRequestIp(getIP()); // 获取IP地址
        apiLog.setParams(paramsStr);
        if (error != null) {
            apiLog.setException(error.getMessage());
        }
        apiLog.setCreateTime(LocalDateTime.now());

        apiLog.setTime(time);

        logger.info("{}", apiLog);
    }

    private String buildParams(ProceedingJoinPoint joinPoint) throws JsonProcessingException {
        //参数值
        Object[] argValues = joinPoint.getArgs();
        //参数名称
        String[] argNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();

        ObjectMapper om = DomainUtil.createObjectMapper();
        String paramsStr = "{";
        if (argValues != null) {
            for (int i = 0; i < argValues.length; i++) {
                paramsStr += " " + argNames[i] + ": " + om.writeValueAsString(argValues[i]);
            }
        }
        paramsStr += " }";
        return paramsStr;
    }

    private String getIP() {
        // 获取request
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }


}


class ApiLog {


    /**
     * 操作用户
     */
    private String username;

    /**
     * 描述
     */
    private String description;

    /**
     * 方法名
     */
    private String method;

    /**
     * 参数
     */
    private String params;

    /**
     * 请求ip
     */
    private String requestIp;

    /**
     * 请求耗时
     */
    private Long time;

    /**
     * 异常详细
     */
    private String exception;

    /**
     * 创建日期
     */
    private LocalDateTime createTime;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getRequestIp() {
        return requestIp;
    }

    public void setRequestIp(String requestIp) {
        this.requestIp = requestIp;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        String sb = "ApiLog{" + "username='" + username + '\'' +
                ", description='" + description + '\'' +
                ", method='" + method + '\'' +
                ", params='" + params + '\'' +
                ", requestIp='" + requestIp + '\'' +
                ", time=" + time +
                ", exception='" + exception + '\'' +
                ", createTime=" + createTime +
                '}';
        return sb;
    }
}

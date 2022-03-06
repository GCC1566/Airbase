package com.gcc.airbase.requestlogserver.aspect;

import com.gcc.airbase.requestlogserver.annotation.RequestRecord;
import com.gcc.airbase.requestlogserver.model.EagleLogEntity;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Component
@Aspect
@Slf4j(topic = "EagleEye-Listener")
public class RequestLogAspect {

    @Autowired
    HttpServletRequest request;


    @Around("@annotation(com.gcc.airbase.requestlogserver.annotation.RequestRecord)")
    public Object aroundAdvice(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        RequestRecord requestRecord = method.getAnnotation(RequestRecord.class);
        EagleLogEntity eagle = new EagleLogEntity(request);
        long startTime = System.currentTimeMillis();
        Object proceed = point.proceed();
        long endTime = System.currentTimeMillis();
        eagle.calculateTimeSpan(requestRecord.timeSpan(),startTime,endTime);
        log.info(eagle.getLogInfo(requestRecord.item(),requestRecord.timeSpan()));
        return proceed;
    }




}

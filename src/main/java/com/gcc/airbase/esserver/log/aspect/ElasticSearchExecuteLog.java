package com.gcc.airbase.esserver.log.aspect;

import com.gcc.airbase.config.AirBaseProperties;
import com.gcc.airbase.esserver.dto.ESearchParamDto;
import com.gcc.airbase.esserver.log.entity.EsExecuteLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;

@Component
@Aspect
@Slf4j(topic = "ElasticSearch Execute Notes")
public class ElasticSearchExecuteLog {

    @Autowired
    private AirBaseProperties properties;

    @Around("@annotation(com.gcc.airbase.esserver.log.annotation.ExecuteLog)")
    public Object aroundAdvice(ProceedingJoinPoint point) throws Throwable {
        Object proceed;
        if(properties.getEsServerLogNotes()){
            EsExecuteLog esLog = new EsExecuteLog();
            MethodSignature signature = (MethodSignature) point.getSignature();
            Method method = signature.getMethod();
            esLog.setMethodName(method.getName());
            fillingParams(point.getArgs(),esLog);
            long startTime = System.currentTimeMillis();
            proceed = point.proceed();
            long endTime = System.currentTimeMillis();
            esLog.setTimeSpan(endTime - startTime);
            log.info(esLog.getLogInfo());
        }else {
            proceed = point.proceed();
        }
        return proceed;
    }

    private void fillingParams(Object[] args,EsExecuteLog esLog){
        if(args.length > 1){
            esLog.setIndexName(String.valueOf(args[0]));
            esLog.setQueryDsl(String.valueOf(args[1]));
        }else if(args.length == 1 && (args[0] instanceof ESearchParamDto)){
            ESearchParamDto esSearchParamDto = (ESearchParamDto)args[0];
            esLog.setIndexName(esSearchParamDto.getIndexName());
            esLog.setQueryDsl(esSearchParamDto.getSearchSource().toString());
        }
    }

}

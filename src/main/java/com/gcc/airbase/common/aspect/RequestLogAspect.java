package com.gcc.airbase.common.aspect;

import com.gcc.airbase.common.annotation.RequestRecord;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Component
@Aspect
public class RequestLogAspect {

    @Autowired
    LogProxy logProxy;

    @Autowired
    HttpServletRequest request;

    private final static String START_STR = "【Request Start】";

    private final static String END_STR = "【Request   End】";

    private final static String ITEM_STR = "=======================";

    @Around("@annotation(com.gcc.airbase.common.annotation.RequestRecord)")
    public Object aroundAdvice(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        RequestRecord requestRecord = method.getAnnotation(RequestRecord.class);
        String item = appendStr(requestRecord.item(),23);
        logProxy.synclog(item+START_STR+item);
        logProxy.synclog("request-method :{}",request.getMethod());
        logProxy.synclog("request-host   :{}",getHost());
        logProxy.synclog("request-uri    :{}",getUri());
        logProxy.synclog("request-param  :{}",getParams());
        long startTime = System.currentTimeMillis();
        Object proceed = point.proceed();
        long endTime = System.currentTimeMillis();
        calculateTimeSpan(requestRecord.timeSpan(),startTime,endTime);
        logProxy.synclog(item+END_STR+item+"\n");
        return proceed;
    }


    private String getUri(){
        return request.getRequestURI();
    }

    private String getHost(){
        String ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (ipAddress.equals("0:0:0:0:0:0:0:1")) {
                return "127.0.0.1";
            }
            return ipAddress;
        }
        if (ipAddress != null && ipAddress.length() > 15) {
            if (ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        return ipAddress;
    }

    private String getParams(){
        try {
            return new String(StreamUtils.copyToByteArray(request.getInputStream()), request.getCharacterEncoding()).trim();
        }catch (Exception e){
            return " get parameter error ";
        }
    }

    private void calculateTimeSpan(Boolean flag,Long startTime,Long endTime){
        if(flag) {
            //todo
            logProxy.synclog("response-time  :{}s", 0.3);
        }
    }

    private String appendStr(String item,int size){
        if(!item.equals("=")) {
            StringBuilder reStr = new StringBuilder(item);
            for (int i = 0; i < size; i++) {
                reStr.append(item);
            }
            return reStr.toString();
        }else {
            return ITEM_STR;
        }
    }


}

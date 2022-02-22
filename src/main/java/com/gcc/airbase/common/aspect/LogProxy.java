package com.gcc.airbase.common.aspect;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j(topic = "EagleEye-Listener")
public class LogProxy {

    public LogProxy(){}

    @Async
    public void synclog(String s,Object ...obj){
        log.info(s,obj);
    }

}

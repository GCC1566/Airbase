package com.gcc.airbase.requestlogserver.aspect;

import com.gcc.airbase.StartApplicationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class LogProxyTest extends StartApplicationTest {

    @Autowired
    LogProxy logProxy;

    @Test
    void synclog() {
        logProxy.synclog("测试数据");
    }
}
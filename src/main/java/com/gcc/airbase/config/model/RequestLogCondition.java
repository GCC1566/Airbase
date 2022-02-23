package com.gcc.airbase.config.model;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class RequestLogCondition implements Condition {

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        String enable = conditionContext.getEnvironment().getProperty("airbase.apilog-enable");
        if(null != enable && !enable.equals("")){
            return Boolean.valueOf(enable);
        }
        return false;
    }

}

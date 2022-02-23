package com.gcc.airbase.config.model;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class EsCondition implements Condition {

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
       String esenable = conditionContext.getEnvironment().getProperty("airbase.esserver-enable");
        if(null != esenable && !esenable.equals("")){
            return Boolean.valueOf(esenable);
        }
        return false;

    }

}

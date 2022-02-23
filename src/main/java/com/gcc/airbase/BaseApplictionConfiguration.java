package com.gcc.airbase;

import com.gcc.airbase.config.AirBaseProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.gcc.airbase.*")
public class BaseApplictionConfiguration {

    @Autowired
    private AirBaseProperties properties;


}
